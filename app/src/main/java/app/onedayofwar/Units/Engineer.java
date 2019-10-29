package app.onedayofwar.Units;

import app.onedayofwar.Field;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.Vector2;

/**
 * Машина инжинеров
 * Размер 3х1
 */

public class Engineer extends Unit {

    public Engineer(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = Assets.engineerImageL;
            icon = Assets.engineerIcon;
            stroke = Assets.engineerStroke;
            iconPos = new Vector2(position.x, position.y);
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

        form = new Vector2[3];
        InitializeFormArray();

        accuracy = 100;
        power = 750;
        hitPoints = 1000;
        armor = 500;
        reloadTime = 4;
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
            if(isVisible && isRight) stroke.horizontalFlip();
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
        offset.SetValue((int)(-70 * Assets.gridCoeff),0);
        strokeOffset.SetValue((int)(-4 * Assets.gridCoeff),(int)(-4 * Assets.gridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            offset.SetValue((int)(-15 * Assets.gridCoeff),0);
        else
            ResetOffset();
    }

    @Override
    protected void TurnImage()
    {
        image = isRight ? Assets.engineerImageR : Assets.engineerImageL;
        stroke.horizontalFlip();
    }

    @Override
    public void Update()
    {

    }
}
