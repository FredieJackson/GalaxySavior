package app.onedayofwar.Activities.BluetoothConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.UUID;

import app.onedayofwar.Activities.GameActivity;
import app.onedayofwar.R;

/**
 * Created by Slava on 09.02.2015.
 */
public class BluetoothController
{
    private static final UUID APP_UUID = UUID.fromString("9f691062-ff6b-4f86-9f6f-8329174d2343");
    private static final String APP_NAME = "ODOWBT";
    public int state;
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    private BluetoothAdapter bluetoothAdapter;
    private ListView devicesListView;
    private ArrayList<BluetoothDevice> devices;
    private ArrayAdapter<String> btArrayAdapter;
    private int selectedDevice;
    private GameActivity activity;
    private final Handler handler;

    private ServerThread serverThread;
    private ClientThread clientThread;
    private ConnectedThread connectedThread;
    private BluetoothSocket enemySocket;

    public boolean isEnemyConnected;

    public BluetoothController(final GameActivity activity)
    {
        this.activity = activity;

        isEnemyConnected = false;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        activity.startActivity(discoverableIntent);

        //region MSG Handler
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case HandlerMSG.SHOW_ATTACK_REQUEST_DIALOG:
                        AlertDialog.Builder attackRequestDialog = new AlertDialog.Builder(activity);
                        attackRequestDialog.setTitle(enemySocket.getRemoteDevice().getName() + " вызывает вас на бой!");

                        attackRequestDialog.setPositiveButton("В атаку!", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                StopClientThread();
                                StartConnectedThread(enemySocket);
                                connectedThread.write(HandlerMSG.ACCEPT_FIGHT_REQUEST);
                                isEnemyConnected = true;
                                StartGame(false);
                            }
                        });
                        attackRequestDialog.setNegativeButton("Отказать", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                StopClientThread();
                                StartConnectedThread(enemySocket);
                                connectedThread.write(HandlerMSG.REJECT_FIGHT_REQUEST);
                            }
                        });
                        attackRequestDialog.show();
                        break;

                    case HandlerMSG.SHOW_ARG1_TOAST:
                        Toast.makeText(activity.getApplicationContext(), "" + msg.arg1, Toast.LENGTH_SHORT).show();
                        break;

                    case HandlerMSG.SHOW_TXT_TOAST:
                        Toast.makeText(activity.getApplicationContext(), "" + msg.obj, Toast.LENGTH_SHORT).show();
                        break;

                    case HandlerMSG.SEND_DATA_TO_TARGET:
                        break;

                    case HandlerMSG.START_GAME:
                        activity.StartGame(msg.arg1 == 1);
                        break;

                    case HandlerMSG.CONNECTION_LOST:
                        Toast.makeText(activity.getApplicationContext(), "CONNECTION LOST", Toast.LENGTH_SHORT).show();
                        Stop();
                        activity.finish();
                        break;
                    case HandlerMSG.GAME_OVER:
                        Stop();
                        activity.GameOver();
                        break;
                }
            }
        };
        //endregion

        devices = new ArrayList<>();
        devicesListView = (ListView)activity.findViewById(R.id.listView);
        btArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice);
        activity.registerReceiver(myBluetoothReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        Scan();
        devicesListView.setAdapter(btArrayAdapter);
        devicesListView.setOnItemClickListener(deviceClickListener);
        selectedDevice = -1;
        StartServerThread();
        Toast.makeText(activity.getApplicationContext(), "Server Started", Toast.LENGTH_SHORT).show();
    }

    private void doDiscovery()
    {
        // If we're already discovering, stop it
        if (bluetoothAdapter.isDiscovering())
        {
            bluetoothAdapter.cancelDiscovery();
        }
        // Request discover from BluetoothAdapter
        bluetoothAdapter.startDiscovery();
    }

    public void Scan()
    {
        selectedDevice = -1;
        btArrayAdapter.clear();
        devices.clear();
        doDiscovery();
    }

    public void CancelScan()
    {
        bluetoothAdapter.cancelDiscovery();
    }

    //region Attack Request
    public void SendAttackRequest()
    {
        if(connectedThread == null)
        {
            if (selectedDevice != -1)
            {
                StartClientThread(devices.get(selectedDevice));
            }
        }
    }

    public void RecieveAttackRequest(BluetoothSocket eSocket)
    {
        enemySocket = eSocket;
        StopServerThread();
        handler.obtainMessage(HandlerMSG.SHOW_ATTACK_REQUEST_DIALOG).sendToTarget();
    }
    //endregion

    //region Start Threads
    public void StartServerThread()
    {
        serverThread = new ServerThread(APP_NAME, APP_UUID, this);
        serverThread.start();
        state = STATE_LISTEN;
    }

    public void StartClientThread(BluetoothDevice targetDevice)
    {
        clientThread = new ClientThread(targetDevice, APP_UUID, this);
        clientThread.start();
        state = STATE_CONNECTING;
    }

    public void StartConnectedThread(BluetoothSocket targetSocket)
    {
        connectedThread = new ConnectedThread(targetSocket, this);
        connectedThread.start();
        state = STATE_CONNECTED;
    }
    //endregion

    //region Stop Threads
    public void StopClientThread()
    {
        if (clientThread != null)
        {
            //clientThread.CloseSocket();
            clientThread = null;
        }
    }

    public void StopServerThread()
    {
        if (serverThread != null)
        {
            serverThread.CloseSocket();
            serverThread = null;
        }
    }

    public void StopConnectedThread()
    {
        if (connectedThread != null)
        {
            //connectedThread.CloseSocket();
            connectedThread = null;
        }
    }

    public void Stop()
    {
        CancelScan();
        if (connectedThread != null)
        {
            connectedThread.CloseSocket();
            connectedThread = null;
        }
        if (serverThread != null)
        {
            serverThread.CloseSocket();
            serverThread = null;
        }
        if (clientThread != null)
        {
            clientThread.CloseSocket();
            clientThread = null;
        }
    }
    //endregion

    public void Destroy()
    {
        activity.unregisterReceiver(myBluetoothReceiver);
        StopClientThread();
        StopServerThread();
        StopConnectedThread();
    }

    //region Listeners
    private final AdapterView.OnItemClickListener deviceClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id)
        {
            Toast.makeText(activity.getApplicationContext(), devices.get(position).getName() + "\n" + devices.get(position).getAddress(), Toast.LENGTH_SHORT).show();
            selectedDevice = position;
        }
    };

    private final BroadcastReceiver myBluetoothReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for(int i = 0; i < devices.size(); i++)
                {
                    if(device.getAddress().equals(devices.get(i).getAddress()))
                    {
                        return;
                    }
                }
                devices.add(device);
                btArrayAdapter.add(device.getName());
                btArrayAdapter.notifyDataSetChanged();
            }
        }
    };
    //endregion


    public void StartGame(boolean isYourTurn)
    {
        handler.obtainMessage(HandlerMSG.START_GAME, isYourTurn ? 1 : 0, 0).sendToTarget();
    }

    public void GameOver()
    {
        handler.obtainMessage(HandlerMSG.GAME_OVER).sendToTarget();
    }

    public void ShowToast(int arg)
    {
        handler.obtainMessage(HandlerMSG.SHOW_ARG1_TOAST, arg, 0).sendToTarget();
    }

    public void ShowToast(String txt)
    {
        handler.obtainMessage(HandlerMSG.SHOW_TXT_TOAST, txt).sendToTarget();
    }

    public void ConnectionLost()
    {
        handler.obtainMessage(HandlerMSG.CONNECTION_LOST).sendToTarget();
    }

    public void SendData(String data)
    {
        connectedThread.write(data);
    }

    public String GetRecievedData()
    {
        return connectedThread.recievedData;
    }
}
