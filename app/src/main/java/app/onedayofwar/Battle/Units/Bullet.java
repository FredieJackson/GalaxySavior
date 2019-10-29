package app.onedayofwar.Battle.Units;

import android.graphics.Color;
import android.graphics.Matrix;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 31.01.2015.
 */
public class Bullet
{
    private Vector2 position;
    private Vector2 destination;
    private Sprite image;
    private int velocity;
    private int velocityY;
    private byte type;
    private Matrix matrix;
    private float curveAngle;
    private float lastAngle;
    public enum State { FLY, LAUNCH, BOOM}
    public State state;

    public Bullet()
    {
        state = State.LAUNCH;
        position = new Vector2();
        destination = new Vector2();
        image = Assets.bullet;
        velocity = 30;
        matrix = new Matrix();
    }

    public void Draw(Graphics g)
    {
        if(state == State.FLY)
        {
            g.drawSprite(image, matrix, null);
            g.drawText(".", 30, position.x, position.y + image.getHeight(), Color.GREEN);
        }
    }

    public void Update(float eTime)
    {
        if(position.y + image.getHeight() >= destination.y)
        {
            state = State.BOOM;
            destination.SetZero();
            position.SetZero();
            lastAngle = 0;
            curveAngle = 0;
            matrix.reset();
            matrix.setTranslate(-image.getWidth()/2, -image.getHeight()/2);
            return;
        }
        velocityY = (int)((float)destination.y / destination.x / destination.x * position.x * position.x) - position.y;
        curveAngle = (float)(Math.atan((double)2 * destination.y * position.x / destination.x / destination.x) * 180 / Math.PI);
        position.x += (int)(velocity * eTime);
        position.y += velocityY;
        matrix.postTranslate((int)(velocity * eTime), velocityY);
        matrix.postRotate(curveAngle - lastAngle, position.x, position.y);
        lastAngle = curveAngle;
    }

    public void Launch(int x, int y, byte type)
    {
        state = State.FLY;
        destination.SetValue(x,y);
        this.type = type;
        switch(type)
        {
            case 0:
                image = Assets.bullet;
                break;
            case 1:
                image = Assets.miniRocket;
                break;
        }
        matrix.setTranslate(-image.getWidth()/2, -image.getHeight()/2);
    }

    public void Reload()
    {
        state = State.LAUNCH;
    }
}
