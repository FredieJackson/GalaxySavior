package app.onedayofwar.Graphics;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Matrix;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Slava on 13.03.2015.
 */
public class Graphics
{
    private GLRenderer renderer;
    private AssetManager assets;
    private Rectangle rectangle;
    private Line line;
    private float[] mvpMatrix;

    public Graphics(GLRenderer renderer, AssetManager assets)
    {
        this.renderer = renderer;
        this.assets = assets;
        mvpMatrix = new float[16];
        rectangle = new Rectangle();
        line  = new Line();
    }

    public Animation newAnimation(String fileName, int frames, int speed, int start, boolean isLooped)
    {
        return new Animation(LoadBitmap(fileName), frames, speed, start, isLooped);
    }

    public Sprite newSprite(String fileName)
    {
        return new Sprite(LoadBitmap(fileName));
    }

    private Bitmap LoadBitmap(String fileName)
    {
        InputStream in = null;
        Bitmap bitmap = null;
        try
        {
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        return bitmap;
    }

    public void DrawRect(float x, float y, int width, int height, int color, boolean isFilled)
    {
        rectangle.setShape(x, y, width, height, color, isFilled);
        Matrix.multiplyMM(mvpMatrix, 0, renderer.vpMatrix, 0, rectangle.matrix, 0);
        rectangle.Draw(mvpMatrix);
    }

    public void DrawLine(float xb, float yb, float xe, float ye, int color)
    {
        line.setShape(xb, yb, xe, ye, color);
        line.Draw(renderer.vpMatrix);
    }

    public void DrawSprite(Sprite sprite, float[] mMatrix)
    {
        Matrix.multiplyMM(mvpMatrix, 0, renderer.vpMatrix, 0, mMatrix, 0);
        sprite.Draw(mvpMatrix);
    }

    public void DrawAnimation(Animation animation, float[] mMatrix)
    {
        Matrix.multiplyMM(mvpMatrix, 0, renderer.vpMatrix, 0, mMatrix, 0);
        animation.Draw(mvpMatrix);
    }
}
