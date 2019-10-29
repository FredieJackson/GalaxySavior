package app.onedayofwar.Campaign.Space;

import android.app.Activity;
import android.graphics.Color;
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
    private Vector2 tmp2;
    private int width;
    private int height;
    private Sprite background;
    private Sprite btnRegion;
    private Vector2 toMove;
    private Vector2 tmp;
    private Button moveBtn;
    private Button nextTurnBtn;
    private PlayerInfo player;
    private int color;
    private int selectedPlanet;
    private int pointsToMove;
    private float spaceVelocityCoeff;


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
        background.Scale(2*(float)Assets.bgWidthCoeff, 2*(float)Assets.bgHeightCoeff);
        background.setPosition(getScreenWidth()/2 + Assets.btnRegion.getWidth()/2, getScreenHeight()/2);
        btnRegion.setPosition(0, Assets.btnRegion.getHeight()/2);
        touchPos = new Vector2();
        lastTouch = new Vector2();
        pointsToMove = 30;
        xmlParser = new XMLParser(activity.getAssets());
        planetController = new PlanetController(this);
        planetController.LoadPlanets(xmlParser);
        player = new PlayerInfo(this, pointsToMove);
        color = Color.RED;
        toMove = new Vector2();
        toMove.SetFalse();
        moveBtn = new Button(Assets.btnInstall,(int)(Assets.btnInstall.getWidth()*Assets.btnCoeff/2),(int)(Assets.btnInstall.getHeight() * Assets.btnCoeff/2),false);
        nextTurnBtn = new Button(Assets.btnFinishInstallation,(int)(Assets.btnFinishInstallation.getWidth()*Assets.btnCoeff/2),(int)(200 + Assets.btnFinishInstallation.getHeight() * Assets.btnCoeff/2), false);
        moveBtn.Scale(Assets.btnCoeff);
        nextTurnBtn.Scale(Assets.btnCoeff);

        spaceVelocityCoeff = Assets.space.getHeight()*1.0f/(width*2);
        tmp = new Vector2();
        tmp2 = new Vector2();
    }

    public void GotoPlanet()
    {
        gameView.getGlView().changeScreen(new PlanetView(gameView.getGlView(), this, planetController.getPlanet(selectedPlanet)));
    }

    public PlayerInfo getPlayer()
    {
        return player;
    }

    public void onTouch(MotionEvent event)      //Здесь весь метод переделан
    {
        lastTouch.SetValue((int) (event.getX() - touchPos.x) * 1.5f, (int) (event.getY() - touchPos.y) * 1.5f);
        touchPos.SetValue((int) event.getX(), (int) event.getY());

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                tmp2.SetValue(tmp.x - gameView.getCameraX(), tmp.y - gameView.getCameraY());
                planetController.SelectPlanet(tmp2);
                if (planetController.isPlanetSelected())
                {
                    toMove.SetValue(planetController.getSelectedPlanet().getMatrix()[12], planetController.getSelectedPlanet().getMatrix()[13]);
                    planetController.doSelectedPlanetFalse();
                }
                else
                    toMove.SetFalse();

                moveBtn.Update(touchPos);
                nextTurnBtn.Update(touchPos);

                if(moveBtn.IsClicked() && tmp.x > Assets.btnRegion.getWidth()/2)
                    player.followToTap(tmp, toMove, (int)gameView.getGlView().getCameraX(), (int)gameView.getGlView().getCameraY());

                if(nextTurnBtn.IsClicked())
                {
                    planetController.NextTurn();
                    player.setPointsToMove(pointsToMove);
                    player.getImage().removeColorFilter();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(touchPos.x > Assets.btnRegion.getWidth()/2)
                    gameView.MoveCamera(lastTouch);
                break;
            case MotionEvent.ACTION_UP:
                moveBtn.Reset();
                nextTurnBtn.Reset();
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
        graphics.DrawParallaxSprite(background, spaceVelocityCoeff);
        drawStars();
        planetController.DrawPlanets(graphics);
        player.Draw(graphics);
        graphics.DrawStaticSprite(btnRegion);
        moveBtn.Draw(graphics);
        nextTurnBtn.Draw(graphics);
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
