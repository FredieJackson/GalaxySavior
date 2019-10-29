package app.onedayofwar.Campaign.Space;

import android.opengl.Matrix;
import android.util.Log;

import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 20.02.2015.
 */
public class Planet
{
    private int oil;
    private int nanoSteel;
    private int syncoCrystals;
    private byte[] spaceGuards;
    private byte[] groundGuards;
    private byte[] buildings;
    private byte size;
    private int radius;
    public float[] matrix;
    private Sprite image;
    private boolean isPlanetConquered;

    int num = 0;

    public Planet(int oil, int nanoSteel, int syncoCrystals, byte[] spaceGuards, byte[] groundGuards, byte[] buildings, byte size)
    {
        this.oil = oil;
        this.nanoSteel = nanoSteel;
        this.syncoCrystals = syncoCrystals;
        this.spaceGuards = spaceGuards.clone();
        this.groundGuards = groundGuards.clone();
        this.buildings = buildings.clone();
        this.size = size;
        matrix  = new float[16];
        Matrix.setIdentityM(matrix, 0);
        isPlanetConquered = false;
    }

    public Planet(int num)
    {
        this.num = num;
        matrix  = new float[16];
        Matrix.setIdentityM(matrix, 0);
        isPlanetConquered = false;
    }

    public void loadToMap(Sprite image, Vector2 position)
    {
        this.image = image;

        //matrix.Rotate((float)Math.random()*361, 0, 0);

        Matrix.translateM(matrix, 0, position.x, position.y, 0);

        Matrix.scaleM(matrix, 0, radius * 2f / image.getHeight(), -radius * 2f / image.getHeight(), 1);
    }

    public byte[] getGroundGuards()
    {
        return groundGuards;
    }

    public byte getFieldSize()
    {
        return size;
    }

    public boolean Select(Vector2 touchPos)
    {
        //mPos[0] = position.x; mPos[1] = position.y;
        //m.mapPoints(mPos);
        //matrix.set(m);
        //return !isPlanetConquered && (touchPos.x > mPos[0] && touchPos.x < mPos[0] + image.getWidth()) && (touchPos.y > mPos[1] && touchPos.y < mPos[1] + image.getHeight());
        return false;
    }

    //int motionEvents = 0;

    public void Draw(Graphics g)
    {
        if(!isPlanetConquered)
        {
            g.DrawSprite(image, matrix);

            //g.drawText("0", 50, (int)tmp.Get(12) + radius, (int)tmp.Get(13) + radius, Color.RED);
            //g.drawRect(tmp.GetX(), tmp.GetY(), 2*radius, 2*radius, Color.YELLOW, false);
        }


        //motionEvents++;
    }

    public void Update()
    {
        //matrix.Rotate(5, 3*radius, 3*radius);
    }

    public void ConquerPlanet()
    {

        //isPlanetConquered = true;



        /*for (int i = 0; i < 4; i++)
        {
            Log.i("PLANET", matrix.Get(0 + i * 4) + " | " + matrix.Get(1 + i * 4) + " | " + matrix.Get(2 + i * 4)  + " | " + matrix.Get(3 + i * 4));
        }
        Log.i("PLANET", "---");*/
    }

    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    public int getRadius()
    {
        return radius;
    }
}
