package app.onedayofwar.Units;

import android.graphics.Paint;
import android.graphics.Rect;

import app.onedayofwar.GameElements.Field;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

abstract public class Unit
{
    //region Variables
    public Vector2 pos;
    public Vector2 offset;
    public boolean isInstalled;
    protected byte zoneID;

    //region Images
    protected Sprite stroke;
    protected Sprite icon;
    protected Sprite image;

    public boolean isSelected;
    protected Vector2 strokeOffset;
    public boolean isRight;
    public Paint strokePaint;
    //endregion

    int accuracy;
    int power;
    int hitPoints;
    int reloadTime;
    public int reload;
    int armor;
    public Vector2 iconPos;
    Vector2[] form;
    boolean[] damagedForm;
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
    abstract protected void TurnImage();
    abstract public byte GetZone();
    //endregion

    //region Draw
    public void Draw(Graphics g)
    {
        DrawStroke(g);
        g.drawSprite(image, pos.x + offset.x, pos.y + offset.y);
        DrawDamagedZones(g);
        if(reload > 0 && !isDead)
            DrawReload(g);

    }

    public void DrawReload(Graphics g)
    {
        g.drawText("" + reload, 24, pos.x + offset.x + image.getWidth()/2, pos.y + offset.y + image.getHeight()/2, strokePaint.getColor());
    }

    public void DrawStroke(Graphics g)
    {
        if(isSelected)
        {
            g.drawSprite(stroke, pos.x + offset.x + strokeOffset.x, pos.y + offset.y + strokeOffset.y, strokePaint.getColor());
        }
    }

    public void DrawIcon(Graphics g)
    {
        if(!iconPos.IsNegative(false))
        {
            g.drawSprite(icon, iconPos.x, iconPos.y);
        }
    }

    public void DrawDamagedZones(Graphics g)
    {
        for(int i = 0; i < form.length; i++)
        {
            if(damagedForm[i])
                g.drawSprite(Assets.signFire, form[i].x - (int)(13 * Assets.gridCoeff), form[i].y - (int)(20 * Assets.gridCoeff));
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
            isRight = false;
            TurnImage();
        }
    }

    public Rect GetStartPosition()
    {
        return new Rect(iconPos.x , iconPos.y, iconPos.x + icon.getWidth(), iconPos.y + icon.getHeight());
    }

    protected void InitializeFormArray()
    {
        damagedForm = new boolean[form.length];
        for(int i = 0; i < form.length; i++)
        {
            damagedForm[i] = false;
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
            TurnImage();
            //stroke.horizontalFlip();
            //image.horizontalFlip();
            ChangeOffset();
        }
    }

    public void strokeSetYellow()
    {
        strokePaint.setARGB(255,255,255,0);
        Graphics.setColorFilter(strokePaint.getColor());
    }
    public void strokeSetRed()
    {
        strokePaint.setARGB(255,255,0,0);
        Graphics.setColorFilter(strokePaint.getColor());
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

    public void Select()
    {
        isSelected = true;
        if(isRight) stroke.horizontalFlip();
    }

    public void Deselect()
    {
        isSelected = false;
        if(isRight) stroke.horizontalFlip();
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
        reload = reloadTime + 1;
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
        /*for(int i = 0; i < form.length; i++)
        {
            if(form[i].Equals(damagedZone))
            {
                damagedForm[i] = true;
                break;
            }
        }*/
        return false;
    }

    public void UpdateDamagedZones(Vector2 damagedZone)
    {
        if(isDead)
        {
            for(int i = 0; i < form.length; i++)
            {
                damagedForm[i] = true;
            }
        }
        else
        {
            for (int i = 0; i < form.length; i++)
            {
                if (form[i].Equals(damagedZone))
                {
                    damagedForm[i] = true;
                    break;
                }
            }
        }
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
        power = power / 2;
    }
}
