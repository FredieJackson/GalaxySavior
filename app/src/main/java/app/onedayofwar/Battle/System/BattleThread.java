package app.onedayofwar.Battle.System;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import app.onedayofwar.System.FPScounter;


public class BattleThread extends Thread
{
    /**Объект класса*/
    private BattleView battleView;

    /**Переменная задавания состояния потока рисования*/
    private boolean isRunning = false;

    /**Конструктор класса*/
    public BattleThread(BattleView battleView)
    {
        this.battleView = battleView;
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
        float eTime;
        short fps = 60;
        Canvas canvas = null;
        Rect rect = new Rect(0,0,battleView.screenWidth, battleView.screenHeight);
        while (isRunning)
        {
            FPScounter.StartCounter();
            startTime = System.nanoTime();
            try
            {
                canvas = battleView.getHolder().lockCanvas(rect);
                synchronized (battleView.getHolder())
                {
                    if(canvas != null)
                    {
                        eTime = 1f/fps * 10;
                        battleView.Update(eTime);
                        //battleView.Draw();

                        //canvas.drawBitmap(battleView.graphics.frameBuffer, null, rect, null);

                        /*if(battleView.paint != null)
                        {
                            canvas.drawText("FPS: " + fps, 50, 100, battleView.paint);
                        }*/
                    }
                }
            }
            finally
            {
                if(canvas != null)
                {
                    battleView.getHolder().unlockCanvasAndPost(canvas);
                }
            }

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
