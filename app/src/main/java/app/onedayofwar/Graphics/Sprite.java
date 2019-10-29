package app.onedayofwar.Graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import app.onedayofwar.Graphics.Graphics.SpriteFormat;
import app.onedayofwar.System.Matrix3;

/**
 * Created by Slava on 03.01.2015.
 */
public class Sprite
{
    public Bitmap bitmap;
    private SpriteFormat format;
    private Matrix3 matrix;
    private int width;
    private int height;

    public Sprite(Bitmap bitmap, Graphics.SpriteFormat format)
    {
        this.bitmap = bitmap;
        this.format = format;
        matrix = new Matrix3();
        width = bitmap.getWidth();
        height = bitmap.getHeight();
    }

    public void horizontalFlip()
    {
        matrix.Scale(-1, 1);
        //bitmap = Bitmap.createBitmap(bitmap, 0,0, getWidth(), getHeight(), matrix, true);
    }

    public void changeSize(int newWidth, int newHeight)
    {
        matrix.Scale(newWidth*1f/width, newHeight*1f/height);
        width = newWidth;
        height = newHeight;
        /*Bitmap tmp = Bitmap.createBitmap(newWidth, newHeight, getConfig());
        Canvas canvas = new Canvas(tmp);
        canvas.drawBitmap(bitmap,new Rect(0,0,getWidth(),getHeight()), new Rect(0,0, newWidth, newHeight), null);
        bitmap = Bitmap.createBitmap(tmp, 0,0, newWidth, newHeight);*/
    }

    public void changeSize(double k)
    {
        matrix.Scale((float)k);
        /*int newWidth = (int)(getWidth() * k);
        int newHeight = (int)(getHeight() * k);
        Bitmap tmp = Bitmap.createBitmap(newWidth, newHeight, getConfig());
        Canvas canvas = new Canvas(tmp);
        canvas.drawBitmap(bitmap, new Rect(0,0,getWidth(),getHeight()), new Rect(0,0, newWidth, newHeight), null);
        bitmap = Bitmap.createBitmap(tmp, 0,0, newWidth, newHeight);*/
    }

    public Matrix3 GetMatrix()
    {
        return matrix;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public Graphics.SpriteFormat getFormat()
    {
        return format;
    }

    public void dispose()
    {
        bitmap.recycle();
    }

    public Bitmap.Config getConfig()
    {
        switch(format)
        {
            case RGB565:
                return Bitmap.Config.RGB_565;
            case ARGB4444:
                return Bitmap.Config.ARGB_4444;
            case ARGB8888:
                return Bitmap.Config.ARGB_8888;
        }
        return Bitmap.Config.ARGB_4444;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }
}
