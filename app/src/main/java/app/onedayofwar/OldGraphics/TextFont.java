package app.onedayofwar.Graphics;

import android.util.Log;

import java.util.ArrayList;

import app.onedayofwar.System.XMLParser;

/**
 * Created by Slava on 24.03.2015.
 */
public class TextFont
{
    private ArrayList<Glyph> glyphs;
    private Sprite sprite;
    private StringBuilder stringBuilder;
    private static final int TEXTURE_SIZE = 72;

    public TextFont(Texture texture, String fileName, XMLParser parser)
    {
        glyphs = new ArrayList<>();
        sprite = new Sprite(texture);
        stringBuilder = new StringBuilder();
        parser.LoadFont(fileName, glyphs);
    }

    void DrawText(String text, Graphics graphics, float x, float y, float rightBorder, int color, float size)
    {
        sprite.ResetMatrix();
        sprite.setPosition(x, y + size/2);
        sprite.Scale(size * 1f / TEXTURE_SIZE);
        sprite.setColorFilter(color);
        for(int i = 0; i < text.length(); i++)
        {
            for(int j = 0; j < glyphs.size(); j++)
            {
                if(text.charAt(i) == '\n')
                {
                    sprite.matrix[12] = x;
                    sprite.Move(0, sprite.getHeight());
                    break;
                }
                if(glyphs.get(j).id == (int)text.charAt(i))
                {
                    Glyph glyph = glyphs.get(j);
                    sprite.setCoords(glyph.x, glyph.y, glyph.width, glyph.height);
                    if(rightBorder > 0 && sprite.matrix[12] + sprite.getWidth() > rightBorder)
                    {
                        sprite.matrix[12] = x;
                        sprite.Move(0, sprite.getHeight());
                    }
                    sprite.Move(sprite.getWidth()/2, 0);
                    graphics.DrawStaticSprite(sprite);
                    sprite.Move(sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size, 0);
                    break;
                }
            }
        }
    }

    void DrawCenteredText(String text, Graphics graphics, float x, float y, int color, float size)
    {
        sprite.setScale(size * 1f / TEXTURE_SIZE);
        sprite.matrix[12] = x;
        sprite.matrix[13] = y;
        sprite.setColorFilter(color);
        int nLineCharNum = text.indexOf("\n");
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
                    sprite.setCoords(glyph.x, glyph.y, glyph.width, glyph.height);
                    if(i == 0)
                    {
                        graphics.DrawSprite(sprite);
                        lX -= sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size;
                        rX += sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size;
                        break;
                    }
                    else
                    {
                        lX -= sprite.getWidth()/2;
                        sprite.matrix[12] = lX;
                        graphics.DrawSprite(sprite);
                        lX -= sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size;
                        glyphFound++;
                    }
                }
                if(i > 0)
                {
                    if (glyph.id == (int) text.charAt(length/2 + i))
                    {
                        sprite.setCoords(glyph.x, glyph.y, glyph.width, glyph.height);
                        rX += sprite.getWidth()/2;
                        sprite.matrix[12] = rX;
                        graphics.DrawSprite(sprite);
                        rX += sprite.getWidth()/2 + TEXTURE_SIZE * 1f / size;
                        glyphFound++;
                    }
                    if (glyphFound == 2)
                        break;
                }
            }
        }
        if(length < text.length())
            DrawCenteredText(text.substring(nLineCharNum + 1, text.length()), graphics, x, y + size, color, size);
    }
}
