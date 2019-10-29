package app.onedayofwar.GEngine.Meshes;


import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import app.onedayofwar.GEngine.Assets;

/**
 * Created by Slava on 15.03.2015.
 */
public class Line
{
    public int program;
    public FloatBuffer vertexBuffer;
    public FloatBuffer colorBuffer;

    public Line(float xb, float yb, float zb, float xe, float ye,float ze, int color)
    {

        vertexBuffer = ByteBuffer.allocateDirect(2 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        setShape(xb, yb, zb, xe, ye, ze, color);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, Assets.vRectangleShader);
        GLES20.glAttachShader(program, Assets.fRectangleShader);
        GLES20.glLinkProgram(program);
    }

    public Line()
    {
        vertexBuffer = ByteBuffer.allocateDirect(2 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        colorBuffer = ByteBuffer.allocateDirect(4 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, Assets.vRectangleShader);
        GLES20.glAttachShader(program, Assets.fRectangleShader);
        GLES20.glLinkProgram(program);
    }

    public void setShape(float xb, float yb, float zb, float xe, float ye, float ze, int color)
    {
        colorBuffer.position(0);
        colorBuffer.put(((color & 0xff0000) >> 16) / 255f);
        colorBuffer.put(((color & 0xff00) >> 8) / 255f);
        colorBuffer.put((color & 0xff) / 255f);
        colorBuffer.put(1);
        colorBuffer.flip();
        vertexBuffer.position(0);
        vertexBuffer.put(xb);
        vertexBuffer.put(yb);
        vertexBuffer.put(zb);
        vertexBuffer.put(xe);
        vertexBuffer.put(ye);
        vertexBuffer.put(ze);
        vertexBuffer.flip();
    }
}
