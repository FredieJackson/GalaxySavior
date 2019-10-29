package app.onedayofwar.GEngine.Meshes;


import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import app.onedayofwar.GEngine.Assets;

/**
 * Created by Slava on 15.03.2015.
 */
public class Rectangle
{
    public int program;
    public FloatBuffer vertexBuffer;
    public FloatBuffer colorBuffer;

    private int width;
    private int height;

    public boolean isFilled;

    public float[] matrix;

    public Rectangle(float x, float y, float z, int width, int height, int color, boolean isFilled)
    {
        this.isFilled = isFilled;
        matrix = new float[16];
        vertexBuffer = ByteBuffer.allocateDirect(4 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        SetShape(x, y, z, width, height, color, isFilled);
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, Assets.vRectangleShader);
        GLES20.glAttachShader(program, Assets.fRectangleShader);
        GLES20.glLinkProgram(program);
    }

    public Rectangle()
    {
        matrix = new float[16];
        vertexBuffer = ByteBuffer.allocateDirect(4 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, Assets.vRectangleShader);
        GLES20.glAttachShader(program, Assets.fRectangleShader);
        GLES20.glLinkProgram(program);
    }

    public void SetShape(float x, float y, float z, int width, int height, int color, boolean isFilled)
    {
        Matrix.setIdentityM(matrix, 0);
        Matrix.translateM(matrix, 0, x, y, z);

        this.isFilled = isFilled;
        this.height = height;
        this.width = width;

        colorBuffer.position(0);
        colorBuffer.put(((color & 0xff0000) >> 16) / 255f);
        colorBuffer.put(((color & 0xff00) >> 8) / 255f);
        colorBuffer.put((color & 0xff) / 255f);
        colorBuffer.put(1);
        colorBuffer.flip();
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
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
