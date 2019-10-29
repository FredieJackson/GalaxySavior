package pack.warshipsbattle;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;

import pack.warshipsbattle.System.*;
import pack.warshipsbattle.Units.*;

public class GameView extends SurfaceView
        implements OnTouchListener, SurfaceHolder.Callback
{
    //region Variables
    private int screenWidth;
    private int screenHeight;
    Field field;
    Field eField;
    Activity activity;

    ArrayList<Unit> army;

    private GameThread gameLoopThread;
    private Vector2 touchPos;
    private boolean isYourTurn;
    private Paint paint;

    private Bitmap test;
    private Panel selectingPanel;
    private Panel[] gates;

    //region Buttons Variables
    //private Button shootBtn;
    private Button cancelBtn;
    private Button turnBtn;
    private Button installBtn;
    private Button installationFinishBtn;
    private Button shootBtn;
    private boolean isButtonPressed;
    //endregion

    //region Unit Installation Variables
    private byte unitNum[];
    private byte selectedUnitZone;
    private boolean isInstallationComplete;
    private byte[] unitCount;
    //endregion

    //endregion

    //region Constructor
    public GameView(Activity activity)
    {
        super(activity.getApplicationContext());
        this.activity = activity;
        Initialize();
    }
    //endregion

    //region Surface Methods
    /** Уничтожение области рисования*/
    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        boolean retry = true;
        gameLoopThread.setRunning(false);
        while (retry)
        {
            try
            {
                gameLoopThread.join();
                retry = false;
            }
            catch (InterruptedException e)
            {

            }
        }
    }
    /** Создание области рисования*/
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        gameLoopThread = new GameThread(this);
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }
    /** Изменение области рисования*/
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }
    //endregion

    //region Initialization
    private void Initialize()
    {
        getHolder().addCallback(this);
        setOnTouchListener(this);

        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        test = BitmapFactory.decodeResource(getResources(), R.drawable.desertiso);
        selectingPanel = new Panel(getResources(), screenWidth - 300, 0, 300, screenHeight, (byte)0);

        gates = new Panel[2];

        for(int i = 0; i < gates.length; i++)
        {
            gates[i] = new Panel(getResources(), 0, screenHeight/2 * i, screenWidth, screenHeight/2, (byte)(3-i));
            gates[i].Move();
        }

        army = new ArrayList<Unit>();

        paint = new Paint();
        paint.setARGB(255,250,240,20);
        paint.setTextSize(40f);
        touchPos = new Vector2();

        field = new Field(getResources(), 0, 0, 15, true);
        eField = new Field(getResources(), -field.width, 0, 15, false);

        ButtonsInitialize();

        isButtonPressed = false;
        isInstallationComplete = false;
        isYourTurn = false;
        selectedUnitZone = -1;

        unitCount = new byte[6];
        unitCount[0] = 1;//6;//Robot
        unitCount[1] = 1;//4;//IFV
        unitCount[2] = 1;//3;//Engineer
        unitCount[3] = 1;//2;//Tank
        unitCount[4] = 1;//2;//Turret
        unitCount[5] = 1;//1;//SONDER

        unitNum = new byte[6];
        for(int i = 0; i < unitNum.length; i++)
        {

            if(i != 0)
                unitNum[i] = (byte)(unitNum[i - 1] + unitCount[i - 1]);
            else
                unitNum[i] = 0;
        }

        for(int i = 0; i < unitCount[0]; i++)
        {
            army.add(new Robot(getResources(), new Vector2(selectingPanel.x + 50, 50)));
        }
        for(int i = 0; i < unitCount[1]; i++)
        {
            army.add(new IFV(getResources(), new Vector2(selectingPanel.x + 50, army.get(unitNum[0]).RGetStartPosition().bottom  + 10)));
        }
        for(int i = 0; i < unitCount[2]; i++)
        {
            army.add(new Engineer(getResources(), new Vector2(selectingPanel.x + 50, army.get(unitNum[1]).RGetStartPosition().bottom  + 10)));
        }
        for(int i = 0; i < unitCount[3]; i++)
        {
            army.add(new Tank(getResources(), new Vector2(selectingPanel.x + 50, army.get(unitNum[2]).RGetStartPosition().bottom  + 10)));
        }
        for(int i = 0; i < unitCount[4]; i++)
        {
            army.add(new Turret(getResources(), new Vector2(selectingPanel.x + 50, army.get(unitNum[3]).RGetStartPosition().bottom  + 10)));
        }
        for(int i = 0; i < unitCount[5]; i++)
        {
            army.add(new SONDER(getResources(), new Vector2(selectingPanel.x + 50, army.get(unitNum[4]).RGetStartPosition().bottom  + 10)));
        }
    }
    //endregion

    //region Update
    public void Update()
    {
        if(!isInstallationComplete)
        {
            selectingPanel.Update();
            AlignArmyPosition();
        }
        if(!gates[0].isMoved)
        {
            for (int i = 0; i < gates.length; i++)
            {
                gates[i].Update();
            }
        }
    }

    public void AlignArmyPosition()
    {
        if (isInstallationComplete)
        {
            for(int i = 0; i < army.size(); i++)
            {
                if(army.get(i).pos.x < 0)
                    army.get(i).pos.x += field.width + 10;
                else
                    army.get(i).pos.x -= field.width + 10;
            }
        }
        else
        {
            if (!selectingPanel.isMoved)
            {
                for (int i = 0; i < army.size(); i++)
                {
                    if (selectedUnitZone > -1)
                    {
                        if (unitNum[selectedUnitZone] != i)
                        {
                            if (!army.get(i).isInstalled)
                                army.get(i).pos.SetValue(selectingPanel.x + 50 + selectingPanel.offsetX - army.get(i).offset.x, army.get(i).pos.y);
                        }
                    }
                    else
                    {
                        if (!army.get(i).isInstalled)
                            army.get(i).pos.SetValue(selectingPanel.x + 50 + selectingPanel.offsetX - army.get(i).offset.x, army.get(i).pos.y);
                    }
                }
                if (selectingPanel.offsetX == 0 && selectingPanel.isOpened)
                    selectingPanel.isMoved = true;
            }
        }
    }

    public void LoadingOpen()
    {
        field.Move();
        eField.Move();
        AlignArmyPosition();
        MoveGates();
    }
    public void MoveGates()
    {
        for (int i = 0; i < gates.length; i++)
        {
            gates[i].Move();
        }
    }
    //endregion

    //region onTouch
    /**
     * Обрабатывает касания
     * @param view
     * @param event
     * @return
     */
    public boolean onTouch(View view, MotionEvent event)
    {
        //Обновляем позицию касания
        touchPos.x = event.getX();
        touchPos.y = event.getY();
        //Обновляем кнопки
        ButtonsUpdate();
        //Если было совершено нажатие на экран
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //Если расстановка не закончена
            if(!isInstallationComplete)
            {
                //Пытаемся выбрать юнит
                SelectUnit();
                //Пытаемся обработать нажатия кнопок
                CheckButtons();
            }
        }
        //Если убрали палец с экрана
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            //Сбрасываем состояние кнопок
            ButtonsReset();
        }
        if(isInstallationComplete)
        {
            if(eField.IsVectorInField(touchPos) && !gates[0].isOpened)
            {
                eField.SelectSocket(touchPos, 0);
            }
            else if(field.IsVectorInField(touchPos) && !gates[0].isOpened)
            {
                field.SelectSocket(touchPos, 0);
                SelectUnit();
            }
        }
        //Если выбран юнит и расстановка не закончена
        if (selectedUnitZone > -1 && !isInstallationComplete)
        {
            //Пытаемся передвигать юнит
            MoveSelectedUnit();
        }
        return true;
    }
    //endregion

    //region Unit Installation
    /**
     * Передвигает выбраный юнит
     */
    public void MoveSelectedUnit()
    {
        //Если касание было не по кнопкам
        if (!isButtonPressed)
        {
            //Если юнит выбран
            if (selectedUnitZone > -1)
            {
                //Вектор касания смещаем на определенную величину, для удобства
                Vector2 tmp = new Vector2(touchPos.x - army.get(unitNum[selectedUnitZone]).RGetStartPosition().width() - 50 - army.get(unitNum[selectedUnitZone]).offset.x, touchPos.y - army.get(unitNum[selectedUnitZone]).RGetStartPosition().height()/2);
                //Если касанемся в пределах поля
                if(field.IsVectorInField(tmp))
                {
                    //Выделяем ячейку на поле
                    field.SelectSocket(tmp, 0);

                    //Перемещаем юнит по ячейкам
                    army.get(unitNum[selectedUnitZone]).pos.SetValue(field.selectedSocket);

                    //Проверяем помехи
                    army.get(unitNum[selectedUnitZone]).CheckPosition(field);
                }
            }
        }
    }

    /**
     * Выбор юнита
     */
    public void SelectUnit()
    {
        if(isInstallationComplete)
        {
            //Получаем локальные координаты клетки поля
            Vector2 tmp = new Vector2(field.GetLocalSocketCoord(field.selectedSocket));
            //Получаем инфу клетки поля
            byte tmpID = field.GetFieldInfo()[(int) tmp.y][(int) tmp.x];
            //Если в клетке стоит юнит
            if (tmpID > -1)
            {
                if(selectedUnitZone > -1)
                    army.get(selectedUnitZone).isSelected = false;
                selectedUnitZone = tmpID;
                army.get(tmpID).isSelected = true;
            }
        }
        else
        {
            isButtonPressed = false;
            //Если юнит не выбран
            if (selectedUnitZone < 0)
            {
                //Получаем прямоугольник касания
                Rect touchRect = new Rect((int) touchPos.x - 3, (int) touchPos.y - 3, (int) touchPos.x + 3, (int) touchPos.y + 3);
                //Пробегаем по всем текущим идам разных типов кораблей
                for (byte i = 0; i < unitNum.length; i++)
                {
                    //Если остались не выбранные корабли определенного типа и прямоугольник касания пересекает прямоугольник стартовой зоны кораблей этого типа
                    if (selectingPanel.isOpened && unitNum[i] > -1 && touchRect.intersect(army.get(unitNum[i]).RGetStartPosition()))
                    {
                        //Выделенному типу присваиваем ид этой зоны
                        selectedUnitZone = i;
                        //Задвигаем панель выбора юнитов
                        selectingPanel.Move();
                        //Устанавливаем позицию по центру поля
                        army.get(unitNum[selectedUnitZone]).pos.SetValue(field.GetGlobalSocketCoord(new Vector2(field.size / 2, field.size / 2)));
                        //Выделяем ячейку на поле
                        field.SelectSocket(new Vector2(army.get(unitNum[selectedUnitZone]).pos.x, army.get(unitNum[selectedUnitZone]).pos.y + 2), 0);
                        //Подсвечиваем юнит
                        army.get(unitNum[selectedUnitZone]).isSelected = true;

                        //Проверяем помехи
                        army.get(unitNum[selectedUnitZone]).CheckPosition(field);

                        //Пока текущий ид выделенного типа указывает на установленный юнит
                        while (army.get(unitNum[selectedUnitZone]).isInstalled)
                            //Увеличиваем текущий ид
                            unitNum[selectedUnitZone]++;
                    }
                }
                //Если юнит так и не выбран
                if (selectedUnitZone < 0)
                {
                    //Если касание было в пределах поля
                    if (field.IsVectorInField(touchPos) && touchPos.x < selectingPanel.x - 5)
                    {
                        //Выделяем клетку поля
                        field.SelectSocket(touchPos, 0);
                        //Получаем локальные координаты клетки поля
                        Vector2 tmp = new Vector2(field.GetLocalSocketCoord(field.selectedSocket));
                        //Получаем инфу клетки поля
                        byte tmpID = field.GetFieldInfo()[(int) tmp.y][(int) tmp.x];
                        //Если в клетке стоит юнит
                        if (tmpID > -1)
                        {
                            //Если меню выбора закрыто
                            if (!selectingPanel.isOpened)
                                //Открываем меню выбора
                                selectingPanel.Move();
                            //Удаляем информацию о нем с поля
                            field.DeleteUnit(army.get(tmpID));
                            //Обнуляем его позицию
                            army.get(tmpID).ResetPosition();
                            //Помечаем его как не установленный
                            army.get(tmpID).isInstalled = false;
                            //Если его ид меньше текущего ида кораблей определенного типа или установлены все корабли данного типа
                            if (tmpID < unitNum[army.get(tmpID).GetZone()] || unitNum[army.get(tmpID).GetZone()] == -1)
                                //записываем в текущий ид кораблей определенного типа значение ида юнита
                                unitNum[army.get(tmpID).GetZone()] = tmpID;
                        }
                    }
                }
            }
        }
    }

    /**
     * Устанавливает юнит на поле
     */
    public void InstallUnit()
    {
        //Если выбран юнит
        if(selectedUnitZone > -1)
        {
            //Если выделена ячейка на поле
            if (!field.selectedSocket.IsNegative()) {
                //Выравниваем позицию юнита по выделеной ячейке
                army.get(unitNum[selectedUnitZone]).pos.SetValue(field.selectedSocket);
                //Если юнит не выходит за границы поля
                if (army.get(unitNum[selectedUnitZone]).SetForm(field.selectedSocket, field, true)) {
                    //Помещаем юнит на поле
                    field.PlaceUnit(army.get(unitNum[selectedUnitZone]).GetForm(), unitNum[selectedUnitZone]);

                    //Обнуляем выделенную ячейку поля
                    field.selectedSocket.SetNegative();

                    //Помечаем юнит как установленный
                    army.get(unitNum[selectedUnitZone]).isInstalled = true;
                    army.get(unitNum[selectedUnitZone]).isSelected = false;
                    //Увеличиваем текущий ид данного типа юнитов
                    unitNum[selectedUnitZone]++;

                    byte startNum = 0;
                    //Расчитываем начальный ид юнитов данного типа
                    for (int i = 0; i < selectedUnitZone; i++)
                        startNum += unitCount[i];

                    //Если установлены все юниты
                    byte installCount = 0;
                    for(int i = startNum; i < startNum + unitCount[selectedUnitZone]; i++)
                    {
                        if(army.get(i).isInstalled)
                            installCount++;
                    }
                    if (installCount == unitCount[selectedUnitZone])
                        //Помечаем тип как установленный
                        unitNum[selectedUnitZone] = -1;

                    if(!selectingPanel.isOpened)
                        selectingPanel.Move();
                    //Обнуляем выделенный тип юнитов
                    selectedUnitZone = -1;
                }
            }
        }
    }
    //endregion

    //region Buttons Controller
    /**
     * Обрабатывает нажатия на кнопки
     */
    public void CheckButtons()
    {
        //Если нажата кнопка установки юнита
        if (installBtn.IsClicked())
        {
            if(!isInstallationComplete)
            {
                InstallUnit();
            }
            else
            {
                MoveGates();
            }
            isButtonPressed = true;
        }
        //Если нажата кнопка поворота юнита
        else if (turnBtn.IsClicked())
        {
            if (selectedUnitZone > -1)
            {
                //Поворачиваем юнит
                army.get(unitNum[selectedUnitZone]).ChangeDirection();
                //Проверяем помехи
                army.get(unitNum[selectedUnitZone]).CheckPosition(field);
            }

            isButtonPressed = true;
        }
        //Если нажата кнопка отмены выбора юнита
        else if (cancelBtn.IsClicked())
        {
            if (selectedUnitZone > -1)
            {
                //Обнуляем позицию выбранного юнита
                army.get(unitNum[selectedUnitZone]).ResetPosition();
                //Обнуляем выделенный сокет поля
                field.selectedSocket.SetNegative();
                selectedUnitZone = -1;
                if(!selectingPanel.isOpened)
                    selectingPanel.Move();
            }
            isButtonPressed = true;
        }
        //Если нажата кнопка завершения установки
        else if (installationFinishBtn.IsClicked())
        {
            if (!isInstallationComplete)
            {
                byte c = 0;
                for (int i = 0; i < unitNum.length; i++)
                {
                    if (unitNum[i] == -1)
                        c++;
                }
                if (c == unitNum.length)
                {
                    isInstallationComplete = true;
                    MoveGates();
                }
            }
            isButtonPressed = true;
        }
        else if(selectingPanel.IsCloseBtnPressed())
        {
            selectingPanel.Move();
            isButtonPressed = true;
        }
    }

    /**
     * Обновляет состояние кнопок
     */
    private void ButtonsUpdate()
    {
        selectingPanel.UpdateCloseBtn(touchPos);
        installationFinishBtn.Update(touchPos);
        if(selectedUnitZone > -1)
        {
            cancelBtn.Update(touchPos);
            turnBtn.Update(touchPos);
            installBtn.Update(touchPos);
        }
    }

    /**
     * Обнуляет состояние кнопок
     */
    private void ButtonsReset()
    {
        installationFinishBtn.Reset();
        cancelBtn.Reset();
        turnBtn.Reset();
        installBtn.Reset();
        selectingPanel.ResetCloseBtn();
    }

    /**
     * Отрисовывает кнопки
     * @param canvas
     */
    private void ButtonsDraw(Canvas canvas)
    {
        if(!isInstallationComplete)
        {
            installationFinishBtn.Draw(canvas);
            if (selectedUnitZone > -1)
            {
                cancelBtn.Draw(canvas);
                turnBtn.Draw(canvas);
                installBtn.Draw(canvas);
            }
        }
        else
        {
            if(isYourTurn)
            {
                installBtn.Draw(canvas);
            }
            else
            {

            }
        }
    }

    /**
     * Инициализирует кнопки
     */
    private void ButtonsInitialize()
    {
        cancelBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_cancel), new Vector2(10, 90), true);
        turnBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_turn), new Vector2(10, field.y + field.height - 150), true);
        installBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_install), new Vector2(10, field.y + field.height - 70), true);
        installationFinishBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_installation_finish), new Vector2(10, 10), true);
        shootBtn = new Button(BitmapFactory.decodeResource(getResources(), R.drawable.btn_shoot), new Vector2(10, 10), true);
    }
    //endregion

    //region Draw
    public void Draw(Canvas canvas)
    {
        if(canvas!=null)
        {
            canvas.drawColor(Color.argb(255, 0, 140, 240));
            if(!isInstallationComplete)
            {
                field.Draw(canvas);
                ButtonsDraw(canvas);
                for(Unit unit : army)
                {
                    if(unit.isInstalled)
                        unit.Draw(canvas);
                }
                selectingPanel.Draw(canvas);
                for(Unit unit : army)
                {
                    if(!unit.isInstalled)
                        unit.Draw(canvas);
                }
            }
            else
            {
                canvas.drawBitmap(test,0,0,null);
                if(field.x >= 0)
                    field.Draw(canvas);
                else if(eField.x >= 0)
                    eField.Draw(canvas);
                ButtonsDraw(canvas);
                for(Unit unit : army)
                {
                    if(unit.isInstalled)
                        unit.Draw(canvas);
                }
            }
            for(int i = 0; i < gates.length; i++)
            {
                gates[i].Draw(canvas);
            }
        }
    }
    //endregion

    //region Test
    private void GoToMainMenu()
    {
        gameLoopThread.setRunning(false);
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }
    //endregion
}