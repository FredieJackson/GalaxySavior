package app.onedayofwar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import app.onedayofwar.System.Button;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 26.11.2014.
 */
public class Panel
{
    static public enum Type{UP, DOWN, LEFT, RIGHT}
    float x;
    float y;
    int offsetX;
    int offsetY;
    Vector2 velocity;
    int width;
    int height;
    boolean isMoved;
    boolean isOpened;
    private Type type;
    private Button closeBtn;
    private Bitmap image;
    Paint paint;

    //region Constructor
    public Panel(Resources res, float x, float y, int width, int height, Type type)
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
        isMoved = true;
        isOpened = true;
        paint = new Paint();
        paint.setARGB(255,200,60,30);
    }
    //endregion

    private void SetVelocity()
    {
        switch (type)
        {
            case LEFT:
                velocity = new Vector2(-20, 0);
                break;
            case UP:
                velocity = new Vector2(0, -20);
                break;
            case RIGHT:
                velocity = new Vector2(20, 0);
                break;
            case DOWN:
                velocity = new Vector2(0, 20);
                break;
        }
    }

    public void Update()
    {
        if(!isMoved)
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
                        closeBtn.ImageFlip();
                        isOpened = false;
                        isMoved = true;

                    }
                    else if (Math.abs(offsetX) <= 0)
                    {
                        offsetX = 0;
                        velocity.ChangeSign();
                        isOpened = true;
                        isMoved = true;
                    }
                break;

                case RIGHT:
                    if (offsetX >= width)
                    {
                        offsetX = width;
                        closeBtn.x -= closeBtn.width;
                        velocity.ChangeSign();
                        closeBtn.ImageFlip();
                        isOpened = false;
                        isMoved = true;

                    }
                    else if (offsetX <= 0)
                    {
                        offsetX = 0;
                        velocity.ChangeSign();
                        isOpened = true;
                        isMoved = true;
                    }
                break;

                case UP:
                    if (Math.abs(offsetY) >= height)
                    {
                        offsetY = -height;
                        velocity.ChangeSign();
                        isMoved = true;
                        isOpened = false;
                    }
                    else if (offsetY >= 0)
                    {
                        offsetY = 0;
                        velocity.ChangeSign();
                        isMoved = true;
                        isOpened = true;
                    }
                break;

                case DOWN:
                    if (offsetY >= height)
                    {
                        offsetY = height;
                        velocity.ChangeSign();
                        isMoved = true;
                        isOpened = false;
                    }
                    else if (offsetY <= 0)
                    {
                        offsetY = 0;
                        velocity.ChangeSign();
                        isMoved = true;
                        isOpened = true;
                    }
                break;
            }
        }
    }

    public void Move()
    {
        if(type == Type.RIGHT)
        {
            if (!isOpened)
            {
                closeBtn.ImageFlip();
                closeBtn.x += closeBtn.width;
            }
        }
        isMoved = false;
    }

    public void Draw(Canvas canvas)
    {
        if(image == null)
            canvas.drawRect(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height, paint);
        else
            canvas.drawBitmap(image, new Rect(0,0,image.getWidth(), image.getHeight()), new RectF(x + offsetX, y + offsetY, x + offsetX + width, y + offsetY + height), null);
        if(type == Type.RIGHT)
            closeBtn.Draw(canvas);
    }

    public boolean IsCloseBtnPressed()
    {
        if(closeBtn.IsClicked())
            return true;
        return false;
    }

    public void UpdateCloseBtn(Vector2 touchPos)
    {
        closeBtn.Update(touchPos);
    }

    public void ResetCloseBtn()
    {
        closeBtn.Reset();
    }

}
