package app.onedayofwar.Campaign.Space;

import android.graphics.Color;
import android.opengl.Matrix;
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
    private Vector2 toMove;
    Vector2 dir;
    Space space;
    int pointsToMove;
    int color;
    private Sprite image;
    private int velocity;
    private boolean isLand;
    public int height;
    public int width;
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
        image = new Sprite(Assets.player);
        toMove = new Vector2();
        toMove.SetFalse();
        dir = new Vector2();

        velocity = 1000;
        width = image.getWidth();
        height = image.getHeight();

        image.setPosition(width/2 + Assets.btnRegion.getWidth()/2, height/2);

        color = Color.RED;
        isLand = false;
        firstBattle = false;
        needLand = false;
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



    public void followToTap(Vector2 touchPos, Vector2 forLand, int x, int y) {
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
    }

    public void Update(float eTime)
    {
        if(needLand)
            letsLending(eTime);
        if(isLand && !firstBattle)
        {
            space.StartBattle();
            isLand = false;
            firstBattle = true;
        }

        if(!toMove.IsFalse())
        {
           // Log.i("NORMAL","ok");
            dir.SetValue((toMove.x - getMatrix()[12]), (toMove.y - getMatrix()[13]));
            dir.Normalize();

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
        if(Math.abs(getMatrix()[12] - toMove.x) < 5*velocity*eTime*dir.x && Math.abs(getMatrix()[13] - toMove.y) < 5*velocity*eTime*dir.y)
        {
            Log.i("TEST","ok");
            velocity *= 0.75;
            pointsToMove = velocity;
            image.Scale(0.7f);
            isLand = true;
        }
    }

    public void setPointsToMove(int value){pointsToMove = value;}

    public int getPointsToMove(){return pointsToMove;}

    public Sprite getImage(){return image;}

}
