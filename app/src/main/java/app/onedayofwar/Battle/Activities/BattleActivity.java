package app.onedayofwar.Battle.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import app.onedayofwar.Battle.BluetoothConnection.BluetoothController;
import app.onedayofwar.Battle.System.BattleView;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.R;
import app.onedayofwar.Battle.System.BattleLoadThread;

public class BattleActivity extends Activity implements SeekBar.OnSeekBarChangeListener
{
    private BattleView battleView;
    private char gameType;
    private BluetoothController btController;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        gameType = getIntent().getCharExtra("type", 'e');

        BattlePlayer.Initialize(0);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        battleView = new BattleView(this, gameType, metricsB.widthPixels, metricsB.heightPixels);

        //region Handler
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch(gameType)
                {
                    case 'c':
                        setContentView(battleView);
                        break;
                    case 's':
                        SingleGameLoad();
                        break;
                    case 'b':
                        BluetoothGameLoad();
                        break;
                    case 'i':

                        break;
                    case 'e':
                        Toast.makeText(getApplicationContext(), "getExtra ERROR", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                }
                Toast.makeText(getApplicationContext(), "LOAD COMPLETE", Toast.LENGTH_SHORT).show();
            }
        };

        //endregion

        BattleLoadThread battleLoadThread = new BattleLoadThread(this);
        battleLoadThread.start();
    }

    public void Initialize()
    {
        battleView.Initialize();
    }

    public void FinishLoading()
    {
        handler.sendEmptyMessage(0);
    }

    public void diffBtnClick(View view)
    {
        SeekBar bar = (SeekBar)findViewById(R.id.diffBar);
        SingleBattle.difficulty = (byte)bar.getProgress();
        StartGame(true);
    }

    private void settingsFont()
    {
        TextView text = (TextView)findViewById(R.id.diffText);
        text.setTypeface(Assets.mainFont);
        Button button = (Button)findViewById(R.id.diffBtn);
        button.setTypeface(Assets.mainFont);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        TextView text = (TextView)findViewById(R.id.diffText);
        text.setText(progress + "%");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar){}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar){}

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Ваши приказания?");

        quitDialog.setPositiveButton("Отступаем!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if(gameType == 'b')
                {
                    btController.Stop();
                }
                setResult(RESULT_CANCELED, null);
                finish();
            }
        });

        quitDialog.setNegativeButton("Ни шагу назад!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which){}
        });

        quitDialog.show();
    }

    private void SingleGameLoad()
    {
        setContentView(R.layout.difficulty);
        settingsFont();
        SeekBar bar = (SeekBar) findViewById(R.id.diffBar);
        bar.setProgress(30);
        TextView text = (TextView) findViewById(R.id.diffText);
        text.setText(30 + "%");
        bar.setOnSeekBarChangeListener(this);
    }

    private void BluetoothGameLoad()
    {
        setContentView(R.layout.bluetooth);
        btController = new BluetoothController(this);
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
        if(gameType == 'b')
        {
            btController.Destroy();
        }
    }

    public void StartGame(boolean isYourTurn)
    {
        if(gameType == 'b')
        {
            battleView.LoadBT(btController);
            btController.CancelScan();
        }
        battleView.SetAttackSequence(isYourTurn);
        setContentView(battleView);
    }

    public void GameOver()
    {
        battleView.GameOver();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        setResult(RESULT_OK, data);
        finish();
    }
}

