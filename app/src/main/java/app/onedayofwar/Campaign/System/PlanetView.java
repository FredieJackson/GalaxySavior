package app.onedayofwar.Campaign.System;

import android.graphics.Color;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.Battle.BattleElements.BattleEnemy;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Campaign.CharacterControl.TechMSG;
import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Campaign.Space.Space;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.GLView;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.UI.Button;

/**
 * Created by Slava on 30.03.2015.
 */
public class PlanetView implements ScreenView
{
    private enum State {MAIN, BUILD, FACTORY, WORKSHOP, UNIT_TRANSACTION, RESOURCES_TRANSACTION, CHOISE_ARMY_TYPE}
    private State state;
    private GLView glView;
    private Space space;
    private Planet planet;
    private Button attackBtn;
    private Button exitBtn;
    private Button buildInfo;
    private Button showFactory;
    private Button resourcesMenuBtn;
    private Button unitMenuBtn;
    private Button exportBtn;
    private Button importBtn;
    private Button selectOil;
    private Button selectCredits;
    private Button selectNanosteel;
    private Button groundArmyBtn;
    private Button spaceArmyBtn;
    private Button[] upgradeBuilding;
    private Button[] createUnit;
    private Vector2 touchPos;
    private Sprite unit;
    private UpgradeSystem upgradeSystem;
    private float buildInfoTextSize;
    private int createType;
    private byte selectedObject;
    //0-none; 1-ground; 2-space;
    private byte unitCreationState;

    public PlanetView(GLView glView, Space space, Planet planet)
    {
        this.glView = glView;
        this.planet = planet;
        this.space = space;
    }

    @Override
    public void Initialize(Graphics graphics)
    {
        glView.setCamera(0, 0);
        state = State.MAIN;
        selectedObject = 0;
        unitCreationState = 0;
        boolean isClear = true;
        if(!planet.IsConquered())
        {
            for(int i = 0; i < planet.getGroundGuards().length; i++)
            {
                if(planet.getGroundGuards()[i] > 0 || planet.getSpaceGuards()[i] > 0)
                    isClear = false;
            }
            if(isClear)
            {
                if(TechMSG.isAILand)
                    planet.AntiConquerPlanet();
                else if (TechMSG.isPlayerLand)
                {
                    planet.ConquerPlanet();
                }

            }
            else if(planet.isSpaceArmyHere())
            {
                Log.i("BATTLE", "START");
                BattlePlayer.fieldSize = 15;
                BattlePlayer.isGround = false;
                BattlePlayer.unitCount = space.getPlayer().getSArmy().clone();
                SingleBattle.difficulty = (byte)(BattlePlayer.level);
                glView.StartBattle(planet, 'c', Math.random() < 0.5);
                BattleEnemy.haveGround = planet.isGroundArmyHere();
            }
        }
        createUnit = new Button[6]; //Сначала космические потом земные
        upgradeBuilding = new Button[5];
        touchPos = new Vector2();
        float unitCoeff = glView.getScreenWidth() * 0.9f /(Assets.robotImage.getWidth() + Assets.ifvImage.getWidth() + Assets.rocketImage.getWidth() + Assets.tankImage.getWidth() + Assets.turretImage.getWidth() + Assets.sonderImage.getWidth() + 5 * 30);
        unit = new Sprite(Assets.robotImage);
        unit.Scale(unitCoeff);
        buildInfoTextSize = 0.05f * glView.getScreenHeight();
        upgradeSystem = new UpgradeSystem(planet);
        createType = 0;
        ButtonsInitialize();
    }

    @Override
    public void Update(float eTime)
    {

    }

    @Override
    public void Draw(Graphics graphics)
    {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        switch(state)
        {
            case MAIN:
                break;

            case BUILD:
                graphics.DrawText("БИРЖА: " + planet.getBuildings()[0], Assets.gsFont, upgradeBuilding[0].getMatrix()[12], upgradeBuilding[0].getMatrix()[13] - upgradeBuilding[0].height/2 - buildInfoTextSize/2, 0, Assets.gsColor, buildInfoTextSize, true);
                graphics.DrawText("НЕФТЯНАЯ СКВАЖИНА: " + planet.getBuildings()[1], Assets.gsFont, upgradeBuilding[1].getMatrix()[12], upgradeBuilding[1].getMatrix()[13] - upgradeBuilding[1].height/2 - buildInfoTextSize/2, 0, Assets.gsColor, buildInfoTextSize, true);
                graphics.DrawText("ШАХТЫ НАНОСТАЛИ: " + planet.getBuildings()[2], Assets.gsFont, upgradeBuilding[2].getMatrix()[12], upgradeBuilding[2].getMatrix()[13] - upgradeBuilding[2].height/2 - buildInfoTextSize/2, 0, Assets.gsColor, buildInfoTextSize, true);
                graphics.DrawText("ЗАВОД: " + planet.getBuildings()[3], Assets.gsFont, upgradeBuilding[3].getMatrix()[12], upgradeBuilding[3].getMatrix()[13] - upgradeBuilding[3].height/2 - buildInfoTextSize/2, 0, Assets.gsColor, buildInfoTextSize, true);
                graphics.DrawText("МАСТЕРСКАЯ: " + planet.getBuildings()[4], Assets.gsFont, upgradeBuilding[4].getMatrix()[12], upgradeBuilding[4].getMatrix()[13] - upgradeBuilding[4].height/2 - buildInfoTextSize/2, 0, Assets.gsColor, buildInfoTextSize, true);

                if(!planet.buildingUpgrade.IsFalse())
                {
                    graphics.DrawText("ИДЕТ СТРОИТЕЛЬСТВО: " + planet.upgradeName + " " + (planet.getBuildings()[(int)planet.buildingUpgrade.x] + 1) + " УРОВЕНЬ", Assets.gsFont, glView.getScreenWidth()/2, glView.getScreenHeight()/2, 0, Assets.gsColor, buildInfoTextSize, true);
                }
                break;

            case FACTORY:
                if(createType == 1)
                {
                    unit.setTexture(Assets.robotImage);
                    unit.setPosition(unit.getWidth() / 2 + 30, 200 + unit.getHeight() / 2);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[0], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.ifvImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[1], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.rocketImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[2], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.tankImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[3], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.turretImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[4], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.sonderImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getGroundGuards()[5], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    if(!planet.unitGroundUpgrade.IsFalse())
                    {
                        graphics.DrawText("ИДЕТ СОЗДАНИЕ:" + planet.unitUpgradeName + " " + (planet.getGroundGuards()[(int)planet.unitGroundUpgrade.x] + 1) + " УРОВЕНЬ", Assets.gsFont, glView.getScreenWidth()/2, glView.getScreenHeight()/2, 0, Assets.gsColor, 40, true);
                    }
                }
                else if(createType == 2)
                {
                    unit.setTexture(Assets.robotImage);
                    unit.setPosition(unit.getWidth() / 2 + 10, 200 + unit.getHeight() / 2);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[0], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, unit.getHeight()/2);

                    unit.setTexture(Assets.akiraImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[1], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.defaintImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[2], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.battleshipImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[3], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2, 0);

                    unit.setTexture(Assets.bioshipImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[4], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2,0);

                    unit.setTexture(Assets.birdOfPreyImage);
                    unit.Move(unit.getWidth() / 2 + 30 , 0);
                    graphics.DrawSprite(unit);
                    graphics.DrawText("x" + planet.getSpaceGuards()[5], Assets.gsFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Assets.gsColor, 30, false);
                    unit.Move(unit.getWidth()/2 ,0);

                    if(!planet.unitSpaceUpgrade.IsFalse())
                    {
                        graphics.DrawText("ИДЕТ СОЗДАНИЕ: " + planet.unitUpgradeName + " " + (planet.getSpaceGuards()[(int)planet.unitSpaceUpgrade.x] + 1) + " УРОВЕНЬ", Assets.gsFont, glView.getScreenWidth()/2, glView.getScreenHeight()/2, 0, Assets.gsColor, 40, true);
                    }
                }
                break;

            case WORKSHOP:

                break;

            case CHOISE_ARMY_TYPE:

                break;

            case UNIT_TRANSACTION:

                break;

            case RESOURCES_TRANSACTION:
                graphics.DrawText("Склад корабля: КРЕДИТЫ: " + space.getPlayer().getResources()[0] + " // НЕФТЬ: " + space.getPlayer().getResources()[1] + " // НАНОСТАЛЬ: " + space.getPlayer().getResources()[2], Assets.gsFont, glView.getScreenWidth()/2, 60, 0, Assets.gsColor, buildInfoTextSize, true);
                break;
        }
        ButtonsDraw(graphics);
        graphics.DrawText("КРЕДИТЫ: " + planet.getResources()[0] + " // НЕФТЬ: " + planet.getResources()[1] + " // НАНОСТАЛЬ: " + planet.getResources()[2], Assets.gsFont, glView.getScreenWidth()/2, buildInfoTextSize/2f, 0, Assets.gsColor, buildInfoTextSize, true);
    }

    @Override
    public void OnTouch(MotionEvent event)
    {
        touchPos.SetValue(event.getX(), event.getY());
        //Обновляем кнопки
        ButtonsUpdate();
        //Если было совершено нажатие на экран
        if(event.getAction() == MotionEvent.ACTION_DOWN)
        {
            //Пытаемся обработать нажатия кнопок
            CheckButtons();
        }
        //Если убрали палец с экрана
        else if(event.getAction() == MotionEvent.ACTION_UP)
        {
            //Сбрасываем состояние кнопок
            ButtonsReset();
        }
    }

    @Override
    public void Resume()
    {
        glView.setCamera(0, 0);
        if(planet.IsConquered())
        {
            MainMenu();
        }
    }

    /**
     * Обрабатывает нажатия на кнопки
     */
    public void CheckButtons()
    {
        if(attackBtn.IsClicked())
        {
            Log.i("BATTLE", "START");
            BattlePlayer.fieldSize = 15;
            BattlePlayer.isGround = true;
            BattlePlayer.unitCount = space.getPlayer().getGArmy().clone();
            SingleBattle.difficulty = (byte)(BattlePlayer.level);
            glView.StartBattle(planet, 'c', Math.random() < 0.5);
        }
        else if(exitBtn.IsClicked())
        {
            switch(state)
            {
                case MAIN:
                    if(TechMSG.isPlayerLand)
                    {
                        TechMSG.isPlayerLand = false;
                        TechMSG.playerMove = false;
                        space.getPlayer().lestTakeOff();
                    }
                    if(TechMSG.isAILand)
                    {
                        TechMSG.isAILand = false;
                        TechMSG.playerMove = true;
                        space.getAI().lestTakeOff();
                    }
                    glView.goBack();
                    break;

                case BUILD:
                    MainMenu();
                    for(int i = 0; i < upgradeBuilding.length; i++)
                    {
                        upgradeBuilding[i].SetInvisible();
                        upgradeBuilding[i].Lock();
                    }

                    break;

                case FACTORY:
                    state = State.CHOISE_ARMY_TYPE;
                    for(int i = 0; i < createUnit.length; i++)
                    {
                        createUnit[i].SetInvisible();
                        createUnit[i].Lock();
                    }
                    spaceArmyBtn.Unlock();
                    spaceArmyBtn.SetVisible();
                    groundArmyBtn.Unlock();
                    groundArmyBtn.SetVisible();
                    break;

                case WORKSHOP:

                    break;

                case UNIT_TRANSACTION:
                    if(unitCreationState == 0)
                    {
                        MainMenu();

                        exportBtn.SetInvisible();
                        exportBtn.Lock();
                        importBtn.SetInvisible();
                        importBtn.Lock();
                    }
                    else
                    {
                        unitCreationState = 0;
                        exportBtn.SetVisible();
                        exportBtn.Unlock();
                        importBtn.SetVisible();
                        importBtn.Unlock();
                    }
                    break;

                case RESOURCES_TRANSACTION:
                    MainMenu();

                    selectOil.RemoveColorFilter();
                    selectCredits.RemoveColorFilter();
                    selectNanosteel.RemoveColorFilter();

                    exportBtn.SetInvisible();
                    exportBtn.Lock();
                    importBtn.SetInvisible();
                    importBtn.Lock();

                    selectOil.SetInvisible();
                    selectOil.Lock();
                    selectCredits.SetInvisible();
                    selectCredits.Lock();
                    selectNanosteel.SetInvisible();
                    selectNanosteel.Lock();
                    break;

                case CHOISE_ARMY_TYPE:

                    MainMenu();
                    groundArmyBtn.SetInvisible();
                    groundArmyBtn.Lock();

                    spaceArmyBtn.SetInvisible();
                    spaceArmyBtn.Lock();
                    break;
            }
        }
        else if(buildInfo.IsClicked())
        {
            state = State.BUILD;
            buildInfo.SetInvisible();
            buildInfo.Lock();
            showFactory.SetInvisible();
            showFactory.Lock();
            resourcesMenuBtn.SetInvisible();
            resourcesMenuBtn.Lock();
            unitMenuBtn.SetInvisible();
            unitMenuBtn.Lock();
            for(int i = 0; i < upgradeBuilding.length; i++)
            {
                upgradeBuilding[i].SetVisible();
                upgradeBuilding[i].Unlock();
            }
            if(planet.getBuildings()[3] > 0)
            {
                showFactory.SetInvisible();
                showFactory.Lock();
            }
        }
        else if(showFactory.IsClicked())
        {
            state = State.FACTORY;
            showFactory.Lock();
            showFactory.SetInvisible();
            buildInfo.Lock();
            buildInfo.SetInvisible();
            resourcesMenuBtn.SetInvisible();
            resourcesMenuBtn.Lock();
            unitMenuBtn.SetInvisible();
            unitMenuBtn.Lock();

            spaceArmyBtn.Unlock();
            spaceArmyBtn.SetVisible();
            groundArmyBtn.Unlock();
            groundArmyBtn.SetVisible();
        }
        else if(resourcesMenuBtn.IsClicked())
        {
            state = State.RESOURCES_TRANSACTION;
            showFactory.Lock();
            showFactory.SetInvisible();
            resourcesMenuBtn.Lock();
            resourcesMenuBtn.SetInvisible();
            unitMenuBtn.SetInvisible();
            unitMenuBtn.Lock();
            buildInfo.Lock();
            buildInfo.SetInvisible();

            exportBtn.SetVisible();
            exportBtn.Unlock();
            importBtn.SetVisible();
            importBtn.Unlock();

            selectOil.SetVisible();
            selectOil.Unlock();
            selectCredits.SetVisible();
            selectCredits.Unlock();
            selectNanosteel.SetVisible();
            selectNanosteel.Unlock();
        }
        else if(unitMenuBtn.IsClicked())
        {
            state = State.UNIT_TRANSACTION;
            showFactory.Lock();
            showFactory.SetInvisible();
            resourcesMenuBtn.Lock();
            resourcesMenuBtn.SetInvisible();
            unitMenuBtn.SetInvisible();
            unitMenuBtn.Lock();
            buildInfo.Lock();
            buildInfo.SetInvisible();

            exportBtn.SetVisible();
            exportBtn.Unlock();
            importBtn.SetVisible();
            importBtn.Unlock();
        }
        else if(exportBtn.IsClicked())
        {
            if(state == State.UNIT_TRANSACTION)
            {

            }
            else if(state == State.RESOURCES_TRANSACTION)
            {
                switch(selectedObject)
                {
                    case 1:
                        if(planet.getResources()[0] >= 50) {
                            space.getPlayer().getResources()[0] += 50;
                            planet.getResources()[0] -= 50;
                        }
                        break;
                    case 2:
                        if(planet.getResources()[1] >= 50) {
                            space.getPlayer().getResources()[1] += 50;
                            planet.getResources()[1] -= 50;
                        }
                        break;
                    case 3:
                        if(planet.getResources()[2] >= 50) {
                            space.getPlayer().getResources()[2] += 50;
                            planet.getResources()[2] -= 50;
                        }
                        break;
                }
            }
        }
        else if(importBtn.IsClicked())
        {
            if(state == State.UNIT_TRANSACTION)
            {

            }
            else if(state == State.RESOURCES_TRANSACTION)
            {
                switch(selectedObject)
                {
                    case 1:
                        if(space.getPlayer().getResources()[0] >= 50)
                        {
                            space.getPlayer().getResources()[0] -= 50;
                            planet.getResources()[0] += 50;
                        }
                        break;
                    case 2:
                        if(space.getPlayer().getResources()[1] >= 50) {
                            space.getPlayer().getResources()[1] -= 50;
                            planet.getResources()[1] += 50;
                        }
                        break;
                    case 3:
                        if(space.getPlayer().getResources()[2] >= 50) {
                            space.getPlayer().getResources()[2] -= 50;
                            planet.getResources()[2] += 50;
                        }
                        break;
                }
            }
        }
        else if(spaceArmyBtn.IsClicked())
        {
            unitCreationState = 2;
            createType = 2;
            state = State.FACTORY;
            groundArmyBtn.SetInvisible();
            groundArmyBtn.Lock();
            spaceArmyBtn.SetInvisible();
            spaceArmyBtn.Lock();

            for(int i = 0; i < createUnit.length; i++)
            {
                createUnit[i].SetVisible();
                createUnit[i].Unlock();
            }
        }
        else if(groundArmyBtn.IsClicked())
        {
            unitCreationState = 1;
            createType = 1;
            state = State.FACTORY;
            groundArmyBtn.SetInvisible();
            groundArmyBtn.Lock();
            spaceArmyBtn.SetInvisible();
            spaceArmyBtn.Lock();
            for(int i = 0; i < createUnit.length; i++)
            {
                createUnit[i].SetVisible();
                createUnit[i].Unlock();
            }
        }
        else if(selectCredits.IsClicked())
        {
            selectedObject = 1;
            selectCredits.SetColorFilter(Color.argb(255, 0, 234, 255));
            selectOil.RemoveColorFilter();
            selectNanosteel.RemoveColorFilter();
        }
        else if(selectOil.IsClicked())
        {
            selectedObject = 2;
            selectOil.SetColorFilter(Color.argb(255, 0, 234, 255));
            selectCredits.RemoveColorFilter();
            selectNanosteel.RemoveColorFilter();
        }
        else if(selectNanosteel.IsClicked())
        {
            selectedObject = 3;
            selectNanosteel.SetColorFilter(Color.argb(255, 0, 234, 255));
            selectOil.RemoveColorFilter();
            selectCredits.RemoveColorFilter();
        }
        else
        {
            for(int i = 0; i < upgradeBuilding.length; i++)
            {
                if (upgradeBuilding[i].IsClicked())
                {
                    upgradeSystem.UpgradeBuild(i);
                    return;
                }
            }
            for(int i = 0; i < createUnit.length; i++)
            {

                if(createUnit[i].IsClicked())
                {
                    if(createType == 1)
                    {
                        upgradeSystem.CreateUnit(i+6);
                        return;
                    }
                    else if(createType == 2)
                    {
                        upgradeSystem.CreateUnit(i);
                        return;
                    }
                }
            }
        }
    }


    public void MainMenu()
    {
        state = State.MAIN;
        buildInfo.SetVisible();
        buildInfo.Unlock();
        resourcesMenuBtn.SetVisible();
        resourcesMenuBtn.Unlock();
        unitMenuBtn.SetVisible();
        unitMenuBtn.Unlock();
        showFactory.SetVisible();
        if(planet.getBuildings()[3] > 0)
            showFactory.Unlock();
    }

    /**
     * Обновляет состояние кнопок
     */
    private void ButtonsUpdate()
    {
        if (planet.IsConquered())
        {
            buildInfo.Update(touchPos);
            showFactory.Update(touchPos);
            resourcesMenuBtn.Update(touchPos);
            unitMenuBtn.Update(touchPos);
            exportBtn.Update(touchPos);
            importBtn.Update(touchPos);
            selectOil.Update(touchPos);
            selectCredits.Update(touchPos);
            selectNanosteel.Update(touchPos);
            groundArmyBtn.Update(touchPos);
            spaceArmyBtn.Update(touchPos);
            for (int i = 0; i < upgradeBuilding.length; i++)
            {
                upgradeBuilding[i].Update(touchPos);
            }
            for (int i = 0; i < upgradeBuilding.length; i++)
            {
                createUnit[i].Update(touchPos);
            }
        }
        else
        {
            attackBtn.Update(touchPos);
        }
        exitBtn.Update(touchPos);
    }

    /**
     * Обнуляет состояние кнопок
     */
    private void ButtonsReset()
    {
        attackBtn.Reset();
        exitBtn.Reset();
        buildInfo.Reset();
        showFactory.Reset();
        resourcesMenuBtn.Reset();
        unitMenuBtn.Reset();
        exportBtn.Reset();
        importBtn.Reset();
        selectOil.Reset();
        selectCredits.Reset();
        selectNanosteel.Reset();
        spaceArmyBtn.Reset();
        groundArmyBtn.Reset();
        for(int i = 0; i < upgradeBuilding.length; i++)
        {
            upgradeBuilding[i].Reset();
        }
        for(int i = 0; i < createUnit.length; i++)
        {
            createUnit[i].Reset();
        }
    }

    /**
     * Отрисовывает кнопки
     * @param
     */
    private void ButtonsDraw(Graphics graphics)
    {
        if(planet.IsConquered())
        {
            buildInfo.Draw(graphics);
            showFactory.Draw(graphics);
            resourcesMenuBtn.Draw(graphics);
            unitMenuBtn.Draw(graphics);
            exportBtn.Draw(graphics);
            importBtn.Draw(graphics);
            selectOil.Draw(graphics);
            selectCredits.Draw(graphics);
            selectNanosteel.Draw(graphics);
            groundArmyBtn.Draw(graphics);
            spaceArmyBtn.Draw(graphics);
            for (int i = 0; i < upgradeBuilding.length; i++)
            {
                upgradeBuilding[i].Draw(graphics);
            }
            for (int i = 0; i < createUnit.length; i++)
            {
                createUnit[i].Draw(graphics);
            }
        }
        else
        {
            attackBtn.Draw(graphics);
        }
        exitBtn.Draw(graphics);
    }

    /**
     * Инициализирует кнопки
     */
    private void ButtonsInitialize()
    {
        attackBtn = new Button(Assets.btnAttack, 0, 0, false);
        attackBtn.Scale(Assets.btnCoeff);
        attackBtn.SetPosition(glView.getScreenWidth()/2, glView.getScreenHeight()/2);

        exitBtn = new Button(Assets.btnQBack, 0, 0, false);
        exitBtn.Scale(Assets.btnCoeff);
        exitBtn.SetPosition(glView.getScreenWidth() - exitBtn.width, glView.getScreenHeight() - exitBtn.height);

        buildInfo = new Button(Assets.btnBuildings, 0, 0, false);
        buildInfo.Scale(Assets.btnCoeff);
        buildInfo.SetPosition(glView.getScreenWidth()/2 - buildInfo.width, glView.getScreenHeight()/2 - buildInfo.height);

        showFactory = new Button(Assets.btnFactory, 0, 0, false);
        showFactory.Scale(Assets.btnCoeff);
        showFactory.SetPosition(glView.getScreenWidth()/2 + showFactory.width, glView.getScreenHeight()/2 - showFactory.height);

        resourcesMenuBtn = new Button(Assets.btnResources, 0, 0, false);
        resourcesMenuBtn.Scale(Assets.btnCoeff);
        resourcesMenuBtn.SetPosition(glView.getScreenWidth()/2 - resourcesMenuBtn.width, glView.getScreenHeight()/2 + resourcesMenuBtn.height);

        unitMenuBtn = new Button(Assets.btnArmy, 0, 0, false);
        unitMenuBtn.Scale(Assets.btnCoeff);
        unitMenuBtn.SetPosition(glView.getScreenWidth()/2 + unitMenuBtn.width, glView.getScreenHeight()/2 + unitMenuBtn.height);

        if(!planet.IsConquered())
        {
            resourcesMenuBtn.SetInvisible();
            resourcesMenuBtn.Lock();

            buildInfo.SetInvisible();
            buildInfo.Lock();
        }
        else
        {
            if(planet.getBuildings()[3] == 0)
                showFactory.Lock();
        }

        for(int i = 0; i < upgradeBuilding.length; i++)
        {
            upgradeBuilding[i] = new Button(Assets.btnBuild, 0, 0, false);
            upgradeBuilding[i].Scale(Assets.btnCoeff);
            upgradeBuilding[i].SetInvisible();
            upgradeBuilding[i].Lock();
        }
        upgradeBuilding[0].SetPosition(space.getScreenWidth()/2 - 2 * upgradeBuilding[0].width, space.getScreenHeight()/2 - upgradeBuilding[0].height);
        upgradeBuilding[1].SetPosition(space.getScreenWidth()/2, space.getScreenHeight()/2 - upgradeBuilding[1].height);
        upgradeBuilding[2].SetPosition(space.getScreenWidth()/2 + 2 * upgradeBuilding[2].width, space.getScreenHeight()/2 - upgradeBuilding[2].height);
        upgradeBuilding[3].SetPosition(space.getScreenWidth()/2 - upgradeBuilding[3].width, space.getScreenHeight()/2 + upgradeBuilding[3].height);
        upgradeBuilding[4].SetPosition(space.getScreenWidth()/2 + upgradeBuilding[3].width, space.getScreenHeight()/2 + upgradeBuilding[4].height);

        for(int i = 0; i < createUnit.length; i++)
        {
            createUnit[i] = new Button(Assets.btnOK, 0, 0, false);
            createUnit[i].Scale(Assets.btnCoeff);
            createUnit[i].SetPosition(createUnit[i].width/2 + i * (createUnit[i].width + 100), 100);
            createUnit[i].SetInvisible();
            createUnit[i].Lock();
        }

        exportBtn = new Button(Assets.btnExport, 0, 0, false);
        exportBtn.Scale(Assets.btnCoeff);
        exportBtn.SetPosition(glView.getScreenWidth()/2 - exportBtn.width, glView.getScreenHeight()/2 + exportBtn.height);
        exportBtn.Lock();
        exportBtn.SetInvisible();

        importBtn = new Button(Assets.btnImport, 0, 0, false);
        importBtn.Scale(Assets.btnCoeff);
        importBtn.SetPosition(glView.getScreenWidth()/2 + importBtn.width, glView.getScreenHeight()/2 + exportBtn.height);
        importBtn.Lock();
        importBtn.SetInvisible();

        selectCredits = new Button(Assets.btnCredits, 0, 0, false);
        selectCredits.Scale(Assets.btnCoeff);
        selectCredits.SetPosition(glView.getScreenWidth()/2 - 2 * exportBtn.width, glView.getScreenHeight()/2 - selectCredits.height);
        selectCredits.Lock();
        selectCredits.SetInvisible();

        selectOil = new Button(Assets.btnOil, 0, 0, false);
        selectOil.Scale(Assets.btnCoeff);
        selectOil.SetPosition(glView.getScreenWidth()/2, glView.getScreenHeight()/2 - selectOil.height);
        selectOil.Lock();
        selectOil.SetInvisible();

        selectNanosteel = new Button(Assets.btnNanosteel, 0, 0, false);
        selectNanosteel.Scale(Assets.btnCoeff);
        selectNanosteel.SetPosition(glView.getScreenWidth()/2 + 2 * exportBtn.width, glView.getScreenHeight()/2 - selectNanosteel.height);
        selectNanosteel.Lock();
        selectNanosteel.SetInvisible();

        groundArmyBtn = new Button(Assets.btnGArmy, 0, 0, false);
        groundArmyBtn.Scale(Assets.btnCoeff);
        groundArmyBtn.SetPosition(glView.getScreenWidth()/2 - groundArmyBtn.width, glView.getScreenHeight()/2);
        groundArmyBtn.SetInvisible();
        groundArmyBtn.Lock();

        spaceArmyBtn = new Button(Assets.btnSArmy, 0, 0, false);
        spaceArmyBtn.Scale(Assets.btnCoeff);
        spaceArmyBtn.SetPosition(glView.getScreenWidth()/2 + groundArmyBtn.width, glView.getScreenHeight()/2);
        spaceArmyBtn.SetInvisible();
        spaceArmyBtn.Lock();
    }
}
