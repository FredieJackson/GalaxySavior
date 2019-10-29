package app.onedayofwar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import app.onedayofwar.System.Button;
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
    public int velocity;
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
    public Panel(Resources res, int x, int y, int width, int height, Type type)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;

        if(type == Type.RIGHT)
        {
            Bitmap btnImg = BitmapFactory.decodeResource(res, R.drawable.btn_panel_close);
            closeBtn = new Button(btnImg, new Vector2(x, height / 2 - btnImg.getHeight() / 2), false);
        }

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
        isStop = true;
        isClose = true;
        paint = new Paint();
        paint.setARGB(255,200,60,30);
    }
    //endregion

    private void SetVelocity()
    {
        switch (type)
        {
            case LEFT:
                velocity = -1;
                break;
            case UP:
                velocity = -1;
                break;
            case RIGHT:
                velocity = 1;
                break;
            case DOWN:
                velocity = 1;
                break;
        }
    }

    public void Update()
    {
        if(!isStop)
        {
            if(type == Type.DOWN || type == Type.UP)
                offsetY += velocity;
            else
                offsetX += velocity;


            switch (type)
            {
                case LEFT:
                    if (Math.abs(offsetX) >= width)
                    {
                        offsetX = -width;
                        closeBtn.x -= closeBtn.width;
                        velocity = -velocity;
                        closeBtn.ImageFlip();
                        isClose = false;
                        isStop = true;

                    }
                    else if (Math.abs(offsetX) <= 0)
                    {
                        offsetX = 0;
                        velocity = - velocity;
                        isClose = true;
                        isStop = true;
                    }
                break;

                case RIGHT:
                    closeBtn.x += velocity;

                    if (offsetX >= width)
                    {
                        offsetX = width;
                        closeBtn.x = x + offsetX - closeBtn.width;
                        velocity = -velocity;
                        closeBtn.ImageFlip();
                        isClose = false;
                        isStop = true;
                    }
                    else if (offsetX <= 0)
                    {
                        offsetX = 0;
                        closeBtn.x = x;
                        velocity = -velocity;
                        isClose = true;
                        isStop = true;
                    }
                break;

                case UP:
                    if (Math.abs(offsetY) >= height)
                    {
                        offsetY = -height;
                        velocity = -velocity;
                        isStop = true;
                        isClose = false;
                    }
                    else if (offsetY >= 0)
                    {
                        offsetY = 0;
                        velocity = -velocity;
                        isStop = true;
                        isClose = true;
                    }
                break;

                case DOWN:
                    if (offsetY >= height)
                    {
                        offsetY = height;
                        velocity = -velocity;
                        isStop = true;
                        isClose = false;
                    }
                    else if (offsetY <= 0)
                    {
                        offsetY = 0;
                        velocity = -velocity;
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
                closeBtn.ImageFlip();
                closeBtn.x += closeBtn.width;
            }
        }
        isStop = false;
    }

    public Vector2 GetPosition()
    {
        return new Vector2(x + offsetX, y + offsetY);
    }

    public void Draw(Canvas canvas)
    {
        //if(image == null)
        if(isClose || !isStop)
            canvas.drawRect(rect, paint);
        /*else
            canvas.drawBitmap(image, new Rect(0,0,image.getWidth(), image.getHeight()), new RectF(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height), null);*/
        if(type == Type.RIGHT)
            closeBtn.Draw(canvas);
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
