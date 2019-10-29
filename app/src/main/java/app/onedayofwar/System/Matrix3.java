package app.onedayofwar.System;

import android.opengl.Matrix;

/**
 * Created by Slava on 15.03.2015.
 */
public class Matrix3
{
    private float[] matrix;
    private float[] tmp;

    public Matrix3()
    {
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);

        tmp = new float[16];
        Matrix.setIdentityM(tmp, 0);
    }

    public float[] getValues()
    {
        return matrix;
    }

    public float getX()
    {
        return matrix[12];
    }

    public float getY()
    {
        return matrix[13];
    }

    public void setX(float x)
    {
        matrix[12] = x;
    }

    public void setY(float y)
    {
        matrix[13] = y;
    }

    public void Translate(float x, float y)
    {
        Matrix.translateM(matrix, 0, x, y, 0);
    }

    public void Scale(float coeff)
    {
        Matrix.scaleM(matrix, 0, coeff, coeff, 1);
    }

    public void SetRotate(float angle)
    {
        Matrix.setRotateM(matrix, 0, angle, 0, 0, 1);
    }

    public void Rotate(float angle)
    {
        Matrix.rotateM(matrix, 0, angle, 0, 0, 1);
    }

    public float[] Multiply(Matrix3 mat)
    {
        Matrix.multiplyMM(tmp, 0, matrix, 0, mat.getValues(), 0);
        return tmp;
    }

}
