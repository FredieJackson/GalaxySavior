package app.onedayofwar.Battle.Mods;

import android.util.Log;

import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.BluetoothConnection.HandlerMSG;
import app.onedayofwar.Battle.System.BattleView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.Battle.Units.Unit;

/**
 * Created by Slava on 06.02.2015.
 */
public class BluetoothBattle extends Battle
{
    private boolean isEnemyInstallationFinish;
    private boolean isResultSend;
    public BluetoothBattle(BattleView battleView)
    {
        super(battleView);
        isEnemyInstallationFinish = false;
        isResultSend = false;
    }

    @Override
    public void InstallationFinish()
    {
        if(!isEnemyInstallationFinish)
        {
            battleView.btController.SendData(HandlerMSG.INSTALLATION_FINISH);
            if(battleView.btController.GetRecievedData().equals(HandlerMSG.INSTALLATION_FINISH))
            {
                isEnemyInstallationFinish = true;
                Log.i("TESTBT", "BattleEnemy installed");
            }
        }
        else
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
    }

    @Override
    public void LoadEnemy()
    {

    }

    @Override
    public boolean PreparePlayerShoot()
    {
        if (eField.selectedSocket.IsFalse() || eField.GetSelectedSocketInfo() == 0)
            return false;

        Vector2 tmp = new Vector2(eField.GetLocalSocketCoord(eField.selectedSocket));

        Log.i("SELECTED_SOCKET", "x: " + tmp.x + " y: " + tmp.y);

        battleView.btController.SendData(HandlerMSG.ATTACK + '|' + tmp.x + '|' + tmp.y + '|' + army.get(selectedUnitZone).GetPower() + '|' + 1);
        eField.GetFieldInfo()[(int)tmp.y][(int)tmp.x] = 0;
        state = BattleState.Shoot;
        return true;
    }

    @Override
    public void PlayerShoot()
    {
        if(BattleEnemy.attackResult != -1)
        {
            army.get(selectedUnitZone).Reload();
            army.get(selectedUnitZone).Deselect();
            if(BattleEnemy.attackResult == 3)
            {
                String[] tmp = BattleEnemy.attackResultData.split("\\.");
                Vector2 tmpCoord = new Vector2();
                for(int i = 0; i < tmp.length - 1; i+=2)
                {
                    tmpCoord.SetValue(Integer.parseInt(tmp[i]), Integer.parseInt(tmp[i + 1]));
                    eField.GetShots()[(int)tmpCoord.y][(int)tmpCoord.x] = 2;
                }
            }
            else
            {
                eField.SetShot(BattleEnemy.attackResult == 2);
            }

            selectedUnitZone = -1;
            BattleEnemy.attackResult = -1;
            battleView.MoveGates();
        }
    }

    @Override
    public boolean PrepareEnemyShoot()
    {
        if (BattleEnemy.target.IsFalse())
            return false;

        if(isResultSend)
            return true;

        BattleEnemy.target.SetValue(field.GetGlobalSocketCoord(BattleEnemy.target));
        field.selectedSocket.SetValue(BattleEnemy.target);

        byte target = field.GetSelectedSocketInfo();
        int result;
        String resultData = "";

        if (field.GetSelectedSocketShot() != 0)
        {
            BattleEnemy.target.SetFalse();
            result = 0;
        }
        else
        {
            if(target >= 0)
            {
                if(army.get(target).SetDamage(BattleEnemy.damage))
                {
                    result = 3;
                    Vector2 tmp = new Vector2();
                    for (int i = 0; i < army.get(target).GetForm().length; i++)
                    {
                        tmp.SetValue(field.GetLocalSocketCoord(army.get(target).GetForm()[i]));
                        resultData += "" + tmp.x + '.' + tmp.y + '.';
                    }
                    resultData = resultData.substring(0, resultData.length() - 1);
                }
                else
                {
                    result = 2;
                }
            }
            else
            {
                result = 1;
            }
        }
        battleView.btController.SendData(HandlerMSG.ATTACK_RESULT + '|' + result + '|' + resultData);
        isResultSend = true;
        Log.i("ENEMY", "SEND_RESULT");
        return true;
    }

    @Override
    public void EnemyShoot()
    {
        isResultSend = false;
        byte target = field.GetSelectedSocketInfo();
        if(target >= 0)
        {
            army.get(target).UpdateDamagedZones(field.selectedSocket);
            if(army.get(target).IsDead())
            {
                Vector2 tmp = new Vector2();
                for (int i = 0; i < army.get(target).GetForm().length; i++)
                {
                    tmp.SetValue(field.GetLocalSocketCoord(army.get(target).GetForm()[i]));
                    field.GetShots()[(int)tmp.y][(int)tmp.x] = 2;
                }
            }
            else
            {
                field.SetShot(true);
            }
        }
        else
        {
            field.SetShot(false);
        }
        CheckPlayerArmy();
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
            battleView.btController.SendData(HandlerMSG.LOSE);
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
