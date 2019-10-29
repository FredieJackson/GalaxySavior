package app.onedayofwar.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.BluetoothConnection.BluetoothController;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Battle.System.BattleView;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.R;

public class BluetoothActivity extends Activity
{

    private BluetoothController btController;
    BluetoothAdapter btAdapter;
    private boolean isBluetoothOff;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth);
        isBluetoothOff = true;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
        {
            Toast.makeText(this, "ERROR: BLUETOOTH UNSUPPORTED", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (btAdapter.isEnabled())
        {
            Toast.makeText(this, "BLUETOOTH IS ON", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "BLUETOOTH IS OFF", Toast.LENGTH_LONG).show();
            setContentView(R.layout.loading);
            isBluetoothOff = true;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        if(btAdapter.isEnabled())
            btController = new BluetoothController(this);
        else
            finish();
    }

    public void scanBtnClick(View view)
    {
        btController.Scan();
    }

    public void attackBtnClick(View view)
    {
        btController.SendAttackRequest();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "OnDestroy", Toast.LENGTH_SHORT).show();
        if(!isBluetoothOff)
            btController.Destroy();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if(isBluetoothOff && btAdapter.isEnabled())
            isBluetoothOff = false;
    }

    public void StartGame(boolean isYourTurn)
    {
        //battleView.LoadBT(btController);
        btController.CancelScan();
    }
}

