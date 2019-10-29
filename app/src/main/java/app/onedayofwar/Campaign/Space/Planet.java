package app.onedayofwar.Campaign.Space;

import android.graphics.Color;
import android.graphics.RectF;

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
    private int[] resources;
    private int[] capacities;
    private byte[] spaceGuards = {0,0,0,0,0,0};//DROID, AKIRA, DEFAINT, STORM, BIOSHIP, BIRD
    private byte[] groundGuards = {0,0,0,0,0,0}; //ROBOT, IVF, ROCKET, TANK,TURRET,SONDER
    //0-credits; 1-oil; 2-nanosteel; 3-factory; 4-workshop;
    private byte[] buildings = {1, 1, 1, 1, 0};
    //0-credits; 1-oil; 2-nanosteel;
    private byte size;
    private int radius;
    private Sprite image;
    private Sprite stroke;
    private Texture texture;
    private boolean isPlanetConquered;
    private RectF touch;
    private RectF planet;
    public Vector2 buildingUpgrade;
    public Vector2 unitGroundUpgrade;
    public Vector2 unitSpaceUpgrade;
    public Vector2 factoryCreation;
    public String upgradeName;
    public String unitUpgradeName;


    public Planet(int oil, int nanosteel, int credits, byte[] spaceGuards, byte[] groundGuards, byte[] buildings, byte size)
    {
        resources = new int[3];
        capacities = new int[3];
        resources[0] = credits;
        resources[1] = oil;
        resources[2] = nanosteel;
        this.spaceGuards = spaceGuards.clone();
        this.groundGuards = groundGuards.clone();
        this.buildings = buildings.clone();
        this.size = size;
        isPlanetConquered = false;
        planet = new RectF();
        touch = new RectF();
        buildingUpgrade = new Vector2();
        buildingUpgrade.SetFalse();
        unitGroundUpgrade = new Vector2();
        unitGroundUpgrade.SetFalse();
        unitSpaceUpgrade = new Vector2();
        unitSpaceUpgrade.SetFalse();
        factoryCreation = new Vector2();
        factoryCreation.SetFalse();
    }

    public Planet()
    {
        resources = new int[3];
        capacities = new int[3];
        isPlanetConquered = false;
        planet = new RectF();
        touch = new RectF();
        buildingUpgrade = new Vector2();
        buildingUpgrade.SetFalse();
        unitGroundUpgrade = new Vector2();
        unitGroundUpgrade.SetFalse();
        unitSpaceUpgrade = new Vector2();
        unitSpaceUpgrade.SetFalse();
        factoryCreation = new Vector2();
        factoryCreation.SetFalse();
    }

    public void loadToMap(Graphics graphics, String path, Vector2 position)
    {
        texture = graphics.LoadTexture(path);
        image = new Sprite(texture);
        image.setPosition(position.x, position.y);
        image.Scale(radius * 2f / image.getHeight());

        stroke = new Sprite(Assets.planetStroke);
        stroke.setPosition(position.x, position.y);
        stroke.Scale(radius * 2.6f / stroke.getHeight());

        AntiConquerPlanet();
    }

    public byte[] getGroundGuards()
    {
        return groundGuards;
    }

    public byte[] getSpaceGuards(){return spaceGuards;}

    public byte[] getBuildings()
    {
        return buildings;
    }

    public int[] getResources()
    {
        return resources;
    }

    public int[] getCapacities()
    {
        return capacities;
    }

    public byte getFieldSize()
    {
        return size;
    }

    public boolean Select(Vector2 touchPos)
    {
        touch.set(touchPos.x - 2, touchPos.y - 2, touchPos.x + 2, touchPos.y + 2);
        planet.set(getMatrix()[12] - radius, getMatrix()[13] - radius, getMatrix()[12]+radius, getMatrix()[13]+radius);
        return touch.intersect(planet);
    }

    public void Draw(Graphics g)
    {
        g.DrawSprite(stroke);
        g.DrawSprite(image);
        /*if(isPlanetConquered)
            g.DrawText("+", Assets.gsFont, getMatrix()[12], getMatrix()[13], 0, buildingUpgrade.IsFalse() ? Color.YELLOW : Color.RED , 72, false);
        else
            g.DrawText("+", Assets.gsFont, getMatrix()[12], getMatrix()[13], 0, buildingUpgrade.IsFalse() ? Color.BLUE : Color.WHITE , 72, false);*/

    }

    public void Update()
    {
        //matrix.Rotate(5, 3*radius, 3*radius);
    }

    public void ConquerPlanet()
    {
        isPlanetConquered = true;
        stroke.setColorFilter(Color.BLUE);
    }

    public void AntiConquerPlanet()
    {
        isPlanetConquered = false;
        stroke.setColorFilter(Color.RED);
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
            resources[0] += buildings[1] * 10;
            resources[1] += buildings[2] * 20;
            resources[2] += buildings[3] * 15;
        }
        if(!unitGroundUpgrade.IsFalse())
        {
            unitGroundUpgrade.y--;
            if(unitGroundUpgrade.y == 0)
            {
                groundGuards[(int)unitGroundUpgrade.x]++;
                unitGroundUpgrade.SetFalse();
            }
        }
        else if(!unitSpaceUpgrade.IsFalse())
        {
            unitSpaceUpgrade.y--;
            if(unitSpaceUpgrade.y == 0)
            {
                spaceGuards[(int)unitSpaceUpgrade.x]++;
                unitSpaceUpgrade.SetFalse();
            }
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
                if(resources[0] >= buildings[0] * 500 && resources[2] >= buildings[0] * 650)
                {
                    resources[0] -= buildings[0] * 500;
                    resources[2] -= buildings[0] * 650;
                    buildingUpgrade.SetValue(0, buildings[0] * 10 + (buildings[0] == 0 ? 5 : 0));
                    upgradeName = "БИРЖА";
                }
                break;
            case 1:
                if(resources[0] >= buildings[1] * 500 && resources[2] >= buildings[1] * 650)
                {
                    resources[0] -= buildings[1] * 500;
                    resources[2] -= buildings[1] * 650;
                    buildingUpgrade.SetValue(1, buildings[1] * 10 + (buildings[1] == 0 ? 5 : 0));
                    upgradeName = "ШАХТЫ НАНОСТАЛИ";
                }
                break;
            case 2:
                if(resources[0] >= buildings[2] * 500 && resources[2] >= buildings[2] * 650)
                {
                    resources[0] -= buildings[2] * 500;
                    resources[2] -= buildings[2] * 650;
                    buildingUpgrade.SetValue(2, buildings[2] * 10 + (buildings[2] == 0 ? 5 : 0));
                    upgradeName = "ШАХТЫ НАНОСТАЛИ";
                }
                break;
            case 3:
                if(resources[0] >= buildings[3] * 500 && resources[2] >= buildings[3] * 650)
                {
                    resources[0] -= buildings[3] * 500;
                    resources[2] -= buildings[3] * 650;
                    buildingUpgrade.SetValue(3, buildings[3] * 10 + (buildings[3] == 0 ? 5 : 0));
                    upgradeName = "ЗАВОД";
                }
                break;
            case 4:
                if(resources[0] >= buildings[4] * 500 && resources[2] >= buildings[4] * 650)
                {
                    resources[0] -= buildings[4] * 500;
                    resources[2] -= buildings[4] * 650;
                    buildingUpgrade.SetValue(4, buildings[4] * 10 + (buildings[4] == 0 ? 5 : 0));
                    upgradeName = "МАСТЕРСКАЯ";
                }
                break;
        }
    }

    public void CreateUnit(int unit)
    {
        if(!unitGroundUpgrade.IsFalse() || !unitSpaceUpgrade.IsFalse())
            return;
        switch (unit)
        {
            case 0: //DROID
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitSpaceUpgrade.SetValue(0, 1);
                    unitUpgradeName = "DROID";
                }
                break;
            case 1: //AKIRA
                if(resources[0] >=10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitSpaceUpgrade.SetValue(1, 1);
                    unitUpgradeName = "AKIRA";
                }
                break;
            case 2: //DEFAINT
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitSpaceUpgrade.SetValue(2, 1);
                    unitUpgradeName = "DEFAINT";
                }
                break;
            case 3: //STORM
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitSpaceUpgrade.SetValue(3, 1);
                    unitUpgradeName = "STORM";
                }
                break;
            case 4: //BIOSHIP
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitSpaceUpgrade.SetValue(4, 1);
                    unitUpgradeName = "BIOSHIP";
                }
                break;
            case 5: //BIRD
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitSpaceUpgrade.SetValue(5, 1);
                    unitUpgradeName = "BIRD";
                }
                break;
            case 6: //ROBOT
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitGroundUpgrade.SetValue(0, 1);
                    unitUpgradeName = "ROBOT";
                }
                break;
            case 7:  //IVF
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitGroundUpgrade.SetValue(1, 1);
                    unitUpgradeName = "IVF";
                }
                break;
            case 8: //ROCKET
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitGroundUpgrade.SetValue(2, 1);
                    unitUpgradeName = "ROCKET";
                }
                break;
            case 9: //TANK
                if(resources[1] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitGroundUpgrade.SetValue(3, 1);
                    unitUpgradeName = "TANK";
                }
                break;
            case 10: //TURRET
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitGroundUpgrade.SetValue(4, 1);
                    unitUpgradeName = "TURRET";
                }
                break;
            case 11: //SONDER
                if(resources[0] >= 10 && resources[1] >= 10)
                {
                    resources[0] -= 10;
                    resources[1] -= 10;
                    unitGroundUpgrade.SetValue(5, 1);
                    unitUpgradeName = "SONDER";
                }
                break;
        }
    }

    public void DBLoad(byte Size, byte[] gArmy, byte[] sArmy, byte[] Buildings, int[] Resources, int[] RCapacity, Vector2 factory, Vector2 building, int owner)
    {
        this.size = Size;
        groundGuards = gArmy.clone();
        spaceGuards = sArmy.clone();
        buildings = Buildings.clone();
        resources[0] = Resources[0];
        resources[1] = Resources[1];
        resources[2] = Resources[2];
        buildingUpgrade.SetValue(building);
        factoryCreation.SetValue(factory);
        capacities = RCapacity.clone();
        if(owner == 1)
            ConquerPlanet();
        else
            AntiConquerPlanet();

        switch ((int)buildingUpgrade.x)
        {
            case 0:
                    upgradeName = "БИРЖА";
                break;
            case 1:

                    upgradeName = "НЕФТЯНАЯ СКВАЖИНА";
                break;
            case 2:

                    upgradeName = "ШАХТЫ НАНОСТАЛИ";
                break;
            case 3:
                    upgradeName = "ЗАВОД";
                break;
            case 4:
                    upgradeName = "МАСТЕРСКАЯ";
                break;
        }
    }

    public String getSkin()
    {
        return texture.getPath();
    }

    public boolean isSpaceArmyHere()
    {
        for(int i = 0; i < spaceGuards.length; i++)
        {
            if(spaceGuards[i] != 0)
                return true;
        }
        return false;
    }

    public boolean isGroundArmyHere()
    {
        for(int i = 0; i < groundGuards.length; i++)
        {
            if(groundGuards[i] != 0)
                return true;
        }
        return false;
    }
}
