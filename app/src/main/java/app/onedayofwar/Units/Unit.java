package app.onedayofwar.Units;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import app.onedayofwar.Field;
import app.onedayofwar.System.Vector2;

abstract public class Unit
{
    //region Variables
    public Vector2 pos;
    public Vector2 offset;
    public boolean isInstalled;

    //region Images
    protected Bitmap stroke;
    public boolean isSelected;
    protected Vector2 strokeOffset;
    protected Bitmap image;
    public boolean isRight;
    protected Paint strokePaint;
    //endregion

    float accuracy;
    float power;
    int hitPoints;
    int reloadTime;
    int armor;
    protected Rect startPos;
    Vector2[] form;
    private Paint testPaintW;
    private Paint testPaintY;
    //endregion

    //region Constructor
    public Unit()
    {
        strokeOffset = new Vector2();
        offset = new Vector2();
        isInstalled = false;
        isRight = false;
        strokePaint = new Paint();
        isSelected = false;
        testPaintW = new Paint();
        testPaintW.setARGB(255,255,255,255);
        testPaintW.setStyle(Paint.Style.STROKE);
        testPaintW.setStrokeWidth(3);
        testPaintY = new Paint();
        testPaintY.setARGB(255,255,255,0);
        testPaintY.setStyle(Paint.Style.STROKE);
        testPaintY.setStrokeWidth(3);
        strokeSetYellow();
    }
    //endregion

    //region Abstract Methods
    abstract public void Update();
    abstract public boolean SetForm(Vector2 startSocket, Field field, boolean isInstallUnit);
    abstract protected void ChangeOffset();
    abstract protected void ResetOffset();
    abstract public byte GetZone();
    //endregion

    public void Draw(Canvas canvas)
    {
        if(isSelected)
            canvas.drawBitmap(stroke, pos.x + offset.x + strokeOffset.x, pos.y + offset.y + strokeOffset.y, strokePaint);
        canvas.drawBitmap(image, pos.x + offset.x, pos.y + offset.y, null);
    }

    public void ResetPosition()
    {
        ResetOffset();
        isSelected = false;
        pos.SetValue(startPos.left - offset.x, startPos.top - offset.y);
        if(isRight)
        {
            image = ImageFlip(image);
            stroke = ImageFlip(stroke);
            isRight = false;
        }
    }

    public Vector2 VGetStartPosition()
    {
        return new Vector2(startPos.left - offset.x, startPos.top - offset.y);
    }

    public Rect RGetStartPosition()
    {
        return new Rect(startPos);
    }

    protected void InitializeFormArray()
    {
        for(int i = 0; i < form.length; i++)
        {
            form[i] = new Vector2();
            form[i].SetNegative();
        }
    }

    public Vector2[] GetForm()
    {
        return form;
    }

    public void ChangeDirection()
    {
        if (isRight)
            isRight = false;
        else
            isRight = true;
        stroke = ImageFlip(stroke);
        image = ImageFlip(image);
        ChangeOffset();
    }

    public void strokeSetYellow()
    {
        ColorFilter filter = new LightingColorFilter(Color.argb(255,255,255,0), 1);
        strokePaint.setARGB(255,255,255,0);
        strokePaint.setColorFilter(filter);
    }
    public void strokeSetRed()
    {
        ColorFilter filter = new LightingColorFilter(Color.argb(255,255,0,0), 1);
        strokePaint.setARGB(255,255,0,0);
        strokePaint.setColorFilter(filter);
    }

    public Bitmap ImageFlip(Bitmap src)
    {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public void CheckPosition(Field field)
    {
        //Если помех для юнита нет
        if(SetForm(pos, field, false))
            //Подсвечиваем желтым
            strokeSetYellow();
        else
            //Подсвечиваем красным
            strokeSetRed();
    }
}
