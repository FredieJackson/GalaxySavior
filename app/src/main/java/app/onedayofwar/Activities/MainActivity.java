package app.onedayofwar.Activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.onedayofwar.Battle.Activities.BattleActivity;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Campaign.Activities.GameActivity;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.R;

public class MainActivity extends Activity
{
    BluetoothAdapter btAdapter;
    private boolean isBluetoothOff;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        isBluetoothOff = false;
        Assets.mainFont = Typeface.createFromAsset(getAssets(), "fonts/hollowpoint.ttf");
    }
    @Override
    protected void onResume()
    {
        super.onResume();
        if(isBluetoothOff && btAdapter.isEnabled())
        {
            StartQuickBattle('b');
            isBluetoothOff = false;
            return;
        }
        setContentView(R.layout.main);
        mainFonts();
    }

    public void ClickStartGameBtn(View view)
    {
        setContentView(R.layout.main_game_mode);
        gameModeFonts();
    }
    public void ClickSingleBtn(View view)
    {
        setContentView(R.layout.main_single_mode);
    }

    public void ClickCampaignBtn(View view)
    {
        StartCampaign();
    }

    public void ClickQuickBtn(View view)
    {
        StartQuickBattle('s');
    }

    public void ClickBluetoothBtn(View view)
    {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null)
        {
            Toast.makeText(this, "ERROR: BLUETOOTH UNSUPPORTED", Toast.LENGTH_LONG).show();
            return;
        }
        if (btAdapter.isEnabled())
        {
            Toast.makeText(this, "BLUETOOTH IS ON", Toast.LENGTH_LONG).show();
            StartQuickBattle('b');
        }
        else
        {
            Toast.makeText(this, "BLUETOOTH IS OFF", Toast.LENGTH_LONG).show();
            setContentView(R.layout.loading);
            isBluetoothOff = true;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    public void ClickInternetBtn(View view)
    {
        StartQuickBattle('i');
    }

    public void ClickBackBtn(View view)
    {
        setContentView(R.layout.main);
        mainFonts();
    }
    public void ClickInfoBtn(View view)
    {
        Toast.makeText(this.getApplicationContext(), R.string.version, Toast.LENGTH_SHORT).show();
    }

    private void StartQuickBattle(char type)
    {
        Intent intent = new Intent(this, BattleActivity.class);
        intent.putExtra("type", type);
        BattlePlayer.fieldSize = 15;
        BattlePlayer.unitCount = new byte[6];
        BattlePlayer.unitCount[0] = 1;
        BattlePlayer.unitCount[1] = 1;
        BattlePlayer.unitCount[2] = 1;
        BattlePlayer.unitCount[3] = 1;
        BattlePlayer.unitCount[4] = 1;
        BattlePlayer.unitCount[5] = 1;
        startActivity(intent);
    }

    private void StartCampaign()
    {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
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
        button =(Button)findViewById(R.id.singleGameBtn);
        button.setTypeface(Assets.mainFont);
        button =(Button)findViewById(R.id.bluetoothBtn);
        button.setTypeface(Assets.mainFont);
        button =(Button)findViewById(R.id.internetBtn);
        button.setTypeface(Assets.mainFont);
        TextView text =(TextView)findViewById(R.id.selectTitle);
        text.setTypeface(Assets.mainFont);
    }
}
