package app.onedayofwar.GEngine.Text;

import java.util.ArrayList;

import app.onedayofwar.GEngine.Meshes.Sprite;
import app.onedayofwar.Utils.IO.XMLParser;

/**
 * Created by Slava on 24.03.2015.
 */
public class TextFont
{
    public ArrayList<Glyph> glyphs;
    public Sprite sprite;
    public StringBuilder stringBuilder;
    public static final int TEXTURE_SIZE = 72;

    public TextFont(Texture texture, String fileName, XMLParser parser)
    {
        glyphs = new ArrayList<>();
        sprite = new Sprite(texture);
        stringBuilder = new StringBuilder();
        parser.LoadFont(fileName, glyphs);
    }

    /*void DrawCenteredText(String text, Loader loader, float x, float y, int color, int size)
    {
        sprite.matrix[0] = size * 1f / TEXTURE_SIZE;
        sprite.matrix[5] = size * 1f / TEXTURE_SIZE;
        sprite.matrix[12] = x;
        sprite.matrix[13] = y;
        sprite.setColorFilter(color);
        int nLineCharNum = text.indexOf("\n");
        //String tmp;
        int length = (nLineCharNum > 0 ? nLineCharNum : text.length());
        float lX = x;
        float rX = x;
        byte glyphFound;
        if(length % 2 == 0)
        {
            text = stringBuilder.delete(0, stringBuilder.length()).append(text, 0, length).append(' ').toString();
            length++;
        }
        for(int i = 0; i <= length/2; i++)
        {
            glyphFound = 0;
            for(int j = 0; j < glyphs.size(); j++)
            {
                Glyph glyph = glyphs.get(j);
                if(glyph.id == (int)text.charAt(length/2 - i))
                {
                    sprite.setShape(glyph.x, glyph.y, glyph.width, glyph.height);
                    if(i == 0)
                    {
                        loader.DrawSprite(sprite);
                        lX -= sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size;
                        rX += sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size;
                        break;
                    }
                    else
                    {
                        lX -= sprite.getWidth()/2;
                        sprite.matrix[12] = lX;
                        loader.DrawSprite(sprite);
                        lX -= sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size;
                        glyphFound++;
                    }
                }
                if(i > 0)
                {
                    if (glyph.id == (int) text.charAt(length/2 + i))
                    {
                        sprite.setShape(glyph.x, glyph.y, glyph.width, glyph.height);
                        rX += sprite.getWidth()/2;
                        sprite.matrix[12] = rX;
                        loader.DrawSprite(sprite);
                        rX += sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size;
                        glyphFound++;
                    }
                    if (glyphFound == 2)
                        break;
                }
            }
        }

        if(length < text.length())
            DrawCenteredText(text.substring(nLineCharNum + 1, text.length()), loader, x, y + size, color, size);
    }*/
}
