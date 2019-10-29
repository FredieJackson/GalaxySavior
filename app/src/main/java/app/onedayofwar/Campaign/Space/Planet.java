package app.onedayofwar.Campaign.Space;

import android.graphics.Color;
import android.graphics.RectF;
import android.util.Log;

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
    public int credits;
    private byte[] spaceGuards = {0,0,0,0,0,0};
    private byte[] groundGuards = {0,0,0,0,0,0};
    //0-credits; 1-oil; 2-nanosteel; 3-factory; 4-workshop;
    private byte[] buildings = {1, 1, 1, 0, 0};
    private byte size;
    private int radius;
    private Sprite image;
    private boolean isPlanetConquered;
    private RectF touch;
    private RectF planet;
    public Vector2 buildingUpgrade;
    public String upgradeName;

    public Planet(int oil, int nanoSteel, int credits, byte[] spaceGuards, byte[] groundGuards, byte[] buildings, byte size)
    {
        this.oil = oil;
        this.nanoSteel = nanoSteel;
        this.credits = credits;
        this.spaceGuards = spaceGuards.clone();
        this.groundGuards = groundGuards.clone();
        this.buildings = buildings.clone();
        this.size = size;
        isPlanetConquered = false;
        planet = new RectF();
        touch = new RectF();
        buildingUpgrade = new Vector2();
        buildingUpgrade.SetFalse();
    }

    public Planet()
    {
        isPlanetConquered = false;
        planet = new RectF();
        touch = new RectF();
        buildingUpgrade = new Vector2();
        buildingUpgrade.SetFalse();
        Log.i("PLANET", getGroundGuards().toString());
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

    public byte[] getBuildings()
    {
        return buildings;
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

    public void Draw(Graphics g)
    {
        g.DrawSprite(image);
        if(isPlanetConquered)
            g.DrawText("+", Assets.arialFont, getMatrix()[12], getMatrix()[13], 0, buildingUpgrade.IsFalse() ? Color.YELLOW : Color.RED , 72);
    }

    public void Update()
    {
        //matrix.Rotate(5, 3*radius, 3*radius);
    }

    public void ConquerPlanet()
    {

        isPlanetConquered = true;
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
        if(!buildingUpgrade.IsFalse())
        {
            buildingUpgrade.y--;
            if(buildingUpgrade.y == 0)
            {
                buildings[(int)buildingUpgrade.x]++;
                buildingUpgrade.SetFalse();
            }
        }
        else
        {
            credits += buildings[1] * 20;
            oil += buildings[1] * 40;
            nanoSteel += buildings[1] * 30;
        }
    }

    public boolean IsConquered()
    {
        return isPlanetConquered;
    }

    public void UpgradeBuilding(int building)
    {
        if(!buildingUpgrade.IsFalse())
            return;
        switch (building)
        {
            case 0:
                if(credits >= buildings[0] * 500 && nanoSteel >= buildings[0] * 650)
                {
                    credits -= buildings[0] * 500;
                    nanoSteel -= buildings[0] * 650;
                    buildingUpgrade.SetValue(0, buildings[0] * 10 + (buildings[0] == 0 ? 5 : 0));
                    upgradeName = "MARKET";
                }
                break;
            case 1:
                if(credits >= buildings[1] * 500 && nanoSteel >= buildings[1] * 650)
                {
                    credits -= buildings[1] * 500;
                    nanoSteel -= buildings[1] * 650;
                    buildingUpgrade.SetValue(1, buildings[1] * 10 + (buildings[1] == 0 ? 5 : 0));
                    upgradeName = "OIL DRILL";
                }
                break;
            case 2:
                if(credits >= buildings[2] * 500 && nanoSteel >= buildings[2] * 650)
                {
                    credits -= buildings[2] * 500;
                    nanoSteel -= buildings[2] * 650;
                    buildingUpgrade.SetValue(2, buildings[2] * 10 + (buildings[2] == 0 ? 5 : 0));
                    upgradeName = "NANOSTEEL MINES";
                }
                break;
            case 3:
                if(credits >= buildings[3] * 500 && nanoSteel >= buildings[3] * 650)
                {
                    credits -= buildings[3] * 500;
                    nanoSteel -= buildings[3] * 650;
                    buildingUpgrade.SetValue(3, buildings[3] * 10 + (buildings[3] == 0 ? 5 : 0));
                    upgradeName = "FACTORY";
                }
                break;
            case 4:
                if(credits >= buildings[4] * 500 && nanoSteel >= buildings[4] * 650)
                {
                    credits -= buildings[4] * 500;
                    nanoSteel -= buildings[4] * 650;
                    buildingUpgrade.SetValue(4, buildings[4] * 10 + (buildings[4] == 0 ? 5 : 0));
                    upgradeName = "WORKSHOP";
                }
                break;
        }
    }

    public void CreateUnit(int unit)
    {

    }
}
