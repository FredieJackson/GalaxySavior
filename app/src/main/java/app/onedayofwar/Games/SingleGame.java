package app.onedayofwar.Games;

import android.widget.Toast;

import java.util.ArrayList;

import app.onedayofwar.GameElements.Enemy;
import app.onedayofwar.System.GameView;
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

    @Override
    public void InstallationFinish()
    {
        if(isYourTurn)
            state = GameState.AttackPrepare;
        else
            state = GameState.Defence;
        gameView.MoveGates();
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
        Vector2 startPos = new Vector2();
        for(int i = 0; i < unitCount[0]; i++)
        {
            eArmy.add(new Robot(startPos, 0, false));
        }
        for(int i = 0; i < unitCount[1]; i++)
        {
            eArmy.add(new IFV(startPos, 1, false));
        }
        for(int i = 0; i < unitCount[2]; i++)
        {
            eArmy.add(new Engineer(startPos, 2, false));
        }
        for(int i = 0; i < unitCount[3]; i++)
        {
            eArmy.add(new Tank(startPos, 3, false));
        }
        for(int i = 0; i < unitCount[4]; i++)
        {
            eArmy.add(new Turret(startPos, 4, false));
        }
        for(int i = 0; i < unitCount[5]; i++)
        {
            eArmy.add(new SONDER(startPos, 5, false));
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
        if(eField.selectedSocket.IsFalse() || eField.GetSelectedSocketSign() != 0)
            return false;
        int target = eField.GetSelectedSocketInfo();
        army.get(selectedUnitZone).Reload();
        army.get(selectedUnitZone).Deselect();

        if(target < 0)
        {
            eField.SetShot(false);
        }
        else
        {
            if(eArmy.get(target).SetDamage(army.get(selectedUnitZone).GetPower()))
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
                eField.SetShot(true);
            }
        }
        CheckEnemyArmy();
        selectedUnitZone = -1;
        return true;
    }

    public void PlayerShoot()
    {
        gameView.MoveGates();
    }

    public boolean PrepareEnemyShoot()
    {
        byte rndUnitID;
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
            Enemy.weaponType = 1;
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
            Enemy.weaponType = 0;
        }

        testLocalView += rndUnitID;

        eArmy.get(rndUnitID).Reload();
        //target = field.GetSelectedSocketInfo();
        Enemy.target.SetValue(field.selectedSocket.x, field.selectedSocket.y);
        Enemy.attacker = rndUnitID;

        /*if(target < 0)
        {
            field.SetShot(false);
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
                field.SetShot(true);
            }
        }*/
        return true;
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
            state = GameState.Win;
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
            state = GameState.Lose;
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

    public void EnemyShoot()
    {
        byte target = field.GetSelectedSocketInfo();
        Enemy.target.SetValue(field.selectedSocket.x, field.selectedSocket.y + field.socketSizeY);

        if(target < 0)
        {
            field.SetShot(false);
        }
        else
        {
            if(army.get(target).SetDamage(eArmy.get(Enemy.attacker).GetPower()))
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
                field.SetShot(true);
            }
        }
        CheckPlayerArmy();
    }
}
