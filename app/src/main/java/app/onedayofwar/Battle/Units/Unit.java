package app.onedayofwar.Battle.Units;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Graphics.Animation;
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
    protected Rect bounds;
    public Vector2 startPos;

    private Animation fire;
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
            bounds = new Rect();
            strokePaint = new Paint();
            strokePaint.setTextSize(30);
            strokeSetYellow();
            fire = new Animation(16, 50, Assets.fire.getWidth(), Assets.fire.getHeight(), true, 4);
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

        //g.drawRect(bounds.left, bounds.top, bounds.width(), bounds.height(), Color.GREEN, false);
        //g.drawRect(pos.x + offset.x, pos.y + offset.y, image.getWidth(), image.getHeight(), Color.RED, false);
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
            {
                fire.SetPos(form[i].x - (int) (20 * Assets.isoGridCoeff), form[i].y - (int) (30 * Assets.isoGridCoeff));
                g.drawSprite(Assets.fire, fire.GetDstRect(), fire.GetSrcRect());
            }
        }

    }
    //endregion

    public void UpdateAnimation(float eTime)
    {
        if(damagedZones != 0)
            fire.Update(eTime);
    }

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

    public Rect GetIconPosition()
    {
        return new Rect(iconPos.x , iconPos.y, iconPos.x + icon.getWidth(), iconPos.y + icon.getHeight());
    }

    public Vector2 GetStartPosition()
    {
        return startPos;
    }

    public Rect GetBounds()
    {
        return bounds;
    }

    public void UpdateBounds()
    {
        bounds.set((int)(image.getWidth()*0.1) + pos.x + offset.x, (int)(image.getHeight()*0.1) + pos.y + offset.y, (int)(image.getWidth()*0.1) + pos.x + offset.x + (int)(image.getWidth()*0.8), (int)(image.getHeight()*0.1) + pos.y + offset.y + (int)(image.getHeight()*0.8));
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
        if(isVisible)
            fire.Start();
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
