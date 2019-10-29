package app.onedayofwar.Units;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import app.onedayofwar.Field;
import app.onedayofwar.R;
import app.onedayofwar.System.Vector2;

/**
 * Экзо-скелет
 * Размер 1х1
 */
public class Robot extends Unit {

    public Robot(Resources resources, Vector2 position)
    {
        super();

        image = BitmapFactory.decodeResource(resources, R.drawable.unit_robot);
        stroke = BitmapFactory.decodeResource(resources, R.drawable.unit_robot_stroke);

        startPos = new Rect((int)position.x, (int)position.y, (int)position.x + image.getWidth(), (int) position.y + image.getHeight());

        Initialize();
    }

    //region Initialization
    private void Initialize()
    {
        ResetOffset();

        pos = new Vector2(startPos.left - offset.x, startPos.top - offset.y);

        form = new Vector2[1];
        InitializeFormArray();

        accuracy = 0.5f;
        power = 1.5f;
        hitPoints = 3000;
        armor = 1000;
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
            if(isRight)
                tmp.SetValue(startSocket.x + sizes.x * i/2 , startSocket.y + sizes.y * i/2);
            else
                tmp.SetValue(startSocket.x - sizes.x * i/2 , startSocket.y + sizes.y * i/2);

            if(-Math.abs(0.5 * ( tmp.x - field.width/2 - field.x)) + field.height + field.y - 3 < tmp.y)
                return false;

            tmpLocal = field.GetLocalSocketCoord(tmp);

            if(field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                return false;

            tmpForm[i].SetValue(tmp);
        }

        if(isInstallUnit)
        {
            for (int i = 0; i < form.length; i++)
                form[i].SetValue(tmpForm[i]);
        }

        return true;
    }

    @Override
    public byte GetZone()
    {
        return 0;
    }

    @Override
    protected void ResetOffset()
    {
        offset.SetValue(-25, -22);
        strokeOffset.SetValue(-5, -5);
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
