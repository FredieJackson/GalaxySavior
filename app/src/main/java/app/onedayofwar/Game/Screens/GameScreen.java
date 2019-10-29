package app.onedayofwar.Game.Screens;

import android.opengl.Matrix;
import android.view.MotionEvent;

import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.GEngine.Assets;
import app.onedayofwar.GEngine.GLRenderer;
import app.onedayofwar.GEngine.Loader;
import app.onedayofwar.Game.Campaign.Space.Space;
import app.onedayofwar.Game.Screens.Screen;
import app.onedayofwar.Utils.IO.DBController;
import app.onedayofwar.Utils.Vector2;

/**
 * Created by Slava on 16.02.2015.
 */
public class GameScreen implements Screen
{
    private Space space;
    private DBController dbController;
    private boolean isNewGame;
    private ScreenController screenController;


    private int turns;
    public Vector2 currentCamera;



    public GameScreen(ScreenController screenController, DBController dbController, boolean isNewGame)
    {
        this.dbController = dbController;
        this.isNewGame = isNewGame;
        this.screenController = screenController;
    }

    public void Initialize(Loader loader)
    {
        turns = 0;
        space = new Space(this);
        space.Initialize(loader);

        currentCamera = new Vector2();
    }

    public void OnTouch(MotionEvent event)
    {
        space.onTouch(event);
    }

    @Override
    public void Resume()
    {
        //glView.getActivity().gameState = MainActivity.GameState.CAMPAIGN;
        //glView.setCamera(currentCamera.x, currentCamera.y);
    }


    public void Update(float eTime)
    {
        space.Update(eTime);
    }

    public void MoveCamera(Vector2 velocity)
    {
        if(getCameraX() + velocity.x > Assets.btnRegion.getWidth())
            velocity.x = -getCameraX() + Assets.btnRegion.getWidth();
        else if(getScreenWidth() - (velocity.x + getCameraX()) > space.getWidth())
            velocity.x = -(space.getWidth() + getCameraX() - getScreenWidth());

        if(getCameraY() + velocity.y > 0)
            velocity.y = -getCameraY();
        else if(getScreenHeight() - (velocity.y + getCameraY()) > space.getHeight())
            velocity.y = -(space.getHeight() + getCameraY() - getScreenHeight());

        viewMatrix[12] += velocity.x;
        viewMatrix[13] += velocity.y;
    }

    public void Draw(GLRenderer renderer)
    {
        space.Draw(renderer);
    }

    public float getCameraX(){ return viewMatrix[12];}

    public float getCameraY()
    {
        return viewMatrix[13];
    }

    public int getScreenWidth()
    {
        return screenController.screenWidth;
    }

    public int getScreenHeight()
    {
        return screenController.screenHeight;
    }

    public boolean IsNewGame()
    {
        return isNewGame;
    }

    public DBController getDBController()
    {
        return dbController;
    }
}
