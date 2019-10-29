package app.onedayofwar.Campaign.Space;

import android.app.Activity;
import android.content.Intent;
import android.opengl.Matrix;
import android.view.MotionEvent;

import java.util.ArrayDeque;

import app.onedayofwar.Battle.Activities.BattleActivity;
import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Campaign.Activities.ActivityRequests;
import app.onedayofwar.Campaign.System.GameView;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.System.Matrix3;
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
    private float[] matrix;
    private Button newPoints;


    public Space(Activity activity, GameView gameView)
    {
        this.activity = activity;
        this.gameView = gameView;
        Initialize();
    }

    public void Initialize()
    {
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        height = Assets.space.getHeight();
        width = Assets.space.getWidth();
        //camera.setTranslate(-width/2, -height/2);
        touchPos = new Vector2();
        lastTouch = new Vector2();
        xmlParser = new XMLParser(activity.getAssets());
        planetController = new PlanetController(this);
        planetController.LoadPlanets(xmlParser);
        newPoints = new Button(Assets.btnFinishInstallation, 0, (int)(150 * Assets.monitorWidthCoeff + Assets.btnFinishInstallation.getWidth()/2 * Assets.btnCoeff), false);
    }

    public void StartBattle(Planet planet) {
        Intent intent = new Intent(activity, BattleActivity.class);
        intent.putExtra("type", 'c');
        BattlePlayer.fieldSize = planet.getFieldSize();
        BattlePlayer.unitCount = planet.getGroundGuards().clone();
        activity.startActivityForResult(intent, ActivityRequests.START_BATTLE);
    }

    public void SetBattleResult(String damage)
    {
        planetController.attackSelectedPlanet(damage.charAt(0) == '1');
    }

    public void onTouch(MotionEvent event)
    {
        lastTouch.SetValue((int) (event.getX() - touchPos.x) * 1.5f, (int) (event.getY() - touchPos.y) * 1.5f);
        touchPos.SetValue((int) event.getX(), (int) event.getY());

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                planetController.SelectPlanet(touchPos);
                if (planetController.isPlanetSelected())
                    StartBattle(planetController.getSelectedPlanet());
                break;

            case MotionEvent.ACTION_MOVE:
                gameView.MoveCamera(lastTouch);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
    }

    public void Update(float eTime)
    {
        planetController.UpdatePlanets();
    }

    public void Draw(Graphics graphics)
    {
        graphics.DrawSprite(Assets.space, matrix);
        drawStars();
        planetController.DrawPlanets(graphics);
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
        return gameView.screenHeight;
    }

    public int getScreenWidth()
    {
        return gameView.screenWidth;
    }
}
