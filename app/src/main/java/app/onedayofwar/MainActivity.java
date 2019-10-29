package app.onedayofwar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.onedayofwar.Graphics.Assets;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
        Assets.mainFont = Typeface.createFromAsset(getAssets(), "fonts/hollowpoint.ttf");//clonewars.ttf");
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        setContentView(R.layout.main);
        mainFonts();
    }
    public void ClickBtn1(View view)
    {
        setContentView(R.layout.main_game_mode);
        gameModeFonts();
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
        mainFonts();
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

    private void mainFonts()
    {
        Button button =(Button)findViewById(R.id.startGameBtn);
        button.setTypeface(Assets.mainFont);
        button =(Button)findViewById(R.id.settingsBtn);
        button.setTypeface(Assets.mainFont);
        button =(Button)findViewById(R.id.infoBtn);
        button.setTypeface(Assets.mainFont);
        TextView text =(TextView)findViewById(R.id.appTitle);
        text.setTypeface(Assets.mainFont);
    }
    private void gameModeFonts()
    {
        Button button =(Button)findViewById(R.id.backBtn);
        button.setTypeface(Assets.mainFont);
        button =(Button)findViewById(R.id.singleBtn);
        button.setTypeface(Assets.mainFont);
        button =(Button)findViewById(R.id.bluetoothBtn);
        button.setTypeface(Assets.mainFont);
        button =(Button)findViewById(R.id.internetBtn);
        button.setTypeface(Assets.mainFont);
        button =(Button)findViewById(R.id.internetBtn);
        button.setTypeface(Assets.mainFont);
        TextView text =(TextView)findViewById(R.id.selectTitle);
        text.setTypeface(Assets.mainFont);
    }
}
