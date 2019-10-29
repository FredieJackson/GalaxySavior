package app.onedayofwar.Campaign.System;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayDeque;

import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Campaign.Activities.ActivityRequests;
import app.onedayofwar.Campaign.Activities.GameActivity;
import app.onedayofwar.Battle.Activities.BattleActivity;
import app.onedayofwar.Campaign.Space.PlanetController;
import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.GLRenderer;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.System.XMLParser;

/**
 * Created by Slava on 16.02.2015.
 */
public class GameView extends GLSurfaceView
    implements ScreenView, View.OnTouchListener
{

    private GameActivity activity;
    private GameThread gameThread;
    private Space space;

    private Vector2 touchPos;
    public int screenWidth;
    public int screenHeight;

    private GLRenderer renderer;
    public Paint paint;
    private int turns;

    ArrayDeque<MotionEvent> motionEvents;

    public String info = "";

    public GameView(GameActivity activity, int screenWidth, int screenHeight)
    {
        super(activity.getApplicationContext());
        this.activity = activity;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        motionEvents = new ArrayDeque<>();
        setEGLContextClientVersion(2);
        renderer = new GLRenderer(activity.getResources(), this);
        setRenderer(renderer);
    }

    public void Initialize(Graphics graphics)
    {
        getHolder().addCallback(this);
        setOnTouchListener(this);

        LoadAssets(graphics);

        turns = 0;
        space = new Space(activity, this);

        paint = new Paint();
        paint.setARGB(255, 250, 240, 20);
    }

    public void LoadAssets(Graphics graphics)
    {
        Assets.space = graphics.newSprite("campaign/space/space.jpg");
        Assets.planet = graphics.newSprite("campaign/space/planet.png");
        scaleImages();
    }

    public void scaleImages()
    {
        Assets.spaceCoeff = 1.0d * screenHeight / Assets.space.getHeight();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        motionEvents.add(event);
        return true;
    }

    public void SyncTouch()
    {
        if(motionEvents.isEmpty())
            return;
        MotionEvent event = motionEvents.poll();
        space.onTouch(event);
    }


    public void Update(float eTime)
    {
        SyncTouch();
        space.Update(eTime);
    }

    public void SetBattleResult(Intent data, int resultCode)
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
    }

    public float getCameraX()
    {
        return renderer.getCameraX();
    }

    public float getCameraY()
    {
        return renderer.getCameraY();
    }

    public void MoveCamera(Vector2 velocity)
    {
        if(getCameraX() + velocity.x > 0)
            velocity.x = -getCameraX();
        else if(screenWidth - (velocity.x + getCameraX()) > space.getWidth())
            velocity.x = -(space.getWidth() + getCameraX() - screenWidth);

        if(getCameraY() + velocity.y > 0)
            velocity.y = -getCameraY();
        else if(screenHeight - (velocity.y + getCameraY()) > space.getHeight())
            velocity.y = -(space.getHeight() + getCameraY() - screenHeight);

        renderer.moveCamera(velocity.x, velocity.y);
    }

    public void Draw(Graphics graphics)
    {
        space.Draw(graphics);
        //graphics.drawText("battle: " + info, 40, screenWidth / 2 , screenHeight / 2 + 50, Color.YELLOW);
    }
}
