package app.onedayofwar.Battle.System;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayDeque;

import app.onedayofwar.Battle.Activities.BattleOverActivity;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.BluetoothConnection.BluetoothController;
import app.onedayofwar.Graphics.GLRenderer;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.UI.Button;
import app.onedayofwar.UI.Panel;
import app.onedayofwar.Battle.Mods.BluetoothBattle;
import app.onedayofwar.Battle.Mods.Battle.*;
import app.onedayofwar.Battle.Mods.Battle;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.UI.Panel.*;

public class BattleView extends GLSurfaceView
implements OnTouchListener, ScreenView
{
    //region Variables
    public static final int sourceHeight = 720;
    public static final int sourceWidth = 1024;
    public static final int sourceDpi = 132;
    public BluetoothController btController;
    public int screenWidth;
    public int screenHeight;
    private Activity activity;
    private GLRenderer renderer;

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

    private float[] bgMatrix;
    private ArrayDeque<MotionEvent> motionEvents;

    private boolean test;

    char typeOfGame;
    //endregion

    //region Constructor
    public BattleView(Activity activity, char typeOfGame, int width, int height)
    {
        super(activity.getApplicationContext());
        this.activity = activity;
        screenWidth = width;
        screenHeight = height;
        this.typeOfGame = typeOfGame;
        motionEvents = new ArrayDeque<>();
        bgMatrix = new float[16];
        Matrix.setIdentityM(bgMatrix, 0);
        Matrix.translateM(bgMatrix, 0, width/2, height/2, 0);
        setEGLContextClientVersion(2);
        renderer = new GLRenderer(activity.getResources(), this);
        setRenderer(renderer);
    }
    //endregion

    //region Initialization
    public void SetAttackSequence(boolean isYourTurn)
    {
        test = isYourTurn;
    }

    public void LoadBT(BluetoothController controller)
    {
        btController = controller;
    }

    public void Initialize(Graphics graphics)
    {
        setOnTouchListener(this);

        LoadAssets(graphics);

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

        battle.isYourTurn = test;

        ButtonsInitialize();
        MoveGates();
    }

    public void LoadAssets(Graphics graphics)
    {
        if(BattlePlayer.unitCount[0] != 0)
        {
            Assets.robotIcon = graphics.newSprite("unit/icon/robot_icon.png");
            Assets.robotImage = graphics.newSprite("unit/image/robot.png");
            Assets.robotStroke = graphics.newSprite("unit/stroke/robot_stroke.png");
        }

        if(BattlePlayer.unitCount[1] != 0)
        {
            Assets.ifvImage = graphics.newSprite("unit/image/ifv.png");
            Assets.ifvIcon = graphics.newSprite("unit/icon/ifv_icon.png");
            Assets.ifvStroke = graphics.newSprite("unit/stroke/ifv_stroke.png");
        }

        if(BattlePlayer.unitCount[2] != 0)
        {
            Assets.engineerImage = graphics.newSprite("unit/image/engineer.png");
            Assets.engineerIcon = graphics.newSprite("unit/icon/rocket_icon.png");
            Assets.engineerStroke = graphics.newSprite("unit/stroke/engineer_stroke.png");
        }
        if(BattlePlayer.unitCount[3] != 0)
        {
            Assets.tankImage = graphics.newSprite("unit/image/tank.png");
            Assets.tankIcon = graphics.newSprite("unit/icon/tank_icon.png");
            Assets.tankStroke = graphics.newSprite("unit/stroke/tank_stroke.png");
        }
        if(BattlePlayer.unitCount[4] != 0)
        {
            Assets.turretImage = graphics.newSprite("unit/image/turret.png");
            Assets.turretIcon = graphics.newSprite("unit/icon/turret_icon.png");
            Assets.turretStroke = graphics.newSprite("unit/stroke/turret_stroke.png");
        }
        if(BattlePlayer.unitCount[5] != 0)
        {
            Assets.sonderImage = graphics.newSprite("unit/image/sonder.png");
            Assets.sonderIcon = graphics.newSprite("unit/icon/sonder_icon.png");
            Assets.sonderStroke = graphics.newSprite("unit/stroke/sonder_stroke.png");
        }

        switch(BattlePlayer.fieldSize)
        {
            //Коэффициент масштабирования поля и юнитов. 0.2f - отступ в долях единицы от верха экрана до верхней точки поля.
            //То есть находим высоту поля для данного экрана, чтобы сверху и снизу было расстояние до края экрана равное определенному количеству процентов высоты экрана.
            //Полученная высота поля должна быть кратна 15, то есть кол-ву клеток поля. А высота и ширина клетки поля должны быть кратны 2. Используем дабл для высокой точности вычислений.
            //Как тебе такое только в голову пришло))
            case 0:
                    Log.i("INIT", "FIELD SIZE = 0");
                    activity.finish();
                    return;
            case 5:
                Assets.grid = graphics.newSprite("field/grid/normal_green_5x5.png");
                Assets.gridIso = graphics.newSprite("field/grid/iso_5x5.png");
                Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.2f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
                Assets.isoGridCoeff = (int)((screenHeight * (1 - 2 * 0.4f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
                break;
            case 15:
                Assets.grid = graphics.newSprite("field/grid/normal_green.png");
                Assets.gridIso = graphics.newSprite("field/grid/iso.png");
                Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.2f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
                Assets.isoGridCoeff = Assets.gridCoeff;
                break;
        }


        Assets.signFire = graphics.newSprite("field/mark/fire.png");
        Assets.signMiss = graphics.newSprite("field/mark/miss_green.png");
        Assets.signMissIso = graphics.newSprite("field/mark/miss_iso.png");
        Assets.signHit = graphics.newSprite("field/mark/hit_green.png");
        Assets.signFlag = graphics.newSprite("field/mark/flag.png");

        Assets.btnCancel = graphics.newSprite("button/cancel.png");
        Assets.btnInstall = graphics.newSprite("button/install.png");
        Assets.btnFinishInstallation = graphics.newSprite("button/installation_finish.png");
        Assets.btnShoot = graphics.newSprite("button/shoot.png");
        Assets.btnTurn = graphics.newSprite("button/turn.png");
        Assets.btnPanelClose = graphics.newSprite("button/panel_close.png");
        Assets.btnFlag = graphics.newSprite("button/flag.png");

        Assets.background = graphics.newSprite("desert.jpg");
        Assets.monitor = graphics.newSprite("monitor.png");

        Assets.bullet = graphics.newSprite("unit/bullet/bullet.png");
        Assets.miniRocket = graphics.newSprite("unit/bullet/miniRocket.png");

        Assets.explode = graphics.newAnimation("animation/explode.png", 24, 100, 0, false);
        //Assets.fire = graphics.newSprite("animation/fire2.png");

        scaleImages();
    }

    private void scaleImages()
    {
        Assets.iconCoeff = ((screenHeight - 70) / 6d) / Assets.robotIcon.getHeight();

        Assets.btnCoeff = screenHeight * 0.17f / Assets.btnCancel.getHeight();

        Assets.dpiCoeff =  (int)getResources().getDisplayMetrics().xdpi / (double)sourceDpi;

        Assets.monitorHeightCoeff = (double)screenHeight / 1080;
        Assets.monitorWidthCoeff = (double)screenWidth / 1920;
        Assets.bgHeightCoeff = screenHeight *1f/ Assets.background.getHeight();
        Assets.bgWidthCoeff = screenWidth *1f/ Assets.background.getWidth();
    }
    //endregion

    //region Update
    public void Update(float eTime)
    {
        SyncTouch();

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
    /**
     * Обрабатывает касания
     * @param view
     * @param event
     * @return
     */
    public boolean onTouch(View view, MotionEvent event)
    {
        motionEvents.add(event);
        return true;
    }

    public void SyncTouch()
    {
        if(motionEvents.isEmpty())
            return;
        MotionEvent event = motionEvents.poll();
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
        cancelBtn = new Button(Assets.btnCancel, (int)(50 * Assets.monitorWidthCoeff + Assets.btnCancel.getWidth()/2 * Assets.btnCoeff), (int)((50 + 30) * Assets.monitorHeightCoeff  + Assets.btnCancel.getHeight() * Assets.btnCoeff + Assets.btnCancel.getHeight()/2 * Assets.btnCoeff), false);
        turnBtn = new Button(Assets.btnTurn, (int)(50 * Assets.monitorWidthCoeff + Assets.btnTurn.getWidth()/2 * Assets.btnCoeff), (int)(screenHeight - 2 * Assets.btnCancel.getHeight() * Assets.btnCoeff - (50 + 30) * Assets.monitorHeightCoeff + Assets.btnCancel.getHeight()/2 * Assets.btnCoeff), false);
        installBtn = new Button(Assets.btnInstall, (int)(50 * Assets.monitorWidthCoeff + Assets.btnInstall.getWidth()/2 * Assets.btnCoeff), (int)(screenHeight - Assets.btnCancel.getHeight() * Assets.btnCoeff - 50 * Assets.monitorHeightCoeff + Assets.btnInstall.getWidth()/2 * Assets.btnCoeff), false);

        shootBtn = new Button(Assets.btnShoot, (int)(170 * Assets.monitorWidthCoeff + Assets.btnShoot.getWidth()/2 * Assets.btnCoeff), (int)(390 * Assets.monitorHeightCoeff + Assets.btnShoot.getWidth()/2 * Assets.btnCoeff), false);
        shootBtn.SetInvisible();
        shootBtn.Lock();
        flagBtn = new Button(Assets.btnFlag, (int)(170 * Assets.monitorWidthCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), (int)(170 * Assets.monitorHeightCoeff + Assets.btnFlag.getWidth()/2 * Assets.btnCoeff), false);
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
            graphics.DrawSprite(Assets.background, bgMatrix);
        }

        battle.DrawFields(graphics);

        ButtonsDraw(graphics);

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

        //graphics.drawText("state: " + battle.state, 20, 50,300, paint.getColor());
        /*graphics.drawText("height: " + screenHeight, 20, 50,350, paint.getColor());
        graphics.drawText("dpi: " + Assets.dpiCoeff, 20, 50,400, paint.getColor());
        graphics.drawText("btn X: " + shootBtn.x, 20, 50,450, paint.getColor());
        graphics.drawText("btn Y: " + shootBtn.y, 20, 50,500, paint.getColor());*/
    }
    //endregion

    //region Test
    public void GameOver(BattleState state, int reward)
    {
        Intent intent = new Intent(activity, BattleOverActivity.class);
        intent.putExtra("result", state == BattleState.Win);
        intent.putExtra("reward", reward);
        activity.startActivityForResult(intent, 2);
    }

    public void GameOver()
    {
        battle.state = BattleState.Win;
        battle.GameOver();
    }
    //endregion
}