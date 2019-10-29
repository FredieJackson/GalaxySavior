package app.onedayofwar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import app.onedayofwar.Games.Game.*;
import app.onedayofwar.Games.Game;
import app.onedayofwar.Games.SingleGame;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.*;
import app.onedayofwar.Panel.*;

public class GameView extends SurfaceView
implements OnTouchListener, SurfaceHolder.Callback
{
    //region Variables
    public static final int sourceHeight = 480;
    public static final int sourceWidth = 800;
    public static final int sourceDpi = 233;
    public Graphics graphics;
    public int screenWidth;
    public int screenHeight;
    private Activity activity;
    private GameThread gameLoopThread;
    public Vector2 touchPos;
    public Paint paint;
    private Game game;

    public Panel selectingPanel;
    public Panel gateUp;
    public Panel gateDown;

    //region Buttons Variables
    private Button cancelBtn;
    private Button turnBtn;
    private Button installBtn;
    private Button installationFinishBtn;
    private Button shootBtn;
    public boolean isButtonPressed;
    //endregion

    //endregion

    //region Constructor
    public GameView(Activity activity, char typeOfGame, int width, int height)
    {
        super(activity.getApplicationContext());
        this.activity = activity;
        screenWidth = width;
        screenHeight = height;
        Initialize();
        switch(typeOfGame)
        {
            case 's':
                game = new SingleGame(this);
            break;
        }
        ButtonsInitialize();
        MoveGates();

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
        gameLoopThread = new GameThread(this);
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
    private void Initialize()
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
    }

    private void LoadAssets()
    {
        Assets.robotImageL = graphics.newSprite("unit/image/robot.png", Graphics.SpriteFormat.ARGB4444);
        Assets.robotImageR = graphics.newSprite("unit/image/robot.png", Graphics.SpriteFormat.ARGB4444);
        Assets.robotImageR.horizontalFlip();
        Assets.robotIcon = graphics.newSprite("unit/icon/robot_icon.png", Graphics.SpriteFormat.RGB565);
        Assets.robotStroke = graphics.newSprite("unit/stroke/robot_stroke.png", Graphics.SpriteFormat.ARGB4444);

        Assets.ifvImageL = graphics.newSprite("unit/image/ifv.png", Graphics.SpriteFormat.ARGB4444);
        Assets.ifvImageR = graphics.newSprite("unit/image/ifv.png", Graphics.SpriteFormat.ARGB4444);
        Assets.ifvImageR.horizontalFlip();
        Assets.ifvIcon = graphics.newSprite("unit/icon/ifv_icon.png", Graphics.SpriteFormat.RGB565);
        Assets.ifvStroke = graphics.newSprite("unit/stroke/ifv_stroke.png", Graphics.SpriteFormat.ARGB4444);

        Assets.engineerImageL = graphics.newSprite("unit/image/engineer.png", Graphics.SpriteFormat.ARGB4444);
        Assets.engineerImageR = graphics.newSprite("unit/image/engineer.png", Graphics.SpriteFormat.ARGB4444);
        Assets.engineerImageR.horizontalFlip();
        Assets.engineerIcon = graphics.newSprite("unit/icon/engineer_icon.png", Graphics.SpriteFormat.RGB565);
        Assets.engineerStroke = graphics.newSprite("unit/stroke/engineer_stroke.png", Graphics.SpriteFormat.ARGB4444);

        Assets.tankImageL = graphics.newSprite("unit/image/tank.png", Graphics.SpriteFormat.ARGB4444);
        Assets.tankImageR = graphics.newSprite("unit/image/tank.png", Graphics.SpriteFormat.ARGB4444);
        Assets.tankImageR.horizontalFlip();
        Assets.tankIcon = graphics.newSprite("unit/icon/tank_icon.png", Graphics.SpriteFormat.RGB565);
        Assets.tankStroke = graphics.newSprite("unit/stroke/tank_stroke.png", Graphics.SpriteFormat.ARGB4444);

        Assets.turretImageL = graphics.newSprite("unit/image/turret.png", Graphics.SpriteFormat.ARGB4444);
        Assets.turretImageR = graphics.newSprite("unit/image/turret.png", Graphics.SpriteFormat.ARGB4444);
        Assets.turretImageR.horizontalFlip();
        Assets.turretIcon = graphics.newSprite("unit/icon/turret_icon.png", Graphics.SpriteFormat.RGB565);
        Assets.turretStroke = graphics.newSprite("unit/stroke/turret_stroke.png", Graphics.SpriteFormat.ARGB4444);

        Assets.sonderImageL = graphics.newSprite("unit/image/sonder.png", Graphics.SpriteFormat.ARGB4444);
        Assets.sonderImageR = graphics.newSprite("unit/image/sonder.png", Graphics.SpriteFormat.ARGB4444);
        Assets.sonderImageR.horizontalFlip();
        Assets.sonderIcon = graphics.newSprite("unit/icon/sonder_icon.png", Graphics.SpriteFormat.RGB565);
        Assets.sonderStroke = graphics.newSprite("unit/stroke/sonder_stroke.png", Graphics.SpriteFormat.ARGB4444);

        Assets.grid = graphics.newSprite("field/grid/normal_green.png", Graphics.SpriteFormat.ARGB4444);
        Assets.gridIso = graphics.newSprite("field/grid/iso.png", Graphics.SpriteFormat.ARGB4444);

        Assets.signFire = graphics.newSprite("field/mark/fire.png", Graphics.SpriteFormat.ARGB4444);
        Assets.signMiss = graphics.newSprite("field/mark/miss_green.png", Graphics.SpriteFormat.ARGB4444);
        Assets.signMissIso = graphics.newSprite("field/mark/miss_iso.png", Graphics.SpriteFormat.ARGB4444);
        Assets.signHit = graphics.newSprite("field/mark/hit_green.png", Graphics.SpriteFormat.ARGB4444);

        Assets.btnCancel = graphics.newSprite("button/cancel.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnInstall = graphics.newSprite("button/install.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnFinishInstallation = graphics.newSprite("button/installation_finish.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnShoot = graphics.newSprite("button/shootBtn.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnTurn = graphics.newSprite("button/turn.png", Graphics.SpriteFormat.ARGB4444);
        Assets.btnPanelClose = graphics.newSprite("button/panel_close.png", Graphics.SpriteFormat.ARGB4444);

        Assets.background = graphics.newSprite("town-c.jpg", Graphics.SpriteFormat.RGB565);
        Assets.monitor = graphics.newSprite("monitorHD.png", Graphics.SpriteFormat.ARGB4444);

        scaleImages();
    }

    private void scaleImages()
    {
        //Коэффициент масштабирования поля и юнитов. 0.2f - отступ в долях единицы от верха экрана до верхней точки поля.
        //То есть находим высоту поля для данного экрана, чтобы сверху и снизу было расстояние до края экрана равное определенному количеству процентов высоты экрана.
        //Полученная высота поля должна быть кратна 15, то есть кол-ву клеток поля. А высота и ширина клетки поля должны быть кратны 2. Используем дабл для высокой точности вычислений.
        //Как тебе такое только в голову пришло))
        double fieldCoeff = (int)((screenHeight * (1 - 2 * 0.2f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
        double iconCoeff = ((screenHeight - 70)/(double)6) / Assets.robotIcon.getHeight();
        double btnCoeff = screenHeight * 0.1f / (double)Assets.btnCancel.getHeight();

        Assets.dpiCoeff =  getResources().getDisplayMetrics().xdpi / sourceDpi;
        Assets.gridCoeff = fieldCoeff;
        Assets.screenHeightCoeff = screenHeight / sourceHeight;
        Assets.screenWidthCoeff = screenWidth / sourceWidth;


        Assets.robotImageL.changeSize(fieldCoeff);
        Assets.robotImageR.changeSize(fieldCoeff);
        Assets.robotIcon.changeSize(iconCoeff);
        Assets.robotStroke.changeSize(fieldCoeff);

        Assets.ifvImageL.changeSize(fieldCoeff);
        Assets.ifvImageR.changeSize(fieldCoeff);
        Assets.ifvIcon.changeSize(iconCoeff);
        Assets.ifvStroke.changeSize(fieldCoeff);

        Assets.engineerImageL.changeSize(fieldCoeff);
        Assets.engineerImageR.changeSize(fieldCoeff);
        Assets.engineerIcon.changeSize(iconCoeff);
        Assets.engineerStroke.changeSize(fieldCoeff);

        Assets.tankImageL.changeSize(fieldCoeff);
        Assets.tankImageR.changeSize(fieldCoeff);
        Assets.tankIcon.changeSize(iconCoeff);
        Assets.tankStroke.changeSize(fieldCoeff);

        Assets.turretImageL.changeSize(fieldCoeff);
        Assets.turretImageR.changeSize(fieldCoeff);
        Assets.turretIcon.changeSize(iconCoeff);
        Assets.turretStroke.changeSize(fieldCoeff);

        Assets.sonderImageL.changeSize(fieldCoeff);
        Assets.sonderImageR.changeSize(fieldCoeff);
        Assets.sonderIcon.changeSize(iconCoeff);
        Assets.sonderStroke.changeSize(fieldCoeff);

        Assets.grid.changeSize(fieldCoeff);
        Assets.gridIso.changeSize(fieldCoeff);

        Assets.signFire.changeSize(fieldCoeff);
        Assets.signMiss.changeSize(fieldCoeff);
        Assets.signMissIso.changeSize(fieldCoeff);
        Assets.signHit.changeSize(fieldCoeff);

        Assets.btnCancel.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnInstall.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnFinishInstallation.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnShoot.changeSize(screenHeight * 0.1f / Assets.btnShoot.getHeight());//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnTurn.changeSize(btnCoeff);//(int)(btnCoeff * Assets.btnSourceSize),(int)(Assets.btnSourceSize * btnCoeff));
        Assets.btnPanelClose.changeSize(btnCoeff);//btnCoeff);

        Assets.background.changeSize(screenWidth, screenHeight);
        Assets.monitor.changeSize(screenWidth, screenHeight);
    }
    //endregion

    //region Update
    public void Update()
    {
        if(game.state == GameState.Installation && !selectingPanel.isStop)
        {
            selectingPanel.Update();
        }
        game.Update();
        if (!gateUp.isStop)
        {
            gateUp.Update();
            gateDown.Update();
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
        installBtn.Lock();
        installBtn.SetInvisible();
    }

    public void DefendingPrepare()
    {
        shootBtn.SetInvisible();
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
        game.OnTouch(event);
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
            if(game.state == GameState.Installation)
            {
                game.InstallUnit();
            }
            else
            {
                if(game.IsUnitSelected())
                {
                    MoveGates();
                }
            }
            isButtonPressed = true;
        }
        //Если нажата кнопка поворота юнита
        else if (turnBtn.IsClicked())
        {
            game.TurnUnit();
            isButtonPressed = true;
        }

        //Если нажата кнопка отмены выбора юнита
        else if (cancelBtn.IsClicked())
        {
            if(game.CancelSelection() && !selectingPanel.isClose)
                selectingPanel.Move();
            isButtonPressed = true;
        }

        //Если нажата кнопка завершения установки
        else if (installationFinishBtn.IsClicked())
        {
            if (game.state == GameState.Installation)
            {
                if (game.CheckInstallationFinish())
                {
                    MoveGates();
                    installBtn.SetPosition((int)((game.eField.initX + game.eField.width + 30) * Assets.screenWidthCoeff), (int)(game.eField.y * Assets.screenHeightCoeff));
                    cancelBtn.Lock();
                    turnBtn.Lock();
                    selectingPanel.CloseBtnLock();
                    installationFinishBtn.Lock();
                }
            }
            isButtonPressed = true;
        }

        else if(selectingPanel.IsCloseBtnPressed())
        {
            selectingPanel.Move();
            isButtonPressed = true;
        }

        else if(shootBtn.IsClicked() && IsGatesOpen())
        {
            if(game.PlayerShoot())
            {
                MoveGates();
                shootBtn.Lock();
            }
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


        if(game.state == GameState.Installation)
        {
            if (game.IsUnitSelected())
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
    }

    /**
     * Отрисовывает кнопки
     * @param
     */
    private void ButtonsDraw()
    {
        if(game.state == GameState.Installation)
        {
            installationFinishBtn.Draw(graphics);
            if (game.IsUnitSelected())
            {
                cancelBtn.Draw(graphics);
                turnBtn.Draw(graphics);
                installBtn.Draw(graphics);
            }
        }
        else
        {
            if(game.isYourTurn)
            {
                installBtn.Draw(graphics);
                shootBtn.Draw(graphics);
            }
        }
    }

    /**
     * Инициализирует кнопки
     */
    private void ButtonsInitialize()
    {
        cancelBtn = new Button(Assets.btnCancel, new Vector2(10, 90), true);
        turnBtn = new Button(Assets.btnTurn, new Vector2(10, game.field.y + game.field.height - 150), true);
        installBtn = new Button(Assets.btnInstall, new Vector2(10, game.field.y + game.field.height - 70), true);
        installationFinishBtn = new Button(Assets.btnFinishInstallation, new Vector2(10, 10), true);
        shootBtn = new Button(Assets.btnShoot, new Vector2((int)(80 * Assets.screenWidthCoeff),(int)(240 * Assets.screenHeightCoeff)), false);
        shootBtn.SetInvisible();
        shootBtn.Lock();
    }
    //endregion

    //region Draw
    public void Draw()
    {
        //graphics.clear(Color.argb(255, 0, 140, 240));
        //canvas.drawColor(Color.argb(255, 0, 140, 240));
        if (game.state == GameState.Attack)
        {
            graphics.clear(Color.rgb(0, 0, 0));
            graphics.drawSprite(Assets.monitor);
        }
        else
        {
            if (game.state == GameState.Installation)
                graphics.drawSprite(Assets.background, 0,0, screenWidth - (screenWidth - selectingPanel.x - selectingPanel.offsetX), screenHeight, 0, 0, screenWidth - (screenWidth - selectingPanel.x - selectingPanel.offsetX), screenHeight);
            else
                graphics.drawSprite(Assets.background);
        }
        game.DrawFields();

        ButtonsDraw();

        game.DrawUnits();

        if (game.state == Game.GameState.Installation)
        {
            if (selectingPanel.isClose || !selectingPanel.isStop)
            {
                selectingPanel.Draw(graphics);
                game.DrawUnitsIcons();
            }
            selectingPanel.DrawButton(graphics);
        }
        if (gateUp.isClose || !gateUp.isStop)
        {
            gateUp.Draw(graphics);
            gateDown.Draw(graphics);
        }
        graphics.drawText("width: " + screenWidth, 20, 50,300, paint.getColor());
        graphics.drawText("height: " + screenHeight, 20, 50,350, paint.getColor());
        graphics.drawText("dpi: " + Assets.dpiCoeff, 20, 50,400, paint.getColor());
        //graphics.drawText("x: " + touchPos.x, 20, 50,400, paint.getColor());
        //graphics.drawText("y: " + touchPos.y, 20, 50,450, paint.getColor());
    }
    //endregion

    //region Test
    private void GoToMainMenu()
    {
        gameLoopThread.setRunning(false);
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

    public void GameOver(GameState state, int reward)
    {
        gameLoopThread.setRunning(false);
        Intent intent = new Intent(activity, GameOverActivity.class);
        intent.putExtra("result", state == GameState.Win);
        intent.putExtra("reward", reward);
        activity.startActivity(intent);
    }
    //endregion
}