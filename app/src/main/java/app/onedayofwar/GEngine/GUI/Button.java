package app.onedayofwar.GEngine.GUI;

import android.graphics.Color;
import android.graphics.RectF;

import app.onedayofwar.GEngine.GLRenderer;
import app.onedayofwar.GEngine.Meshes.Sprite;
import app.onedayofwar.GEngine.Text.Texture;
import app.onedayofwar.Utils.Vector2;

/**
 * Created by Slava on 08.11.2014.
 */
public class Button
{
    public int width;
    public int height;
    private RectF rect;
    private boolean isClicked;
    private boolean isLocked;
    private boolean isAnimated;
    private boolean isVisible;
    private Sprite image;

    public Button(Texture texture, float x, float y, boolean isAnimated)
    {
        image = new Sprite(texture);
        image.setPosition(x, y);
        width = image.getWidth();
        height = image.getHeight();
        rect = new RectF(x - width/2, y - height/2, x + width/2, y + height/2);
        isClicked = false;
        isLocked = false;
        isVisible = true;
        this.isAnimated = isAnimated;
    }

    public void Draw(GLRenderer renderer)
    {
        if(isVisible)
        {
            renderer.DrawSprite(image);
            /*if (isClicked && isAnimated)
                g.drawSprite(image, rect.left + 5, rect.top + 5, rect.width() - 5, rect.height() - 5, 0, 0, rect.width(), rect.height());
            else
                g.drawSprite(image, x, y);*/

        }
    }

    public void Flip()
    {
        //image.Scale(-1, 1);
    }

    public void Update(Vector2 touchPos)
    {
        if(!isLocked && isVisible)
        {
            rect.set(image.matrix[12] - width/2, image.matrix[13] - height/2, image.matrix[12] + width/2, image.matrix[13] + height/2);

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
        image.setColorFilter(Color.argb(255, 85, 85, 85));
    }

    public void Unlock()
    {
        isLocked = false;
        image.removeColorFilter();
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
        image.setPosition(x, y);
    }

    public void Scale(float s)
    {
        image.Scale(s);
        width = image.getWidth();
        height = image.getHeight();
    }

    public float[] getMatrix()
    {
        return image.matrix;
    }

    public void SetColorFilter(int color)
    {
        image.setColorFilter(color);
    }

    public void RemoveColorFilter()
    {
        image.removeColorFilter();
    }

}
