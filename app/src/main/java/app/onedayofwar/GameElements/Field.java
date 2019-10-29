package app.onedayofwar.GameElements;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.Units.Unit;

public class Field
{

    //region Variables
    public int x;
    public int y;
    public int initX;
    public int initY;
    public int width;
    public int height;
    public int socketSizeX;
    public int socketSizeY;
    public int size;
    public Vector2 selectedSocket;
    public Vector2 globalSocketCoord;
    public Vector2 localSocketCoord;

    byte[][] fieldInfo;
    byte[][] shots;
    private Path selectedSocketForm;
    public Paint selectedSocketPaint;
    private boolean isIso;

    Paint testTextPaint;
    //endregion

    //region Constructor
    public Field(int x, int y, int size, boolean isIso)
    {
        initX = x;
        initY = y;

        //this.resources = resources;
        this.size = size;
        this.isIso = isIso;

        Initialize();
    }
    //endregion

    //region Initialization
    private void Initialize()
    {
        x = initX;
        y = initY;

        //Если изометрия
        if(isIso)
        {
            width = Assets.gridIso.getWidth();
            height = Assets.gridIso.getHeight();
        }
        else
        {
            width = Assets.grid.getWidth();
            height = Assets.grid.getHeight();
        }

        socketSizeX = width / size;
        socketSizeY = height / size;

        fieldInfo = new byte[size][size];
        shots = new byte[size][size];
        InitFieldInfo();

        //region SelectedSocket Paint and Path
        selectedSocket = new Vector2(-1,-1);
        selectedSocketForm = new Path();
        selectedSocketPaint = new Paint();
        //selectedSocketPaint.setARGB(250,240,20);
        selectedSocketPaint.setARGB(255,0,255,0);
        selectedSocketPaint.setAntiAlias(true);
        selectedSocketPaint.setStrokeWidth(3);
        selectedSocketPaint.setStyle(Paint.Style.STROKE);
        //endregion

        testTextPaint = new Paint();
        testTextPaint.setARGB(255,250,240,20);
        globalSocketCoord = new Vector2();
        localSocketCoord = new Vector2();
    }
    //endregion

    //region Draw
    /**
     * производит отрисовку поля
     * @param
     */
    public void Draw(Graphics g)
    {
        if(isIso)
        {
            g.drawSprite(Assets.gridIso, x, y);
        }
        else
        {
            g.drawSprite(Assets.grid, x, y);
            DrawFieldInfo(g);
            DrawSelectedSocket(g);
        }
    }
    /**
     * Отрисовывает выделенную ячейку
     * @param
     */
    private void DrawSelectedSocket(Graphics g)
    {
        if (!selectedSocket.IsFalse())
            g.drawPath(selectedSocketForm, selectedSocketPaint.getColor());
    }
    //endregion

    public void Move()
    {
        if(x < 0)
            x = initX;
        else
            x = -width - 10;
        selectedSocket.SetFalse();
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
                shots[i][j] = 0;
            }
        }

    }

    public byte GetSelectedSocketInfo()
    {
        localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
        return fieldInfo[localSocketCoord.y][localSocketCoord.x];
    }

    public byte GetSelectedSocketSign()
    {
        localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
        return shots[localSocketCoord.y][localSocketCoord.x];
    }

    public boolean IsIso()
    {
        return isIso;
    }

    public byte[][] GetFieldInfo()
    {
        return fieldInfo;
    }

    public byte[][] GetShots()
    {
        return shots;
    }

    public void SetShot(boolean isGoodShot)
    {
        localSocketCoord.SetValue(GetLocalSocketCoord(selectedSocket));
        if(isGoodShot)
            shots[localSocketCoord.y][localSocketCoord.x] = 2;
        else
            shots[localSocketCoord.y][localSocketCoord.x] = 1;
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

            fieldInfo[localSocketCoord.y][localSocketCoord.x] = (byte)unitID;

            SetRestrictedArea(localSocketCoord);
        }
    }

    /**
     * Окружить юнит запретной зоной, в которую нельзя ставить другие юниты
     * @param localCoord
     */
    private void SetRestrictedArea(Vector2 localCoord)
    {
        // Расчеты ведутся в локальных координатах
        for(int i = 0; i < 3; i++)
        {
            //проверка на выход за границу массива
            if(localCoord.x - 1 + i >= 0 && localCoord.x - 1 + i < 15)
            {
                //проверка на выход за границу массива
                if (localCoord.y - 1 >= 0)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[localCoord.y - 1][localCoord.x - 1 + i] < 0)
                        fieldInfo[localCoord.y - 1][localCoord.x - 1 + i]--;
                }
                //проверка на выход за границу массива
                if (localCoord.y + 1 < 15)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[localCoord.y + 1][localCoord.x - 1 + i] < 0)
                        fieldInfo[localCoord.y + 1][localCoord.x - 1 + i]--;
                }
            }
            //проверка на выхо
            // д за границу массива
            if(localCoord.y - 1 + i >= 0 && localCoord.y - 1 + i < 15)
            {
                //проверка на выход за границу массива
                if (localCoord.x - 1 >= 0)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[localCoord.y - 1 + i][localCoord.x - 1] < 0)
                        fieldInfo[localCoord.y - 1 + i][localCoord.x - 1]--;
                }
                //проверка на выход за границу массива
                if (localCoord.x + 1 < 15)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[localCoord.y - 1 + i][localCoord.x + 1] < 0)
                        fieldInfo[localCoord.y - 1 + i][localCoord.x + 1]--;
                }
            }
        }
    }

    /**
     * Удаление запретной зоны, в которую нельзя ставить другие юниты
     * @param form
     */
    private void DeleteRestrictedArea(Vector2[] form)
    {
        // Расчеты ведутся в локальных координатах
        for(int k = 0; k < form.length; k++)
        {
            for (int i = 0; i < 3; i++)
            {
                Vector2 tmp = GetLocalSocketCoord(form[k]);

                //проверка на выход за границу массива
                if (tmp.x - 1 + i >= 0 && tmp.x - 1 + i < 15)
                {
                    //проверка на выход за границу массива
                    if (tmp.y - 1 >= 0)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[tmp.y - 1][tmp.x - 1 + i] < -1)
                            fieldInfo[tmp.y - 1][tmp.x - 1 + i]++;
                    }

                    //проверка на выход за границу массива
                    if (tmp.y + 1 < 15)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[tmp.y + 1][tmp.x - 1 + i] < -1)
                            fieldInfo[tmp.y + 1][tmp.x - 1 + i]++;
                    }
                }
                //проверка на выход за границу массива
                if (tmp.y - 1 + i >= 0 && tmp.y - 1 + i < 15)
                {
                    //проверка на выход за границу массива
                    if (tmp.x - 1 >= 0)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[tmp.y - 1 + i][tmp.x - 1] < -1)
                            fieldInfo[tmp.y - 1 + i][tmp.x - 1]++;
                    }

                    //проверка на выход за границу массива
                    if (tmp.x + 1 < 15)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[tmp.y - 1 + i][tmp.x + 1] < -1)
                            fieldInfo[tmp.y - 1 + i][tmp.x + 1]++;
                    }
                }
            }
        }
    }

    /**
     * Удаляет юнита с поля
     * @param unit
     */
    public void DeleteUnit(Unit unit)
    {
        DeleteRestrictedArea(unit.GetForm());
        for(int i = 0; i < unit.GetForm().length; i++)
        {
            Vector2 tmp = GetLocalSocketCoord(unit.GetForm()[i]);
            fieldInfo[tmp.y][tmp.x] = -1;
        }
    }

    /**
     * Отрисовка информации о поле
     * @param
     */
    public void DrawFieldInfo(Graphics g)
    {
        if(!isIso)
        {
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                {
                    localSocketCoord.SetValue(j,i);
                    globalSocketCoord.SetValue(GetGlobalSocketCoord(localSocketCoord));

                    /*testTextPaint.setARGB(255,0,0,255);

                    if(fieldInfo[i][j] >=0)
                    {
                        testTextPaint.setARGB(255,255,0,0);
                    }
                    if(fieldInfo[i][j] == -1)
                    {
                        testTextPaint.setARGB(255,250,240,20);
                    }
                    g.drawText("" + fieldInfo[i][j], 15, globalSocketCoord.x + 5, globalSocketCoord.y + 25, testTextPaint.getColor());*/

                    switch (shots[i][j])
                    {
                        case 1:
                            g.drawSprite(Assets.signMiss, globalSocketCoord.x, globalSocketCoord.y);
                            break;
                        case 2:
                            g.drawSprite(Assets.signHit, globalSocketCoord.x, globalSocketCoord.y);
                            break;
                    }
                }
            }
        }

        //region TEST
        else
        {
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                {
                    if(shots[i][j] == 1)
                    {
                        localSocketCoord.SetValue(j,i);
                        globalSocketCoord.SetValue(GetGlobalSocketCoord(localSocketCoord));
                        g.drawSprite(Assets.signMissIso, globalSocketCoord.x - (int)(20 * Assets.gridCoeff), (int)(globalSocketCoord.y + 2 * Assets.gridCoeff));
                    }
                }
            }
        }
        //endregion
    }
    public void ClearFieldInfo()
    {
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                fieldInfo[i][j] = -1;
            }
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
        //если не изометрия
        if(!isIso)
        {
            return new Vector2((socketGlobalCoord.x - x) / socketSizeX, (socketGlobalCoord.y - y) / socketSizeY);
        }
        else
        {
            //начинаем перебор прямых с координаты верхней точки ромба
            for (int i = 0; i < size; i++)
            {
                //проверяем через функцию прямой с положительным коэффициэнтом совпадает ли у функции и у ячейки
                if (socketGlobalCoord.y == (0.5 * (socketGlobalCoord.x - (x + width / 2)) + i * socketSizeY + y))
                {
                    localSocketCoord.y = i;
                }

                //проверяем через функцию прямой с отрицательным коэффициэнтом совпадает ли у функции и у ячейки
                if (socketGlobalCoord.y == (-0.5 * (socketGlobalCoord.x - (x + width / 2)) + i * socketSizeY + y))
                {
                    localSocketCoord.x = i;
                }

                //если вычислили локальные координаты
                if (localSocketCoord.x != -1 && localSocketCoord.y != -1)
                    break;
            }
        }
        return localSocketCoord;
    }

    /**
     * Возвращает глобальные координаты ячейки поля по локальным координатам
     * @param socketLocalCoord
     * @return
     */
    public Vector2 GetGlobalSocketCoord(Vector2 socketLocalCoord)
    {
        //если не изометрия
        if(!isIso)
        {
            globalSocketCoord.SetValue(x + socketLocalCoord.x * socketSizeX, y + socketLocalCoord.y * socketSizeY);
        }
        else
        {
            globalSocketCoord.SetValue(x + width / 2 + socketSizeX / 2 * (socketLocalCoord.x - socketLocalCoord.y), y + socketSizeY / 2 * (socketLocalCoord.y + socketLocalCoord.x));
        }
        return globalSocketCoord;
    }
    public Vector2 GetGlobalSocketCoord(int localX, int localY)
    {
        //если не изометрия
        if(!isIso)
        {
            globalSocketCoord.SetValue(x + localX * socketSizeX, y + localY * socketSizeY);
        }
        else
        {
            globalSocketCoord.SetValue(x + width / 2 + socketSizeX / 2 * (localX - localY), y + socketSizeY / 2 * (localY + localX));
        }
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
        //если касание произошло в пределах поля
        if (IsVectorInField(touchPos))
        {
            //если не изометрия
            if (!isIso)
            {
                selectedSocket.x = x + ((touchPos.x - x) / socketSizeX) * socketSizeX;
                selectedSocket.y = y + ((touchPos.y - y) / socketSizeY) * socketSizeY;
            }
            else
            {
                end:for (int i = 0; i < size; i++)
                {
                    for (int j = 0; j < size; j++)
                    {
                        //проверяем касание по функции ромба для каждой ячейки
                        if (socketSizeY * Math.abs(touchPos.x + (i - j) * socketSizeX/2 - x - width/2) + socketSizeX * (touchPos.y - y - (i + j) * socketSizeY/2) <= socketSizeY * socketSizeX)
                        {
                            selectedSocket.x = x - i * socketSizeX/2 + width/2 + j * socketSizeX/2;
                            selectedSocket.y = y + i * socketSizeY/2 + j * socketSizeY/2;
                            break end;
                        }
                    }
                }
            }
        }
        //если удалось выделить ячейку, то придаем ей конкретную форму
        if(!selectedSocket.IsFalse())
            SetSelectedSocketForm(socketForm);
    }

    /**
     * Проверяет лежит ли вектор в пределах поля
     * @param vector
     * @return
     */
    public boolean IsVectorInField(Vector2 vector)
    {
        if(isIso)
        {
            if (Math.abs(0.5*(vector.x - (x + width/2))) + y <= vector.y && -Math.abs(0.5*(vector.x - (x + width/2))) + y + height >= vector.y)
                return true;
        }
        else
        {
            if (vector.x > x && vector.x < x + width && vector.y > y && vector.y < y + height)
                return true;
        }
        return false;
    }

    /**
     * Придает выделенной ячейке форму в зависимости от передаваемого параметра
     * @param socketForm
     */
    private void SetSelectedSocketForm(int socketForm)
    {
        selectedSocketForm.reset();

        float right;
        float top;
        float left;
        float bottom;

        //Если не изометрия
        if(!isIso)
        {
            switch (socketForm)
            {
                //Прямоугольник размером с ячейку
                case 0:
                    right = selectedSocket.x + socketSizeX;
                    left = selectedSocket.x;
                    top = selectedSocket.y;
                    bottom = selectedSocket.y + socketSizeY;
                    break;
                //Крест
                case 1:
                    //Vertical Rectangle
                    left = selectedSocket.x;

                    top = selectedSocket.y - socketSizeY;
                    if (top < y)
                        top = y;

                    right = selectedSocket.x + socketSizeX;

                    bottom = selectedSocket.y + 2 * socketSizeY;
                    if (bottom > y + height)
                        bottom = y + height;

                    //Horizontal Rectangle
                    float leftH = selectedSocket.x - socketSizeX;
                    if (leftH < x)
                        leftH = x;
                    float topH = selectedSocket.y;
                    float rightH = selectedSocket.x + 2 * socketSizeX;
                    if (rightH > x + width)
                        rightH = x + width;
                    float bottomH = selectedSocket.y + socketSizeY;

                    selectedSocketForm.addRect(leftH, topH, rightH, bottomH, Path.Direction.CW);
                    break;
                //Прямоугольник побольше
                case 2:
                    left = selectedSocket.x - socketSizeX;
                    if (left < x)
                        left = x;

                    top = selectedSocket.y - socketSizeY;
                    if (top < y)
                        top = y;

                    right = selectedSocket.x + 2 * socketSizeX;
                    if (right > x + width)
                        right = x + width;

                    bottom = selectedSocket.y + 2 * socketSizeY;
                    if (bottom > y + height)
                        bottom = y + height;

                    break;

                //Вертикальная линия длиной во все поле
                case 3:
                    left = x;
                    top = selectedSocket.y;
                    right = x + width;
                    bottom = selectedSocket.y + socketSizeY;
                    break;

                //Горизонтальная линия длиной во все поле
                case 4:
                    left = selectedSocket.x;
                    top = y;
                    right = selectedSocket.x + socketSizeX;
                    bottom = y + height;
                    break;

                //По дефолту прямоугольник
                default:
                    right = selectedSocket.x + socketSizeX;
                    left = selectedSocket.x;
                    top = selectedSocket.y;
                    bottom = selectedSocket.y + socketSizeY;
                    break;
            }
            selectedSocketForm.addRect(left, top, right, bottom, Path.Direction.CW);
        }
        else
        {
            switch (socketForm)
            {
                //Прямоугольник размером с ячейку
                case 0:
                    selectedSocketForm.setLastPoint(selectedSocket.x, selectedSocket.y);
                    selectedSocketForm.lineTo(selectedSocket.x + socketSizeX/2, selectedSocket.y + socketSizeY/2);
                    selectedSocketForm.lineTo(selectedSocket.x, selectedSocket.y + socketSizeY);
                    selectedSocketForm.lineTo(selectedSocket.x - socketSizeX/2, selectedSocket.y + socketSizeY/2);
                    selectedSocketForm.lineTo(selectedSocket.x, selectedSocket.y);
                    break;
            }
        }
    }
    //endregion

}



