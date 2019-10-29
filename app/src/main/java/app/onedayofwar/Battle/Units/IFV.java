package app.onedayofwar.Battle.Units;

import app.onedayofwar.Battle.BattleElements.Field;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.Vector2;

/**
 * Боевая машина пехоты
 * Размер 2х1
 */

public class IFV extends Unit{

    public IFV(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = Assets.ifvImageL;
            icon = Assets.ifvIcon;
            stroke = Assets.ifvStroke;
            iconPos = new Vector2(position.x, position.y);
            startPos = new Vector2(iconPos);
        }
        this.zoneID = (byte)zoneID;
        Initialize();
    }

    //region Initialization
    private void Initialize()
    {
        if(isVisible)
        {
            ResetOffset();
            pos = new Vector2(0, -image.getHeight());
        }

        form = new Vector2[2];
        InitializeFormArray();

        accuracy = 100;
        power = 5000;
        hitPoints = 500;
        armor = 500;
        reloadTime = 3;
    }
    //endregion

    @Override
    public boolean SetForm(Vector2 startSocket, Field field, boolean isInstallUnit)
    {
        Vector2 tmp = new Vector2();
        Vector2[] tmpForm = new Vector2[form.length];
        Vector2 sizes = new Vector2(field.GetSocketsSizes());

        for(int i = 0 ; i < form.length; i++)
            tmpForm[i] = new Vector2();

        Vector2 tmpLocal;

        for(int i = 0; i < form.length; i++)
        {
            if(field.IsIso())
            {
                if (isRight)
                    tmp.SetValue(startSocket.x + sizes.x * i / 2, startSocket.y + sizes.y * i / 2);
                else
                    tmp.SetValue(startSocket.x - sizes.x * i / 2, startSocket.y + sizes.y * i / 2);

                if (-Math.abs(0.5 * (tmp.x - field.width / 2 - field.x)) + field.height + field.y - 3 < tmp.y)
                    return false;
            }
            else
            {
                if (isRight)
                    tmp.SetValue(startSocket.x + sizes.x * i, startSocket.y);
                else
                    tmp.SetValue(startSocket.x, startSocket.y + sizes.y * i);

                if (tmp.y >= field.y + field.height || tmp.x >= field.x + field.width)
                    return false;
            }
            tmpLocal = field.GetLocalSocketCoord(tmp);

            if(field.GetFieldInfo()[tmpLocal.y][tmpLocal.x] != -1)
                return false;

            tmpForm[i].SetValue(tmp);
        }

        if(isInstallUnit)
        {
            for (int i = 0; i < form.length; i++)
                form[i].SetValue(tmpForm[i]);
            //if(isVisible && isRight) stroke.horizontalFlip();
        }

        return true;
    }

    @Override
    public byte GetZone()
    {
        return zoneID;
    }

    @Override
    protected void ResetOffset()
    {
        offset.SetValue((int)(-55 * Assets.isoGridCoeff),(int)( -26 * Assets.isoGridCoeff));
        strokeOffset.SetValue((int)(-5 * Assets.isoGridCoeff),(int)( -5 * Assets.isoGridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            offset.SetValue((int)(-22 * Assets.isoGridCoeff),(int)( -26 * Assets.isoGridCoeff));
        else
            ResetOffset();
    }

    @Override
    protected void TurnImage()
    {
        image = isRight ? Assets.ifvImageR : Assets.ifvImageL;
        stroke.horizontalFlip();
    }

    @Override
    public void Update()
    {

    }
}
