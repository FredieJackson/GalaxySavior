package app.onedayofwar.Units;

import app.onedayofwar.GameElements.Field;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.Vector2;

/**
 * Станция управления лучом смерти
 * Размер 5
 *
 *        X   X
 * Форма    Х
 *        Х   Х
 */
public class SONDER extends Unit
{

    public SONDER(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = Assets.sonderImageL;
            icon = Assets.sonderIcon;
            stroke = Assets.sonderStroke;
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

        form = new Vector2[6];
        InitializeFormArray();

        accuracy = 100;
        power = 2000;
        hitPoints = 3000;
        armor = 500;
        reloadTime = 10;
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
        byte counter = 0;
        byte toJ = 3;
        byte toI = 4;

        if(isRight)
        {
            toJ = 4;
            toI = 3;
        }

        for(int j = 0; j < toJ; j++)
        {
            for (int i = 0; i < toI; i++)
            {
                if((!isRight && (num == 0 || num == 3 || num == 5 || num == 6 || num == 8 || num == 11)) ||
                    (isRight && (num == 0 || num == 2 || num == 4 || num == 7 || num == 9 || num == 11)))
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

                    if (field.GetFieldInfo()[tmpLocal.y][tmpLocal.x] != -1)
                        return false;

                    tmpForm[counter].SetValue(tmp);

                    counter++;
                }
                num++;
            }
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
        offset.SetValue((int)(-99 * Assets.gridCoeff), 0);
        strokeOffset.SetValue((int)(-5 * Assets.gridCoeff),(int)( -4 * Assets.gridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            offset.SetValue((int)(-76 * Assets.gridCoeff), 0);
        else
            ResetOffset();
    }

    @Override
    protected void TurnImage()
    {
        image = isRight ? Assets.sonderImageR : Assets.sonderImageL;
        stroke.horizontalFlip();
    }

    @Override
    public void Update()
    {

    }
}
