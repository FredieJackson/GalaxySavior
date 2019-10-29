package app.onedayofwar.Campaign.System;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import app.onedayofwar.Campaign.System.GameView;
import app.onedayofwar.System.FPScounter;

/**
 * Created by Slava on 16.02.2015.
 */
public class GameThread extends Thread
{
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
        /*long startTime;
        long sleepTime;
        float eTime;
        short fps = 60;
        Canvas canvas = null;
        Rect rect = new Rect(0,0, gameView.screenWidth, gameView.screenHeight);
        while (isRunning)
        {
            FPScounter.StartCounter();
            startTime = System.nanoTime();
            try
            {
                canvas = gameView.getHolder().lockCanvas(null);
                synchronized (gameView.getHolder())
                {
                    if(canvas != null)
                    {
                        eTime = 1f/fps * 10;
                        gameView.Update(eTime);
                        gameView.Draw();

                        canvas.drawBitmap(gameView.graphics.frameBuffer, null, rect, null);

                        if(gameView.paint != null)
                        {
                            canvas.drawText("FPS: " + fps, 50, 100, gameView.paint);
                        }
                    }
                }
            }
            finally
            {
                if(canvas != null)
                {
                    gameView.getHolder().unlockCanvasAndPost(canvas);
                }
            }
            sleepTime = 15 - (int)((System.nanoTime() - startTime) / 1000000f);

            if (sleepTime > 0)
            {
                try
                {
                    sleep(sleepTime);
                }
                catch (InterruptedException e)
                {

                }
            }

            fps = FPScounter.StopAndPost();
        }*/
    }
}