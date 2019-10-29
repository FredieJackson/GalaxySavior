package app.onedayofwar.Campaign.Space;

import android.graphics.Color;

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
    private Vector2 dir;
    private Vector2 angleVector;
    private float rightAngle;
    private float leftAngle;
    private Space space;
    private int pointsToMove;
    private int color;
    private Sprite image;
    private int velocity;
    private int currentVelocity;
    private int counter;
    private int counterLand;
    public int height;
    public int width;
    public double angle;
    private boolean isLand;
    private boolean needRotate;
    private boolean needLand;
    private boolean needTakeOff;


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
        angleVector = new Vector2();

        velocity = 1000;
        currentVelocity = velocity;
        width = image.getWidth();
        height = image.getHeight();

        image.setPosition(width/2 + 500, height/2+350);

        color = Color.RED;
        isLand = false;
        needLand = false;
        needRotate = false;
        needTakeOff = false;
    }

    public float[] getMatrix()
    {
        return image.matrix;
    }

    public void Draw(Graphics graphics)
    {

        graphics.DrawSprite(image);
        //graphics.("" + pointsToMove, 15, 50, 50, color);
        if(!toMove.IsFalse())
            graphics.DrawLine(image.matrix[12], image.matrix[13], toMove.x, toMove.y, color);
        graphics.DrawLine(image.matrix[12], image.matrix[13], image.matrix[12] + lastV.x, image.matrix[13]+lastV.y, Color.WHITE);
        graphics.DrawLine(image.matrix[12], image.matrix[13],image.matrix[12] + dir.x*100, image.matrix[13] + dir.y*100, Color.BLUE);
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

        if(!toMove.IsFalse())
        {
            if(!dir.IsFalse())
                lastV.SetValue(dir.x, dir.y);
            else
                lastV.SetValue(0, velocity);
            dir.SetValue((toMove.x - image.matrix[12]), (toMove.y - image.matrix[13]));
        }

        angle = Math.acos((lastV.x*dir.x + lastV.y*dir.y)/(Math.sqrt(lastV.x*lastV.x + lastV.y*lastV.y)*Math.sqrt(dir.x*dir.x+dir.y*dir.y)));
        angle *= 180f/Math.PI;
        needRotate = true;
        angleVector.SetValue(lastV.y, -lastV.x);
        rightAngle = (float)(Math.acos((angleVector.x*dir.x + angleVector.y*dir.y)/(Math.sqrt(angleVector.x*angleVector.x + angleVector.y*angleVector.y)*Math.sqrt(dir.x*dir.x + dir.y*dir.y))));
        angleVector.SetValue(-lastV.y, lastV.x);
        leftAngle = (float)(Math.acos((angleVector.x*dir.x + angleVector.y*dir.y)/(Math.sqrt(angleVector.x*angleVector.x + angleVector.y*angleVector.y)*Math.sqrt(dir.x*dir.x + dir.y*dir.y))));
        if(leftAngle < rightAngle)
        {
            angle *= -1;
        }
        dir.Normalize();
    }

    public void Update(float eTime)
    {
        if(needRotate)
        {
            image.Rotate((float)angle/10, 0, 0, 1);
            counter++;
            if(counter == 10)
            {
                needRotate = false;
                counter = 0;
            }
        }

        if(needLand)
            letsLending(eTime);

        if (needTakeOff)
        {
            counterLand++;
            if(counterLand == 5)
            {
                needTakeOff = false;
                counterLand = 0;
            }
            image.Scale(1.43f);
            currentVelocity = velocity;
            needLand = false;
        }

        if(isLand)
        {
            currentVelocity *= 0.75;
            pointsToMove = velocity;
            image.Scale(0.7f);
            counterLand++;
            if(counterLand == 5)
            {
                isLand = false;
                counterLand = 0;
                space.GotoPlanet();
            }
        }

        if(!toMove.IsFalse())
        {
            if(Math.abs(toMove.x - getMatrix()[12]) < Math.abs((int)(currentVelocity*eTime*dir.x))  || Math.abs(getMatrix()[13] - toMove.y) < Math.abs((int)(currentVelocity*eTime*dir.y)))
                toMove.SetFalse();

            if(!toMove.IsFalse() && pointsToMove > 0)
            {
                image.Move(currentVelocity*dir.x*eTime, currentVelocity*dir.y*eTime);
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
        if(Math.abs(getMatrix()[12] - toMove.x) < Math.abs(5*currentVelocity*eTime*dir.x) || Math.abs(getMatrix()[13] - toMove.y) < Math.abs(5*currentVelocity*eTime*dir.y))
            isLand = true;
    }

    public void lestTakeOff()
    {
        needTakeOff = true;
    }

    public byte[] getArmy()
    {
        return army;
    }

    public void setPointsToMove(int value){pointsToMove = value;}

    public int getPointsToMove(){return pointsToMove;}

    public Sprite getImage(){return image;}

}