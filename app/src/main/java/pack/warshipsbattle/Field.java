package pack.warshipsbattle;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import pack.warshipsbattle.System.Vector2;
import pack.warshipsbattle.Units.Unit;

public class Field
{
    //region Variables
    public float x;
    public float y;
    private float scaleX;
    private float scaleY;
    public int width;
    public int height;
    int socketSizeX;
    int socketSizeY;
    int size;
    public Vector2 selectedSocket;

    private Resources resources;
    byte[][] fieldInfo;
    private Bitmap image;
    private Bitmap[] infoSigns;
    private Path selectedSocketForm;
    public Paint selectedSocketPaint;
    private boolean isIso;

    Paint testTextPaint;
    //endregion

    //region Constructor
    public Field(Resources resources, float x, float y, int size, boolean isIso)
    {
        this.x = x;
        this.y = y;
        this.resources = resources;
        this.size = size;
        this.isIso = isIso;

        Initialize();
    }
    //endregion

    //region Initialization
    private void Initialize()
    {
        //Если изометрия
        if(isIso)
            image = BitmapFactory.decodeResource(resources, R.drawable.grid_iso);
        else
            image = BitmapFactory.decodeResource(resources, R.drawable.grid);

        width = image.getWidth();
        height = image.getHeight();

        socketSizeX = width / size;
        socketSizeY = height / size;

        fieldInfo = new byte[size][size];
        infoSigns = new Bitmap[2];
        InitFieldInfo();

        //region SelectedSocket Paint and Path
        selectedSocket = new Vector2(-1,-1);
        selectedSocketForm = new Path();
        selectedSocketPaint = new Paint();
        selectedSocketPaint.setARGB(255,250,240,20);
        selectedSocketPaint.setAntiAlias(true);
        selectedSocketPaint.setStrokeWidth(3);
        selectedSocketPaint.setStyle(Paint.Style.STROKE);
        //endregion

        testTextPaint = new Paint();
        testTextPaint.setARGB(255,250,240,20);
    }
    //endregion

    //region Draw
    /**
     * производит отрисовку поля
     * @param canvas
     */
    public void Draw(Canvas canvas)
    {
        canvas.drawBitmap(image, x, y, null);
        //DrawFieldInfo(canvas);
        if(!isIso)
            DrawSelectedSocket(canvas);
    }
    /**
     * Отрисовывает выделенную ячейку
     * @param canvas
     */
    private void DrawSelectedSocket(Canvas canvas)
    {
        if (!selectedSocket.IsNegative())
            canvas.drawPath(selectedSocketForm, selectedSocketPaint);
    }
    //endregion

    //region Shooting
    public void Shoot(Vector2 shootPos)
    {
        if (fieldInfo[(int) shootPos.y][(int) shootPos.x] == 0)
            fieldInfo[(int) shootPos.y][(int) shootPos.x] = 1;

    }

    public void GetShot(Vector2 shootPos)
    {
        if (fieldInfo[(int) shootPos.y][(int) shootPos.x] == 0)
            fieldInfo[(int) shootPos.y][(int) shootPos.x] = 2;
    }

    public void Move()
    {
        if(x < 0)
            x = 0;
        else
            x = -width - 10;
        selectedSocket.SetNegative();
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
                //если не изометрия
                if(!isIso)
                    fieldInfo[i][j] = 0;
                else
                    fieldInfo[i][j] = -1;
            }
        }

        infoSigns[1] = BitmapFactory.decodeResource(resources, R.drawable.sign_hit);
        infoSigns[0] = BitmapFactory.decodeResource(resources, R.drawable.sign_miss);
    }

    public byte[][] GetFieldInfo()
    {
        return fieldInfo;
    }

    /**
     * Поместить юнит, который прошел все проверки в поле
     * @param form
     * @param unitID
     */
    public void PlaceUnit(Vector2[] form, byte unitID)
    {
        Vector2 tmp = new Vector2();
        for(int i = 0; i < form.length; i++)
        {
            //получаем локальные координаты
            tmp.SetValue(GetLocalSocketCoord(form[i]));

            fieldInfo[(int)tmp.y][(int)tmp.x] = unitID;

            SetRestrictedArea(tmp);
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
                    if (fieldInfo[(int) localCoord.y - 1][(int) localCoord.x - 1 + i] < 0)
                        fieldInfo[(int) localCoord.y - 1][(int) localCoord.x - 1 + i]--;
                }
                //проверка на выход за границу массива
                if (localCoord.y + 1 < 15)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[(int) localCoord.y + 1][(int) localCoord.x - 1 + i] < 0)
                        fieldInfo[(int) localCoord.y + 1][(int) localCoord.x - 1 + i]--;
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
                    if (fieldInfo[(int) localCoord.y - 1 + i][(int) localCoord.x - 1] < 0)
                        fieldInfo[(int) localCoord.y - 1 + i][(int) localCoord.x - 1]--;
                }
                //проверка на выход за границу массива
                if (localCoord.x + 1 < 15)
                {
                    //если в ячейке пусто, то делаем ее запретной зоной
                    if (fieldInfo[(int) localCoord.y - 1 + i][(int) localCoord.x + 1] < 0)
                        fieldInfo[(int) localCoord.y - 1 + i][(int) localCoord.x + 1]--;
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
                        if (fieldInfo[(int) tmp.y - 1][(int) tmp.x - 1 + i] < -1)
                            fieldInfo[(int) tmp.y - 1][(int) tmp.x - 1 + i]++;
                    }

                    //проверка на выход за границу массива
                    if (tmp.y + 1 < 15)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[(int) tmp.y + 1][(int) tmp.x - 1 + i] < -1)
                            fieldInfo[(int) tmp.y + 1][(int) tmp.x - 1 + i]++;
                    }
                }
                //проверка на выход за границу массива
                if (tmp.y - 1 + i >= 0 && tmp.y - 1 + i < 15)
                {
                    //проверка на выход за границу массива
                    if (tmp.x - 1 >= 0)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[(int) tmp.y - 1 + i][(int) tmp.x - 1] < -1)
                            fieldInfo[(int) tmp.y - 1 + i][(int) tmp.x - 1]++;
                    }

                    //проверка на выход за границу массива
                    if (tmp.x + 1 < 15)
                    {
                        //если в ячейке запретная зона, то делаем ее свободной
                        if (fieldInfo[(int) tmp.y - 1 + i][(int) tmp.x + 1] < -1)
                            fieldInfo[(int) tmp.y - 1 + i][(int) tmp.x + 1]++;
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
            fieldInfo[(int)tmp.y][(int)tmp.x] = -1;
        }
    }

    /**
     * Отрисовка информации о поле
     * @param canvas
     */
    private void DrawFieldInfo(Canvas canvas)
    {
        Vector2 tmp = new Vector2();
        if(!isIso)
        {
            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                {
                    tmp.SetValue(GetGlobalSocketCoord(new Vector2(j,i)));
                    switch (fieldInfo[i][j])
                    {
                        case 1:
                            canvas.drawBitmap(infoSigns[0], tmp.x, tmp.y, null);
                            break;
                        case 2:
                            canvas.drawBitmap(infoSigns[1], tmp.x, tmp.y, null);
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
                    tmp.SetValue(GetGlobalSocketCoord(new Vector2(j, i)));
                    canvas.drawText("" + fieldInfo[i][j], tmp.x - 5, tmp.y + socketSizeY/2, testTextPaint);
                }
            }
        }
        //endregion
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
        Vector2 tmp = new Vector2();
        tmp.SetNegative();
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
                    tmp.y = i;
                }

                //проверяем через функцию прямой с отрицательным коэффициэнтом совпадает ли у функции и у ячейки
                if (socketGlobalCoord.y == (-0.5 * (socketGlobalCoord.x - (x + width / 2)) + i * socketSizeY + y))
                {
                    tmp.x = i;
                }

                //если вычислили локальные координаты
                if (tmp.x != -1 && tmp.y != -1)
                    break;
            }
        }
        return tmp;
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
            return new Vector2(x + socketLocalCoord.x * socketSizeX, y + socketLocalCoord.y * socketSizeY);
        }
        else
        {
            return new Vector2(x + width / 2 + socketSizeX / 2 * (socketLocalCoord.x - socketLocalCoord.y), y + socketSizeY / 2 * (socketLocalCoord.y + socketLocalCoord.x));
        }
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
        //если не изометрия
        if(!isIso)
        {
            //если касание произошло в пределах поля
            if ((touchPos.x > x && touchPos.x < x + width) && (touchPos.y > y && touchPos.y < y + height))
            {
                selectedSocket.x = x + ((int) (touchPos.x - x) / socketSizeX) * socketSizeX;
                selectedSocket.y = y + ((int) (touchPos.y - y) / socketSizeY) * socketSizeY;
            }
        }
        else
        {
            //если касание произошло в пределах поля
            if (IsVectorInField(touchPos))
            {
                for (int i = 0; i < size; i++)
                {
                    boolean exit = false;
                    for (int j = 0; j < size; j++)
                    {
                        //проверяем касание по функции ромба для каждой ячейки
                        if (socketSizeY * Math.abs(touchPos.x + (i - j) * socketSizeX / 2 - x - width / 2) + socketSizeX * (touchPos.y - y - (i + j) * socketSizeY / 2) <= socketSizeY * socketSizeX)
                        {
                            selectedSocket.x = x - i * socketSizeX / 2 + width / 2 + j * socketSizeX / 2;
                            selectedSocket.y = y + i * socketSizeY / 2 + j * socketSizeY / 2;
                            exit = true;
                            break;
                        }
                    }
                    if (exit)
                        break;
                }
            }
            else
            {
                selectedSocket.SetNegative();
            }
        }
        //если удалось выделить ячейку, то придаем ей конкретную форму
        if(!selectedSocket.IsNegative())
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
            if (Math.abs(0.5 * (vector.x - (width / 2 + x))) + y <= vector.y && -Math.abs(0.5 * (vector.x - (width / 2 + x))) + height + y >= vector.y)
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
        if(!isIso) {

            switch (socketForm) {
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



