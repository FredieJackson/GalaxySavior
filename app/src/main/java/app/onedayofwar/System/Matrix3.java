package app.onedayofwar.System;

import android.graphics.Matrix;

/**
 * Created by Slava on 04.03.2015.
 */
public class Matrix3
{
    //region OPENGL
    private Matrix matrix;
    private float[] mValues;

    private static Matrix3 multMat;
    private static float[] multMatVal;

    private static Vector2 multVec;
    private static float[] multVecVal;

    public Matrix3()
    {
        matrix = new Matrix();
        mValues = new float[16];
        multMatVal = new float[16];
        multVec = new Vector2();
        multVecVal = new float[4];
        android.opengl.Matrix.setIdentityM(mValues, 0);
        android.opengl.Matrix.setIdentityM(multMatVal, 0);
    }

    public void Translate(int dx, int dy)
    {
        android.opengl.Matrix.translateM(mValues, 0, dx, dy, 0);
        UpdateMatrix();
    }

    public void Scale(float coeff)
    {
        android.opengl.Matrix.scaleM(mValues, 0, coeff, coeff , 1);
        UpdateMatrix();
    }

    public void Scale(float sx, float sy)
    {
        android.opengl.Matrix.scaleM(mValues, 0, sx, sy , 1);
        UpdateMatrix();
    }

    public void Rotate(float angle, int x, int y)
    {
        android.opengl.Matrix.setIdentityM(multMatVal, 0);
        /*for (int i = 0; i < 4; i++)
        {
            Log.i("IDENTITY", multMatVal[0 + i * 4] + " | " + multMatVal[1 + i * 4] + " | " + multMatVal[2 + i * 4] + " | " + multMatVal[3 + i * 4]);
        }
        Log.i("PLANET", "---");*/

        android.opengl.Matrix.translateM(multMatVal, 0, x, y, 0);
        /*for (int i = 0; i < 4; i++)
        {
            Log.i("TRANS", multMatVal[0 + i * 4] + " | " + multMatVal[1 + i * 4] + " | " + multMatVal[2 + i * 4] + " | " + multMatVal[3 + i * 4]);
        }
        Log.i("PLANET", "---");*/

        android.opengl.Matrix.rotateM(multMatVal, 0, angle, 0, 0, 1);
        /*for (int i = 0; i < 4; i++)
        {
            Log.i("ROTATE", multMatVal[0 + i * 4] + " | " + multMatVal[1 + i * 4] + " | " + multMatVal[2 + i * 4] + " | " + multMatVal[3 + i * 4]);
        }
        Log.i("PLANET", "---");*/

        android.opengl.Matrix.translateM(multMatVal, 0, -x, -y, 0);

        /*for (int i = 0; i < 4; i++)
        {
            Log.i("TRANS -1", multMatVal[0 + i * 4] + " | " + multMatVal[1 + i * 4] + " | " + multMatVal[2 + i * 4] + " | " + multMatVal[3 + i * 4]);
        }
        Log.i("PLANET", "---");*/

        android.opengl.Matrix.multiplyMM(multMatVal, 0, mValues, 0, multMatVal, 0);

        /*for (int i = 0; i < 4; i++)
        {
            Log.i("MULTIPLY", multMatVal[0 + i * 4] + " | " + multMatVal[1 + i * 4] + " | " + multMatVal[2 + i * 4] + " | " + multMatVal[3 + i * 4]);
        }
        Log.i("PLANET", "---");*/

        for(int i = 0; i < multMatVal.length; i++)
        {
            mValues[i] = multMatVal[i];
        }
        UpdateMatrix();
    }

    private void UpdateMatrix()
    {
        multMatVal[0] = mValues[0];
        multMatVal[1] = mValues[4];
        multMatVal[2] = mValues[12];
        multMatVal[3] = mValues[1];
        multMatVal[4] = mValues[5];
        multMatVal[5] = mValues[13];
        multMatVal[6] = mValues[3];
        multMatVal[7] = mValues[7];
        multMatVal[8] = mValues[15];
        matrix.setValues(multMatVal);
        android.opengl.Matrix.setIdentityM(multMatVal, 0);
    }

    public float Get(int num)
    {
        return mValues[num];
    }

    public float[] GetValues()
    {
        return mValues;
    }

    public Matrix3 GetMultiply(Matrix3 mat)
    {
        if(multMat == null)
        {
            multMat = new Matrix3();
        }

        android.opengl.Matrix.setIdentityM(multMatVal, 0);
        android.opengl.Matrix.multiplyMM(multMatVal, 0, mValues, 0, mat.GetValues(), 0);

        multMat.SetValues(multMatVal);

        return multMat;
    }

    public Vector2 GetMultiply(Vector2 vec)
    {
        multVecVal[0] = vec.x;
        multVecVal[1] = vec.y;
        multVecVal[2] = 0;
        multVecVal[3] = 1;

        android.opengl.Matrix.multiplyMV(multVecVal, 0, mValues, 0, multVecVal, 0);
        multVec.SetValue((int)multVecVal[0], (int)multVecVal[1]);

        return multVec;
    }

    public void SetValues(float[] values)
    {
        for(int i = 0; i < values.length; i++)
        {
            mValues[i] = values[i];
        }
        UpdateMatrix();
    }

    public Matrix GetMatrix()
    {
        return matrix;
    }

    public int GetX()
    {
        return (int)mValues[12];
    }

    public int GetY()
    {
        return (int)mValues[13];
    }
    //endregion
}


