package app.onedayofwar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        setContentView(R.layout.main);
    }
    public void ClickBtn1(View view)
    {
        setContentView(R.layout.main_game_mode);
    }
    public void ClickSingleBtn(View view)
    {
        StartGame('s');
    }
    public void ClickBluetoothBtn(View view)
    {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
        {
            Toast.makeText(this, "ERROR: BLUETOOTH UNSUPPORTED", Toast.LENGTH_LONG).show();
            return;
        }
        if (btAdapter.isEnabled())
        {
            Toast.makeText(this, "BLUETOOTH IS ON", Toast.LENGTH_LONG).show();
            StartGame('b');
        }
        else
        {
            Toast.makeText(this, "BLUETOOTH IS OFF", Toast.LENGTH_LONG).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
            if(btAdapter.isEnabled())
                StartGame('b');
        }
    }
    public void ClickInternetBtn(View view)
    {
        StartGame('i');
    }
    public void ClickBackBtn(View view)
    {
        setContentView(R.layout.main);
    }
    public void ClickBtn2(View view)
    {

    }
    public void ClickBtn3(View view)
    {

    }
    private void StartGame(char type)
    {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
        setContentView(R.layout.loading);
    }
}
