package app.onedayofwar.GEngine.Text;

/**
 * Created by Slava on 25.03.2015.
 */
public class Glyph
{
    public int id;
    public int x, y;
    public int width, height;
    public Glyph(int id, int x, int y, int width, int height)
    {
        this.id = id;
        this.height = height;
        this.width = width;
        this.x = x;
        this.y = y;
    }
}
