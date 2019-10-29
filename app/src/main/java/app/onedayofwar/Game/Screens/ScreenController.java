package app.onedayofwar.Game.Screens;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayDeque;

import app.onedayofwar.GEngine.GLRenderer;
import app.onedayofwar.GEngine.GLSurface;
import app.onedayofwar.GEngine.Loader;


/**
 * Created by Slava on 20.07.2015.
 */
public class ScreenController implements View.OnTouchListener
{
    private GLSurface glSurface;
    public int screenWidth;
    public int screenHeight;
    private ArrayDeque<MotionEvent> motionEventsList;
    private ArrayDeque<Screen> screensHistory;
    private Screen currentScreen;

    public ScreenController()
    {
        motionEventsList = new ArrayDeque<>();
        screensHistory = new ArrayDeque<>();
    }

    public void Initialize(GLSurface glSurface, Loader loader, int screenWidth, int screenHeight)
    {
        this.glSurface = glSurface;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        currentScreen = new MainScreen(glSurface.getContext(), this);
        currentScreen.Initialize(loader);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        motionEventsList.add(event);
        return true;
    }

    public void SyncTouch()
    {
        if(motionEventsList.isEmpty()) return;
        MotionEvent event = motionEventsList.poll();
        if(event == null) return;

        currentScreen.OnTouch(event);
    }

    public void Update()
    {
        SyncTouch();
        currentScreen.Update(0);
    }

    public void Draw(GLRenderer renderer)
    {
        renderer.prepareMatrices(currentScreen.viewMatrix);
        currentScreen.Draw(renderer);
    }

}
