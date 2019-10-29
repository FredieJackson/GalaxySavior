package app.onedayofwar.Campaign.Space;

import android.graphics.Color;
import android.graphics.RectF;
import android.opengl.Matrix;

import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.Sprite;
import app.onedayofwar.Graphics.Texture;
import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 20.02.2015.
 */
public class Planet
{
    public int oil;
    public int nanoSteel;
    private int syncoCrystals;
    private byte[] spaceGuards;
    private byte[] groundGuards = {1,1,1,1,1,1};
    private byte[] buildings;
    private byte size;
    private int radius;
    private Sprite image;
    private boolean isPlanetConquered;
    private RectF touch;
    private RectF planet;

    public Planet(int oil, int nanoSteel, int syncoCrystals, byte[] spaceGuards, byte[] groundGuards, byte[] buildings, byte size)
    {
        this.oil = oil;
        this.nanoSteel = nanoSteel;
        this.syncoCrystals = syncoCrystals;
        this.spaceGuards = spaceGuards.clone();
        this.groundGuards = groundGuards.clone();
        this.buildings = buildings.clone();
        this.size = size;
        isPlanetConquered = false;
        planet = new RectF();
        touch = new RectF();
    }

    public Planet()
    {
        isPlanetConquered = false;
        planet = new RectF();
        touch = new RectF();
    }

    public void loadToMap(Texture texture, Vector2 position)
    {
        image = new Sprite(texture);

        //matrix.Rotate((float)Math.random()*361, 0, 0);

        image.setPosition(position.x, position.y);
        image.Scale(radius * 2f / image.getHeight());
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
        touch.set(touchPos.x-2,touchPos.y-2,touchPos.x+2,touchPos.y+2);
        planet.set(getMatrix()[12] - radius, getMatrix()[13] - radius, getMatrix()[12]+radius, getMatrix()[13]+radius);
        return touch.intersect(planet);
    }

    //int motionEvents = 0;

    public void Draw(Graphics g)
    {
        if(!isPlanetConquered)
        {
            g.DrawSprite(image);
        }
        g.DrawRect(touch.centerX(), touch.centerY(), 4, 4, Color.RED, false);

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

    public float[] getMatrix()
    {
        return image.matrix;
    }

    public void NextTurn()
    {

    }
}
