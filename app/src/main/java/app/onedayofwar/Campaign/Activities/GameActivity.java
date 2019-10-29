package app.onedayofwar.Campaign.Activities;

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
import android.widget.Toast;

import app.onedayofwar.Campaign.System.GameView;
import app.onedayofwar.Campaign.System.GameLoadThread;
import app.onedayofwar.R;

/**
 * Created by Slava on 16.02.2015.
 */
public class GameActivity extends Activity
{
    private GameView gameView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        gameView = new GameView(this, metrics.widthPixels, metrics.heightPixels);

        //region Handler
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);

                Toast.makeText(getApplicationContext(), "GAME LOAD COMPLETE", Toast.LENGTH_SHORT).show();
            }
        };
        //endregion

        //setContentView(R.layout.loading);
        setContentView(gameView);
        //GameLoadThread gameLoadThread = new GameLoadThread(this);
        //gameLoadThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Log.i("RESULT", "test " + requestCode + " / " + resultCode + " / ");
        if(requestCode == ActivityRequests.START_BATTLE)
        {
            gameView.SetBattleResult(data, resultCode);
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        quitDialog.setTitle("Выйти?");

        quitDialog.setPositiveButton("Да!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });

        quitDialog.setNegativeButton("Нет!", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which){}
        });

        quitDialog.show();
    }

    public void FinishLoading()
    {
        handler.sendEmptyMessage(0);
    }
}
