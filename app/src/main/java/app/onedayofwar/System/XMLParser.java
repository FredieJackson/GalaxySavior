package app.onedayofwar.System;

import android.content.res.AssetManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Campaign.Space.PlanetController;

/**
 * Created by Slava on 20.02.2015.
 */
public class XMLParser
{
    private XmlPullParserFactory xmlFactory;
    private XmlPullParser parser;
    private AssetManager assets;

    public XMLParser(AssetManager assets)
    {
        this.assets = assets;
        try
        {
            xmlFactory = XmlPullParserFactory.newInstance();
            parser = xmlFactory.newPullParser();
            InputStream in;
            try
            {
                //Log.i("LOAD", "XMLParser LOADED");
                in = assets.open("campaign/levels.xml");
                parser.setInput(in, null);
            }
            catch(IOException e)
            {
                Log.i("XML.INIT", e.getMessage());
            }
        }
        catch(XmlPullParserException e)
        {
            Log.i("XML.INIT", e.getMessage());
        }
    }

    public Planet getPlanet(int num)
    {
        int i = 0;
        Planet tmpPlanet = null;
        int oil = 0;
        int nanoSteel = 0;
        int syncoCrystals = 0;
        byte size = 0;
        byte[] buildings = null;
        byte[] spaceGuard = null;
        byte[] groundGuard = null;
        try
        {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT)
            {
                switch(parser.getEventType())
                {
                    case  XmlPullParser.START_TAG:
                        if(parser.getName().equals("planet"))
                        {
                            if(++i == num)
                            {
                                size = Byte.parseByte(parser.getAttributeValue(0));
                            }
                        }
                        break;
                    case  XmlPullParser.END_TAG:
                        if(i == num)
                        {
                            if (parser.getName().equals("resources"))
                            {
                                oil = Integer.parseInt(parser.getAttributeValue(0));
                                nanoSteel = Integer.parseInt(parser.getAttributeValue(1));
                                syncoCrystals = Integer.parseInt(parser.getAttributeValue(2));
                                Log.i("RESOURCES" , "" + oil);
                                Log.i("RESOURCES" , "" + nanoSteel);
                                Log.i("RESOURCES" , "" + syncoCrystals);
                            }
                            else if (parser.getName().equals("space_guard"))
                            {
                                spaceGuard = new byte[parser.getAttributeCount()];
                                for (int j = 0; j < spaceGuard.length; j++)
                                {
                                    spaceGuard[j] = Byte.parseByte(parser.getAttributeValue(j));
                                    Log.i("SPACEGUARD" , j + " / " + spaceGuard[j]);
                                }
                            }
                            else if (parser.getName().equals("ground_guard"))
                            {
                                groundGuard = new byte[parser.getAttributeCount()];
                                for (int j = 0; j < groundGuard.length; j++)
                                {
                                    groundGuard[j] = Byte.parseByte(parser.getAttributeValue(j));
                                    Log.i("GROUNDGUARD" , j + " / " + groundGuard[j]);
                                }
                            }
                            else if (parser.getName().equals("buildings"))
                            {
                                buildings = new byte[parser.getAttributeCount()];
                                for (int j = 0; j < buildings.length; j++)
                                {
                                    buildings[j] = Byte.parseByte(parser.getAttributeValue(j));
                                    Log.i("BUILDINGS" , j + " / " + buildings[j]);
                                }
                            }
                            else if (parser.getName().equals("planet"))
                            {
                                parser = null;
                                tmpPlanet = new Planet(oil, nanoSteel, syncoCrystals, spaceGuard, groundGuard, buildings, size);
                                Log.i("NEWPLANET" , "CREATE");
                                return tmpPlanet;
                            }
                        }
                        break;
                }
                try
                {
                    parser.next();
                }
                catch (IOException e)
                {
                    Log.i("XML.GETPLANET", e.getMessage());
                }
            }
        }
        catch(XmlPullParserException e)
        {
            Log.i("XML.GETPLANET", e.getMessage());
        }
        return null;
    }

    public void LoadAllPlanets(PlanetController planetController)
    {
        int num = 0;
        int i = 0;
        int oil = 0;
        int nanoSteel = 0;
        int syncoCrystals = 0;
        byte size = 0;
        byte[] buildings = null;
        byte[] spaceGuards = null;
        byte[] groundGuards = null;
        Log.i("LOAD", "PLANETS LOADING");
        try
        {
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT)
            {
                switch(parser.getEventType())
                {
                    case  XmlPullParser.START_TAG:
                        if(parser.getName().equals("planet"))
                        {
                            size = Byte.parseByte(parser.getAttributeValue(0));
                        }
                        break;
                    case  XmlPullParser.END_TAG:
                            if (parser.getName().equals("resources"))
                            {
                                oil = Integer.parseInt(parser.getAttributeValue(0));
                                nanoSteel = Integer.parseInt(parser.getAttributeValue(1));
                                syncoCrystals = Integer.parseInt(parser.getAttributeValue(2));
                                //Log.i("RESOURCES" , "" + oil);
                                //Log.i("RESOURCES" , "" + nanoSteel);
                                //Log.i("RESOURCES" , "" + syncoCrystals);
                            }
                            else if (parser.getName().equals("space_guard"))
                            {
                                spaceGuards = new byte[parser.getAttributeCount()];
                                for (int j = 0; j < spaceGuards.length; j++)
                                {
                                    spaceGuards[j] = Byte.parseByte(parser.getAttributeValue(j));
                                    //Log.i("SPACEGUARD" , j + " / " + spaceGuards[j]);
                                }
                            }
                            else if (parser.getName().equals("ground_guard"))
                            {
                                groundGuards = new byte[parser.getAttributeCount()];
                                for (int j = 0; j < groundGuards.length; j++)
                                {
                                    groundGuards[j] = Byte.parseByte(parser.getAttributeValue(j));
                                    //Log.i("GROUNDGUARD" , j + " / " + groundGuards[j]);
                                }
                            }
                            else if (parser.getName().equals("buildings"))
                            {
                                buildings = new byte[parser.getAttributeCount()];
                                for (int j = 0; j < buildings.length; j++)
                                {
                                    buildings[j] = Byte.parseByte(parser.getAttributeValue(j));
                                    //Log.i("BUILDINGS" , j + " / " + buildings[j]);
                                }
                            }
                            else if (parser.getName().equals("planet"))
                            {
                                num++;
                                planetController.AddPlanet(oil, nanoSteel, syncoCrystals, spaceGuards, groundGuards, buildings, size);
                                //Log.i("NEWPLANET" , "CREATE");
                            }
                        break;
                }
                try
                {
                    parser.next();
                }
                catch (IOException e)
                {
                    Log.i("XML.GETPLANET", e.getMessage());
                }
            }
        }
        catch(XmlPullParserException e)
        {
            Log.i("XML.GETPLANET", e.getMessage());
        }
    }

}
