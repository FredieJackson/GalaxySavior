package app.onedayofwar.System;

import android.graphics.Canvas;
import app.onedayofwar.GameView;


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
