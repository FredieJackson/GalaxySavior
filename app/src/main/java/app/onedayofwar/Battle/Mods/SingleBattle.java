package app.onedayofwar.Battle.Mods;

import android.util.Log;

import java.util.ArrayList;

import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.System.BattleView;
import app.onedayofwar.Battle.Units.Ground.Engineer;
import app.onedayofwar.Battle.Units.Ground.IFV;
import app.onedayofwar.Battle.Units.Ground.Robot;
import app.onedayofwar.Battle.Units.Ground.SONDER;
import app.onedayofwar.Battle.Units.Ground.Tank;
import app.onedayofwar.Battle.Units.Ground.Turret;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 24.12.2014.
 */
public class SingleBattle extends Battle
{
    ArrayList<Unit> eArmy;
    public static byte difficulty;
    private boolean isEnemyShotPrepeared;

    public SingleBattle(BattleView battleView)
    {
        super(battleView);
        isEnemyShotPrepeared = false;
    }

    @Override
    public void InstallationFinish()
    {
        if(isYourTurn)
        {
            state = BattleState.AttackPrepare;
            battleView.AttackPrepare();
        }
        else
        {
            state = BattleState.Defence;
            battleView.DefendingPrepare();
        }

        battleView.MoveGates();
    }

    //region BattleEnemy Loading
    public void LoadEnemy()
    {
        InitializeEnemy();
        PlaceEnemy();
        //Toast.makeText(gameView.getContext(), "ENEMY IS LOADED", Toast.LENGTH_LONG).show();
    }

    public void InitializeEnemy()
    {
        eArmy = new ArrayList<>();
        byte[] eUnitCount = battleView.planet == null ? unitCount : battleView.planet.getGroundGuards();
        Vector2 startPos = new Vector2();
        for(int j = 0; j < eUnitCount.length; j++)
        {
            switch(j)
            {
                case 0:
                    for(int i = 0; i < eUnitCount[0]; i++)
                        eArmy.add(new Robot(startPos, 0, false));
                    break;
                case 1:
                    for(int i = 0; i < eUnitCount[1]; i++)
                        eArmy.add(new IFV(startPos, 1, false));
                    break;
                case 2:
                    for(int i = 0; i < eUnitCount[2]; i++)
                        eArmy.add(new Engineer(startPos, 2, false));
                    break;
                case 3:
                    for(int i = 0; i < eUnitCount[3]; i++)
                        eArmy.add(new Tank(startPos, 3, false));
                    break;
                case 4:
                    for(int i = 0; i < eUnitCount[4]; i++)
                        eArmy.add(new Turret(startPos, 4, false));
                    break;
                case 5:
                    for(int i = 0; i < eUnitCount[5]; i++)
                        eArmy.add(new SONDER(startPos, 5, false));
                    break;
            }
        }
    }

    public void PlaceEnemy()
    {
        Vector2 tmpSocket = new Vector2();
        byte count;
        int tryCount;
        while(true)
        {
            count = 0;
            timeOut:for(int i = 0; i < eArmy.size(); i++)
            {
                tryCount = 0;
                while(true)
                {
                    if((int)(Math.random()* 2 + 1) == 2)
                        eArmy.get(i).ChangeDirection();

                    tmpSocket.SetValue(eField.GetGlobalSocketCoord((int)(Math.random() * eField.size),(int)(Math.random() * eField.size)));

                    if(eArmy.get(i).SetForm(tmpSocket, eField, true))
                    {
                        eField.PlaceUnit(eArmy.get(i).GetForm(), i);
                        eArmy.get(i).isInstalled = true;
                        break;
                    }
                    if(tryCount > 300)
                        break timeOut;
                    tryCount++;
                }
                count++;
            }
            if(count == eArmy.size())
                break;
        }
    }
    //endregion

    public boolean PreparePlayerShoot()
    {

        return true;
    }

    public void PlayerShoot(){}

    public boolean PrepareEnemyShoot()
    {


        return true;
    }

    public void EnemyShoot()
    {

    }

    public void CheckEnemyArmy()
    {
        //region eArmy
        boolean isGood = false;
        boolean isGameOver = true;

        for(Unit unit : eArmy)
        {
            if(!unit.IsDead())
            {
                isGameOver = false;
                unit.NextTurn();
                if (!unit.IsReloading())
                    isGood = true;
            }
        }
        if(isGameOver)
        {
            testLocalView = "YOU WIN!";
            state = BattleState.Win;
            GameOver();
        }
        else if(!isGood)
        {
            for(Unit unit : eArmy)
            {
                if(!unit.IsDead())
                {
                    unit.ResetReload();
                    break;
                }
            }
        }
        //endregion
    }

    public void CheckPlayerArmy()
    {
        //region army
        boolean isGood = false;
        boolean isGameOver = true;
        for(Unit unit : army)
        {
            if(!unit.IsDead())
            {
                unit.NextTurn();
                isGameOver = false;
                if (!unit.IsReloading())
                    isGood = true;
            }
        }
        if(isGameOver)
        {
            testLocalView = "YOU LOSE!";
            state = BattleState.Lose;
            GameOver();
        }
        else if(!isGood)
        {
            for(Unit unit : army)
            {
                if(!unit.IsDead())
                {
                    unit.ResetReload();
                    break;
                }
            }
        }
        //endregion
    }
}
