package pack.warshipsbattle.System;

/**
 * Created by Slava on 26.10.2014.
 */

public class Vector2
{
    public float x;
    public float y;

    //region Constructor
    public Vector2(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 sourceVector)
    {
        x = sourceVector.x;
        y = sourceVector.y;
    }

    public Vector2()
    {
        x = 0;
        y = 0;
    }
    //endregion

    public void SetZero()
    {
        x = 0;
        y = 0;
    }

    public void SetNegative()
    {
        x = -1;
        y = -1;
    }

    public void Normalize()
    {
        float length = (float)Math.sqrt(x*x + y*y);
        x /= length;
        y /= length;
    }

    public void SetValue(Vector2 sourceVector)
    {
        x = sourceVector.x;
        y = sourceVector.y;
    }

    public void SetValue(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public boolean IsNegative()
    {
        if(x == -1 && y == -1)
            return true;
        return false;
    }
}
