package app.onedayofwar.Units;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import app.onedayofwar.Field;
import app.onedayofwar.R;
import app.onedayofwar.System.Vector2;

/**
 * Турель
 * Размер 5
 *
 *           Х
 * Форма    ХХХ
 *           Х
 */
public class Turret extends Unit {

    public Turret(Resources resources, Vector2 position)
    {
        super();

        image = BitmapFactory.decodeResource(resources, R.drawable.unit_turret);
        stroke = BitmapFactory.decodeResource(resources, R.drawable.unit_turret_stroke);

        startPos = new Rect((int)position.x, (int)position.y, (int)position.x + image.getWidth(), (int)position.y + image.getHeight());

        Initialize();
    }

    //region Initialization
    private void Initialize()
    {
        //для прямоугольника -76
        ResetOffset();

        pos = new Vector2(startPos.left - offset.x, startPos.top - offset.y);

        form = new Vector2[5];
        InitializeFormArray();

        accuracy = 0.5f;
        power = 1.5f;
        hitPoints = 3000;
        armor = 1000;
        reloadTime = 3;
    }
    //endregion

    //region Set Form
    @Override
    public boolean SetForm(Vector2 startSocket, Field field, boolean isInstallUnit)
    {
        Vector2 tmp = new Vector2();
        Vector2 startPos = new Vector2(startSocket);
        Vector2[] tmpForm = new Vector2[form.length];
        Vector2 sizes = new Vector2(field.GetSocketsSizes());
        for(int i = 0 ; i < form.length; i++)
            tmpForm[i] = new Vector2();

        Vector2 tmpLocal;
        for(int i = 0; i < 3; i++)
        {
            tmp.SetValue(startPos.x - sizes.x * i/2 , startPos.y + sizes.y * i/2);

            if(0.5 * (tmp.x - field.width/2 - field.x) + field.height + field.y - 3 < tmp.y)
                return false;

            tmpLocal = field.GetLocalSocketCoord(tmp);

            if(field.GetFieldInfo()[(int)tmpLocal.y][(int)tmpLocal.x] != -1)
                return false;

            tmpForm[i].SetValue(tmp);
        }

        startPos.SetValue(startPos.x - sizes.x, startPos.y);

        for(int i = 3; i < form.length; i++)
        {
            tmp.SetValue(startPos.x + sizes.x * (i-3), startPos.y + sizes.y * (i-3));

            if( -0.5 * (tmp.x - field.width/2 - field.x) + field.height + field.y - 3 < tmp.y || -0.5 * (tmp.x - field.width/2 - field.x) + field.y - 3 > tmp.y)
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
    //endregion

    @Override
    public byte GetZone()
    {
        return 4;
    }

    @Override
    protected void ResetOffset()
    {
        // test (-76,0)
        offset.SetValue (-86, -7);
        strokeOffset.SetValue(-4, -4);
    }

    @Override
    protected void ChangeOffset()
    {
        if(isRight)
            offset.SetValue(-86,-7);
        else
            ResetOffset();
    }

    @Override
    public void Update()
    {

    }
}
