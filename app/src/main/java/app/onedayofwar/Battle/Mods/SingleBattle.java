package app.onedayofwar.Battle.Mods;

import java.util.ArrayList;

import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.Units.Engineer;
import app.onedayofwar.Battle.Units.IFV;
import app.onedayofwar.Battle.Units.Robot;
import app.onedayofwar.Battle.Units.SONDER;
import app.onedayofwar.Battle.Units.Tank;
import app.onedayofwar.Battle.Units.Turret;
import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Battle.System.BattleView;
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
        SwapFields();
        InitializeEnemy();
        PlaceEnemy();
        SwapFields();
        //Toast.makeText(gameView.getContext(), "ENEMY IS LOADED", Toast.LENGTH_LONG).show();
    }

    public void InitializeEnemy()
    {
        eArmy = new ArrayList<>();
        Vector2 startPos = new Vector2();
        for(int j = 0; j < unitCount.length; j++)
        {
            switch(j)
            {
                case 0:
                    for(int i = 0; i < unitCount[0]; i++)
                        eArmy.add(new Robot(startPos, 0, false));
                    break;
                case 1:
                    for(int i = 0; i < unitCount[1]; i++)
                        eArmy.add(new IFV(startPos, 1, false));
                    break;
                case 2:
                    for(int i = 0; i < unitCount[2]; i++)
                        eArmy.add(new Engineer(startPos, 2, false));
                    break;
                case 3:
                    for(int i = 0; i < unitCount[3]; i++)
                        eArmy.add(new Tank(startPos, 3, false));
                    break;
                case 4:
                    for(int i = 0; i < unitCount[4]; i++)
                        eArmy.add(new Turret(startPos, 4, false));
                    break;
                case 5:
                    for(int i = 0; i < unitCount[5]; i++)
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
        if(eField.selectedSocket.IsFalse() || eField.GetSelectedSocketShot() == 1 || eField.GetSelectedSocketShot() == 2)
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
                    eField.GetShots()[tmpLocalFormCoord.y][tmpLocalFormCoord.x] = 2;
                }
            }
            else
            {
                eField.SetShot(true);
            }
        }
        CheckEnemyArmy();
        selectedUnitZone = -1;
        battleView.MoveGates();
        state = BattleState.Shoot;
        return true;
    }

    public void PlayerShoot(){}

    public boolean PrepareEnemyShoot()
    {
        if(isEnemyShotPrepeared)
            return true;

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
            while(field.GetSelectedSocketShot() != 0);

            testLocalView = "Crit! ";
            BattleEnemy.weaponType = 1;
        }
        else
        {
            do
            {
                rndLocalCoord.SetValue((int) (Math.random() * field.size), (int) (Math.random() * field.size));
                rndSocket.SetValue(field.GetGlobalSocketCoord(rndLocalCoord));
                field.selectedSocket.SetValue(rndSocket);
            }
            while (field.GetSelectedSocketShot() != 0);

            testLocalView = "Normal ";
            BattleEnemy.weaponType = 0;
        }

        testLocalView += rndUnitID;

        eArmy.get(rndUnitID).Reload();
        BattleEnemy.target.SetValue(field.selectedSocket.x, field.selectedSocket.y + field.socketSizeY/2);
        BattleEnemy.attacker = rndUnitID;
        isEnemyShotPrepeared = true;

        return true;
    }

    public void EnemyShoot()
    {
        byte target = field.GetSelectedSocketInfo();
        BattleEnemy.target.SetValue(field.selectedSocket.x - field.socketSizeX/2, field.selectedSocket.y + field.socketSizeY/2);

        if(target < 0)
        {
            field.SetShot(false);
        }
        else
        {
            if(army.get(target).SetDamage(eArmy.get(BattleEnemy.attacker).GetPower()))
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
            army.get(target).UpdateDamagedZones(field.selectedSocket);
        }
        BattleEnemy.target.SetValue(field.selectedSocket);
        field.selectedSocket.SetFalse();
        isEnemyShotPrepeared = false;
        CheckPlayerArmy();
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
