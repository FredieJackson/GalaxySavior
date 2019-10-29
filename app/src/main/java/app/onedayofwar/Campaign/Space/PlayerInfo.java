package app.onedayofwar.Campaign.Space;

import android.graphics.Color;
import android.util.Log;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Никита on 06.03.2015.
 */
public class PlayerInfo
{
    private byte[] army;
    private Vector2 toMove;
    private Vector2 lastV;
    private Vector2 newV;
    Vector2 dir;
    Space space;
    int pointsToMove;
    int color;
    private Sprite image;
    private int velocity;
    private boolean isLand;
    public int height;
    public int width;
    private int counter;
    public float angle;
    private boolean needRotate;
    private boolean firstBattle;
    private boolean needLand;


    public PlayerInfo(Space space, int pointsToMove)
    {
        this.space = space;
        this.pointsToMove = pointsToMove;
        Initialize();
    }

    public void Initialize()
    {
        army = new byte[]{4, 0, 0, 0, 0, 0};
        image = new Sprite(Assets.player);
        toMove = new Vector2();
        toMove.SetFalse();
        dir = new Vector2();

        dir.SetFalse();
        lastV = new Vector2();
        newV = new Vector2();

        velocity = 1000;
        width = image.getWidth();
        height = image.getHeight();
        counter = 0;

        image.setPosition(width/2, height/2);

        color = Color.RED;
        isLand = false;
        firstBattle = false;
        needLand = false;
        needRotate = false;
    }

    public float[] getMatrix()
    {
        return image.matrix;
    }

    public void Draw(Graphics graphics)
    {

        graphics.DrawSprite(image);
        //graphics.("" + pointsToMove, 15, 50, 50, color);
    }



    public void followToTap(Vector2 touchPos, Vector2 forLand, int x, int y)
    {
        if(!forLand.IsFalse())
        {
            toMove.SetValue(forLand);
            needLand = true;
        }
        else
        {
            toMove.SetValue(touchPos.x - x, touchPos.y - y);
            needLand = false;
        }
        if(!dir.IsFalse())
        {
            lastV.SetValue(dir.x*velocity,dir.y*velocity);
            newV.SetValue(toMove.x - image.matrix[12], toMove.y - image.matrix[13]);
            angle = (float)Math.acos((lastV.x*newV.x + lastV.y*newV.y)/(Math.sqrt(lastV.x*lastV.x + lastV.y*lastV.y)*Math.sqrt(newV.x*newV.x + newV.y*newV.y)));
            needRotate = true;
            angle *= 180/Math.PI;
        }
        else
        {
            lastV.SetValue(0,velocity);
            newV.SetValue(toMove.x - image.matrix[12], toMove.y - image.matrix[13]);
            angle = (float)Math.acos((lastV.x*newV.x + lastV.y*newV.y)/(Math.sqrt(lastV.x*lastV.x + lastV.y*lastV.y)*Math.sqrt(newV.x*newV.x + newV.y*newV.y)));
            needRotate = true;
            angle *= 180/Math.PI;
        }
        Log.i("ANGLE", ""+angle);
        dir.SetValue((toMove.x - getMatrix()[12]), (toMove.y - getMatrix()[13]));
        dir.Normalize();
    }

    public void Update(float eTime)
    {
        if(needRotate)
        {
            image.Rotate(angle/5, 0, 0, 1);
            counter++;
            if(counter == 5)
            {
                needRotate = false;
                counter = 0;
            }
        }
        if(needLand)
            letsLending(eTime);
        if(isLand && !firstBattle)
        {
            space.GotoPlanet();
            isLand = false;
            firstBattle = true;
        }

        if(!toMove.IsFalse())
        {
            if(Math.abs(toMove.x - getMatrix()[12]) < velocity*eTime*dir.x || Math.abs(getMatrix()[13] - toMove.y) < velocity*eTime*dir.y)
                toMove.SetFalse();


                if(!toMove.IsFalse() && pointsToMove > 0){
                    getMatrix()[12] += (int)(velocity*dir.x*eTime);
                    getMatrix()[13] += (int)(velocity*dir.y*eTime);
                    if(!isLand)
                        pointsToMove--;
                    if(pointsToMove == 0)
                    {
                        toMove.SetFalse();
                        image.setColorFilter(color);
                    }
                }
        }
    }

    public void letsLending(float eTime)
    {
        if(Math.abs(getMatrix()[12] - toMove.x) < 5*velocity*eTime*dir.x || Math.abs(getMatrix()[13] - toMove.y) < 5*velocity*eTime*dir.y)
        {
            Log.i("TEST","ok");
            velocity *= 0.75;
            pointsToMove = velocity;
            image.Scale(0.7f);
            isLand = true;
        }
    }

    public byte[] getArmy()
    {
        return army;
    }

    public void setPointsToMove(int value){pointsToMove = value;}

    public int getPointsToMove(){return pointsToMove;}

    public Sprite getImage(){return image;}

}
