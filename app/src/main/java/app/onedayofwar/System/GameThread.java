package app.onedayofwar.System;

import android.graphics.Canvas;

import app.onedayofwar.FPScounter;
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
        long startTime;
       // float elapsedTime;
        long sleepTime;
        short fps = 0;
        Canvas canvas = null;
        while (isRunning)
        {
            //elapsedTime = (System.nanoTime()-startTime) / 1000000.0f;
            FPScounter.StartCounter();
            startTime = System.nanoTime();
            try
            {
                canvas = gameView.getHolder().lockCanvas(null);
                synchronized (gameView.getHolder())
                {
                    if(canvas != null)
                    {
                        gameView.Update();
                        gameView.Draw(canvas);

                        if(gameView.paint != null)
                            canvas.drawText("FPS: "+ fps, 50,50, gameView.paint);

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

            sleepTime = 15 - (System.nanoTime() - startTime)/1000000;

            if(sleepTime > 0)
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
        }
    }
}
