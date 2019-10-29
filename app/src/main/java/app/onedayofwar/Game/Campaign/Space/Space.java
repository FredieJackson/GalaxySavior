package app.onedayofwar.Game.Campaign.Space;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.GEngine.Assets;
import app.onedayofwar.GEngine.GLRenderer;
import app.onedayofwar.GEngine.Loader;
import app.onedayofwar.GEngine.Meshes.Sprite;
import app.onedayofwar.Game.Campaign.CharacterControl.AI;
import app.onedayofwar.Game.Campaign.CharacterControl.Player;
import app.onedayofwar.Game.Campaign.CharacterControl.TechMSG;
import app.onedayofwar.Game.Screens.GameScreen;
import app.onedayofwar.Utils.Vector2;
import app.onedayofwar.Utils.IO.XMLParser;
import app.onedayofwar.GEngine.GUI.Button;

/**
 * Created by Slava on 24.02.2015.
 */
public class Space
{
    private Vector2 touchPos;
    private Vector2 lastTouch;
    private Vector2 tmp2;
    private Vector2 toMove;
    private Vector2 tmp;
    private Vector2 infoTextPos;

    private XMLParser xmlParser;
    private PlanetController planetController;
    private GameScreen gameScreen;

    private Sprite background;
    private Sprite btnRegion;

    private Button moveBtn;
    private Button nextStep;

    private Player player;
    private AI AI;

    private int width;
    private int height;
    private int color;
    private int selectedPlanet;
    private int pointsToMove;
    private float spaceVelocityCoeff;


    public Space(GameScreen gameScreen)
    {
        this.gameScreen = gameScreen;
    }

    public void Initialize(Loader loader)            //Тут сам сравни по ошибкам, которые выскочат что нового есть, еще нужно в GameScreen добавить всякие коэфициенты кнопок и т.д
    {
        background = new Sprite(Assets.space);
        btnRegion = new Sprite(Assets.btnRegion);

        height = 8000;
        width = 8000;

       // background.Scale(2 * (float) Assets.bgWidthCoeff, 2 * (float) Assets.bgHeightCoeff);
        background.setPosition(getScreenWidth() / 2 + Assets.btnRegion.getWidth() / 2, getScreenHeight() / 2);
        btnRegion.setPosition(0, Assets.btnRegion.getHeight()/2);

        touchPos = new Vector2();
        lastTouch = new Vector2();

        pointsToMove = 100;

        if(gameScreen.IsNewGame())
        {
            gameScreen.getDBController().Delete();
            xmlParser = new XMLParser(loader.assets);
            planetController = new PlanetController(this);
            planetController.LoadPlanets(loader, xmlParser);
            player = new Player(this, pointsToMove);
            AI = new AI(this, pointsToMove, player);
        }
        else
        {
            //gameScreen.getDBController().LoadWorld();
            planetController = new PlanetController(this);
            planetController.LoadPlanets(loader, gameScreen.getDBController());
            player = new Player(this, pointsToMove);
            gameScreen.getDBController().LoadCharacter(true, player);
            AI = new AI(this, pointsToMove, player);
            gameScreen.getDBController().LoadCharacter(false, AI);
        }

        color = Color.RED;
        toMove = new Vector2();
        infoTextPos = new Vector2();
        toMove.SetFalse();

        moveBtn = new Button(Assets.btnMove, 0, 0,false);
        moveBtn.Scale(Assets.btnCoeff);
        moveBtn.SetPosition(moveBtn.width/2, moveBtn.height / 2);


        nextStep = new Button(Assets.btnEndTurn, 0 , 0, false);
        nextStep.Scale(Assets.btnCoeff);
        nextStep.SetPosition(nextStep.width/2, gameScreen.getScreenHeight()/2);


        TechMSG.isAttack = false;
        TechMSG.playerMove = true;
        TechMSG.selectedPlanet = -1;
        TechMSG.isAILand = false;
        TechMSG.isPlayerLand = false;
        TechMSG.isFirstPlanetConquered = false;
        TechMSG.isReadyForUpdate = false;
        TechMSG.isFinishedWay = true;
        TechMSG.isNeedMove = true;
        TechMSG.isAttacked = false;
        TechMSG.attackedPlanet = -1;

        spaceVelocityCoeff = Assets.space.getHeight()*1.0f/(width*2);

        tmp = new Vector2();
        tmp2 = new Vector2();
    }

    public void GotoPlanet()
    {
       // gameScreen.getGlView().changeScreen(new PlanetScreen(gameScreen.getGlView(), this, planetController.getPlanet(selectedPlanet)));
    }

    public Player getPlayer()
    {
        return player;
    }

    public AI getAI(){return AI;}

    public void onTouch(MotionEvent event)
    {
        lastTouch.SetValue((int) (event.getX() - touchPos.x) * 1.5f, (int) (event.getY() - touchPos.y) * 1.5f);
        touchPos.SetValue((int) event.getX(), (int) event.getY());

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                tmp2.SetValue(tmp.x - gameScreen.getCameraX(), tmp.y - gameScreen.getCameraY());
                planetController.SelectPlanet(tmp2);
                if (planetController.isPlanetSelected())
                {
                    toMove.SetValue(planetController.getSelectedPlanet().getMatrix()[12], planetController.getSelectedPlanet().getMatrix()[13]);
                    selectedPlanet = planetController.getSelectedPlanetNum();
                    for(int i = 0; i < AI.getConqueredPlanets().size(); i++)
                    {
                        if(selectedPlanet == AI.getConqueredPlanets().get(i))
                        {
                            TechMSG.attackedPlanet = selectedPlanet;
                            TechMSG.isAttacked = true;
                            AI.calculateLevel();
                            break;
                        }
                    }
                    planetController.doSelectedPlanetFalse();
                    gameScreen.currentCamera.SetValue(GetCameraX(), GetCameraY());
                }
                else
                    toMove.SetFalse();

                ButtonsUpdate();

                CheckButtons();
                break;

            case MotionEvent.ACTION_MOVE:
                if(touchPos.x > Assets.btnRegion.getWidth()/2)
                    gameScreen.MoveCamera(lastTouch);
                break;
            case MotionEvent.ACTION_UP:
                ResetButtons();
                break;
        }
        tmp.SetValue(touchPos);
    }


    public void Update(float eTime)
    {
        planetController.UpdatePlanets();
        player.Update(eTime);
        AI.Update(eTime);
    }



    public void Draw(GLRenderer renderer)
    {
        //renderer.DrawParallaxSprite(background, spaceVelocityCoeff);
        planetController.DrawPlanets(renderer);
        player.Draw(renderer);
        AI.Draw(renderer);
        //loader.DrawStaticSprite(btnRegion);
        moveBtn.Draw(renderer);
        nextStep.Draw(renderer);
        AI.infoPlanetsDraw(renderer);

        //loader.DrawText("AI....", Assets.gsFont, gameScreen.getGlView().getScreenWidth()/2, gameScreen.getGlView().getScreenHeight()/2, 0, Color.RED, 100);
    }

    public void Attacked()
    {

    }



    public void NextTurn()
    {
        AI.InverseMyStep();
        player.setPointsToMove(pointsToMove);
        getPlanetController().NextTurn();
        Save();
        TechMSG.playerMove = true;
        PrepareToPlayerStep();
    }


    public void CheckButtons()
    {
        if(moveBtn.IsClicked())
        {
            Log.i("READY", ""+TechMSG.isFinishedWay);
            if(TechMSG.isFinishedWay && player.getPointsToMove() > 0)
            {
                if(tmp.x > Assets.btnRegion.getWidth()/2)
                {
                    player.followToTap(tmp, toMove, (int) gameScreen.getCameraX(), (int) gameScreen.getCameraY());
                    Log.i("TMP", "x " + tmp.x + " y " + tmp.y);
                    Log.i("TOMOVE", ""+toMove.IsFalse());
                    TechMSG.isNeedMove = true;
                    TechMSG.isFinishedWay = false;
                }
            }
            else
            {
                TechMSG.isNeedMove = true;
            }
        }

        else if(nextStep.IsClicked())
        {
            if(TechMSG.playerMove)
            {
                AI.setPointsToMove(pointsToMove);
                TechMSG.playerMove = false;
                TechMSG.isNeedMove = false;
                PrepareToAIStep();
                player.InverseMyStep();
                AI.behaviorControl();
            }
        }


    }

    public void ButtonsUpdate()
    {
        moveBtn.Update(touchPos);
        nextStep.Update(touchPos);
    }


    public void ResetButtons()
    {
        moveBtn.Reset();
        nextStep.Reset();
    }

    public void PrepareToAIStep()
    {
        moveBtn.Lock();
        nextStep.Lock();
    }

    public void PrepareToPlayerStep()
    {
        moveBtn.Unlock();
        nextStep.Unlock();
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getScreenHeight()
    {
        return gameScreen.getScreenHeight();
    }

    public int getScreenWidth()
    {
        return gameScreen.getScreenWidth();
    }

    public float GetCameraX(){return gameScreen.getCameraX();}

    public float GetCameraY(){return gameScreen.getCameraY();}

    public PlanetController getPlanetController(){return planetController;}

    public void Save()
    {
        gameScreen.getDBController().SaveWorld(this);
        gameScreen.getDBController().SavePlanets(planetController.getPlanets());
        gameScreen.getDBController().SaveCharacter(true, player);
        gameScreen.getDBController().SaveCharacter(false, AI);
    }

}
