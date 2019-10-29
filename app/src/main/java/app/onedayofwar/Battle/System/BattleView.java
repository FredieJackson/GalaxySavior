package app.onedayofwar.Battle.System;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import app.onedayofwar.Battle.Activities.BattleOverActivity;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.BluetoothConnection.BluetoothController;
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

public class BattleView extends SurfaceView
implements OnTouchListener, SurfaceHolder.Callback
{
    //region Variables
    public static final int sourceHeight = 720;
    public static final int sourceWidth = 1024;
    public static final int sourceDpi = 132;
    public BluetoothController btController;
    public Graphics graphics;
    public int screenWidth;
    public int screenHeight;
    private Activity activity;
    private BattleThread gameLoopThread;
    public Vector2 touchPos;
    public Paint paint;
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
    }
    //endregion

    //region Surface Methods
    /** Уничтожение области рисования*/
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        gameLoopThread.setRunning(false);
        while (retry)
        {
            try
            {
                gameLoopThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {

            }
        }
    }
    /** Создание области рисования*/
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        gameLoopThread = new BattleThread(this);
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }
    /** Изменение области рисования*/
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }
    //endregion

    //region Initialization
    public void SetAttackSequence(boolean isYourTurn)
    {
        battle.isYourTurn = isYourTurn;
    }

    public void LoadBT(BluetoothController controller)
    {
        btController = controller;
    }

    public void Initialize()
    {
        getHolder().addCallback(this);
        setOnTouchListener(this);

        graphics = new Graphics(activity.getAssets(), screenWidth, screenHeight);
        LoadAssets();

        selectingPanel = new Panel(screenWidth - screenWidth/4, 0, screenWidth/4, screenHeight, Type.RIGHT);

        gateUp = new Panel(0, 0, screenWidth, screenHeight/2, Type.UP);
        gateDown = new Panel(0, screenHeight/2, screenWidth, screenHeight/2, Type.DOWN);

        touchPos = new Vector2();

        isButtonPressed = false;

        paint = new Paint();
        paint.setARGB(255,250,240,20);
        paint.setTextSize(40f);

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

    private void LoadAssets()
    {
        Assets.robotIcon = graphics.newSprite("unit/icon/robot_icon.png", Graphics.SpriteFormat.RGB565);
        if(BattlePlayer.unitCount[0] != 0)
        {
            Assets.robotImageL = graphics.newSprite("unit/image/robot.png", Graphics.SpriteFormat.ARGB4444);
            Assets.robotImageR = graphics.newSprite("unit/image/robot.png", Graphics.SpriteFormat.ARGB4444);
            Assets.robotImageR.horizontalFlip();
            Assets.robotStroke = graphics.newSprite("unit/stroke/robot_stroke.png", Graphics.SpriteFormat.ARGB4444);
        }
        if(BattlePlayer.unitCount[1] != 0)
        {
            Assets.ifvImageL = graphics.newSprite("unit/image/ifv.png", Graphics.SpriteFormat.ARGB4444);
            Assets.ifvImageR = graphics.newSprite("unit/image/ifv.png", Graphics.SpriteFormat.ARGB4444);
            Assets.ifvImageR.horizontalFlip();
            Assets.ifvIcon = graphics.newSprite("unit/icon/ifv_icon.png", Graphics.SpriteFormat.RGB565);
            Assets.ifvStroke = graphics.newSprite("unit/stroke/ifv_stroke.png", Graphics.SpriteFormat.ARGB4444);
        }
        if(BattlePlayer.unitCount[2] != 0)
        {
            Assets.engineerImageL = graphics.newSprite("unit/image/engineer.png", Graphics.SpriteFormat.ARGB4444);
            Assets.engineerImageR = graphics.newSprite("unit/image/engineer.png", Graphics.SpriteFormat.ARGB4444);
            Assets.engineerImageR.horizontalFlip();
            Assets.engineerIcon = graphics.newSprite("unit/icon/rocket_icon.png", Graphics.SpriteFormat.RGB565);
            Assets.engineerStroke = graphics.newSprite("unit/stroke/engineer_stroke.png", Graphics.SpriteFormat.ARGB4444);
        }
        if(BattlePlayer.unitCount[3] != 0)
        {
            Assets.tankImageL = graphics.newSprite("unit/image/tank.png", Graphics.SpriteFormat.ARGB4444);
            Assets.tankImageR = graphics.newSprite("unit/image/tank.png", Graphics.SpriteFormat.ARGB4444);
            Assets.tankImageR.horizontalFlip();
            Assets.tankIcon = graphics.newSprite("unit/icon/tank_icon.png", Graphics.SpriteFormat.RGB565);
            Assets.tankStroke = graphics.newSprite("unit/stroke/tank_stroke.png", Graphics.SpriteFormat.ARGB4444);
        }
        if(BattlePlayer.unitCount[4] != 0)
        {
            Assets.turretImageL = graphics.newSprite("unit/image/turret.png", Graphics.SpriteFormat.ARGB4444);
            Assets.turretImageR = graphics.newSprite("unit/image/turret.png", Graphics.SpriteFormat.ARGB4444);
            Assets.turretImageR.horizontalFlip();
            Assets.turretIcon = graphics.newSprite("unit/icon/turret_icon.png", Graphics.SpriteFormat.RGB565);
            Assets.turretStroke = graphics.newSprite("unit/stroke/turret_stroke.png", Graphics.SpriteFormat.ARGB4444);
        }
        if(BattlePlayer.unitCount[5] != 0)
        {
            Assets.sonderImageL = graphics.newSprite("unit/image/sonder.png", Graphics.SpriteFormat.ARGB4444);
            Assets.sonderImageR = graphics.newSprite("unit/image/sonder.png", Graphics.SpriteFormat.ARGB4444);
            Assets.sonderImageR.horizontalFlip();
            Assets.sonderIcon = graphics.newSprite("unit/icon/sonder_icon.png", Graphics.SpriteFormat.RGB565);
            Assets.sonderStroke = graphics.newSprite("unit/stroke/sonder_stroke.png", Graphics.SpriteFormat.ARGB4444);
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
                Assets.grid = graphics.newSprite("field/grid/normal_green_5x5.png", Graphics.SpriteFormat.ARGB4444);
                Assets.gridIso = graphics.newSprite("field/grid/iso_5x5.png", Graphics.SpriteFormat.ARGB4444);
                Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.2f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
                Assets.isoGridCoeff = (int)((screenHeight * (1 - 2 * 0.4f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
                break;
            case 15:
                Assets.grid = graphics.newSprite("field/grid/normal_green.png", Graphics.SpriteFormat.ARGB4444);
                Assets.gridIso = graphics.newSprite("field/grid/iso.png", Graphics.SpriteFormat.ARGB4444);
                Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.2f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
                Assets.isoGridCoeff = Assets.gridCoeff;
                break;
        }


        Assets.signFire = graphics.newSprite("field/mark/fire.png", Graphics.SpriteFormat.ARGB4444);
        Assets.signMiss = graphics.newSprite("field/mark/miss_green.png", Graphics.SpriteFormat.ARGB4444);
        Assets.signMissIso = graphics.newSprite("field/mark/miss_iso.png", Graphics.SpriteFormat.ARGB4444);
        Assets.signHit = graphics.newSprite("field/mark/hit_green.png", Graphics.SpriteFormat.ARGB4444);
        Assets.signFlag = graphics.newSprite("field/mark/flag.png", Graphics.SpriteFormat.ARGB4444);

        Assets.btnCancel = graphics.newSprite("button/cancel.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnInstall = graphics.newSprite("button/install.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnFinishInstallation = graphics.newSprite("button/installation_finish.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnShoot = graphics.newSprite("button/shoot.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnTurn = graphics.newSprite("button/turn.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnPanelClose = graphics.newSprite("button/panel_close.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnFlag = graphics.newSprite("button/flag.png", Graphics.SpriteFormat.ARGB4444);

        Assets.background = graphics.newSprite("town-c.jpg", Graphics.SpriteFormat.RGB565);
        Assets.monitor = graphics.newSprite("monitor.png", Graphics.SpriteFormat.ARGB4444);

        Assets.bullet = graphics.newSprite("unit/bullet/bullet.png", Graphics.SpriteFormat.ARGB4444);
        Assets.miniRocket = graphics.newSprite("unit/bullet/miniRocket.png", Graphics.SpriteFormat.ARGB4444);

        Assets.explode = graphics.newSprite("animation/explode.png", Graphics.SpriteFormat.ARGB4444);
        Assets.fire = graphics.newSprite("animation/fire2.png", Graphics.SpriteFormat.ARGB4444);

        scaleImages();
    }

    private void scaleImages()
    {
        double iconCoeff = ((screenHeight - 70) / 6d) / Assets.robotIcon.getHeight();
        double btnCoeff = screenHeight * 0.17f / Assets.btnCancel.getHeight();

        Assets.dpiCoeff =  (int)getResources().getDisplayMetrics().xdpi / (double)sourceDpi;
        Assets.monitorHeightCoeff = (double)screenHeight / 1080;
        Assets.monitorWidthCoeff = (double)screenWidth / 1920;
        Assets.screenHeightCoeff = screenHeight / sourceHeight;
        Assets.screenWidthCoeff = screenWidth / sourceWidth;

        Assets.robotIcon.changeSize(iconCoeff);

        if(BattlePlayer.unitCount[0] != 0)
        {
            Assets.robotImageL.changeSize(Assets.isoGridCoeff);
            Assets.robotImageR.changeSize(Assets.isoGridCoeff);
            Assets.robotStroke.changeSize(Assets.isoGridCoeff);
        }
        if(BattlePlayer.unitCount[1] != 0)
        {
            Assets.ifvImageL.changeSize(Assets.isoGridCoeff);
            Assets.ifvImageR.changeSize(Assets.isoGridCoeff);
            Assets.ifvIcon.changeSize(iconCoeff);
            Assets.ifvStroke.changeSize(Assets.isoGridCoeff);
        }
        if(BattlePlayer.unitCount[2] != 0)
        {
            Assets.engineerImageL.changeSize(Assets.isoGridCoeff);
            Assets.engineerImageR.changeSize(Assets.isoGridCoeff);
            Assets.engineerIcon.changeSize(iconCoeff);
            Assets.engineerStroke.changeSize(Assets.isoGridCoeff);
        }
        if(BattlePlayer.unitCount[3] != 0)
        {
            Assets.tankImageL.changeSize(Assets.isoGridCoeff);
            Assets.tankImageR.changeSize(Assets.isoGridCoeff);
            Assets.tankIcon.changeSize(iconCoeff);
            Assets.tankStroke.changeSize(Assets.isoGridCoeff);
        }
        if(BattlePlayer.unitCount[4] != 0)
        {
            Assets.turretImageL.changeSize(Assets.isoGridCoeff);
            Assets.turretImageR.changeSize(Assets.isoGridCoeff);
            Assets.turretIcon.changeSize(iconCoeff);
            Assets.turretStroke.changeSize(Assets.isoGridCoeff);
        }
        if(BattlePlayer.unitCount[5] != 0)
        {
            Assets.sonderImageL.changeSize(Assets.isoGridCoeff);
            Assets.sonderImageR.changeSize(Assets.isoGridCoeff);
            Assets.sonderIcon.changeSize(iconCoeff);
            Assets.sonderStroke.changeSize(Assets.isoGridCoeff);
        }

        Assets.grid.changeSize(Assets.gridCoeff);
        Assets.gridIso.changeSize(Assets.isoGridCoeff);

        Assets.signFire.changeSize(Assets.isoGridCoeff);
        Assets.signMiss.changeSize(Assets.gridCoeff);
        Assets.signMissIso.changeSize(Assets.isoGridCoeff);
        Assets.signHit.changeSize(Assets.gridCoeff);
        Assets.signFlag.changeSize(Assets.gridCoeff);

        Assets.btnCancel.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnInstall.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnFinishInstallation.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnShoot.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnFlag.changeSize(btnCoeff);
        Assets.btnTurn.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnPanelClose.changeSize(btnCoeff);//btnCoeff);

        Assets.background.changeSize(screenWidth, screenHeight);
        Assets.monitor.changeSize(screenWidth, screenHeight);

        Assets.bullet.changeSize(Assets.isoGridCoeff);
        Assets.miniRocket.changeSize(Assets.isoGridCoeff);

        Assets.explode.changeSize(Assets.isoGridCoeff);
        Assets.fire.changeSize(Assets.isoGridCoeff);
    }
    //endregion

    //region Update
    public void Update(float eTime)
    {
        if(battle.state == BattleState.Installation && !selectingPanel.isStop)
        {
            selectingPanel.Update(eTime);
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
        return true;
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
    private void ButtonsDraw()
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
        installationFinishBtn = new Button(Assets.btnFinishInstallation, (int)(50 * Assets.monitorWidthCoeff), (int)(50 * Assets.monitorHeightCoeff), false);
        cancelBtn = new Button(Assets.btnCancel, (int)(50 * Assets.monitorWidthCoeff), (int)((50 + 30) * Assets.monitorHeightCoeff  + Assets.btnCancel.getHeight()), false);
        turnBtn = new Button(Assets.btnTurn, (int)(50 * Assets.monitorWidthCoeff), (int)(screenHeight - 2 * Assets.btnCancel.getHeight() - (50 + 30) * Assets.monitorHeightCoeff), false);
        installBtn = new Button(Assets.btnInstall, (int)(50 * Assets.monitorWidthCoeff), (int)(screenHeight - Assets.btnCancel.getHeight() - 50 * Assets.monitorHeightCoeff), false);

        shootBtn = new Button(Assets.btnShoot, (int)(170 * Assets.monitorWidthCoeff), (int)(390 * Assets.monitorHeightCoeff), false);
        shootBtn.SetInvisible();
        shootBtn.Lock();
        flagBtn = new Button(Assets.btnFlag, (int)(170 * Assets.monitorWidthCoeff), (int)(170 * Assets.monitorHeightCoeff), false);
        flagBtn.SetInvisible();
        flagBtn.Lock();

    }
    //endregion

    //region Draw
    public void Draw()
    {
        //graphics.clear(Color.argb(255, 0, 140, 240));
        //canvas.drawColor(Color.argb(255, 0, 140, 240));
        if (battle.state == BattleState.Attack || battle.state == BattleState.Shoot)
        {
            graphics.clear(Color.rgb(0, 0, 0));
            graphics.drawSprite(Assets.monitor);
        }
        else
        {
            if (battle.state == BattleState.Installation)
                graphics.drawSprite(Assets.background, 0,0, screenWidth - (screenWidth - selectingPanel.x - selectingPanel.offsetX), screenHeight, 0, 0, screenWidth - (screenWidth - selectingPanel.x - selectingPanel.offsetX), screenHeight);
            else
                graphics.drawSprite(Assets.background);
        }
        battle.DrawFields();

        ButtonsDraw();

        battle.DrawUnits();

        if (battle.state == BattleState.Installation)
        {
            if (selectingPanel.isClose || !selectingPanel.isStop)
            {
                selectingPanel.Draw(graphics);
                battle.DrawUnitsIcons();
            }
            selectingPanel.DrawButton(graphics);
        }
        if (gateUp.isClose || !gateUp.isStop)
        {
            gateUp.Draw(graphics);
            gateDown.Draw(graphics);
        }

        graphics.drawText("state: " + battle.state, 20, 50,300, paint.getColor());
        /*graphics.drawText("height: " + screenHeight, 20, 50,350, paint.getColor());
        graphics.drawText("dpi: " + Assets.dpiCoeff, 20, 50,400, paint.getColor());
        graphics.drawText("btn X: " + shootBtn.x, 20, 50,450, paint.getColor());
        graphics.drawText("btn Y: " + shootBtn.y, 20, 50,500, paint.getColor());*/
    }
    //endregion

    //region Test
    public void GameOver(BattleState state, int reward)
    {
        gameLoopThread.setRunning(false);
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