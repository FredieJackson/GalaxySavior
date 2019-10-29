package app.onedayofwar.Units;

import app.onedayofwar.Field;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.Vector2;

/**
 * Танк
 * Размер 3х2
 */
public class Tank extends Unit{

    public Tank(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = Assets.tankImageL;
            icon = Assets.tankIcon;
            stroke = Assets.tankStroke;
            iconPos = new Vector2(position.x, position.y);
        }
        this.zoneID = (byte)zoneID;
        Initialize();
    }

    //region Initialization
    private void Initialize()
    {
        //для прямоугольника -78
        if(isVisible)
        {
            ResetOffset();
            pos = new Vector2(0, -image.getHeight());
        }

        form = new Vector2[6];
        InitializeFormArray();

        accuracy = 100;
        power = 1500;
        hitPoints = 2000;
        armor = 1000;
        reloadTime = 5;
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

        byte num = 0;
        byte toJ = 2;
        byte toI = 3;

        if(isRight)
        {
            toJ = 3;
            toI = 2;
        }

        for(int j = 0; j < toJ; j++)
        {
            for (int i = 0; i < toI; i++)
            {

                if(field.IsIso())
                {
                    tmp.SetValue(startSocket.x - sizes.x / 2 * (i - j), startSocket.y + sizes.y / 2 * (i + j));

                    if (-Math.abs(0.5 * (tmp.x - field.width / 2 - field.x)) + field.height + field.y - 3 < tmp.y)
                        return false;
                }
                else
                {
                    tmp.SetValue(startSocket.x + sizes.x * j, startSocket.y + sizes.y *i);

                    if (tmp.y >= field.y + field.height || tmp.x >= field.x + field.width)
                        return false;
                }

                tmpLocal = field.GetLocalSocketCoord(tmp);

                if (field.GetFieldInfo()[tmpLocal.y][
                        tmpLocal.x] != -1)
                    return false;

                tmpForm[num].SetValue(tmp);

                num++;
            }
        }

        if(isInstallUnit)
        {
            for (int i = 0; i < form.length; i++)
                form[i].SetValue(tmpForm[i]);
           // if(isVisible && isRight) stroke.horizontalFlip();
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
        //для прямоугольника -78
        offset.SetValue((int)(-69 * Assets.gridCoeff), 0);
        strokeOffset.SetValue((int)(-5 * Assets.gridCoeff),(int)( -4 * Assets.gridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            //для прямоугольника -52
            offset.SetValue((int)(-42 * Assets.gridCoeff), 0);
        else
            //для прямоугольника -78
            ResetOffset();
    }

    @Override
    protected void TurnImage()
    {
        image = isRight ? Assets.tankImageR : Assets.tankImageL;
        stroke.horizontalFlip();
    }

    @Override
    public void Update()
    {

    }
}
