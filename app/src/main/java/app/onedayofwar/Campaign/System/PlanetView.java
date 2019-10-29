package app.onedayofwar.Campaign.System;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.Mods.SingleBattle;
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
    private GLView glView;
    private Space space;
    private Planet planet;
    private Button attackBtn;
    private Button exitBtn;
    private Button buildInfo;
    private Button upgradeMarketBtn;
    private Button upgradeOilDrillBtn;
    private Button upgradeNanosteelMinesBtn;
    private Button upgradeFactory;
    private Button upgradeWorkshop;
    private Vector2 touchPos;
    private Sprite gArmy;
    private boolean showBuildInfo;

    public PlanetView(GLView glView, Space space, Planet planet)
    {
        this.glView = glView;
        this.planet = planet;
        this.space = space;
    }

    @Override
    public void Initialize(Graphics graphics)
    {
        boolean isClear = true;
        for(int i = 0; i < planet.getGroundGuards().length; i++)
        {
            if(planet.getGroundGuards()[i] > 0)
                isClear = false;
        }
        if(isClear)
            planet.ConquerPlanet();
        touchPos = new Vector2();
        showBuildInfo = false;
        gArmy = new Sprite(Assets.robotImage);
        gArmy.Scale((float)Assets.isoGridCoeff);
        ButtonsInitialize();
    }

    @Override
    public void Update(float eTime)
    {

    }

    @Override
    public void Draw(Graphics graphics)
    {
        if(planet.IsConquered())
        {
            if(showBuildInfo)
            {
                graphics.DrawText("Market: " + planet.getBuildings()[0], Assets.arialFont, upgradeMarketBtn.getMatrix()[12] - upgradeMarketBtn.width/2, upgradeMarketBtn.getMatrix()[13] - upgradeMarketBtn.height/2 - 30, 0, Color.GREEN, 30);
                graphics.DrawText("Oil Drill: " + planet.getBuildings()[1], Assets.arialFont, upgradeMarketBtn.getMatrix()[12] - upgradeMarketBtn.width/2, upgradeOilDrillBtn.getMatrix()[13] - upgradeMarketBtn.height/2 - 30, 0, Color.GREEN, 30);
                graphics.DrawText("Nanosteel Mines: " + planet.getBuildings()[2], Assets.arialFont, upgradeMarketBtn.getMatrix()[12] - upgradeMarketBtn.width/2, upgradeNanosteelMinesBtn.getMatrix()[13] - upgradeMarketBtn.height/2 - 30, 0, Color.GREEN, 30);
                graphics.DrawText("Factory: " + planet.getBuildings()[3], Assets.arialFont, upgradeMarketBtn.getMatrix()[12] - upgradeMarketBtn.width/2, upgradeFactory.getMatrix()[13] - upgradeMarketBtn.height/2 - 30, 0, Color.GREEN, 30);
                graphics.DrawText("Workshop: " + planet.getBuildings()[4], Assets.arialFont, upgradeMarketBtn.getMatrix()[12] - upgradeMarketBtn.width/2, upgradeWorkshop.getMatrix()[13] - upgradeMarketBtn.height/2 - 30, 0, Color.GREEN, 30);

                if(!planet.buildingUpgrade.IsFalse())
                {
                    graphics.DrawText("UPGRADE IN PROGRESS:\n" + planet.upgradeName + " " + (planet.getBuildings()[(int)planet.buildingUpgrade.x] + 1) + " LEVEL", Assets.arialFont, glView.getScreenWidth()/2 - 100, glView.getScreenHeight()/2, 0, Color.GREEN, 40);
                }
            }
            else
            {
                graphics.DrawText("Credits: " + planet.credits + "\nOil: " + planet.oil + "\nNanosteel: " + planet.nanoSteel, Assets.arialFont, 0, 0, 0, Color.GREEN, 50);

                gArmy.setTexture(Assets.robotImage);
                gArmy.setPosition(gArmy.getWidth() / 2 + 30, 150 + gArmy.getHeight() / 2 + 10);
                graphics.DrawSprite(gArmy);
                graphics.DrawText("x" + planet.getGroundGuards()[0], Assets.arialFont, gArmy.matrix[12] + gArmy.getWidth() / 2, gArmy.matrix[13], 0, Color.GREEN, 30);
                gArmy.Move(0, gArmy.getHeight() / 2);

                gArmy.setTexture(Assets.ifvImage);
                gArmy.Move(gArmy.getWidth() / 2 + 30 - gArmy.matrix[12], 10 + gArmy.getHeight() / 2);
                graphics.DrawSprite(gArmy);
                graphics.DrawText("x" + planet.getGroundGuards()[1], Assets.arialFont, gArmy.matrix[12] + gArmy.getWidth() / 2, gArmy.matrix[13], 0, Color.GREEN, 30);
                gArmy.Move(0, gArmy.getHeight() / 2);

                gArmy.setTexture(Assets.engineerImage);
                gArmy.Move(gArmy.getWidth() / 2 + 30 - gArmy.matrix[12], 10 + gArmy.getHeight() / 2);
                graphics.DrawSprite(gArmy);
                graphics.DrawText("x" + planet.getGroundGuards()[2], Assets.arialFont, gArmy.matrix[12] + gArmy.getWidth() / 2, gArmy.matrix[13], 0, Color.GREEN, 30);
                gArmy.Move(0, gArmy.getHeight() / 2);

                gArmy.setTexture(Assets.tankImage);
                gArmy.Move(gArmy.getWidth() / 2 + 30 - gArmy.matrix[12], 10 + gArmy.getHeight() / 2);
                graphics.DrawSprite(gArmy);
                graphics.DrawText("x" + planet.getGroundGuards()[3], Assets.arialFont, gArmy.matrix[12] + gArmy.getWidth() / 2, gArmy.matrix[13], 0, Color.GREEN, 30);
                gArmy.Move(0, gArmy.getHeight() / 2);

                gArmy.setTexture(Assets.turretImage);
                gArmy.Move(gArmy.getWidth() / 2 + 30 - gArmy.matrix[12], 10 + gArmy.getHeight() / 2);
                graphics.DrawSprite(gArmy);
                graphics.DrawText("x" + planet.getGroundGuards()[4], Assets.arialFont, gArmy.matrix[12] + gArmy.getWidth() / 2, gArmy.matrix[13], 0, Color.GREEN, 30);
                gArmy.Move(0, gArmy.getHeight() / 2);

                gArmy.setTexture(Assets.sonderImage);
                gArmy.Move(gArmy.getWidth() / 2 + 30 - gArmy.matrix[12], 10 + gArmy.getHeight() / 2);
                graphics.DrawSprite(gArmy);
                graphics.DrawText("x" + planet.getGroundGuards()[5], Assets.arialFont, gArmy.matrix[12] + gArmy.getWidth() / 2, gArmy.matrix[13], 0, Color.GREEN, 30);
                gArmy.Move(0, gArmy.getHeight() / 2);
            }
        }
        ButtonsDraw(graphics);
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
            BattlePlayer.unitCount = space.getPlayer().getArmy().clone();
            SingleBattle.difficulty = (byte)(Math.random()*10);
            glView.StartBattle(planet, 'c', Math.random() < 0.5);
        }
        else if(exitBtn.IsClicked())
        {
            if(showBuildInfo)
                showBuildInfo = false;
            else
                glView.goBack();
        }
        else if(buildInfo.IsClicked())
        {
            showBuildInfo = true;
        }
        else if(upgradeMarketBtn.IsClicked())
        {
            planet.UpgradeBuilding(0);
        }
        else if(upgradeOilDrillBtn.IsClicked())
        {
            planet.UpgradeBuilding(1);
        }
        else if(upgradeNanosteelMinesBtn.IsClicked())
        {
            planet.UpgradeBuilding(2);
        }
        else if(upgradeFactory.IsClicked())
        {
            planet.UpgradeBuilding(3);
        }
        else if(upgradeWorkshop.IsClicked())
        {
            planet.UpgradeBuilding(4);
        }
    }

    /**
     * Обновляет состояние кнопок
     */
    private void ButtonsUpdate()
    {
        if(planet.IsConquered())
        {
            buildInfo.Update(touchPos);
            if(showBuildInfo && planet.buildingUpgrade.IsFalse())
            {
                upgradeMarketBtn.Update(touchPos);
                upgradeOilDrillBtn.Update(touchPos);
                upgradeNanosteelMinesBtn.Update(touchPos);
                upgradeFactory.Update(touchPos);
                upgradeWorkshop.Update(touchPos);
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
        upgradeMarketBtn.Reset();
        upgradeOilDrillBtn.Reset();
        upgradeNanosteelMinesBtn.Reset();
        upgradeFactory.Reset();
        upgradeWorkshop.Reset();
    }

    /**
     * Отрисовывает кнопки
     * @param
     */
    private void ButtonsDraw(Graphics graphics)
    {
        if(planet.IsConquered())
        {
            if(!showBuildInfo)
                buildInfo.Draw(graphics);
            else
            {
                if(planet.buildingUpgrade.IsFalse())
                {
                    upgradeMarketBtn.Draw(graphics);
                    upgradeOilDrillBtn.Draw(graphics);
                    upgradeNanosteelMinesBtn.Draw(graphics);
                    upgradeFactory.Draw(graphics);
                    upgradeWorkshop.Draw(graphics);
                }
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
        exitBtn = new Button(Assets.btnCancel, 0, 0, false);
        exitBtn.Scale(Assets.btnCoeff);
        exitBtn.SetPosition(glView.getScreenWidth() - exitBtn.width/2 - 50, glView.getScreenHeight() - exitBtn.height/2 - 50);

        attackBtn = new Button(Assets.btnFinishInstallation, 0, 0, false);
        attackBtn.Scale(Assets.btnCoeff);
        attackBtn.SetPosition(exitBtn.getMatrix()[12], exitBtn.getMatrix()[13] - attackBtn.height - 10);

        buildInfo = new Button(Assets.btnTurn, 0, 0, false);
        buildInfo.Scale(Assets.btnCoeff);
        buildInfo.SetPosition(attackBtn.getMatrix()[12], attackBtn.getMatrix()[13] - buildInfo.height - 10);

        upgradeMarketBtn = new Button(Assets.btnInstall, 0, 0, false);
        upgradeMarketBtn.Scale(Assets.btnCoeff);
        upgradeMarketBtn.SetPosition(upgradeMarketBtn.width/2 + 50, upgradeMarketBtn.height/2 + 30);

        upgradeOilDrillBtn = new Button(Assets.btnInstall, 0, 0, false);
        upgradeOilDrillBtn.Scale(Assets.btnCoeff);
        upgradeOilDrillBtn.SetPosition(upgradeMarketBtn.getMatrix()[12], upgradeMarketBtn.getMatrix()[13] + upgradeOilDrillBtn.height + 30);

        upgradeNanosteelMinesBtn = new Button(Assets.btnInstall, 0, 0, false);
        upgradeNanosteelMinesBtn.Scale(Assets.btnCoeff);
        upgradeNanosteelMinesBtn.SetPosition(upgradeMarketBtn.getMatrix()[12], upgradeOilDrillBtn.getMatrix()[13] + upgradeOilDrillBtn.height + 30);

        upgradeFactory = new Button(Assets.btnInstall, 0, 0, false);
        upgradeFactory.Scale(Assets.btnCoeff);
        upgradeFactory.SetPosition(upgradeMarketBtn.getMatrix()[12], upgradeNanosteelMinesBtn.getMatrix()[13] + upgradeOilDrillBtn.height + 30);

        upgradeWorkshop = new Button(Assets.btnInstall, 0, 0, false);
        upgradeWorkshop.Scale(Assets.btnCoeff);
        upgradeWorkshop.SetPosition(upgradeMarketBtn.getMatrix()[12], upgradeFactory.getMatrix()[13] + upgradeOilDrillBtn.height + 30);
    }
}
