package app.onedayofwar.GEngine;

import android.app.Activity;
import android.opengl.GLSurfaceView;

import app.onedayofwar.Game.Screens.ScreenController;

/**
 * Created by Slava on 17.02.2015.
 */
public class GLSurface extends GLSurfaceView
{
    private GLRenderer renderer;
    private ScreenController screenController;

    public GLSurface(Activity activity, ScreenController screenController)
    {
        super(activity);
        setEGLContextClientVersion(2);
        renderer = new GLRenderer(this);
        setRenderer(renderer);
        this.screenController = screenController;
    }

    public void Initialize(Loader loader, int width, int height)
    {
        screenController.Initialize(this, loader, width, height);
        setOnTouchListener(screenController);
    }

    public void Update()
    {
        screenController.Update();
    }

    public void Draw(GLRenderer renderer)
    {
        screenController.Draw(renderer);
    }
}
