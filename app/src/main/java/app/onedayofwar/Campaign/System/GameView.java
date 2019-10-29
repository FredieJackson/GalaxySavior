package app.onedayofwar.Campaign.System;

import android.content.Intent;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayDeque;

import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.GLRenderer;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.Graphics.TextFont;
import app.onedayofwar.System.GLView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.System.XMLParser;

/**
 * Created by Slava on 16.02.2015.
 */
public class GameView implements ScreenView
{
    private Space space;

    private Vector2 touchPos;

    private GLView glView;
    public Paint paint;
    private int turns;

    public String info = "";

    public GameView(GLView glView)
    {
        this.glView = glView;
    }

    public void Initialize(Graphics graphics)
    {
        turns = 0;
        space = new Space(glView.getActivity(), this);

        paint = new Paint();
        paint.setARGB(255, 250, 240, 20);
    }

    public void OnTouch(MotionEvent event)
    {
        //if(event.getAction() == MotionEvent.ACTION_DOWN)
          //  renderer.changeScreen(new TestScreen(renderer));
        space.onTouch(event);
    }

    @Override
    public void Resume()
    {
        glView.getActivity().gameState = MainActivity.GameState.CAMPAIGN;
    }


    public void Update(float eTime)
    {
        space.Update(eTime);
    }

   /* public void SetBattleResult(Intent data, int resultCode)
    {
        if(resultCode == activity.RESULT_OK)
        {
            String damage = data.getStringExtra("result");
            info = new String(resultCode == activity.RESULT_OK ? damage : "Leave");
            space.SetBattleResult(damage);
        }
        else
        {

        }
    }*/

    public void MoveCamera(Vector2 velocity)
    {
        if(glView.getCameraX() + velocity.x > Assets.btnRegion.getWidth())
            velocity.x = -glView.getCameraX() + Assets.btnRegion.getWidth();
        else if(glView.getScreenWidth() - (velocity.x + glView.getCameraX()) > space.getWidth())
            velocity.x = -(space.getWidth() + glView.getCameraX() - glView.getScreenWidth());

        if(glView.getCameraY() + velocity.y > 0)
            velocity.y = -glView.getCameraY();
        else if(glView.getScreenHeight() - (velocity.y + glView.getCameraY()) > space.getHeight())
            velocity.y = -(space.getHeight() + glView.getCameraY() - glView.getScreenHeight());

        glView.moveCamera(velocity.x, velocity.y);
    }

    public void Draw(Graphics graphics)
    {
        space.Draw(graphics);
        //graphics.drawText("battle: " + info, 40, screenWidth / 2 , screenHeight / 2 + 50, Color.YELLOW);
    }

    public GLView getGlView()
    {
        return glView;
    }
}
