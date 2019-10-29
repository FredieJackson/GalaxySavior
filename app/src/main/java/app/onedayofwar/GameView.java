package app.onedayofwar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import app.onedayofwar.Games.Game;
import app.onedayofwar.Games.SingleGame;
import app.onedayofwar.System.*;
import app.onedayofwar.Panel.*;

public class GameView extends SurfaceView
implements OnTouchListener, SurfaceHolder.Callback
{
    //region Variables
    public int screenWidth;
    public int screenHeight;
    private Activity activity;
    private GameThread gameLoopThread;
    public Vector2 touchPos;
    public Paint paint;
    private Game game;

    private Bitmap test;
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
    public GameView(Activity activity, char typeOfGame)
    {
        super(activity.getApplicationContext());
        this.activity = activity;
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
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){}
    //endregion

    //region Initialization
    private void Initialize()
    {
        getHolder().addCallback(this);
        setOnTouchListener(this);

        //region Get Screen Sizes
        /*WindowManager wm = (WindowManager) activity.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        try
        {
            Method mGetRawH = Display.class.getMethod("getRawHeight");
            Method mGetRawW = Display.class.getMethod("getRawWidth");
            screenWidth = (Integer) mGetRawW.invoke(display);
            screenHeight = (Integer) mGetRawH.invoke(display);
            Toast.makeText(activity.getApplicationContext(), "Size: " + screenWidth + " x " + screenHeight, Toast.LENGTH_LONG);
        }
        catch (Exception e)
        {
            Toast.makeText(activity.getApplicationContext(), "Couldn't use reflection to get the real display metrics", Toast.LENGTH_LONG);
        }*/
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        //endregion

        selectingPanel = new Panel(getResources(), screenWidth - screenWidth/4, 0, screenWidth/4, screenHeight, Type.RIGHT);

        gateUp = new Panel(getResources(), 0, 0, screenWidth, screenHeight/2, Type.UP);
        gateDown = new Panel(getResources(), 0, screenHeight/2, screenWidth, screenHeight/2, Type.DOWN);

        touchPos = new Vector2();

        isButtonPressed = false;

        paint = new Paint();
        paint.setARGB(255,250,240,20);
        paint.setTextSize(40f);

    }
    //endregion

    //region Update
    public void Update()
    {
        game.Update();
        if(!game.isInstallationComplete && !selectingPanel.isStop)
        {
            selectingPanel.Update();
        }
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
        touchPos.SetValue((int)event.getX(), (int)event.getY());

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
            if(!game.isInstallationComplete)
            {
                game.InstallUnit();
            }
            else
            {
                MoveGates();
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
            if (!game.isInstallationComplete)
            {
                if (game.CheckInstallationFinish())
                {
                    MoveGates();
                    installBtn.SetPosition(shootBtn.GetPosition());
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

        else if(shootBtn.IsClicked())
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

        if(game.IsUnitSelected())
        {
            cancelBtn.Update(touchPos);
            turnBtn.Update(touchPos);
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
     * @param canvas
     */
    private void ButtonsDraw(Canvas canvas)
    {
        if(!game.isInstallationComplete)
        {
            installationFinishBtn.Draw(canvas);
            if (game.IsUnitSelected())
            {
                cancelBtn.Draw(canvas);
                turnBtn.Draw(canvas);
                installBtn.Draw(canvas);
            }
        }
        else
        {
            if(game.isYourTurn)
            {
                installBtn.Draw(canvas);
                shootBtn.Draw(canvas);
            }
        }
    }

    /**
     * Инициализирует кнопки
     */
    private void ButtonsInitialize()
    {
        cancelBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_cancel), new Vector2(10, 90), true);
        turnBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_turn), new Vector2(10, game.field.y + game.field.height - 150), true);
        installBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_install), new Vector2(10, game.field.y + game.field.height - 70), true);
        installationFinishBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_installation_finish), new Vector2(10, 10), true);
        shootBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_shoot), new Vector2(game.eField.initX + game.eField.width + 30, 10), true);
        shootBtn.SetInvisible();
        shootBtn.Lock();
    }
    //endregion

    //region Draw
    public void Draw(Canvas canvas)
    {
        if(canvas!=null)
        {
            canvas.drawColor(Color.argb(255, 0, 140, 240));
            game.DrawFields(canvas);
            ButtonsDraw(canvas);
            game.DrawUnits(canvas);
            if(!game.isInstallationComplete)
            {
                selectingPanel.Draw(canvas);
               if(selectingPanel.isClose || !selectingPanel.isStop);
                    game.DrawUnitsIcons(canvas);
            }
            if(gateUp.isClose || !gateUp.isStop)
            {
                gateUp.Draw(canvas);
                gateDown.Draw(canvas);
            }
            //canvas.drawText("width: " + screenWidth, 50,50, paint);
            //canvas.drawText("height: " + screenHeight, 50,100, paint);
        }
    }
    //endregion

    //region Test
    private void GoToMainMenu()
    {
        gameLoopThread.setRunning(false);
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
    //endregion
}