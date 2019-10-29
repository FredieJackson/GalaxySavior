package app.onedayofwar.Graphics;

import android.content.res.Resources;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Slava on 08.01.2015.
 */
public class GLRenderer implements GLSurfaceView.Renderer
{
    private Resources res;
    private Graphics graphics;
    private ScreenView screenView;

    private long startTime;
    private long sleepTime;
    private float eTime = 0.016f;

    private boolean isLoaded;

    final float[] vpMatrix = new float[16];
    private final float[] projectionMatrix = new float[16];
    final float[] viewMatrix = new float[16];


    public GLRenderer(Resources res, ScreenView screenView)
    {
        this.res = res;
        this.screenView = screenView;
        isLoaded = false;
        Log.i("TEST", "RENDERER CREATED");
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        Log.i("TEST", "SURFACE CREATED");
        if(!isLoaded)
        {
            graphics = new Graphics(this, res.getAssets());
            screenView.Initialize(graphics);
            isLoaded = true;
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        Log.i("TEST", "SURFACE CHANGED");

        GLES20.glViewport(0, 0, width, height);

        Matrix.orthoM(projectionMatrix, 0, 0, width, height, 0, 1, -1);

        for (int i = 0; i < 4; i++)
        {
            Log.i("PLANET", projectionMatrix[0 + i * 4] + " | " + projectionMatrix[1 + i * 4] + " | " + projectionMatrix[2 + i * 4]  + " | " + projectionMatrix[3 + i * 4]);
        }
        Log.i("PLANET", "---");

        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f);

        for (int i = 0; i < 4; i++)
        {
            Log.i("PLANET", viewMatrix[0 + i * 4] + " | " + viewMatrix[1 + i * 4] + " | " + viewMatrix[2 + i * 4]  + " | " + viewMatrix[3 + i * 4]);
        }
        Log.i("PLANET", "---");

        GLES20.glClearColor(0.12f, 0.56f, 1, 1);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        startTime = System.currentTimeMillis();
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        for (int i = 0; i < 4; i++)
        {
            Log.i("PLANET", vpMatrix[0 + i * 4] + " | " + vpMatrix[1 + i * 4] + " | " + vpMatrix[2 + i * 4]  + " | " + vpMatrix[3 + i * 4]);
        }
        Log.i("PLANET", "---");
        screenView.Update(eTime);
        screenView.Draw(graphics);
        sleepTime = 15 - (int)((System.currentTimeMillis() - startTime));
        if(sleepTime > 0)
        {
            try
            {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException e)
            {

            }
        }
        eTime = (System.currentTimeMillis() - startTime) / 1000f;
    }

    public static int loadShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public float getCameraX()
    {
        return viewMatrix[12];
    }

    public float getCameraY()
    {
        return viewMatrix[13];
    }

    public void moveCamera(float dx, float dy)
    {
        Matrix.translateM(viewMatrix, 0, dx, dy, 0);
    }

}
