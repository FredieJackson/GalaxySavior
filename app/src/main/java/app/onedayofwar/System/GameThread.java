package app.onedayofwar.System;

import android.graphics.Canvas;
import android.os.SystemClock;

import app.onedayofwar.FPScounter;
import app.onedayofwar.GameView;
import app.onedayofwar.Graphics.Graphics;


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
        long startTime;
        long sleepTime;
        short fps = 0;
        //double test = 0;
        //double minTest = 1000;
        //double deltaTest = 0;
        Canvas canvas = null;
        while (isRunning)
        {
            //elapsedTime = (int)((System.nanoTime() - startTime) / 1000000);
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
                        gameView.Draw();

                        canvas.drawBitmap(gameView.graphics.frameBuffer, null, canvas.getClipBounds(), null);

                        if(gameView.paint != null)
                        {
                            canvas.drawText("FPS: " + fps, 50, 100, gameView.paint);
                            /*canvas.drawText("MAX: " + test, 400, 100, gameView.paint);
                            canvas.drawText("MIN: " + minTest, 400, 150, gameView.paint);
                            canvas.drawText("test: " + deltaTest, 400, 200, gameView.paint);*/

                        }
                        /*if (gameView.paint != null)
                        {
                            canvas.drawText("elapsed time: " + elapsedTime, 50, 50, gameView.paint);
                            if(elapsedTime > 0)
                                canvas.drawText("FPS: " + (1000 / elapsedTime), 50, 100, gameView.paint);
                        }*/
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


            /*deltaTest = (double)(System.nanoTime() - startTime) / 1000000;
            if(deltaTest > test)
                test = deltaTest;
            if(deltaTest < minTest)
                minTest = deltaTest;
            if(System.nanoTime() % 10000000000L < 500000000L)
            {
                test = 0;
                minTest = 1000;
            }*/
            sleepTime = 15 - (int)((System.nanoTime() - startTime) / 1000000);

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

        }
    }
}
