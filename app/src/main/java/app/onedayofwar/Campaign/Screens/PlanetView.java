package app.onedayofwar.Campaign.Screens;

import android.graphics.Color;
import android.opengl.GLES20;
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
    private enum State {MAIN, BUILD, FACTORY, WORKSHOP, UNIT_TRANSACTION}
    private State state;
    private GLView glView;
    private Space space;
    private Planet planet;
    private Button attackBtn;
    private Button exitBtn;
    private Button buildInfo;
    private Button showFactory;
    private Button upgradeBuilding[];
    private Button createUnit[];
    private Vector2 touchPos;
    private Sprite unit;
    private float buildInfoTextSize;

    public PlanetView(GLView glView, Space space, Planet planet)
    {
        this.glView = glView;
        this.planet = planet;
        this.space = space;
    }

    @Override
    public void Initialize(Graphics graphics)
    {
        state = State.MAIN;
        boolean isClear = true;
        for(int i = 0; i < planet.getGroundGuards().length; i++)
        {
            if(planet.getGroundGuards()[i] > 0)
                isClear = false;
        }
        if(isClear)
            planet.ConquerPlanet();

        createUnit = new Button[6];
        upgradeBuilding = new Button[5];
        touchPos = new Vector2();
        float unitCoeff = glView.getScreenWidth() * 0.9f /(Assets.robotImage.getWidth() + Assets.ifvImage.getWidth() + Assets.engineerImage.getWidth() + Assets.tankImage.getWidth() + Assets.turretImage.getWidth() + Assets.sonderImage.getWidth() + 5 * 30);
        unit = new Sprite(Assets.robotImage);
        unit.Scale(unitCoeff);
        buildInfoTextSize = 0.04f * glView.getScreenHeight();
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
                graphics.DrawText("Credits: " + planet.credits + "\nOil: " + planet.oil + "\nNanosteel: " + planet.nanoSteel, Assets.arialFont, 0, 0, 0, Color.GREEN, 50);

                unit.setTexture(Assets.robotImage);
                unit.setPosition(unit.getWidth() / 2 + 30, 150 + unit.getHeight() / 2 + 10);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[0], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.ifvImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[1], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.engineerImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[2], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.tankImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[3], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.turretImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[4], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.sonderImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[5], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);
                break;

            case BUILD:
                graphics.DrawText("Market: " + planet.getBuildings()[0], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[0].getMatrix()[13] - upgradeBuilding[0].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);
                graphics.DrawText("Oil Drill: " + planet.getBuildings()[1], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[1].getMatrix()[13] - upgradeBuilding[1].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);
                graphics.DrawText("Nanosteel Mines: " + planet.getBuildings()[2], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[2].getMatrix()[13] - upgradeBuilding[2].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);
                graphics.DrawText("Factory: " + planet.getBuildings()[3], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[3].getMatrix()[13] - upgradeBuilding[3].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);
                graphics.DrawText("Workshop: " + planet.getBuildings()[4], Assets.arialFont, upgradeBuilding[0].getMatrix()[12] - upgradeBuilding[0].width/2, upgradeBuilding[4].getMatrix()[13] - upgradeBuilding[4].height/2 - buildInfoTextSize, 0, Color.GREEN, buildInfoTextSize);

                if(!planet.buildingUpgrade.IsFalse())
                {
                    graphics.DrawText("UPGRADE IN PROGRESS:\n" + planet.upgradeName + " " + (planet.getBuildings()[(int)planet.buildingUpgrade.x] + 1) + " LEVEL", Assets.arialFont, glView.getScreenWidth()/2 - 100, glView.getScreenHeight()/2, 0, Color.GREEN, 40);
                }
                break;

            case FACTORY:
                unit.setTexture(Assets.robotImage);
                unit.setPosition(unit.getWidth() / 2 + 30, 150 + unit.getHeight() / 2 + 10);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[0], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.ifvImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[1], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.engineerImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[2], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.tankImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[3], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.turretImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[4], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);

                unit.setTexture(Assets.sonderImage);
                unit.Move(unit.getWidth() / 2 + 30 - unit.matrix[12], 10 + unit.getHeight() / 2);
                graphics.DrawSprite(unit);
                graphics.DrawText("x" + planet.getGroundGuards()[5], Assets.arialFont, unit.matrix[12] + unit.getWidth() / 2, unit.matrix[13], 0, Color.GREEN, 30);
                unit.Move(0, unit.getHeight() / 2);
                break;

            case WORKSHOP:

                break;

            case UNIT_TRANSACTION:

                break;
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
        if(planet.IsConquered())
        {
            attackBtn.Lock();
            attackBtn.SetInvisible();

            showFactory.SetVisible();
            showFactory.Unlock();

            buildInfo.SetVisible();
            buildInfo.Unlock();
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
            BattlePlayer.unitCount = space.getPlayer().getArmy().clone();
            SingleBattle.difficulty = (byte)(Math.random()*10);
            glView.StartBattle(planet, 'c', Math.random() < 0.5);
        }
        else if(exitBtn.IsClicked())
        {
            switch(state)
            {
                case MAIN:
                    space.getPlayer().lestTakeOff();
                    glView.goBack();
                    break;

                case BUILD:
                    state = State.MAIN;
                    buildInfo.SetVisible();
                    buildInfo.Unlock();
                    for(int i = 0; i < upgradeBuilding.length; i++)
                    {
                        upgradeBuilding[i].SetInvisible();
                        upgradeBuilding[i].Lock();
                    }
                    if(planet.getBuildings()[3] > 0)
                    {
                        showFactory.SetVisible();
                        showFactory.Unlock();
                    }
                    break;

                case FACTORY:
                    state = State.MAIN;
                    for(int i = 0; i < createUnit.length; i++)
                    {
                        createUnit[i].SetInvisible();
                        createUnit[i].Lock();
                    }
                    showFactory.Unlock();
                    showFactory.SetVisible();
                    buildInfo.Unlock();
                    buildInfo.SetVisible();
                    break;

                case WORKSHOP:

                    break;

                case UNIT_TRANSACTION:

                    break;
            }
        }
        else if(buildInfo.IsClicked())
        {
            state = State.BUILD;
            buildInfo.SetInvisible();
            buildInfo.Lock();
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

            for(int i = 0; i < createUnit.length; i++)
            {
                createUnit[i].SetVisible();
                createUnit[i].Unlock();
            }
        }
        else
        {
            for(int i = 0; i < upgradeBuilding.length; i++)
            {
                if (upgradeBuilding[i].IsClicked())
                {
                    planet.UpgradeBuilding(i);
                    return;
                }
            }
            for(int i = 0; i < createUnit.length; i++)
            {
                if(createUnit[i].IsClicked())
                {
                    planet.CreateUnit(i);
                    return;
                }
            }
        }
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
        float upgradeBtnCoeff = glView.getScreenHeight() * 0.9f / (planet.getBuildings().length * (Assets.btnInstall.getHeight() + buildInfoTextSize));

        exitBtn = new Button(Assets.btnCancel, 0, 0, false);
        exitBtn.Scale(Assets.btnCoeff);
        exitBtn.SetPosition(glView.getScreenWidth() - exitBtn.width/2 - 50, glView.getScreenHeight() - exitBtn.height/2 - 50);

        attackBtn = new Button(Assets.btnFinishInstallation, 0, 0, false);
        attackBtn.Scale(Assets.btnCoeff);
        attackBtn.SetPosition(exitBtn.getMatrix()[12], exitBtn.getMatrix()[13] - attackBtn.height - 10);

        buildInfo = new Button(Assets.btnTurn, 0, 0, false);
        buildInfo.Scale(Assets.btnCoeff);
        buildInfo.SetPosition(exitBtn.getMatrix()[12], exitBtn.getMatrix()[13] - attackBtn.height - 10);
        if(!planet.IsConquered())
        {
            buildInfo.SetInvisible();
            buildInfo.Lock();
        }

        showFactory = new Button(Assets.btnInstall, 0, 0, false);
        showFactory.Scale(Assets.btnCoeff);
        showFactory.SetPosition(attackBtn.getMatrix()[12], buildInfo.getMatrix()[13] - showFactory.height - 10);
        if(!planet.IsConquered() || planet.getBuildings()[3] == 0)
        {
            showFactory.SetInvisible();
            showFactory.Lock();
        }

        for(int i = 0; i < upgradeBuilding.length; i++)
        {
            upgradeBuilding[i] = new Button(Assets.btnInstall, 0, 0, false);
            upgradeBuilding[i].Scale(upgradeBtnCoeff);
            upgradeBuilding[i].SetPosition(upgradeBuilding[i].width/2 + 50, upgradeBuilding[i].height/2 + buildInfoTextSize +  i * (upgradeBuilding[i].height + buildInfoTextSize));
            upgradeBuilding[i].SetInvisible();
            upgradeBuilding[i].Lock();
        }

        for(int i = 0; i < createUnit.length; i++)
        {
            createUnit[i] = new Button(Assets.btnInstall, 0, 0, false);
            createUnit[i].Scale(Assets.btnCoeff);
            createUnit[i].SetPosition(createUnit[i].width/2 + i * createUnit[i].width, glView.getScreenHeight()/2);
            createUnit[i].SetInvisible();
            createUnit[i].Lock();
        }
    }
}
