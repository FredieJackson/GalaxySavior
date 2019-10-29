package app.onedayofwar.Battle.System;

import android.app.Activity;
import android.content.Intent;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayDeque;

import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Battle.Activities.BattleOverActivity;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.BluetoothConnection.BluetoothController;
import app.onedayofwar.Battle.Mods.Battle;
import app.onedayofwar.Battle.Mods.Battle.BattleState;
import app.onedayofwar.Battle.Mods.BluetoothBattle;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.GLRenderer;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.Graphics.TextFont;
import app.onedayofwar.System.GLView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.System.XMLParser;
import app.onedayofwar.UI.Button;
import app.onedayofwar.UI.Panel;
import app.onedayofwar.UI.Panel.Type;

public class BattleView implements ScreenView
{
    //region Variables
    public BluetoothController btController;
    public int screenWidth;
    public int screenHeight;
    private GLView glView;
    //private Activity activity;


    public Vector2 touchPos;
    private Battle battle;

    public Panel selectingPanel;
    public Panel gateUp;
    public Panel gateDown;

    //region Buttons Variables
    private Button cancelBtn;
    private Button turnBtn;
    private Button installBtn;
    private Button installationFinishBtn;
    private Button shootBtn;
    private Button flagBtn;
    public boolean isButtonPressed;
    //endregion

    private Sprite background;

    private float[] bgMatrix;
    private ArrayDeque<MotionEvent> motionEvents;

    char typeOfGame;
    //endregion

    //region Constructor
    public BattleView(GLView glView, char typeOfGame, boolean isYourTurn)
    {
        this.glView = glView;
        screenWidth = glView.getScreenWidth();
        screenHeight = glView.getScreenHeight();
        this.typeOfGame = typeOfGame;
        motionEvents = new ArrayDeque<>();
        bgMatrix = new float[16];
        Matrix.setIdentityM(bgMatrix, 0);
        Matrix.translateM(bgMatrix, 0, screenWidth/2, screenHeight/2, 0);
    }
    //endregion

    @Override
    public void Resume()
    {
        glView.getActivity().gameState = MainActivity.GameState.BATTLE;
    }

    public void LoadBT(BluetoothController controller)
    {
        btController = controller;
    }

    public void Initialize(Graphics graphics)
    {
        background = new Sprite(Assets.background);
        background.setPosition(screenWidth/2 ,screenHeight /2);
        background.Scale((float)Assets.bgWidthCoeff, (float)Assets.bgHeightCoeff);

        selectingPanel = new Panel(screenWidth - screenWidth/8, screenHeight/2, screenWidth/4, screenHeight, Type.RIGHT);

        gateUp = new Panel(screenWidth/2, screenHeight/4, screenWidth, screenHeight/2, Type.UP);
        gateDown = new Panel(screenWidth/2, screenHeight - screenHeight/4, screenWidth, screenHeight/2, Type.DOWN);

        Matrix.scaleM(bgMatrix, 0, (float)Assets.bgWidthCoeff, -(float)Assets.bgHeightCoeff, 1);

        touchPos = new Vector2();

        isButtonPressed = false;

        switch(typeOfGame)
        {
            case 's':
            case 'c':
                battle = new SingleBattle(this);
                break;
            case 'b':
                battle = new BluetoothBattle(this);
                break;
        }

        ButtonsInitialize();
        MoveGates();
    }
    //endregion

    //region Update
    public void Update(float eTime)
    {
        if(battle.state == BattleState.Installation && !selectingPanel.isStop)
        {
            selectingPanel.Update(eTime);
            battle.AlignArmyPosition(eTime);
        }

        battle.Update(eTime);

        if (!gateUp.isStop)
        {
            gateUp.Update(eTime);
            gateDown.Update(eTime);
        }
    }

    public void MoveGates()
    {
        gateDown.Move();
        gateUp.Move();
    }

    public boolean IsGatesClose()
    {
        return gateUp.isClose && gateUp.isStop;
    }

    public boolean IsGatesOpen()
    {
        return !gateUp.isClose && gateUp.isStop;
    }

    public void ShootingPrepare()
    {
        shootBtn.SetVisible();
        shootBtn.Unlock();
        flagBtn.SetVisible();
        flagBtn.Unlock();
        installBtn.Lock();
        installBtn.SetInvisible();
    }

    public void DefendingPrepare()
    {
        shootBtn.SetInvisible();
        shootBtn.Lock();
        flagBtn.SetInvisible();
        flagBtn.Lock();
        installBtn.Lock();
        installBtn.SetInvisible();
    }

    public void AttackPrepare()
    {
        installBtn.Unlock();
        installBtn.SetVisible();
    }

    //endregion

    //region onTouch
    public void OnTouch(MotionEvent event)
    {
        //Обновляем позицию касания
        touchPos.SetValue((int)event.getX(),(int)event.getY());

        //Обновляем кнопки
        ButtonsUpdate();
        //Если было совершено нажатие на экран
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //Пытаемся обработать нажатия кнопок
            CheckButtons();
        }
        //Если убрали палец с экрана
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            //Сбрасываем состояние кнопок
            ButtonsReset();
        }
        battle.OnTouch(event);
    }
    //endregion

    //region Buttons Controller
    /**
     * Обрабатывает нажатия на кнопки
     */
    public void CheckButtons()
    {
        //Если нажата кнопка установки юнита
        if (installBtn.IsClicked())
        {
            if(battle.state == BattleState.Installation)
            {
                battle.InstallUnit();
            }
            else
            {
                if(battle.IsUnitSelected())
                {
                    MoveGates();
                }
            }
            isButtonPressed = true;
        }
        //Если нажата кнопка поворота юнита
        else if (turnBtn.IsClicked())
        {
            battle.TurnUnit();
            isButtonPressed = true;
        }

        //Если нажата кнопка отмены выбора юнита
        else if (cancelBtn.IsClicked())
        {
            if(battle.CancelSelection() && !selectingPanel.isClose)
                selectingPanel.Move();
            isButtonPressed = true;
        }

        //Если нажата кнопка завершения установки
        else if (installationFinishBtn.IsClicked())
        {
            if (battle.state == BattleState.Installation)
            {
                if (battle.CheckInstallationFinish())
                {
                    MoveGates();
                    installBtn.SetPosition((int)(screenWidth - Assets.btnInstall.getWidth() - 100 * Assets.monitorWidthCoeff), (int)(100 * Assets.monitorHeightCoeff));
                    cancelBtn.Lock();
                    turnBtn.Lock();
                    selectingPanel.CloseBtnLock();
                    installationFinishBtn.Lock();
                }
            }
            isButtonPressed = true;
        }

        else if(selectingPanel.IsCloseBtnPressed() && selectingPanel.isStop)
        {
            selectingPanel.Move();
            isButtonPressed = true;
        }

        else if(shootBtn.IsClicked() && IsGatesOpen())
        {
            battle.PreparePlayerShoot();
        }

        else if(flagBtn.IsClicked() && IsGatesOpen())
        {
            battle.eField.SetFlag();
        }
    }

    /**
     * Обновляет состояние кнопок
     */
    private void ButtonsUpdate()
    {
        selectingPanel.UpdateCloseBtn(touchPos);
        installationFinishBtn.Update(touchPos);
        shootBtn.Update(touchPos);
        flagBtn.Update(touchPos);

        if(battle.state == BattleState.Installation)
        {
            if (battle.IsUnitSelected())
            {
                installBtn.Update(touchPos);
                cancelBtn.Update(touchPos);
                turnBtn.Update(touchPos);
            }
        }
        else
        {
            installBtn.Update(touchPos);
        }
    }

    /**
     * Обнуляет состояние кнопок
     */
    private void ButtonsReset()
    {
        installationFinishBtn.Reset();
        cancelBtn.Reset();
        turnBtn.Reset();
        installBtn.Reset();
        selectingPanel.ResetCloseBtn();
        shootBtn.Reset();
        flagBtn.Reset();
    }

    /**
     * Отрисовывает кнопки
     * @param
     */
    private void ButtonsDraw(Graphics graphics)
    {
        if(battle.state == BattleState.Installation)
        {
            installationFinishBtn.Draw(graphics);
            if (battle.IsUnitSelected())
            {
                cancelBtn.Draw(graphics);
                turnBtn.Draw(graphics);
                installBtn.Draw(graphics);
            }
        }
        else
        {
            installBtn.Draw(graphics);
            shootBtn.Draw(graphics);
            flagBtn.Draw(graphics);
        }
    }

    /**
     * Инициализирует кнопки
     */
    private void ButtonsInitialize()
    {
        installationFinishBtn = new Button(Assets.btnFinishInstallation, (int)(50 * Assets.monitorWidthCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), (int)(50 * Assets.monitorHeightCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), false);
        installationFinishBtn.Scale(Assets.btnCoeff);
        cancelBtn = new Button(Assets.btnCancel, (int)(50 * Assets.monitorWidthCoeff + Assets.btnCancel.getWidth()/2 * Assets.btnCoeff), (int)((50 + 30) * Assets.monitorHeightCoeff  + Assets.btnCancel.getHeight() * Assets.btnCoeff + Assets.btnCancel.getHeight()/2 * Assets.btnCoeff), false);
        cancelBtn.Scale(Assets.btnCoeff);
        turnBtn = new Button(Assets.btnTurn, (int)(50 * Assets.monitorWidthCoeff + Assets.btnTurn.getWidth()/2 * Assets.btnCoeff), (int)(screenHeight - 2 * Assets.btnCancel.getHeight() * Assets.btnCoeff - (50 + 30) * Assets.monitorHeightCoeff + Assets.btnCancel.getHeight()/2 * Assets.btnCoeff), false);
        turnBtn.Scale(Assets.btnCoeff);
        installBtn = new Button(Assets.btnInstall, (int)(50 * Assets.monitorWidthCoeff + Assets.btnInstall.getWidth()/2 * Assets.btnCoeff), (int)(screenHeight - Assets.btnCancel.getHeight() * Assets.btnCoeff - 50 * Assets.monitorHeightCoeff + Assets.btnInstall.getWidth()/2 * Assets.btnCoeff), false);
        installBtn.Scale(Assets.btnCoeff);

        shootBtn = new Button(Assets.btnShoot, (int)(170 * Assets.monitorWidthCoeff + Assets.btnShoot.getWidth()/2 * Assets.btnCoeff), (int)(390 * Assets.monitorHeightCoeff + Assets.btnShoot.getWidth()/2 * Assets.btnCoeff), false);
        shootBtn.Scale(Assets.btnCoeff);
        shootBtn.SetInvisible();
        shootBtn.Lock();
        flagBtn = new Button(Assets.btnFlag, (int)(170 * Assets.monitorWidthCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), (int)(170 * Assets.monitorHeightCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), false);
        flagBtn.Scale(Assets.btnCoeff);
        flagBtn.SetInvisible();
        flagBtn.Lock();
    }
    //endregion

    //region Draw
    public void Draw(Graphics graphics)
    {
        if (battle.state == BattleState.Attack || battle.state == BattleState.Shoot)
        {
            //Assets.monitor.Draw(graphics);
        }
        else
        {
            graphics.DrawSprite(background);
        }

        battle.DrawFields(graphics);

        ButtonsDraw(graphics);

        if(battle.state != BattleState.Attack && battle.state != BattleState.Shoot)
            battle.DrawUnits(graphics);

        if (battle.state == BattleState.Installation)
        {
            if (selectingPanel.isClose || !selectingPanel.isStop)
            {
                selectingPanel.Draw(graphics);
                battle.DrawUnitsIcons(graphics);
            }
            selectingPanel.DrawButton(graphics);
        }
        if (gateUp.isClose || !gateUp.isStop)
        {
            gateUp.Draw(graphics);
            gateDown.Draw(graphics);
        }
    }
    //endregion

    //region Test
    public void GameOver(BattleState state, int reward)
    {
        /*Intent intent = new Intent(activity, BattleOverActivity.class);
        intent.putExtra("result", state == BattleState.Win);
        intent.putExtra("reward", reward);
        activity.startActivityForResult(intent, 2);*/
    }

    public void GameOver()
    {
        battle.state = BattleState.Win;
        battle.GameOver();
    }
    //endregion
}