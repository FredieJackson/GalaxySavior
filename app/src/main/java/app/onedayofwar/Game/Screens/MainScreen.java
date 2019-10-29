package app.onedayofwar.Game.Screens;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLES20;
import android.view.MotionEvent;

import app.onedayofwar.GEngine.Assets;
import app.onedayofwar.GEngine.GLRenderer;
import app.onedayofwar.GEngine.Loader;
import app.onedayofwar.Utils.Vector2;
import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Utils.IO.DBController;
import app.onedayofwar.GEngine.GUI.Button;

/**
 * Created by Slava on 29.03.2015.
 */
public class MainScreen implements Screen
{
    private ScreenController screenController;

    private DBController dbController;

    private Vector2 touchPos;

    private Button startGame;
    private Button singleGame;
    private Button bluetoothGame;
    private Button campaing;
    private Button quickBattle;
    private Button back;
    private Button newGame;
    private Button loadGame;

    private Button easyGame;
    private Button normalGame;
    private Button hardGame;
    private Button godGame;

    private Button groundBattle;
    private Button spaceBattle;

    public static byte startBTBattle;

    public MainScreen(Context context, ScreenController screenController)
    {
        this.screenController = screenController;
        touchPos = new Vector2();
        dbController = new DBController(context);
        startBTBattle = 0;
    }

    @Override
    public void Initialize(Loader loader)
    {
        float scale = 0.15f * screenController.screenHeight / Assets.btnStartGame.getHeight();

        startGame = new Button(Assets.btnStartGame, screenController.screenWidth/2, screenController.screenHeight/2, false);
        startGame.Scale(scale);

        bluetoothGame = new Button(Assets.btnBluetoothGame, screenController.screenWidth/2, screenController.screenHeight/2, false);
        bluetoothGame.Scale(scale);
        bluetoothGame.SetInvisible();
        bluetoothGame.Lock();

        singleGame = new Button(Assets.btnSingleGame, screenController.screenWidth/2, bluetoothGame.getMatrix()[13] - bluetoothGame.height - 10, false);
        singleGame.Scale(scale);
        singleGame.SetInvisible();
        singleGame.Lock();

        campaing = new Button(Assets.btnCampaing, screenController.screenWidth/2, bluetoothGame.getMatrix()[13] - bluetoothGame.height - 10, false);
        campaing.Scale(scale);
        campaing.SetInvisible();
        campaing.Lock();

        quickBattle = new Button(Assets.btnQuickBattle, screenController.screenWidth/2, screenController.screenHeight/2, false);
        quickBattle.Scale(scale);
        quickBattle.SetInvisible();
        quickBattle.Lock();

        back = new Button(Assets.btnBack, screenController.screenWidth/2, bluetoothGame.getMatrix()[13] + bluetoothGame.height + 10, false);
        back.Scale(scale);
        back.SetInvisible();
        back.Lock();

        newGame = new Button(Assets.btnNewGame, screenController.screenWidth/2, bluetoothGame.getMatrix()[13] - bluetoothGame.height - 10, false);
        newGame.Scale(scale);
        newGame.SetInvisible();
        newGame.Lock();

        loadGame = new Button(Assets.btnLoadGame, screenController.screenWidth/2, screenController.screenHeight/2, false);
        loadGame.Scale(scale);
        loadGame.SetInvisible();
        loadGame.Lock();

        easyGame = new Button(Assets.btnEasyGame, screenController.screenWidth/2, screenController.screenHeight/2 - 2*(bluetoothGame.height + 10), false);
        easyGame.Scale(scale);
        easyGame.SetInvisible();
        easyGame.Lock();

        normalGame = new Button(Assets.btnNormalGame, screenController.screenWidth/2, screenController.screenHeight/2 - bluetoothGame.height - 10, false);
        normalGame.Scale(scale);
        normalGame.SetInvisible();
        normalGame.Lock();

        hardGame = new Button(Assets.btnHardGame, screenController.screenWidth/2, screenController.screenHeight/2, false);
        hardGame.Scale(scale);
        hardGame.SetInvisible();
        hardGame.Lock();

        godGame = new Button(Assets.btnGodGame, screenController.screenWidth/2, screenController.screenHeight/2 + bluetoothGame.height + 10, false);
        godGame.Scale(scale);
        godGame.SetInvisible();
        godGame.Lock();

        spaceBattle = new Button(Assets.btnSArmy, 0, 0, false);
        spaceBattle.Scale(scale);
        spaceBattle.SetPosition(screenController.screenWidth/2 - spaceBattle.width, screenController.screenHeight/2 - spaceBattle.height - 10);
        spaceBattle.SetInvisible();
        spaceBattle.Lock();

        groundBattle = new Button(Assets.btnGArmy, 0, 0, false);
        groundBattle.Scale(scale);
        groundBattle.SetPosition(screenController.screenWidth/2 + groundBattle.width, screenController.screenHeight/2 - groundBattle.height - 10);
        groundBattle.SetInvisible();
        groundBattle.Lock();
    }

    @Override
    public void Update(float eTime)
    {
        if(startBTBattle > 0)
        {
            //glView.StartBattle(null, 'b', startBTBattle == 1);
            startBTBattle = 0;
        }
    }

    @Override
    public void Draw(GLRenderer renderer)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        ButtonsDraw(renderer);
    }

    @Override
    public void OnTouch(MotionEvent event)
    {
        touchPos.SetValue(event.getX(), event.getY());

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
    }

    @Override
    public void Resume()
    {
        //glView.getActivity().gameState = MainActivity.GameState.MENU;
        //glView.setCamera(0, 0);
        touchPos.SetZero();
    }

    /**
     * Обрабатывает нажатия на кнопки
     */
    public void CheckButtons()
    {
        if(startGame.IsClicked())
        {
            startGame.SetInvisible();
            startGame.Lock();
            singleGame.Unlock();
            singleGame.SetVisible();
            bluetoothGame.Unlock();
            bluetoothGame.SetVisible();
            back.Unlock();
            back.SetVisible();
        }
        else if(singleGame.IsClicked())
        {
            singleGame.SetInvisible();
            singleGame.Lock();
            bluetoothGame.Lock();
            bluetoothGame.SetInvisible();
            campaing.SetVisible();
            campaing.Unlock();
            quickBattle.SetVisible();
            quickBattle.Unlock();
        }
        else if(bluetoothGame.IsClicked())
        {
            //glView.getActivity().CheckBT();

            startGame.SetVisible();
            startGame.Unlock();

            singleGame.SetInvisible();
            singleGame.Lock();

            bluetoothGame.SetInvisible();
            bluetoothGame.Lock();

            campaing.SetInvisible();
            campaing.Lock();

            quickBattle.SetInvisible();
            quickBattle.Lock();

            back.SetInvisible();
            back.Lock();

            newGame.SetInvisible();
            newGame.Lock();

            loadGame.SetInvisible();
            loadGame.Lock();
        }
        else if(campaing.IsClicked())
        {
            campaing.SetInvisible();
            campaing.Lock();

            quickBattle.SetInvisible();
            quickBattle.Lock();

            newGame.SetVisible();
            newGame.Unlock();

            loadGame.SetVisible();
            if(dbController.IsGameSaved()) loadGame.Unlock();
        }
        else if(easyGame.IsClicked())
        {
            //StartBattle(5, Math.random() < 0.6);
        }
        else if(normalGame.IsClicked())
        {
            //StartBattle(20, Math.random() < 0.4);
        }
        else if(hardGame.IsClicked())
        {
            // StartBattle(40, false);
        }
        else if(godGame.IsClicked())
        {
            // StartBattle(100, false);
        }
        else if(quickBattle.IsClicked())
        {
            spaceBattle.SetVisible();
            spaceBattle.Unlock();

            groundBattle.SetVisible();
            groundBattle.Unlock();

            campaing.SetInvisible();
            campaing.Lock();

            quickBattle.SetInvisible();
            quickBattle.Lock();
        }
        else if(groundBattle.IsClicked())
        {

            easyGame.SetVisible();
            easyGame.Unlock();

            normalGame.SetVisible();
            normalGame.Unlock();

            hardGame.SetVisible();
            hardGame.Unlock();

            godGame.SetVisible();
            godGame.Unlock();

            spaceBattle.SetInvisible();
            spaceBattle.Lock();

            groundBattle.SetInvisible();
            groundBattle.Lock();

            back.SetPosition(screenController.screenWidth/2, screenController.screenHeight/2 + 2*(bluetoothGame.height + 10));
        }
        else if(spaceBattle.IsClicked())
        {

            easyGame.SetVisible();
            easyGame.Unlock();

            normalGame.SetVisible();
            normalGame.Unlock();

            hardGame.SetVisible();
            hardGame.Unlock();

            godGame.SetVisible();
            godGame.Unlock();

            spaceBattle.SetInvisible();
            spaceBattle.Lock();

            groundBattle.SetInvisible();
            groundBattle.Lock();

            back.SetPosition(screenController.screenWidth/2, screenController.screenHeight/2 + 2*(bluetoothGame.height + 10));
        }
        else if(back.IsClicked())
        {
            SetMainMenu();
        }
        else if(newGame.IsClicked())
        {
            //glView.StartCampaign(dbController, true);
            SetMainMenu();
        }
        else if(loadGame.IsClicked())
        {
            //glView.StartCampaign(dbController, false);
            SetMainMenu();
        }
    }

    public void SetMainMenu()
    {
        startGame.SetVisible();
        startGame.Unlock();

        singleGame.SetInvisible();
        singleGame.Lock();

        bluetoothGame.SetInvisible();
        bluetoothGame.Lock();

        campaing.SetInvisible();
        campaing.Lock();

        quickBattle.SetInvisible();
        quickBattle.Lock();

        back.SetPosition(screenController.screenWidth/2, screenController.screenHeight/2 + bluetoothGame.height + 10);
        back.SetInvisible();
        back.Lock();

        newGame.SetInvisible();
        newGame.Lock();

        loadGame.SetInvisible();
        loadGame.Lock();

        easyGame.SetInvisible();
        easyGame.Lock();

        normalGame.SetInvisible();
        normalGame.Lock();

        hardGame.SetInvisible();
        hardGame.Lock();

        godGame.SetInvisible();
        godGame.Lock();

        spaceBattle.SetInvisible();
        spaceBattle.Lock();

        groundBattle.SetInvisible();
        groundBattle.Lock();
    }

    /**
     * Обновляет состояние кнопок
     */
    private void ButtonsUpdate()
    {
        startGame.Update(touchPos);
        singleGame.Update(touchPos);
        bluetoothGame.Update(touchPos);
        campaing.Update(touchPos);
        quickBattle.Update(touchPos);
        back.Update(touchPos);
        newGame.Update(touchPos);
        loadGame.Update(touchPos);
        easyGame.Update(touchPos);
        normalGame.Update(touchPos);
        hardGame.Update(touchPos);
        godGame.Update(touchPos);
        groundBattle.Update(touchPos);
        spaceBattle.Update(touchPos);
    }

    /**
     * Обнуляет состояние кнопок
     */
    private void ButtonsReset()
    {
        startGame.Reset();
        singleGame.Reset();
        bluetoothGame.Reset();
        campaing.Reset();
        quickBattle.Reset();
        back.Reset();
        newGame.Reset();
        loadGame.Reset();
        easyGame.Reset();
        normalGame.Reset();
        hardGame.Reset();
        godGame.Reset();
        groundBattle.Reset();
        spaceBattle.Reset();
    }

    /**
     * Отрисовывает кнопки
     * @param
     */
    private void ButtonsDraw(GLRenderer renderer)
    {
        startGame.Draw(renderer);
        singleGame.Draw(renderer);
        bluetoothGame.Draw(renderer);
        campaing.Draw(renderer);
        quickBattle.Draw(renderer);
        back.Draw(renderer);
        newGame.Draw(renderer);
        loadGame.Draw(renderer);
        easyGame.Draw(renderer);
        normalGame.Draw(renderer);
        hardGame.Draw(renderer);
        godGame.Draw(renderer);
        groundBattle.Draw(renderer);
        spaceBattle.Draw(renderer);
    }
}
