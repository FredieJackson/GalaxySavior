package app.onedayofwar.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import app.onedayofwar.Activities.BluetoothConnection.BluetoothController;
import app.onedayofwar.System.GameView;
import app.onedayofwar.Games.SingleGame;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.GameElements.Player;
import app.onedayofwar.R;

public class GameActivity extends Activity implements SeekBar.OnSeekBarChangeListener
{
    private GameView gameView;
    private char gameType;
    private BluetoothController btController;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        gameType = getIntent().getCharExtra("type", 'e');
        switch(gameType)
        {
            case 's':
                SingleGameLoad();
                break;
            case 'b':
                BluetoothGameLoad();
                break;
            case 'i':

                break;
            case 'e':
                Toast.makeText(this, "getExtra ERROR", Toast.LENGTH_SHORT).show();
                finish();
                return;
        }

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        Player.Initialize();

        gameView = new GameView(this, gameType, metricsB.widthPixels, metricsB.heightPixels);

    }

    public void diffBtnClick(View view)
    {
        SeekBar bar = (SeekBar)findViewById(R.id.diffBar);
        SingleGame.difficulty = (byte)bar.getProgress();
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
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }

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
        if(gameType == 'b')
        {
            btController.Destroy();
        }
    }

    public void StartGame(boolean isYourTurn)
    {
        if(gameType == 'b')
        {
            gameView.LoadBT(btController);
            btController.CancelScan();
        }
        gameView.SetAttackSequence(isYourTurn);
        setContentView(gameView);
    }

    public void GameOver()
    {
        gameView.GameOver();
    }
}

