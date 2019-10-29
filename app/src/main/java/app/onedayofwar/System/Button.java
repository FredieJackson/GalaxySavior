package app.onedayofwar.System;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Created by Slava on 08.11.2014.
 */
public class Button {

    public int x;
    public int y;
    public int width;
    public int height;
    private Bitmap image;
    private boolean isClicked;
    private boolean isLocked;
    private boolean isAnimated;
    private boolean isVisible;

    public Button(Bitmap image, Vector2 position, boolean isAnimated)
    {
        this.image = image;
        x = position.x;
        y = position.y;
        width = image.getWidth();
        height = image.getHeight();
        isClicked = false;
        isLocked = false;
        isVisible = true;
        this.isAnimated = isAnimated;
    }

    public void Draw(Canvas canvas)
    {
        if(isVisible)
        {
            if (isClicked && isAnimated)
                canvas.drawBitmap(image, new Rect(0, 0, height, width), new Rect( x + 5, y + 5, x + width - 5, y + height - 5), null);
            else
                canvas.drawBitmap(image, x, y, null);
        }
    }

    public void Update(Vector2 touchPos)
    {
        if(!isLocked && isVisible)
        {
            if (touchPos.x < x + width && touchPos.x > x && touchPos.y > y && touchPos.y < y + height)
                isClicked = true;
            else
                isClicked = false;
        }
    }

    public void Reset()
    {
        isClicked = false;
    }

    public boolean IsClicked()
    {
        return isClicked;
    }

    public void ImageFlip()
    {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        image = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
    }

    public void Lock()
    {
        isLocked = true;
    }
    public void Unlock()
    {
        isLocked = false;
    }

    public void SetVisible()
    {
        isVisible = true;
    }
    public void SetInvisible()
    {
        isVisible = false;
    }

    public void SetPosition(Vector2 pos)
    {
        x = pos.x;
        y = pos.y;
    }

    public Vector2 GetPosition()
    {
        return new Vector2(x,y);
    }

}
