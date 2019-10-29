package app.onedayofwar.OldBattle.Units.Space;

import app.onedayofwar.OldBattle.BattleElements.Field;
import app.onedayofwar.OldBattle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 23.03.2015.
 */

/* Корабль Дефеинт
Форма
    ХХ
    ХХ
    ХХ
Количество на поле 1шт.
 */
public class Defaint extends Unit {
    public Defaint(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = new Sprite(Assets.defaintImage);
            image.Scale((float)Assets.isoGridCoeff);

            icon = new Sprite(Assets.defaintIcon);
            icon.setPosition(position.x, position.y);
            icon.Scale((float)Assets.iconCoeff);

            stroke = new Sprite(Assets.defaintStroke);
            stroke.Scale((float)Assets.isoGridCoeff);
        }

        this.zoneID = (byte)zoneID;
        Initialize();
    }

    //region Initialization
    private void Initialize()
    {
        if(isVisible)
        {
            ResetPosition();
        }

        form = new Vector2[6];
        InitializeFormArray();

        accuracy = 100;
        power = 7500;
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
        Vector2 sizes = field.GetSocketsSizes();

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

                    if (-Math.abs(0.5 * (tmp.x - field.getMatrix()[12])) + field.height/2 + field.getMatrix()[13] - 3 < tmp.y)
                        return false;
                }
                else
                {
                    tmp.SetValue(startSocket.x + sizes.x * j, startSocket.y + sizes.y *i);

                    if (tmp.y >= field.getMatrix()[13] + field.height/2 || tmp.x >= field.getMatrix()[12] + field.width/2)
                        return false;
                }

                tmpLocal = field.GetLocalSocketCoord(tmp);

                if (field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
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
        offset.SetValue((int)(41 * Assets.isoGridCoeff), (int)(-68 * Assets.isoGridCoeff));
        strokeOffset.SetValue((int)(-4 * Assets.isoGridCoeff),(int)(-4 * Assets.isoGridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            offset.SetValue((int)(-29 * Assets.isoGridCoeff),(int)(-70 * Assets.isoGridCoeff));
        else
            ResetOffset();
    }

    @Override
    public void Update()
    {

    }
}
