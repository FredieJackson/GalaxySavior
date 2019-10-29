package app.onedayofwar.System;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import app.onedayofwar.Campaign.Space.Planet;

/**
 * Created by Slava on 08.04.2015.
 */
public class DBController
{
    //region Класс для работы с БД

    class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        public void onCreate(SQLiteDatabase db)
        {
            Log.i("DB HELPER", "--- onCreate database ---");

            db.execSQL("CREATE TABLE WORLD(" +
                    "Width INT," +
                    "Height INT," +
                    "Planets INT," +
                    "Turn INT," +
                    "Info INT" +
                    ");");
            db.execSQL("CREATE TABLE PLANETS( " +
                    "ID INT" +
                    "Coords TEXT" +
                    "Radius REAL" +
                    "Skin TEXT" +
                    "GArmy TEXT" +
                    "SArmy TEXT" +
                    "Buildings TEXT" +
                    "Resources TEXT" +
                    "RCapacity TEXT" +
                    "Creation TEXT" +
                    "Info TEXT");
            db.execSQL("CREATE TABLE AI(" +
                    "Coords TEXT" +
                    "Resources TEXT" +
                    "Planets TEXT" +
                    "Info TEXT");
            db.execSQL("CREATE TABLE PLAYER(" +
                    "Coords TEXT" +
                    "Resources TEXT" +
                    "Planets TEXT" +
                    "Info TEXT");

        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS WORLD");
            /*db.execSQL("DROP TABLE IF EXISTS PLAYER");
            db.execSQL("DROP TABLE IF EXISTS AI");
            db.execSQL("DROP TABLE IF EXISTS PLANETS");*/
            onCreate(db);
        }
    }
    //endregion

    DBHelper dbHelper;
    SQLiteDatabase db;
    ContentValues contentValues;

    private final String BD_NAME = "GSBD";
    private final int VERSION = 1;

    public DBController(Context context)
    {
        dbHelper = new DBHelper(context, BD_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
        //dbHelper.close();
        contentValues = new ContentValues();
    }


    public void Insert(String width, String height, int planets, int turn, String info)
    {
        contentValues.clear();
        contentValues.put("Width", width);
        contentValues.put("Height", height);
        contentValues.put("Planets", planets);
        contentValues.put("Turn", turn);
        contentValues.put("Info", info);
        db.insert("WORLD", null, contentValues);
    }

    public void Read(String table)
    {
        Log.d("DB READ", "Read table " + table);
        Cursor c = db.query(table, null, null, null, null, null, null);

        Log.d("DB READ", "Records count = " + c.getCount());
        if (c.moveToFirst())
        {
            do
            {
                Log.d("DB READ", c.getString(c.getColumnIndex("Width")) + " | " + c.getString(c.getColumnIndex("Height")) + " | " +  c.getString(c.getColumnIndex("Planets")) + " | " +  c.getString(c.getColumnIndex("Turn")) + " | " +  c.getString(c.getColumnIndex("Info")));
            } while (c.moveToNext());
        }

        c.close();
    }

    public void Delete(String table)
    {
        Log.d("DB DELETE", "Delete all from table " + table);
        db.delete(table, null, null);
    }

    public boolean LoadPlanets()
    {
        Cursor c = db.query("WORLD", null, null, null, null, null, null);
        if (c.moveToFirst())
        {

               // players[i] = new Player(Assets.tank, c.getInt(c.getColumnIndex("Width")), c.getInt(c.getColumnIndex("Height")));
                c.moveToNext();
            c.close();
            return true;
        }
        else
        {
            c.close();
            return false;
        }
    }

    public void SavePlanets(ArrayList<Planet> planets)
    {
        for(int i = 0; i < planets.size(); i++)
        {
            Planet planet = planets.get(i);
            contentValues.clear();
            contentValues.put("ID", i);
            contentValues.put("Coords", planet.getMatrix()[12] + "|" + planet.getMatrix()[12]);
            contentValues.put("Radius", planet.getRadius());
            contentValues.put("Skin", "test");
            contentValues.put("gArmy", planet.getGroundGuards().toString());
            db.insert("PLANETS", null, contentValues);
        }
    }
}
