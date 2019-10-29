package app.onedayofwar;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.onedayofwar.Graphics.Assets;

/**
 * Created by Slava on 26.01.2015.
 */
public class GameOverActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.game_over);
        setFont();

        TextView rewardTxt = (TextView)findViewById(R.id.rewardText);
        rewardTxt.setText("" + getIntent().getIntExtra("reward", 0));
        TextView resultTxt = (TextView)findViewById(R.id.resultText);
        resultTxt.setText(getIntent().getBooleanExtra("result", false) ? "YOU WIN!" : "YOU LOSE!");
    }

    private void setFont()
    {
        TextView txt = (TextView)findViewById(R.id.rewardText);
        txt.setTypeface(Assets.mainFont);
        txt = (TextView)findViewById(R.id.resultText);
        txt.setTypeface(Assets.mainFont);
        txt = (TextView)findViewById(R.id.textView);
        txt.setTypeface(Assets.mainFont);
        txt = (TextView)findViewById(R.id.textView3);
        txt.setTypeface(Assets.mainFont);
        Button btn = (Button)findViewById(R.id.gameOverBtn);
        btn.setTypeface(Assets.mainFont);
    }

    public void gameOverBtnClick(View view)
    {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}
