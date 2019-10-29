package app.onedayofwar.Game.Campaign.CharacterControl;

import java.util.ArrayList;

import app.onedayofwar.GEngine.GLRenderer;
import app.onedayofwar.GEngine.Loader;
import app.onedayofwar.GEngine.Meshes.Sprite;
import app.onedayofwar.Game.Campaign.Space.Space;

/**
 * Created by Никита on 06.04.2015.
 */
public abstract class Character
{
    Sprite image;
    MoveBehavior moveBehavior;
    Space space;
    int velocity;
    int pointsToMove;
    int height;
    int width;
    boolean myStep;
    byte[] gArmy;
    byte[] sArmy;
    int[] resources;
    ArrayList<Integer> conqueredPlanets;

    protected Character(Space space, int pointsToMove)
    {
        this.space = space;
        this.pointsToMove = pointsToMove;
        resources = new int[3];
        Initialize();
    }

    protected abstract void Initialize();

    public void Draw(GLRenderer renderer)
    {
        renderer.DrawSprite(image);
    }

    public float[] getMatrix()
    {
        return image.matrix;
    }

    public byte[] getSArmy()
    {
        return sArmy;
    }

    public byte[] getGArmy()
    {
        return gArmy;
    }

    public Sprite getImage(){return image;}

    public void setPointsToMove(int value){moveBehavior.setPointsToMove(value); TechMSG.isStopped = false; myStep = true;}

    public int getPointsToMove(){return moveBehavior.getPointsToMove();}

    public int[] getResources()
    {
        return resources;
    }

    public ArrayList<Integer> getConqueredPlanets(){return conqueredPlanets;}

    public void DBLoad(float x, float y, int movePoints, int[] resources, byte[] gArmy, byte[] sArmy)
    {
        this.resources = resources.clone();
        this.sArmy = sArmy.clone();
        this.gArmy = gArmy.clone();
        moveBehavior.setPointsToMove(movePoints);
        getMatrix()[12] = x;
        getMatrix()[13] = y;

    }
    public void InverseMyStep(){myStep = false;}
}
