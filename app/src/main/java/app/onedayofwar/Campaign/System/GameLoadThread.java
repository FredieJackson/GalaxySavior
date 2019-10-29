package app.onedayofwar.Campaign.System;

import app.onedayofwar.Campaign.Activities.GameActivity;

/**
 * Created by Slava on 25.02.2015.
 */
public class GameLoadThread extends Thread
{
    private GameActivity gameActivity;

    public GameLoadThread(GameActivity gameActivity)
    {
        this.gameActivity = gameActivity;
    }

    public void run()
    {
        /*gameActivity.Initialize();
        gameActivity.FinishLoading();*/
    }

}
