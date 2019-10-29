package app.onedayofwar.Graphics;

import android.graphics.Rect;

import app.onedayofwar.System.Vector2;

/**
 * Created by Slava on 14.02.2015.
 */
public class Animation
{
    private int frames;
    private int currentFrame;
    private int speed;
    private float tick;
    private int width;
    private int height;
    private int start;
    private Rect dstRect;
    private Rect srcRect;
    private boolean isLooped;
    private boolean isStart;

    public Animation(int frames, int speed, int width, int height, boolean isLooped, int start)
    {
        this.frames = frames;
        this.speed = speed;
        this.width = width/frames;
        this.height = height;
        this.isLooped = isLooped;
        this.start = start;
        isStart = false;
        tick = 0;
        currentFrame = 0;
        srcRect = new Rect(0, 0, this.width, height);
        dstRect = new Rect(0, 0, width, height);
    }

    public void Update(float eTime)
    {
        if(isStart)
        {
            if (tick >= speed)
            {
                if (currentFrame == frames - 1)
                {
                    if(!isLooped)
                    {
                        isStart = false;
                    }
                    currentFrame = start;
                }
                srcRect.set(width * currentFrame, 0, width * (currentFrame + 1), height);
                tick = 0;
                currentFrame++;
            }
            else
            {
                tick += eTime * 100;
            }
        }
    }

    public Rect GetDstRect()
    {
        return dstRect;
    }

    public Rect GetSrcRect()
    {
        return srcRect;
    }

    public void SetPos(int x, int y)
    {
        dstRect.set(x, y , x + width, y + height);
    }

    public void Start()
    {
        isStart = true;
    }

    public boolean IsStart()
    {
        return isStart;
    }
}
