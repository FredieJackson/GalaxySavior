package app.onedayofwar.Games;

import android.widget.Toast;

import junit.framework.Test;

import java.util.ArrayList;

import app.onedayofwar.GameView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.Units.*;

/**
 * Created by Slava on 24.12.2014.
 */
public class SingleGame extends Game
{
    ArrayList<Unit> eArmy;
    public static byte difficulty;
    public SingleGame(GameView gameView)
    {
        super(gameView);
    }

    //region Enemy Loading
    public void LoadEnemy()
    {
        SwapFields();
        InitializeEnemy();
        PlaceEnemy();
        SwapFields();
        Toast.makeText(gameView.getContext(), "ENEMY IS LOADED", Toast.LENGTH_LONG).show();
    }

    public void InitializeEnemy()
    {
        eArmy = new ArrayList<>();
        for(int i = 0; i < unitCount[0]; i++)
        {
            eArmy.add(new Robot(new Vector2(), 0, false));
        }
        for(int i = 0; i < unitCount[1]; i++)
        {
            eArmy.add(new IFV(new Vector2(), 1, false));
        }
        for(int i = 0; i < unitCount[2]; i++)
        {
            eArmy.add(new Engineer(new Vector2(), 2, false));
        }
        for(int i = 0; i < unitCount[3]; i++)
        {
            eArmy.add(new Tank(new Vector2(), 3, false));
        }
        for(int i = 0; i < unitCount[4]; i++)
        {
            eArmy.add(new Turret(new Vector2(), 4, false));
        }
        for(int i = 0; i < unitCount[5]; i++)
        {
            eArmy.add(new SONDER(new Vector2(), 5, false));
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
            eField.ClearFieldInfo();
            timeOut:for(int i = 0; i < eArmy.size(); i++)
            {
                tryCount = 0;
                while(true)
                {
                    if((int)(Math.random()* 2 + 1) == 2)
                        eArmy.get(i).ChangeDirection();

                    tmpSocket.SetValue((int)(Math.random()* eField.width + eField.x),(int)(Math.random()* eField.height + eField.y));

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


    public void NextTurn()
    {
        turns++;

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
            state = GameState.Lose;
        }
        if(!isGood)
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

        //region eArmy
        isGood = false;
        isGameOver = true;
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
            state = GameState.Win;
        }
        if(!isGood)
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

        if(state == GameState.Win || state == GameState.Lose)
        {
            GameOver();
        }
    }

    public boolean PlayerShoot()
    {
        if(eField.selectedSocket.IsFalse() || eField.GetSelectedSocketSign() != 0)
            return false;
        int target = eField.GetSelectedSocketInfo();
        army.get(selectedUnitZone).Reload();
        army.get(selectedUnitZone).Deselect();

        if(target < 0)
        {
            eField.SetSign(false);
        }
        else
        {
            if(eArmy.get(target).SetDamage(army.get(selectedUnitZone).GetPower(), eField.selectedSocket))
            {
                Vector2 tmpLocalFormCoord = new Vector2();
                for(int i = 0; i < eArmy.get(target).GetForm().length; i++)
                {
                    tmpLocalFormCoord.SetValue(eField.GetLocalSocketCoord(eArmy.get(target).GetForm()[i]));
                    eField.GetShots()[tmpLocalFormCoord.y][ tmpLocalFormCoord.x] = 2;
                }
            }
            else
            {
                eField.SetSign(true);
            }
        }
        selectedUnitZone = -1;
        return true;
    }

    public void EnemyShoot()
    {
        byte rndUnitID;
        byte target;
        Vector2 rndLocalCoord = new Vector2();
        Vector2 rndSocket = new Vector2();

        do
        {
            rndUnitID = (byte) (Math.random() * eArmy.size());
        }
        while (eArmy.get(rndUnitID).IsDead() || eArmy.get(rndUnitID).IsReloading());


        if((int)(Math.random()*101) <= difficulty)
        {
            byte rndTargetID;
            do
            {
                rndTargetID = (byte) (Math.random() * army.size());
            }
            while (army.get(rndTargetID).IsDead());

            do
            {
                field.selectedSocket.SetValue(army.get(rndTargetID).GetForm()[(int)(Math.random() * army.get(rndTargetID).GetForm().length)]);
            }
            while(field.GetSelectedSocketSign() != 0);

            testLocalView = "Crit! ";
        }
        else
        {
            do
            {
                rndLocalCoord.SetValue((int) (Math.random() * field.size), (int) (Math.random() * field.size));
                rndSocket.SetValue(field.GetGlobalSocketCoord(rndLocalCoord));
                field.selectedSocket.SetValue(rndSocket);
            }
            while (field.GetSelectedSocketSign() != 0);

            testLocalView = "Normal ";
        }

        testLocalView += rndUnitID;

        eArmy.get(rndUnitID).Reload();
        target = field.GetSelectedSocketInfo();

        if(target < 0)
        {
            field.SetSign(false);
        }
        else
        {
            if(army.get(target).SetDamage(eArmy.get(rndUnitID).GetPower(), field.selectedSocket))
            {
                Vector2 tmp = new Vector2();
                for(int i = 0; i < army.get(target).GetForm().length; i++)
                {
                    tmp.SetValue(field.GetLocalSocketCoord(army.get(target).GetForm()[i]));
                    field.GetShots()[tmp.y][tmp.x] = 2;
                }
            }
            else
            {
                field.SetSign(true);
            }
        }
    }

}
