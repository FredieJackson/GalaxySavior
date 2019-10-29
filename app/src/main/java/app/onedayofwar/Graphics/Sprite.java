package app.onedayofwar.Graphics;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import app.onedayofwar.Graphics.Graphics.SpriteFormat;

/**
 * Created by Slava on 03.01.2015.
 */
public class Sprite
{
    Bitmap bitmap;
    SpriteFormat format;
    Matrix matrix;

    public Sprite(Bitmap bitmap, Graphics.SpriteFormat format)
    {
        this.bitmap = bitmap;
        this.format = format;
        matrix = new Matrix();
    }

    public void horizontalFlip()
    {
        matrix.setScale(-1.0f, 1.0f);
        bitmap = Bitmap.createBitmap(bitmap, 0,0, getWidth(), getHeight(), matrix, true);
    }

    public void changeSize(int newWidth, int newHeight)
    {
        Bitmap tmp = Bitmap.createBitmap(newWidth, newHeight, getConfig());
        Canvas canvas = new Canvas(tmp);
        canvas.drawBitmap(bitmap,new Rect(0,0,getWidth(),getHeight()), new Rect(0,0, newWidth, newHeight), null);
        bitmap = Bitmap.createBitmap(tmp, 0,0, newWidth, newHeight);
    }

    public void changeSize(double k)
    {
        int newWidth = (int)(getWidth() * k);
        int newHeight = (int)(getHeight() * k);
        Bitmap tmp = Bitmap.createBitmap(newWidth, newHeight, getConfig());
        Canvas canvas = new Canvas(tmp);
        canvas.drawBitmap(bitmap, new Rect(0,0,getWidth(),getHeight()), new Rect(0,0, newWidth, newHeight), null);
        bitmap = Bitmap.createBitmap(tmp, 0,0, newWidth, newHeight);
    }

    public int getWidth()
    {
        return bitmap.getWidth();
    }

    public int getHeight()
    {
        return bitmap.getHeight();
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
