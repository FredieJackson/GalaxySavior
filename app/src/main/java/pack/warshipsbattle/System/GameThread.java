package pack.warshipsbattle.System;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;

import pack.warshipsbattle.GameView;
import pack.warshipsbattle.R;

public class GameThread extends Thread {
    /**Объект класса*/
    private GameView gameView;

    /**Переменная задавания состояния потока рисования*/
    private boolean isRunning = false;

    /**Конструктор класса*/
    public GameThread(GameView gameView)
    {
        this.gameView = gameView;
    }

    /**Задание состояния потока*/
    public void setRunning(boolean run)
    {
        isRunning = run;
    }

    /** Действия, выполняемые в потоке */
    public void run()
    {
        while (isRunning)
        {
            Canvas canvas = null;
            try
            {
                canvas = gameView.getHolder().lockCanvas(null);
                synchronized (gameView.getHolder())
                {
                    gameView.Update();
                    gameView.Draw(canvas);
                }
            }
            finally
            {
                if (canvas != null)
                {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
