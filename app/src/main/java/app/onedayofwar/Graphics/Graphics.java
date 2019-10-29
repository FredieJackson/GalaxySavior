package app.onedayofwar.Graphics;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Slava on 03.01.2015.
 */
public class Graphics
{
    public static enum SpriteFormat {RGB565, ARGB4444, ARGB8888}
    AssetManager assets;
    public Bitmap frameBuffer;
    int width;
    int height;
    Canvas canvas;
    Paint paint;
    static ColorFilter colorFilter;
    Rect srcRect;
    Rect dstRect;

    public Graphics(AssetManager assets, int width, int height)
    {
        this.assets = assets;
        this.frameBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(frameBuffer);
        this.width = width;
        this.height = height;
        paint = new Paint();
        dstRect = new Rect();
        srcRect = new Rect();
    }

    public Sprite newSprite(String fileName, SpriteFormat format)
    {
        Bitmap.Config config;

        if (format == SpriteFormat.RGB565)
            config = Bitmap.Config.RGB_565;
        else if (format == SpriteFormat.ARGB4444)
            config = Bitmap.Config.ARGB_4444;
        else
            config = Bitmap.Config.ARGB_8888;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;
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

        if (bitmap.getConfig() == Bitmap.Config.RGB_565)
            format = SpriteFormat.RGB565;
        else if (bitmap.getConfig() == Bitmap.Config.ARGB_4444)
            format = SpriteFormat.ARGB4444;
        else
            format = SpriteFormat.ARGB8888;
        return new Sprite(bitmap, format);
    }

    static public void setColorFilter(int color)
    {
        colorFilter = new LightingColorFilter(color, 1);
    }

    public void clear(int color)
    {
        canvas.drawRGB((color & 0xff0000) >> 16, (color & 0xff00) >> 8, (color & 0xff));
    }

    public void drawPixel(int x, int y, int color)
    {
        paint.setColor(color);
        canvas.drawPoint(x, y, paint);
    }

    public void drawText(String text, int size, int x, int y, int color)
    {
        paint.setColor(color);
        paint.setTextSize(size);
        canvas.drawText(text, x, y, paint);
    }

    public void drawLine(int x, int y, int x2, int y2, int color)
    {
        paint.setColor(color);
        canvas.drawLine(x, y, x2, y2, paint);
    }

    public void drawRect(int x, int y, int width, int height, int color)
    {
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width , y + height, paint);
    }

    public void drawSprite(Sprite sprite, int x, int y, int width, int height, int srcX, int srcY, int srcWidth, int srcHeight)
    {
        srcRect.left = srcX;
        srcRect.top = srcY;
        srcRect.right = srcX + srcWidth;
        srcRect.bottom = srcY + srcHeight;
        dstRect.left = x;
        dstRect.top = y;
        dstRect.right = x + width;
        dstRect.bottom = y + height;
        canvas.drawBitmap(sprite.bitmap, srcRect, dstRect, null);
    }

    public void drawSprite(Sprite sprite, Rect dst, Rect src)
    {
        canvas.drawBitmap(sprite.bitmap, src, dst, null);
    }

    public void drawSprite(Sprite sprite, Matrix matrix)
    {
        canvas.drawBitmap(sprite.bitmap, matrix, null);
    }

    public void drawSprite(Sprite sprite)
    {
        canvas.drawBitmap(sprite.bitmap, null, canvas.getClipBounds(), null);
    }

    public void drawSprite(Sprite sprite, int x, int y)
    {
        canvas.drawBitmap(sprite.bitmap, x, y, null);
    }

    public void drawSprite(Sprite sprite, int x, int y, int color)
    {
        paint.setColor(color);
        paint.setColorFilter(colorFilter);
        canvas.drawBitmap(sprite.bitmap, x, y, paint);
        paint.setColorFilter(null);
    }

    public void drawPath(Path path, int color)
    {
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        canvas.drawPath(path, paint);
        paint.reset();
    }

    public int getWidth()
    {
        return width;//frameBuffer.getWidth();
    }

    public int getHeight()
    {
        return height;//frameBuffer.getHeight();
    }
}
