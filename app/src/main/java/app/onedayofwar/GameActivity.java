package app.onedayofwar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import app.onedayofwar.Games.Game;
import app.onedayofwar.Games.SingleGame;
import app.onedayofwar.Graphics.Assets;

public class GameActivity extends Activity implements SeekBar.OnSeekBarChangeListener
{
    GameView gameView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.difficulty);

        settingsFont();
        SeekBar bar = (SeekBar) findViewById(R.id.diffBar);
        bar.setProgress(30);
        TextView text = (TextView) findViewById(R.id.diffText);
        text.setText(30 + "%");
        bar.setOnSeekBarChangeListener(this);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        Player.Initialize();

        gameView = new GameView(this, getIntent().getCharExtra("type", 'e'), metricsB.widthPixels, metricsB.heightPixels);

    }

    public void diffBtnClick(View view)
    {
        SeekBar bar = (SeekBar)findViewById(R.id.diffBar);
        SingleGame.difficulty = (byte)bar.getProgress();
        setContentView(gameView);
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
}

