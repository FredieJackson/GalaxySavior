package app.onedayofwar.Battle.Units;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.Matrix;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Graphics.*;
import app.onedayofwar.System.Vector2;

abstract public class Unit
{
    //region Variables
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
    //endregion

    protected int accuracy;
    protected int power;
    protected int hitPoints;
    protected int reloadTime;
    protected int armor;
    public int reload;

    Vector2[] form;
    boolean[] damagedForm;
    byte damagedZones;
    boolean isDead;
    protected boolean isVisible;
    protected RectF bounds;

    public float[] matrix;
    public float[] iconMatrix;

    //private Animation fire;
    //endregion

    //region Constructor
    public Unit(boolean isVisible, Vector2 position)
    {
        this.isVisible = isVisible;
        isInstalled = false;
        isRight = false;
        isDead = false;
        isSelected = false;
        if(isVisible)
        {
            matrix = new float[16];
            Matrix.setIdentityM(matrix, 0);
            Matrix.scaleM(matrix, 0, (float)Assets.isoGridCoeff, -(float)Assets.isoGridCoeff, 1);

            iconMatrix = new float[16];
            Matrix.setIdentityM(iconMatrix, 0);
            Matrix.translateM(iconMatrix, 0, position.x, position.y, 0);
            Matrix.scaleM(iconMatrix, 0, (float)Assets.iconCoeff, -(float)Assets.iconCoeff, 1);

            strokeOffset = new Vector2();
            offset = new Vector2();

            bounds = new RectF();
            //fire = new Animation(16, 50, Assets.fire.getWidth(), Assets.fire.getHeight(), true, 4);
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
    public void Draw(Graphics graphics)
    {
        DrawStroke(graphics);

        matrix[12] -= offset.x;
        matrix[13] -= offset.y;
        graphics.DrawSprite(image, matrix);
        matrix[12] += offset.x;
        matrix[13] += offset.y;

        DrawDamagedZones(graphics);
        if (reload > 0 && !isDead)
            DrawReload(graphics);

        //g.drawRect(bounds.left, bounds.top, bounds.width(), bounds.height(), Color.GREEN, false);
        //g.drawRect(pos.x + offset.x, pos.y + offset.y, image.getWidth(), image.getHeight(), Color.RED, false);
    }

    public void DrawReload(Graphics graphics)
    {
        //g.drawText("" + reload, 24, pos.x + offset.x + image.getWidth()/2, pos.y + offset.y + image.getHeight()/2, strokePaint.getColor());
    }

    public void DrawStroke(Graphics graphics)
    {
        if(isSelected)
        {
             matrix[12] -= offset.x;
             matrix[13] -= offset.y;
             graphics.DrawSprite(stroke, matrix);
             matrix[12] += offset.x;
             matrix[13] += offset.y;
            //g.drawSprite(stroke, pos.x + offset.x + strokeOffset.x, pos.y + offset.y + strokeOffset.y, strokePaint.getColor());
        }
    }

    public void DrawIcon(Graphics graphics)
    {
       /* if(!iconPos.IsNegative(false))
        {
            //g.drawSprite(icon, iconPos.x, iconPos.y);
        }*/
        graphics.DrawSprite(icon, iconMatrix);
    }

    public void DrawDamagedZones(Graphics graphics)
    {
        for(int i = 0; i < form.length; i++)
        {
            if(damagedForm[i])
            {
                //fire.SetPos(form[i].x - (int) (20 * Assets.isoGridCoeff), form[i].y - (int) (30 * Assets.isoGridCoeff));
                //g.drawSprite(Assets.fire, fire.GetDstRect(), fire.GetSrcRect());
            }
        }

    }
    //endregion

    public void UpdateAnimation(float eTime)
    {
        //if(damagedZones != 0)
            //fire.Update(eTime);
    }

    public float getWidth()
    {
        return Math.abs(matrix[0])*image.getWidth();
    }

    public float getHeight()
    {
        return Math.abs(matrix[5])*image.getHeight();
    }

    public void ResetPosition()
    {
        ResetOffset();
        strokeSetYellow();
        isSelected = false;
        matrix[12] = 0;
        matrix[13] = -getHeight();

        if(isRight)
        {
            isRight = false;
            TurnImage();
        }
    }

    public RectF GetIconPosition()
    {
        return new RectF(iconMatrix[12] - Math.abs(iconMatrix[0])*icon.getWidth()/2, iconMatrix[13] - Math.abs(iconMatrix[5])*icon.getHeight()/2, iconMatrix[12] + Math.abs(iconMatrix[0])*icon.getWidth()/2, iconMatrix[13] +  Math.abs(iconMatrix[5])*icon.getHeight()/2);
    }

    public RectF GetBounds()
    {
        return bounds;
    }

    public void UpdateBounds()
    {
        bounds.set(matrix[12] - offset.x - (int)(getWidth() * 0.4), matrix[13] - offset.y - (int)(getHeight() * 0.4), matrix[12] - offset.x + (int)(getWidth() * 0.4), matrix[13] - offset.y + (int)(getHeight() * 0.4));
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
            ChangeOffset();
        }
    }

    public void strokeSetYellow()
    {
        stroke.setColorFilter(Color.argb(255,255,255,0));
    }
    public void strokeSetRed()
    {
        stroke.setColorFilter(Color.argb(255,255, 0,0));
    }

    public void CheckPosition(Field field)
    {
        //Если помех для юнита нет
        if(SetForm(field.selectedSocket, field, false))
            //Подсвечиваем желтым
            strokeSetYellow();
        else
            //Подсвечиваем красным
            strokeSetRed();
    }

    public void SetPosition(Vector2 position)
    {
        matrix[12] = position.x;
        matrix[13] = position.y;
    }

    public void Select()
    {
        isSelected = true;
    }

    public void Deselect()
    {
        isSelected = false;
    }

    public void NextTurn()
    {
        if(reload > 0)
            reload--;
        else
        {
            if(isVisible)
                image.removeColorFilter();
        }
    }

    public boolean IsReloading()
    {
        return reload > 0;
    }

    public void Reload()
    {
        reload = reloadTime + 1;
        if(isVisible)
            image.setColorFilter(Color.MAGENTA);
    }

    public boolean SetDamage(int damage)
    {
        damagedZones++;
        //if(isVisible)
        //    fire.Start();
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
            if(isVisible)
                image.setColorFilter(Color.RED);
            return true;
        }
        else if(damagedZones == form.length)
        {
            if(isVisible)
                image.setColorFilter(Color.RED);
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
        if(isVisible)
            image.removeColorFilter();
        power = power / 2;
    }
}
