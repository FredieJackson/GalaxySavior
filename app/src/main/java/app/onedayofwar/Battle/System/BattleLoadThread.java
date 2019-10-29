package app.onedayofwar.Battle.System;

import app.onedayofwar.Battle.Activities.BattleActivity;

/**
 * Created by Slava on 14.02.2015.
 */
public class BattleLoadThread extends Thread
{
    private BattleActivity battleActivity;

    public BattleLoadThread(BattleActivity battleActivity)
    {
        this.battleActivity = battleActivity;
    }

    public void run()
    {
        battleActivity.Initialize();
        battleActivity.FinishLoading();
    }

}
