package app.onedayofwar.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.GLView;
import app.onedayofwar.System.MainView;

public class MainActivity extends Activity
{
    private GLView glView;

    public enum GameState {BATTLE, MENU, CAMPAIGN}
    public GameState gameState;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gameState = GameState.MENU;

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        glView = new GLView(this, metrics.widthPixels, metrics.heightPixels);
        Assets.mainFont = Typeface.createFromAsset(getAssets(), "fonts/hollowpoint.ttf");
        setContentView(glView);
    }



    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(this);
        String title;
        String positive;
        String negative;
        switch(gameState)
        {
            case MENU:
                title = "Выйти?";
                positive = "Да!";
                negative = "нет!";
                break;
            case BATTLE:
                title = "Ваши приказания?";
                positive = "Отступаем!";
                negative = "Ни шагу назад!";
                break;
            case CAMPAIGN:
                title = "Выйти?";
                positive = "Да!";
                negative = "Нет!";
                break;
            default:
                title = "Выйти?";
                positive = "Да!";
                negative = "Нет!";
                break;
        }
        quitDialog.setTitle(title);

        quitDialog.setPositiveButton(positive, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (gameState == GameState.MENU)
                    finish();
                else
                    glView.goBack();
            }
        });

        quitDialog.setNegativeButton(negative, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which){}
        });

        quitDialog.show();
    }
}
