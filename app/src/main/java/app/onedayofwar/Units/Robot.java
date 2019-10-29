package app.onedayofwar.Units;

import app.onedayofwar.GameElements.Field;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.System.Vector2;

/**
 * Экзо-скелет
 * Размер 1х1
 */
public class Robot extends Unit {

    public Robot(Vector2 position, int zoneID, boolean isVisible)
    {
        super(isVisible);

        if (isVisible)
        {
            image = Assets.robotImageL;
            icon = Assets.robotIcon;
            stroke = Assets.robotStroke;
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

        form = new Vector2[1];
        InitializeFormArray();

        accuracy = 100;
        power = 250;
        hitPoints = 500;
        armor = 0;
        reloadTime = 1;
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
                tmp.SetValue(startSocket.x - sizes.x * i/2 , startSocket.y + sizes.y * i/2);
            else
                tmp.SetValue(startSocket.x, startSocket.y);

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
        offset.SetValue((int)(-25 * Assets.gridCoeff),(int)( -22 * Assets.gridCoeff));
        strokeOffset.SetValue((int)(-5 * Assets.gridCoeff),(int)( -5 * Assets.gridCoeff));
    }

    @Override
    protected void TurnImage()
    {
        image = isRight ? Assets.robotImageR : Assets.robotImageL;
        stroke.horizontalFlip();
    }

    @Override
    protected void ChangeOffset()
    {
    }

    @Override
    public void Update()
    {

    }

}
