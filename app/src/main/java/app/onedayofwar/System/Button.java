package app.onedayofwar.System;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Created by Slava on 08.11.2014.
 */
public class Button {

    public float x;
    public float y;
    public int width;
    public int height;
    private Rect rect;
    private Bitmap image;
    private boolean isClicked;
    private boolean isLocked;
    private boolean isAnimated;

    public Button(Bitmap image, Vector2 position, boolean isAnimated)
    {
        this.image = image;
        x = position.x;
        y = position.y;
        width = image.getWidth();
        height = image.getHeight();
        rect = new Rect((int)x, (int)y, (int)x + width,(int)y + height);
        isClicked = false;
        isLocked = false;
        this.isAnimated = isAnimated;
    }

    public void Draw(Canvas canvas)
    {
        if(isClicked && isAnimated)
            canvas.drawBitmap(image, new Rect(0,0,height,width), new Rect((int)x + 5,(int)y + 5, (int)x + width - 5,(int)y + height - 5), null);
        else
            canvas.drawBitmap(image, x, y, null);
    }

    public void Update(Vector2 touchPos)
    {
        if(!isLocked)
        {
            rect = new Rect((int)x,(int)y, (int)x + width,(int)y +height);
            if (new Rect((int) touchPos.x - 2, (int) touchPos.y - 2, (int) touchPos.x + 2, (int) touchPos.y + 2).intersect(rect))
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
}
