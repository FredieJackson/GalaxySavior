package app.onedayofwar.Games;

import android.util.Log;

import app.onedayofwar.Activities.BluetoothConnection.HandlerMSG;
import app.onedayofwar.GameElements.Enemy;
import app.onedayofwar.System.GameView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.Units.Unit;

/**
 * Created by Slava on 06.02.2015.
 */
public class BluetoothGame extends Game
{
    private boolean isEnemyInstallationFinish;
    private boolean isResultSend;
    public BluetoothGame(GameView gameView)
    {
        super(gameView);
        isEnemyInstallationFinish = false;
        isResultSend = false;
    }

    @Override
    public void InstallationFinish()
    {
        if(!isEnemyInstallationFinish)
        {
            gameView.btController.SendData(HandlerMSG.INSTALLATION_FINISH);
            if(gameView.btController.GetRecievedData().equals(HandlerMSG.INSTALLATION_FINISH))
            {
                isEnemyInstallationFinish = true;
                Log.i("TESTBT", "Enemy installed");
            }
        }
        else
        {
            if(isYourTurn)
            {
                state = GameState.AttackPrepare;
                gameView.AttackPrepare();
            }
            else
            {
                state = GameState.Defence;
                gameView.DefendingPrepare();
            }

            gameView.MoveGates();
        }
    }

    @Override
    public void LoadEnemy()
    {

    }

    @Override
    public boolean PreparePlayerShoot()
    {
        if (eField.selectedSocket.IsFalse())
            return false;

        Vector2 tmp = new Vector2(eField.GetLocalSocketCoord(eField.selectedSocket));

        Log.i("SELECTED_SOCKET", "x: " + tmp.x + " y: " + tmp.y);
        if(eField.GetSelectedSocketInfo() == 0)
            return false;

        gameView.btController.SendData(HandlerMSG.ATTACK + '|' + tmp.x + '|' + tmp.y + '|' + army.get(selectedUnitZone).GetPower() + '|' + 1);
        eField.GetFieldInfo()[tmp.y][tmp.x] = 0;
        state = GameState.Shoot;
        return true;
    }

    @Override
    public void PlayerShoot()
    {
        if(Enemy.attackResult != -1)
        {
            army.get(selectedUnitZone).Reload();
            army.get(selectedUnitZone).Deselect();
            if(Enemy.attackResult == 3)
            {
                String[] tmp = Enemy.attackResultData.split("\\.");
                Vector2 tmpCoord = new Vector2();
                for(int i = 0; i < tmp.length - 1; i+=2)
                {
                    tmpCoord.SetValue(Integer.parseInt(tmp[i]), Integer.parseInt(tmp[i + 1]));
                    eField.GetShots()[tmpCoord.y][tmpCoord.x] = 2;
                }
            }
            else
            {
                eField.SetShot(Enemy.attackResult == 2);
            }

            selectedUnitZone = -1;
            Enemy.attackResult = -1;
            gameView.MoveGates();
        }
    }

    @Override
    public boolean PrepareEnemyShoot()
    {
        if (Enemy.target.IsFalse())
            return false;

        if(isResultSend)
            return true;

        Enemy.target.SetValue(field.GetGlobalSocketCoord(Enemy.target));
        field.selectedSocket.SetValue(Enemy.target);

        byte target = field.GetSelectedSocketInfo();
        int result;
        String resultData = "";

        if (field.GetSelectedSocketSign() != 0)
        {
            Enemy.target.SetFalse();
            result = 0;
        }
        else
        {
            if(target >= 0)
            {
                if(army.get(target).SetDamage(Enemy.damage))
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
        gameView.btController.SendData(HandlerMSG.ATTACK_RESULT + '|' + result + '|' + resultData);
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
                    field.GetShots()[tmp.y][tmp.x] = 2;
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
            state = GameState.Lose;
            gameView.btController.SendData(HandlerMSG.LOSE);
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
