package app.onedayofwar.System;

import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.UI.Button;

/**
 * Created by Slava on 29.03.2015.
 */
public class MainView implements ScreenView
{
    GLView glView;
    Vector2 touchPos;

    Button startGame;
    Button singleGame;
    Button bluetoothGame;
    Button campaing;
    Button quickBattle;
    Button back;

    public static byte startBTBattle;

    public MainView(GLView glView)
    {
        this.glView = glView;
        touchPos = new Vector2();
        startBTBattle = 0;
    }

    @Override
    public void Initialize(Graphics graphics)
    {
        float scale = 0.2f * glView.getScreenHeight() / Assets.btnStartGame.getHeight();
        startGame = new Button(Assets.btnStartGame, glView.getScreenWidth()/2, glView.getScreenHeight()/2, false);
        startGame.Scale(scale);

        bluetoothGame = new Button(Assets.btnBluetoothGame, glView.getScreenWidth()/2, glView.getScreenHeight()/2, false);
        bluetoothGame.Scale(scale);
        bluetoothGame.SetInvisible();
        bluetoothGame.Lock();

        singleGame = new Button(Assets.btnSingleGame, glView.getScreenWidth()/2, bluetoothGame.getMatrix()[13] - bluetoothGame.height - 10, false);
        singleGame.Scale(scale);
        singleGame.SetInvisible();
        singleGame.Lock();

        campaing = new Button(Assets.btnCampaing, glView.getScreenWidth()/2, bluetoothGame.getMatrix()[13] - bluetoothGame.height - 10, false);
        campaing.Scale(scale);
        campaing.SetInvisible();
        campaing.Lock();

        quickBattle = new Button(Assets.btnQuickBattle, glView.getScreenWidth()/2, glView.getScreenHeight()/2, false);
        quickBattle.Scale(scale);
        quickBattle.SetInvisible();
        quickBattle.Lock();

        back = new Button(Assets.btnBack, glView.getScreenWidth()/2, bluetoothGame.getMatrix()[13] + bluetoothGame.height + 10, false);
        back.Scale(scale);
        back.SetInvisible();
        back.Lock();
    }

    @Override
    public void Update(float eTime)
    {
        if(startBTBattle > 0)
        {
            glView.StartBattle(null, 'b', startBTBattle == 1);
            startBTBattle = 0;
        }
    }

    @Override
    public void Draw(Graphics graphics)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        ButtonsDraw(graphics);
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
        glView.getActivity().gameState = MainActivity.GameState.MENU;
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
            glView.getActivity().CheckBT();
            Log.i("MV", "BT CLICK");
        }
        else if(campaing.IsClicked())
        {
            glView.StartCampaign();
        }
        else if(quickBattle.IsClicked())
        {
            SingleBattle.difficulty = (byte)(Math.random() * 101);
            BattlePlayer.unitCount = new byte[] {2, 0, 0, 0, 0, 0};
            BattlePlayer.fieldSize = 15;
            glView.StartBattle(null, 's', Math.random() > 0.5);
        }
        else if(back.IsClicked())
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

            back.SetInvisible();
            back.Lock();
        }

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
    }

    /**
     * Отрисовывает кнопки
     * @param
     */
    private void ButtonsDraw(Graphics graphics)
    {
        startGame.Draw(graphics);
        singleGame.Draw(graphics);
        bluetoothGame.Draw(graphics);
        campaing.Draw(graphics);
        quickBattle.Draw(graphics);
        back.Draw(graphics);
    }
}
