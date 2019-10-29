package app.onedayofwar.GEngine.Meshes;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import app.onedayofwar.GEngine.Assets;
import app.onedayofwar.GEngine.Text.Texture;

/**
 * Created by Slava on 05.03.2015.
 */
public class Sprite
{
    public int program;
    public FloatBuffer vertexBuffer;
    public FloatBuffer uvBuffer;

    private int width;
    private int height;

    public float[] color;
    public float[] matrix;

    private Texture texture;

    public Sprite(Texture texture)
    {
        color = new float[4];
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        height = texture.getHeight();
        width = texture.getWidth();
        this.texture = texture;

        vertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(-width/2);
        vertexBuffer.put(height/2);
        vertexBuffer.put(-width/2);
        vertexBuffer.put(-height/2);
        vertexBuffer.put(width/2);
        vertexBuffer.put(-height/2);
        vertexBuffer.put(width/2);
        vertexBuffer.put(height/2);
        vertexBuffer.flip();
        uvBuffer = ByteBuffer.allocateDirect(4 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        uvBuffer.put(0.0f);
        uvBuffer.put(0.0f);
        uvBuffer.put(0.0f);
        uvBuffer.put(1.0f);
        uvBuffer.put(1.0f);
        uvBuffer.put(1.0f);
        uvBuffer.put(1.0f);
        uvBuffer.put(0.0f);
        uvBuffer.flip();

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, Assets.vSpriteShader);
        GLES20.glAttachShader(program,  Assets.fSpriteShader);
        GLES20.glLinkProgram(program);
    }

    public void Move(float x, float y)
    {
        matrix[12] += x;
        matrix[13] += y;
    }

    public void setPosition(float x, float y)
    {
        matrix[12] = x;
        matrix[13] = y;
    }

    public void setShape(float x, float y, int width, int height)
    {
        uvBuffer.position(0);
        uvBuffer.put(x / texture.getWidth());
        uvBuffer.put(y / texture.getHeight());
        uvBuffer.put(x / texture.getWidth());
        uvBuffer.put(y / texture.getHeight() + height * 1f/ texture.getHeight());
        uvBuffer.put(x / texture.getWidth() + width * 1f/ texture.getWidth());
        uvBuffer.put(y / texture.getHeight()  + height * 1f/ texture.getHeight());
        uvBuffer.put(x / texture.getWidth() + width * 1f/ texture.getWidth());
        uvBuffer.put(y / texture.getHeight());
        uvBuffer.flip();
        vertexBuffer.position(0);
        vertexBuffer.put(-width/2);
        vertexBuffer.put(height/2);
        vertexBuffer.put(-width/2);
        vertexBuffer.put(-height/2);
        vertexBuffer.put(width/2);
        vertexBuffer.put(-height/2);
        vertexBuffer.put(width/2);
        vertexBuffer.put(height/2);
        vertexBuffer.flip();
        this.width = (int)(width * matrix[0]);
        this.height = (int)(height * matrix[0]);
    }

    public void Scale(float coeff)
    {
        Matrix.scaleM(matrix, 0, coeff, coeff, 1);
        width = (int)(width * coeff);
        height = (int)(height * coeff);
    }

    public void setColorFilter(int color)
    {
        this.color[0] = ((color & 0xff0000) >> 16) / 255f;
        this.color[1] = ((color & 0xff00) >> 8) / 255f;
        this.color[2] = (color & 0xff) / 255f;
        this.color[3] = 1;
    }

    public void removeColorFilter()
    {
        color[3] = 0;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public int getTexture()
    {
        return texture.getId();
    }

}