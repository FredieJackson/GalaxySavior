package app.onedayofwar.OldBattle.Units.Space;

import app.onedayofwar.OldBattle.BattleElements.Field;
import app.onedayofwar.OldBattle.Units.Unit;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 21.03.2015.
 */
/* Корабль Хищная птица
Форма   X
       XXX
 */
public class BirdOfPrey extends Unit {

    public BirdOfPrey(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if(isVisible)
        {
            image = new Sprite(Assets.birdOfPreyImage);
            image.Scale((float)Assets.isoGridCoeff);

            icon = new Sprite(Assets.birdOfPreyIcon);
            icon.setPosition(position.x, position.y);
            icon.Scale((float)Assets.iconCoeff);

            stroke = new Sprite(Assets.birdOfPreyStroke);
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

        form = new Vector2[4];
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
        Vector2 socket = new Vector2();
        Vector2[] tmpForm = new Vector2[form.length];
        Vector2 sizes = field.GetSocketsSizes();
        Vector2 tmpLocal;

        for(int i = 0 ; i < form.length; i++)
            tmpForm[i] = new Vector2();

        if(field.IsIso())
        {
            if (!isRight)
            {
                socket.SetValue(startSocket.x, startSocket.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if (field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;

                tmpForm[0].SetValue(socket);
                socket.SetValue(startSocket.x + sizes.x/2, startSocket.y-sizes.y/2);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if (Math.abs(0.5 *(socket.x -  field.getMatrix()[12])) + field.getMatrix()[13] - field.height/2 - 3 >= socket.y || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;

                tmpForm[1].SetValue(socket);
                socket.SetValue(startSocket.x - sizes.x / 2, startSocket.y - sizes.y / 2);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if (Math.abs(0.5 *( socket.x - field.getMatrix()[12])) + field.getMatrix()[13] - field.height/2 - 3 >= socket.y || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;

                tmpForm[2].SetValue(socket);
                socket.SetValue(startSocket.x + sizes.x / 2, startSocket.y + sizes.y / 2);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if (-Math.abs(0.5 * (socket.x - field.getMatrix()[12])) + field.height/2 + field.getMatrix()[13] - 3 <= socket.y || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;

                tmpForm[3].SetValue(socket);
            }
            else
            {
                socket.SetValue(startSocket.x, startSocket.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if (field.GetFieldInfo()[(int) field.GetLocalSocketCoord(socket).x][(int) field.GetLocalSocketCoord(socket).y] != -1)
                    return false;
                tmpForm[0].SetValue(socket);
                socket.SetValue(startSocket.x - sizes.x/2, startSocket.y + sizes.y/2);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if (-Math.abs(0.5 *( socket.x -  field.getMatrix()[12])) + field.height/2 + field.getMatrix()[13] - 3 <= socket.y  || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[1].SetValue(socket);
                socket.SetValue(startSocket.x +  sizes.x / 2, startSocket.y - sizes.y / 2);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if (Math.abs(0.5 *( socket.x - field.getMatrix()[12])) + field.getMatrix()[13] - field.height/2 - 3 >= socket.y || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[2].SetValue(socket);
                socket.SetValue(startSocket.x + sizes.x / 2, startSocket.y + sizes.y / 2);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if (-Math.abs(0.5 * (socket.x - field.getMatrix()[12])) + field.height/2 + field.getMatrix()[13] - 3 <= socket.y || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[3].SetValue(socket);
            }
        }

        else
        {
            if(!isRight)
            {
                socket.SetValue(startSocket.x, startSocket.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if(field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[0].SetValue(socket);
                socket.SetValue(startSocket.x - sizes.x, startSocket.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if(socket.x <= field.getMatrix()[12] || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[1].SetValue(socket);
                socket.SetValue(startSocket.x + sizes.x, startSocket.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if(socket.x >= field.getMatrix()[12] + field.width || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[2].SetValue(socket);
                socket.SetValue(startSocket.x, startSocket.y - sizes.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if(socket.y <= field.getMatrix()[13] || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[3].SetValue(socket);
            }
            else
            {
                socket.SetValue(startSocket.x, startSocket.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if(field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[0].SetValue(socket);
                socket.SetValue(startSocket.x, startSocket.y - sizes.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if(socket.y <= field.getMatrix()[13] || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[1].SetValue(socket);
                socket.SetValue(startSocket.x + sizes.x, startSocket.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if(socket.x >= field.getMatrix()[12] + field.width || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[2].SetValue(socket);
                socket.SetValue(startSocket.x, startSocket.y - sizes.y);
                tmpLocal = field.GetLocalSocketCoord(socket);
                if(socket.y <= field.getMatrix()[13] || field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                    return false;
                tmpForm[3].SetValue(socket);
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
        offset.SetValue((int)(7 * Assets.isoGridCoeff), (int)(-24 * Assets.isoGridCoeff));
        strokeOffset.SetValue((int)(-4 * Assets.isoGridCoeff),(int)(-4 * Assets.isoGridCoeff));
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            offset.SetValue((int)(-41 * Assets.isoGridCoeff),(int)(-55 * Assets.isoGridCoeff));
        else
            ResetOffset();
    }

    @Override
    public void Update()
    {

    }
}
