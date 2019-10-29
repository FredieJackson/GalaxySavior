package app.onedayofwar.Battle.BattleElements;

import android.graphics.Color;

import app.onedayofwar.Battle.Units.Unit;
import app.onedayofwar.Graphics.Animation;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

public class Field
{
    //region Variables
    private Sprite sprite;
    private Sprite signSprite;

    public int width;
    public int height;
    public int socketSizeX;
    public int socketSizeY;
    public int size;
    public Vector2 selectedSocket;
    public Vector2 globalSocketCoord;
    public Vector2 localSocketCoord;

    public Animation explodeAnimation;

    byte[][] fieldInfo;

    private int color;

    //endregion

    //region Constructor
    public Field(int x, int y, int size, double socketSize)
    {
        this.size = size;

        sprite = new Sprite(Assets.grid);
        sprite.Move(x, y);
        sprite.Scale((float)Assets.gridCoeff);

        signSprite = new Sprite(Assets.signMiss);
        signSprite.Scale((float)Assets.gridCoeff);

        Initialize();
    }
    //endregion

    //region Initialization
    private void Initialize()
    {
        width = sprite.getWidth();
        height = sprite.getHeight();

        socketSizeX = width / size;
        socketSizeY = height / size;

        fieldInfo = new byte[size][size];

        InitFieldInfo();

        selectedSocket = new Vector2(-1,-1);

        globalSocketCoord = new Vector2();
        localSocketCoord = new Vector2();


        color = Color.RED;

        if(BattlePlayer.isGround)
        {
            explodeAnimation = new Animation(Assets.explosion, 24, 100, 0, false);
        }
        else
        {
            explodeAnimation = new Animation(Assets.airExplosion, 49, 10, 0, false);
        }
        explodeAnimation.Scale((float)Assets.isoGridCoeff);
    }
    //endregion

    //region
    public void UpdateAnimation(float eTime)
    {
        explodeAnimation.Update(eTime);
    }
    //endregion

    //region Draw
    /**
     * производит отрисовку поля
     * @param
     */
    public void Draw(Graphics g)
    {
        g.DrawSprite(sprite);
        DrawSelectedSocket(g);
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public float[] getMatrix()
    {
        return sprite.matrix;
    }

    /**
     * Отрисовывает выделенную ячейку
     * @param
     */
    private void DrawSelectedSocket(Graphics g)
    {
        if (!selectedSocket.IsFalse())
           g.DrawRect(selectedSocket.x + socketSizeX/2, selectedSocket.y + socketSizeY/2, socketSizeX, socketSizeY, Color.argb(255, 0 , 255, 0), false);// g.drawPath(selectedSocketForm, selectedSocketPaint.getColor());
    }
    //endregion

    //region FieldInfo Methods
    /**
     * Заполнение начальными значениями информации о массиве, а так же инициализация картинок знаков на поле
     */
    public void InitFieldInfo()
    {
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                fieldInfo[i][j] = -1;
            }
        }

    }

    public byte GetSelectedSocketInfo()
    {
        localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
        if(localSocketCoord.IsNegative(false))
            return -1;
        return fieldInfo[(int)localSocketCoord.y][(int)localSocketCoord.x];
    }

    public byte[][] GetFieldInfo()
    {
        return fieldInfo;
    }

    /**
     * Поместить юнит, который прошел все проверки на поле
     * @param form
     * @param unitID
     */
    public void PlaceUnit(Vector2[] form, int unitID)
    {
        for(int i = 0; i < form.length; i++)
        {
            //получаем локальные координаты
            localSocketCoord.SetValue(GetLocalSocketCoord(form[i]));

            fieldInfo[(int)localSocketCoord.y][(int)localSocketCoord.x] = (byte)unitID;
        }
    }

    /**
     * Удаляет юнита с поля
     * @param unit
     */
    public void DeleteUnit(Unit unit)
    {
        for(int i = 0; i < unit.GetForm().length; i++)
        {
            Vector2 tmp = GetLocalSocketCoord(unit.GetForm()[i]);
            fieldInfo[(int)tmp.y][(int)tmp.x] = -1;
        }
    }

    //endregion

    //region Coord Converters
    /**
     * Возвращает локальные (0,1,2...size-1) координаты ячейки поля по глобальным координатам
     * @param socketGlobalCoord
     * @return
     */
    public Vector2 GetLocalSocketCoord(Vector2 socketGlobalCoord)
    {
        localSocketCoord.SetFalse();
        localSocketCoord.SetValue((int)(socketGlobalCoord.x - getMatrix()[12] + width/2) / socketSizeX, (int)(socketGlobalCoord.y - getMatrix()[13] + height/2) / socketSizeY);
        return localSocketCoord;
    }

    /**
     * Возвращает глобальные координаты ячейки поля по локальным координатам
     * @param socketLocalCoord
     * @return
     */
    public Vector2 GetGlobalSocketCoord(Vector2 socketLocalCoord)
    {
        globalSocketCoord.SetValue(getMatrix()[12] - width/2 + socketLocalCoord.x * socketSizeX, getMatrix()[13] - height/2 + socketLocalCoord.y * socketSizeY);
        return globalSocketCoord;
    }

    public Vector2 GetGlobalSocketCoord(int localX, int localY)
    {

        globalSocketCoord.SetValue(getMatrix()[12] - width/2 + localX * socketSizeX, getMatrix()[13] - height/2 + localY * socketSizeY);
        return globalSocketCoord;
    }

    /**
     * Возвращает размеры ячейки поля
     * @return
     */
    public Vector2 GetSocketsSizes()
    {
        return new Vector2(socketSizeX, socketSizeY);
    }
    //endregion

    //region SelectedSocket Methods
    /**
     * Вычисляет координаты ячейки в пределах которой было совершено касание
     * @param touchPos
     * @param socketForm
     */
    public void SelectSocket(Vector2 touchPos, int socketForm)
    {
        selectedSocket.x = getMatrix()[12] - width/2 + ((int)(touchPos.x - getMatrix()[12] + width/2) / socketSizeX) * socketSizeX;
        selectedSocket.y = getMatrix()[13] - height/2 + ((int)(touchPos.y - getMatrix()[13] + height/2) / socketSizeY) * socketSizeY;
    }
}



