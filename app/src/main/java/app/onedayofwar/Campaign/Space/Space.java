package app.onedayofwar.Campaign.Space;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

import app.onedayofwar.Campaign.System.PlanetView;
import app.onedayofwar.Campaign.System.GameView;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;
import app.onedayofwar.System.XMLParser;
import app.onedayofwar.UI.Button;

/**
 * Created by Slava on 24.02.2015.
 */
public class Space
{
    private XMLParser xmlParser;
    private PlanetController planetController;
    private Activity activity;
    private GameView gameView;
    private Vector2 touchPos;
    private Vector2 lastTouch;
    private int width;
    private int height;
    private Sprite background;
    private Sprite btnRegion;
    private Vector2 toMove;
    private Vector2 tmp;
    private Button ok;
    private Button newPoints;
    private PlayerInfo player;
    private int color;
    private int selectedPlanet;
    private int pointsToMove;


    public Space(Activity activity, GameView gameView)
    {
        this.activity = activity;
        this.gameView = gameView;
        Initialize();
    }

    public void Initialize()            //Тут сам сравни по ошибкам, которые выскочат что нового есть, еще нужно в GameView добавить всякие коэфициенты кнопок и т.д
    {
        background = new Sprite(Assets.space);
        btnRegion = new Sprite(Assets.btnRegion);
        height = 2000;
        width = 2000;
        background.setPosition(getScreenWidth()/2 + Assets.btnRegion.getWidth()/2, getScreenHeight()/2);
        btnRegion.setPosition(0, Assets.btnRegion.getHeight()/2);
        touchPos = new Vector2();
        lastTouch = new Vector2();
        pointsToMove = 100000;
        xmlParser = new XMLParser(activity.getAssets());
        planetController = new PlanetController(this);
        planetController.LoadPlanets(xmlParser);
        player = new PlayerInfo(this, pointsToMove);
        color = Color.RED;
        toMove = new Vector2();
        toMove.SetFalse();
        ok = new Button(Assets.btnInstall,(int)(Assets.btnInstall.getWidth()*Assets.btnCoeff/2),(int)(Assets.btnInstall.getHeight() * Assets.btnCoeff/2),false);
        newPoints = new Button(Assets.btnFinishInstallation,(int)(Assets.btnFinishInstallation.getWidth()*Assets.btnCoeff/2),(int)(200 + Assets.btnFinishInstallation.getHeight() * Assets.btnCoeff/2), false);
        tmp = new Vector2();
    }

    public void StartBattle()
    {
        //Метод в основном новый
        Log.i("START BATTLE", "");
        gameView.getGlView().changeScreen(new PlanetView(gameView.getGlView(), planetController.getPlanet(selectedPlanet)));
    }

    public void SetBattleResult(String damage)
    {
        planetController.attackSelectedPlanet(damage.charAt(0) == '1');
    }

    public void onTouch(MotionEvent event)      //Здесь весь метод переделан
    {
        lastTouch.SetValue((int) (event.getX() - touchPos.x) * 1.5f, (int) (event.getY() - touchPos.y) * 1.5f);
        touchPos.SetValue((int) event.getX(), (int) event.getY());

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                planetController.SelectPlanet(tmp);
                if (planetController.isPlanetSelected())
                {
                    toMove.SetValue(planetController.getSelectedPlanet().getMatrix()[12], planetController.getSelectedPlanet().getMatrix()[13]);
                    planetController.doSelectedPlanetFalse();
                }
                else
                    toMove.SetFalse();

                ok.Update(touchPos);
                newPoints.Update(touchPos);

                if(ok.IsClicked() && tmp.x > Assets.btnRegion.getWidth()/2)
                    player.followToTap(tmp, toMove, (int)gameView.getGlView().getCameraX(), (int)gameView.getGlView().getCameraY());

                if(player.getPointsToMove() == 0 && newPoints.IsClicked())
                {
                    player.setPointsToMove(pointsToMove);
                    player.getImage().removeColorFilter();
                }



                break;

            case MotionEvent.ACTION_MOVE:
                if(touchPos.x > Assets.btnRegion.getWidth()/2)
                    gameView.MoveCamera(lastTouch);
                break;
            case MotionEvent.ACTION_UP:
                ok.Reset();
                newPoints.Reset();
                break;
        }
        tmp.SetValue(touchPos);
        }


    public void Update(float eTime)
    {
        planetController.UpdatePlanets();
        player.Update(eTime);           //Одна новая команда
    }

    public void Draw(Graphics graphics)     //Тут тоже парочка
    {
        graphics.DrawParallaxSprite(background);
        drawStars();
        planetController.DrawPlanets(graphics);
        player.Draw(graphics);
        graphics.DrawStaticSprite(btnRegion);
        ok.Draw(graphics);
        newPoints.Draw(graphics);
    }

    public void drawStars()
    {

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
        return gameView.getGlView().getScreenHeight();
    }

    public int getScreenWidth()
    {
        return gameView.getGlView().getScreenWidth();
    }
}
