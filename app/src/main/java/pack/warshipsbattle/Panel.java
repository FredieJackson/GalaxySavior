package pack.warshipsbattle;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import pack.warshipsbattle.System.Button;
import pack.warshipsbattle.System.Vector2;

/**
 * Created by Slava on 26.11.2014.
 */
public class Panel {
    float x;
    float y;
    int offsetX;
    int offsetY;
    Vector2 velocity;
    int width;
    int height;
    boolean isMoved;
    boolean isOpened;
    byte openType;
    private Button closeBtn;
    private Bitmap image;
    Paint paint;

    //region Constructor
    public Panel(Resources res, float x, float y, int width, int height, byte openType)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.openType = openType;

        if(openType == 0)
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
        switch (openType)
        {
            case 0:
                velocity = new Vector2(20,0);
                break;
            case 1:
                velocity = new Vector2(-20,0);
                break;
            case 2:
                velocity = new Vector2(0,20);
                break;
            case 3:
                velocity = new Vector2(0,-20);
                break;
        }
    }

    public void Update()
    {
        if(!isMoved)
        {
            if(isOpened)
            {
                offsetX += velocity.x;
                offsetY += velocity.y;
                if(openType == 0)
                    closeBtn.x += velocity.x;
            }
            else
            {
                offsetX -= velocity.x;
                offsetY -= velocity.y;
                if(openType == 0)
                    closeBtn.x -= velocity.x;
            }

            if(openType < 2)
            {

                if (Math.abs(offsetX) >= width)
                {
                    if(openType == 0)
                        offsetX = width;
                    else
                        offsetX = -width;
                    closeBtn.x -= closeBtn.width;
                    isOpened = false;
                    closeBtn.ImageFlip();
                    isMoved = true;
                }
                else if (Math.abs(offsetX) <= 0)
                {
                    offsetX = 0;
                    isOpened = true;
                }
            }
            else
            {
                if (Math.abs(offsetY) >= height)
                {
                    if(openType == 2)
                    {
                        offsetY = height;
                    }
                    else
                    {
                        offsetY = -height;
                    }
                    isMoved = true;
                    isOpened = false;
                }
                else if (Math.abs(offsetY) <= 0)
                {
                    offsetY = 0;
                    isMoved = true;
                    isOpened = true;
                }
            }
        }
    }

    public void Move()
    {
        if(openType == 0)
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
        if(openType == 0)
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
