package app.onedayofwar.GameElements;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.GameElements.Button;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 26.11.2014.
 */
public class Panel
{
    static public enum Type{UP, DOWN, LEFT, RIGHT}
    public int x;
    public int y;
    public int offsetX;
    public int offsetY;
    public Vector2 velocity;
    public int width;
    int height;
    public boolean isStop;
    public boolean isClose;
    private Type type;
    private Button closeBtn;
    private Bitmap image;
    Rect rect;
    Paint paint;

    //region Constructor
    public Panel(int x, int y, int width, int height, Type type)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
       /* if(openType == 3)
            image = BitmapFactory.decodeResource(res,R.drawable.gate_top);
        else if(openType == 2)
            image = BitmapFactory.decodeResource(res,R.drawable.gate_bottom);*/

        Initialize();
    }
    //endregion

    //region Initialization
    public void Initialize()
    {
        offsetX = 0;
        offsetY = 0;
        SetVelocity();
        rect = new Rect(x, y, x + width, y + height);

        if(type == Type.RIGHT)
            closeBtn = new Button(Assets.btnPanelClose, x, height / 2 - Assets.btnPanelClose.getHeight() / 2, false);

        isStop = true;
        isClose = true;
        paint = new Paint();
        paint.setARGB(255,31,31,31);
    }
    //endregion

    private void SetVelocity()
    {
        switch (type)
        {
            case LEFT:
                velocity = new Vector2(-15, 0);
                break;
            case UP:
                velocity = new Vector2(0, -10);
                break;
            case RIGHT:
                velocity = new Vector2(15, 0);
                break;
            case DOWN:
                velocity = new Vector2(0, 10);
                break;
        }
    }

    public void Update()
    {
        if(!isStop)
        {
            offsetX += velocity.x;
            offsetY += velocity.y;
            if (type == Type.RIGHT)
                closeBtn.x += velocity.x;

            switch (type)
            {
                case LEFT:
                    if (Math.abs(offsetX) >= width)
                    {
                        offsetX = -width;
                        closeBtn.x -= closeBtn.width;
                        velocity.ChangeSign();
                        closeBtn.Flip();
                        isClose = false;
                        isStop = true;

                    }
                    else if (Math.abs(offsetX) <= 0)
                    {
                        offsetX = 0;
                        velocity.ChangeSign();
                        isClose = true;
                        isStop = true;
                    }
                break;

                case RIGHT:
                    if (offsetX >= width)
                    {
                        offsetX = width;
                        closeBtn.x = x + offsetX -closeBtn.width;
                        velocity.ChangeSign();
                        closeBtn.Flip();
                        isClose = false;
                        isStop = true;

                    }
                    else if (offsetX <= 0)
                    {
                        offsetX = 0;
                        closeBtn.x = x;
                        velocity.ChangeSign();
                        isClose = true;
                        isStop = true;
                    }
                break;

                case UP:
                    if (Math.abs(offsetY) >= height)
                    {
                        offsetY = -height;
                        velocity.ChangeSign();
                        isStop = true;
                        isClose = false;
                    }
                    else if (offsetY >= 0)
                    {
                        offsetY = 0;
                        velocity.ChangeSign();
                        isStop = true;
                        isClose = true;
                    }
                break;

                case DOWN:
                    if (offsetY >= height)
                    {
                        offsetY = height;
                        velocity.ChangeSign();
                        isStop = true;
                        isClose = false;
                    }
                    else if (offsetY <= 0)
                    {
                        offsetY = 0;
                        velocity.ChangeSign();
                        isStop = true;
                        isClose = true;
                    }
                break;
            }
        }
        rect.set(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height);
    }

    public void Move()
    {
        if(type == Type.RIGHT)
        {
            if (!isClose)
            {
                closeBtn.Flip();
                closeBtn.x += closeBtn.width;
            }
        }
        isStop = false;
    }

    public Vector2 GetPosition()
    {
        return new Vector2(x + offsetX, y + offsetY);
    }

    public void Draw(Graphics g)
    {
        //if(image == null)
        if(isClose || !isStop)
            g.drawRect(rect.left, rect.top, rect.width(), rect.height(), paint.getColor());
        /*else
            canvas.drawBitmap(image, new Rect(0,0,image.getWidth(), image.getHeight()), new RectF(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height), null);*/
    }

    public void DrawButton(Graphics g)
    {
        closeBtn.Draw(g);
    }

    public boolean IsCloseBtnPressed()
    {
        return closeBtn.IsClicked();
    }

    public void UpdateCloseBtn(Vector2 touchPos)
    {
        closeBtn.Update(touchPos);
    }

    public void ResetCloseBtn()
    {
        closeBtn.Reset();
    }

    public void CloseBtnLock() { closeBtn.Lock(); }

}
