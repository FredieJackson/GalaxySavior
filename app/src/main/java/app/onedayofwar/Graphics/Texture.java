package app.onedayofwar.Graphics;

/**
 * Created by Slava on 22.03.2015.
 */
public class Texture
{
    private int id;
    private int width;
    private int height;

    public Texture(int id, int width, int height)
    {
        this.id = id;
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getId() {
        return id;
    }
}
