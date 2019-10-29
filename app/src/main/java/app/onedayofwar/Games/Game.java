package app.onedayofwar.Games;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;

import app.onedayofwar.Field;
import app.onedayofwar.GameView;
import app.onedayofwar.System.*;
import app.onedayofwar.Units.*;

/**
 * Created by Slava on 24.12.2014.
 */
public abstract class Game
{
    //region Variables
    public Field field;
    public Field eField;
    protected GameView gameView;
    public boolean isYourTurn;
    public ArrayList<Unit> army;
    public Unit[] drawArmySequence;
    protected int turns;

    //region Unit Installation Variables
    private byte unitNum[];
    protected byte[] unitCount;
    protected byte selectedUnitZone;
    public boolean isInstallationComplete;
    //endregion

    //endregion

    protected String testLocalView = "";

    //region Constructor
    protected Game(GameView gameView)
    {
        this.gameView = gameView;
        Initialize();
    }
    //endregion

    //region Abstract Methods
    abstract public void LoadEnemy();
    abstract public void NextTurn();
    abstract public boolean PlayerShoot();
    abstract public void EnemyShoot();
    //endregion

    //region Initialization
    protected void Initialize()
    {
        turns = 0;
        army = new ArrayList<>();

        field = new Field(gameView.getResources(), 50, 50, 15, true);
        eField = new Field(gameView.getResources(), 50, 50, 15, false);
        eField.Move();

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
            army.add(new Robot(gameView.getResources(), new Vector2(gameView.selectingPanel.x + gameView.selectingPanel.width/2 - 50, 10), 0, true));
        }
        for(int i = 0; i < unitCount[1]; i++)
        {
            army.add(new IFV(gameView.getResources(), new Vector2(gameView.selectingPanel.x + gameView.selectingPanel.width/2 - 50, army.get(unitNum[0]).GetStartPosition().bottom  + 10), 1, true));
        }
        for(int i = 0; i < unitCount[2]; i++)
        {
            army.add(new Engineer(gameView.getResources(), new Vector2(gameView.selectingPanel.x + gameView.selectingPanel.width/2 - 50, army.get(unitNum[1]).GetStartPosition().bottom  + 10), 2, true));
        }
        for(int i = 0; i < unitCount[3]; i++)
        {
            army.add(new Tank(gameView.getResources(), new Vector2(gameView.selectingPanel.x + gameView.selectingPanel.width/2 - 50, army.get(unitNum[2]).GetStartPosition().bottom  + 10), 3, true));
        }
        for(int i = 0; i < unitCount[4]; i++)
        {
            army.add(new Turret(gameView.getResources(), new Vector2(gameView.selectingPanel.x + gameView.selectingPanel.width/2 - 50, army.get(unitNum[3]).GetStartPosition().bottom  + 10), 4, true));
        }
        for(int i = 0; i < unitCount[5]; i++)
        {
            army.add(new SONDER(gameView.getResources(), new Vector2(gameView.selectingPanel.x + gameView.selectingPanel.width/2 - 50, army.get(unitNum[4]).GetStartPosition().bottom  + 10), 5, true));
        }
        drawArmySequence = new Unit[army.size()];
        for(int i = 0; i < army.size(); i++)
        {
            drawArmySequence[i] = army.get(i);
        }
        LoadEnemy();
    }
    //endregion

    //region Update
    public void Update()
    {
        if(!isInstallationComplete && !gameView.selectingPanel.isStop)
            AlignArmyPosition();

        if(gameView.IsGatesClose() && isYourTurn)
        {
            if(!isInstallationComplete)
            {
                isInstallationComplete = true;
                gameView.MoveGates();
            }
            else if(selectedUnitZone > -1)
            {
                SwapFields();
                gameView.ShootingPrepare();
                gameView.MoveGates();
            }
            else if(selectedUnitZone == -1)
            {
                SwapFields();
                EnemyShoot();
                NextTurn();
                gameView.DefendingPrepare();
                gameView.MoveGates();
            }
        }
    }
    //endregion

    //region OnTouch
    public void OnTouch(MotionEvent event)
    {
        //Если было совершено нажатие на экран
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //Если расстановка не закончена
            if(!isInstallationComplete)
            {
                //Пытаемся выбрать юнит
                SelectUnit();
            }
        }
        //Если убрали палец с экрана
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {

        }

        if(isInstallationComplete && !gameView.IsGatesClose())
        {
            if(eField.IsVectorInField(gameView.touchPos))
            {
                eField.SelectSocket(gameView.touchPos, 0);
            }
            else if(field.IsVectorInField(gameView.touchPos))
            {
                field.SelectSocket(gameView.touchPos, 0);
                SelectUnit();
            }
        }

        //Если выбран юнит и расстановка не закончена
        if (selectedUnitZone > -1 && !isInstallationComplete)
        {
            //Пытаемся передвигать юнит
            MoveSelectedUnit();
        }
    }
    //endregion

    //region Unit Installation
    public void TurnUnit()
    {
        if (selectedUnitZone > -1)
        {
            //Поворачиваем юнит
            army.get(unitNum[selectedUnitZone]).ChangeDirection();
            //Проверяем помехи
            army.get(unitNum[selectedUnitZone]).CheckPosition(field);
        }
    }

    public boolean IsUnitSelected()
    {
        return selectedUnitZone > -1;
    }

    public boolean CancelSelection()
    {
        if (selectedUnitZone > -1)
        {
            //Обнуляем позицию выбранного юнита
            army.get(unitNum[selectedUnitZone]).ResetPosition();
            //Обнуляем выделенный сокет поля
            field.selectedSocket.SetFalse();
            selectedUnitZone = -1;
            return true;
        }
        return false;
    }
    public boolean CheckInstallationFinish()
    {
        byte c = 0;
        for (int i = 0; i < unitNum.length; i++)
        {
            if (unitNum[i] == -1)
                c++;
        }
        if (c == unitNum.length)
        {
            isYourTurn = true;
            return true;
        }
        return false;
    }
    /**
     * Передвигает выбраный юнит
     */
    public void MoveSelectedUnit()
    {
        //Если касание было не по кнопкам
        if (!gameView.isButtonPressed)
        {
            //Если юнит выбран
            if (selectedUnitZone > -1)
            {
                //Вектор касания смещаем на определенную величину, для удобства
                Vector2 tmp = new Vector2(gameView.touchPos.x - army.get(unitNum[selectedUnitZone]).GetStartPosition().width() - 50 - army.get(unitNum[selectedUnitZone]).offset.x, gameView.touchPos.y - army.get(unitNum[selectedUnitZone]).GetStartPosition().height()/2);
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
        if (isInstallationComplete)
        {
            //Получаем локальные координаты клетки поля
            Vector2 tmp = new Vector2(field.GetLocalSocketCoord(field.selectedSocket));
            //Получаем инфу клетки поля
            byte tmpID = field.GetFieldInfo()[tmp.y][tmp.x];
            //Если в клетке стоит юнит
            if (tmpID > -1 && !army.get(tmpID).IsDead() && !army.get(tmpID).IsReloading())
            {
                if (selectedUnitZone > -1)
                    army.get(selectedUnitZone).isSelected = false;

                selectedUnitZone = tmpID;
                army.get(tmpID).isSelected = true;
            }
        }
        else
        {
            gameView.isButtonPressed = false;
            //Если юнит не выбран
            if (selectedUnitZone < 0)
            {
                //Получаем прямоугольник касания
                Rect touchRect = new Rect(gameView.touchPos.x - 3, gameView.touchPos.y - 3, gameView.touchPos.x + 3, gameView.touchPos.y + 3);
                //Пробегаем по всем текущим идам разных типов кораблей
                for (byte i = 0; i < unitNum.length; i++)
                {
                    //Если остались не выбранные корабли определенного типа и прямоугольник касания пересекает прямоугольник стартовой зоны кораблей этого типа
                    if (gameView.selectingPanel.isClose && gameView.selectingPanel.isStop && unitNum[i] > -1 && touchRect.intersect(army.get(unitNum[i]).GetStartPosition()))
                    {
                        //Выделенному типу присваиваем ид этой зоны
                        selectedUnitZone = i;

                        //Перемещаем в конец, чтоб перекрывал остальные юниты
                        Unit tmpUnit = drawArmySequence[drawArmySequence.length - 1];
                        byte tmpUnitNum = -1;

                        for(byte u = 0; u < drawArmySequence.length; u++)
                        {
                            if(army.get(unitNum[i]).equals(drawArmySequence[u]))
                            {
                                tmpUnitNum = u;
                                break;
                            }
                        }

                        if(tmpUnitNum == -1)
                        {
                            Toast.makeText(gameView.getContext(), "tmpUnitNum = -1", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            drawArmySequence[drawArmySequence.length - 1] = drawArmySequence[tmpUnitNum];
                            drawArmySequence[tmpUnitNum] = tmpUnit;
                        }

                        //Задвигаем панель выбора юнитов
                        gameView.selectingPanel.Move();
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

                        break;
                    }
                }
                //Если юнит так и не выбран
                if (selectedUnitZone < 0)
                {
                    //Если касание было в пределах поля
                    if (field.IsVectorInField(gameView.touchPos) && gameView.touchPos.x < gameView.selectingPanel.GetPosition().x - 5)
                    {
                        //Выделяем клетку поля
                        field.SelectSocket(gameView.touchPos, 0);
                        //Получаем локальные координаты клетки поля
                        Vector2 tmp = new Vector2(field.GetLocalSocketCoord(field.selectedSocket));
                        //Получаем инфу клетки поля
                        byte tmpID = field.GetFieldInfo()[tmp.y][tmp.x];
                        //Если в клетке стоит юнит
                        if (tmpID > -1)
                        {
                            //Если меню выбора закрыто
                            if (!gameView.selectingPanel.isClose)
                                //Открываем меню выбора
                                gameView.selectingPanel.Move();
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

    public void AlignArmyPosition()
    {
        if(isInstallationComplete)
        {
            for (Unit unit : army)
            {
                if (unit.pos.x < 0)
                    unit.pos.x += field.width + field.initX + 10;
                else
                    unit.pos.x -= field.width + field.initX + 10;
            }
        }
        else if (!gameView.selectingPanel.isStop)
        {
            for (int i = 0, j = 0; i < unitCount.length; i++)
            {
                j += unitCount[i];
                army.get(j - 1).iconOffset.x += gameView.selectingPanel.velocity;
            }
        }
    }

    public void SwapFields()
    {
        field.Move();
        eField.Move();
        AlignArmyPosition();
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
            if (!field.selectedSocket.IsFalse())
            {
                //Выравниваем позицию юнита по выделеной ячейке
                army.get(unitNum[selectedUnitZone]).pos.SetValue(field.selectedSocket);
                //Если юнит не выходит за границы поля
                if (army.get(unitNum[selectedUnitZone]).SetForm(field.selectedSocket, field, true))
                {
                    //Помещаем юнит на поле
                    field.PlaceUnit(army.get(unitNum[selectedUnitZone]).GetForm(), unitNum[selectedUnitZone]);

                    //Обнуляем выделенную ячейку поля
                    field.selectedSocket.SetFalse();

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

                    if(!gameView.selectingPanel.isClose)
                        gameView.selectingPanel.Move();
                    //Обнуляем выделенный тип юнитов
                    selectedUnitZone = -1;
                }
            }
        }
    }
    //endregion

    public void DrawUnits(Canvas canvas)
    {
        for(Unit unit : drawArmySequence)
        {
            if(!unit.pos.IsNegative(false))
                unit.Draw(canvas);
        }
        if(isInstallationComplete && field.x >= 0)
        {
            field.DrawFieldInfo(canvas);
            canvas.drawText(testLocalView, 50, 150, army.get(0).strokePaint);
        }
    }

    public void DrawFields(Canvas canvas)
    {
        if(isInstallationComplete)
        {
            if (eField.x >= 0)
                eField.Draw(canvas);
        }
        else
        {
            field.Draw(canvas);
        }
    }

    public void DrawUnitsIcons(Canvas canvas)
    {
        for(int i = 0, j = 0; i < unitCount.length; i++)
        {
            j += unitCount[i];
            army.get(j - 1).DrawIcon(canvas);
        }
    }

    //endregion
}
