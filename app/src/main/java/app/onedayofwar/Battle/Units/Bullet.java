package app.onedayofwar.Battle.Units;

import android.opengl.Matrix;
import android.util.Log;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 31.01.2015.
 */
public class Bullet
{
    private Vector2 destination;
    private Sprite image;
    private float velocity;
    private float velocityY;
    private byte type;
    private float[] matrix;
    private float curveAngle;
    private float lastAngle;
    public enum State { FLY, LAUNCH, BOOM}
    public State state;

    public Bullet()
    {
        state = State.LAUNCH;
        destination = new Vector2();
        image = Assets.bullet;
        velocity = 300;
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
    }

    public void Draw(Graphics graphics)
    {
        if(state == State.FLY)
        {
            graphics.DrawSprite(image, matrix);
        }
    }

    public void Update(float eTime)
    {
        if(matrix[13] + image.getHeight() >= destination.y)
        {
            state = State.BOOM;
            destination.SetZero();
            lastAngle = 0;
            curveAngle = 0;
            Matrix.setIdentityM(matrix, 0);
            return;
        }
        velocityY = destination.y / destination.x / destination.x * matrix[12] * matrix[12] - matrix[13];
        curveAngle = (float)(Math.atan(2d * destination.y * matrix[12] / destination.x / destination.x) * 180 / Math.PI);
        matrix[12] += velocity * eTime;
        matrix[13] += velocityY;
        Matrix.rotateM(matrix, 0, curveAngle - lastAngle, 0, 0, 1);
        lastAngle = curveAngle;
    }

    public void Launch(float x, float y, byte type)
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
    }

    public void Reload()
    {
        state = State.LAUNCH;
    }
}
