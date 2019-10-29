package app.onedayofwar.UI;

import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.Matrix;
import android.util.Log;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 08.11.2014.
 */
public class Button
{
    public int width;
    public int height;
    private RectF rect;
    private Sprite image;
    private boolean isClicked;
    private boolean isLocked;
    private boolean isAnimated;
    private boolean isVisible;
    public float[] matrix;

    public Button(Sprite image, int x, int y, boolean isAnimated)
    {
        this.image = image;
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        Matrix.translateM(matrix, 0, x, y, 0);
        Matrix.scaleM(matrix, 0, (float)Assets.btnCoeff, -(float)Assets.btnCoeff, 1);
        width = (int)(image.getWidth() * Assets.btnCoeff);
        height = (int)(image.getHeight() * Assets.btnCoeff);
        rect = new RectF(x - width/2, y - height/2, x + width/2, y + height/2);
        isClicked = false;
        isLocked = false;
        isVisible = true;
        this.isAnimated = isAnimated;
    }

    public void Draw(Graphics graphics)
    {
        if(isVisible)
        {
            graphics.DrawSprite(image, matrix);
            /*if (isClicked && isAnimated)
                g.drawSprite(image, rect.left + 5, rect.top + 5, rect.width() - 5, rect.height() - 5, 0, 0, rect.width(), rect.height());
            else
                g.drawSprite(image, x, y);*/

        }
    }

    public void Flip()
    {
        Matrix.scaleM(matrix, 0, -1, 1, 1);
    }

    public void Update(Vector2 touchPos)
    {
        if(!isLocked && isVisible)
        {
            rect.set(matrix[12] - width/2, matrix[13] - height/2, matrix[12] + width/2, matrix[13] + height/2);

            if (touchPos.x > rect.left - 5 && touchPos.x < rect.right + 5 && touchPos.y > rect.top - 5 && touchPos.y < rect.bottom + 5)
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

    public void SetPosition(float x, float y)
    {
        matrix[12] = x;
        matrix[13] = y;
    }

}
