package app.onedayofwar.Game.Screens;

import android.view.MotionEvent;

import app.onedayofwar.GEngine.GLRenderer;
import app.onedayofwar.GEngine.Loader;

/**
 * Created by Slava on 20.07.2015.
 */
public interface Screen
{
    public final float viewMatrix[] = new float[16];
    public void Initialize(Loader loader);
    public void Draw(GLRenderer renderer);
    public void Update(float eTime);
    public void OnTouch(MotionEvent motionEvent);
    public void Resume();
}
