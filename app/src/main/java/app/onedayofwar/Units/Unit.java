package app.onedayofwar.Units;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

import app.onedayofwar.Field;
import app.onedayofwar.System.Vector2;

abstract public class Unit
{
    //region Variables
    public Vector2 pos;
    public Vector2 offset;
    public Vector2 iconOffset;
    public boolean isInstalled;
    protected byte zoneID;

    //region Images
    protected Bitmap stroke;
    protected Bitmap icon;
    public boolean isSelected;
    protected Vector2 strokeOffset;
    protected Bitmap image;
    public boolean isRight;
    public Paint strokePaint;
    //endregion

    int accuracy;
    int power;
    int hitPoints;
    int reloadTime;
    int reload;
    int armor;
    public Vector2 iconPos;
    Vector2[] form;
    byte damagedZones;
    boolean isDead;
    protected boolean isVisible;
    //endregion

    //region Constructor
    public Unit(boolean isVisible)
    {
        this.isVisible = isVisible;
        isInstalled = false;
        isRight = false;
        isDead = false;
        isSelected = false;
        if(isVisible)
        {
            iconOffset = new Vector2();
            strokeOffset = new Vector2();
            offset = new Vector2();
            strokePaint = new Paint();
            strokePaint.setTextSize(30);
            strokeSetYellow();
        }
        damagedZones = 0;
        reload = 0;
    }
    //endregion

    //region Abstract Methods
    abstract public void Update();
    abstract public boolean SetForm(Vector2 startSocket, Field field, boolean isInstallUnit);
    abstract protected void ChangeOffset();
    abstract protected void ResetOffset();
    abstract public byte GetZone();
    //endregion

    //region Draw
    public void Draw(Canvas canvas)
    {
        DrawStroke(canvas);
        canvas.drawBitmap(image, pos.x + offset.x, pos.y + offset.y, null);
        if(reload > 0 && !isDead)
            DrawReload(canvas);
    }
    public void DrawReload(Canvas canvas)
    {
        canvas.drawText("" + reload, pos.x + offset.x + image.getWidth()/2, pos.y + offset.y + image.getHeight()/2, strokePaint);
    }

    public void DrawStroke(Canvas canvas)
    {
        if(isSelected)
        {
            canvas.drawBitmap(stroke, pos.x + offset.x + strokeOffset.x, pos.y + offset.y + strokeOffset.y, strokePaint);
        }
    }

    public void DrawIcon(Canvas canvas)
    {
        if(!isInstalled)
        {
            canvas.drawBitmap(icon, iconPos.x + iconOffset.x, iconPos.y + iconOffset.y, null);
        }
    }
    //endregion

    public void ResetPosition()
    {
        ResetOffset();
        isSelected = false;
        pos.SetValue(0, -image.getHeight());
        if(isRight)
        {
            image = ImageFlip(image);
            stroke = ImageFlip(stroke);
            isRight = false;
        }
    }

    public Rect GetStartPosition()
    {
        return new Rect(iconPos.x ,iconPos.y, iconPos.x + icon.getWidth(), iconPos.y + icon.getHeight());
    }

    protected void InitializeFormArray()
    {
        for(int i = 0; i < form.length; i++)
        {
            form[i] = new Vector2();
            form[i].SetFalse();
        }
    }

    public Vector2[] GetForm()
    {
        return form;
    }

    public void ChangeDirection()
    {
        isRight = !isRight;
        if(isVisible)
        {
            stroke = ImageFlip(stroke);
            image = ImageFlip(image);
            ChangeOffset();
        }
    }

    public void strokeSetYellow()
    {
        strokePaint.setARGB(255,255,255,0);
        strokePaint.setColorFilter(new LightingColorFilter(strokePaint.getColor(), 1));
    }
    public void strokeSetRed()
    {
        strokePaint.setARGB(255,255,0,0);
        strokePaint.setColorFilter(new LightingColorFilter(strokePaint.getColor(), 1));
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

    public void NextTurn()
    {
        if(reload > 0)
            reload--;
    }

    public boolean IsReloading()
    {
        return reload > 0;
    }

    public void Reload()
    {
        reload = reloadTime;
    }

    public boolean SetDamage(int damage)
    {
        damagedZones++;
        if(armor >= damage)
            armor -= damage;
        else
        {
            hitPoints -= damage - armor;
            armor = 0;
        }
        if(hitPoints <= 0)
        {
            isDead = true;
            return true;
        }
        else if(damagedZones == form.length)
        {
            isDead = true;
        }
        return false;
    }

    public int GetPower()
    {
        return power;
    }

    public boolean IsDead()
    {
        return isDead;
    }

    public void ResetReload()
    {
        reload = 0;
        power = power / 10;
    }
}
