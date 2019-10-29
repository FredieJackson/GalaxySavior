package app.onedayofwar.UI;

import android.graphics.Rect;

import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 08.11.2014.
 */
public class Button
{
    public int x;
    public int y;
    public int width;
    public int height;
    private Rect rect;
    private Sprite image;
    private boolean isClicked;
    private boolean isLocked;
    private boolean isAnimated;
    private boolean isVisible;

    public Button(Sprite image, int x, int y, boolean isAnimated)
    {
        this.image = image;
        this.x = x;
        this.y = y;
        width = image.getWidth();
        height = image.getHeight();
        rect = new Rect(x, y, x + width, y + height);
        isClicked = false;
        isLocked = false;
        isVisible = true;
        this.isAnimated = isAnimated;
    }

    public void Draw(Graphics g)
    {
        if(isVisible)
        {
            if (isClicked && isAnimated)
                g.drawSprite(image, rect.left + 5, rect.top + 5, rect.width() - 5, rect.height() - 5, 0, 0, rect.width(), rect.height());
            else
                g.drawSprite(image, x, y);
        }
    }

    public void Flip()
    {
        image.horizontalFlip();
    }

    public void Update(Vector2 touchPos)
    {
        if(!isLocked && isVisible)
        {
            rect.set(x, y, x + width, y + height);

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

    public void SetPosition(Vector2 pos)
    {
        x = pos.x;
        y = pos.y;
    }

    public void SetPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2 GetPosition()
    {
        return new Vector2(x,y);
    }

}
