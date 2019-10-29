package app.onedayofwar.GEngine.Meshes;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import app.onedayofwar.GEngine.Assets;

/**
 * Created by Slava on 14.06.2015.
 */
public class Model
{
    public  int program;
    public FloatBuffer vertexBuffer;
    public FloatBuffer normalBuffer;
    public FloatBuffer texCoordBuffer;
    public ShortBuffer indexBuffer;
    public boolean isTextured;
    public int[] textures;
    public float[] matrix;
    public float[] color;

    //region Old Constructors
    /*Model(ArrayList<Vector3> verctices, ArrayList<Vector3> normals, ArrayList<Vector3> texCoords, ArrayList<Vector3> vIndices , ArrayList<Vector3> nIndices, ArrayList<Vector3> tIndices)
    {
        //textures = new ArrayList<>();
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        Matrix.translateM(matrix, 0, 0, -10, -50);
        Matrix.scaleM(matrix,0, 0.10f, 0.10f, 0.10f);
        //Matrix.rotateM(matrix, 0, 45, 0, 1, 0);
        //Matrix.rotateM(matrix, 0, 90, 0, 1, 0);

        vertexBuffer = ByteBuffer.allocateDirect(verctices.size() * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.position(0);
        for(int i = 0; i < verctices.size(); i++)
        {
            vertexBuffer.put(verctices.get(i).x);
            vertexBuffer.put(verctices.get(i).y);
            vertexBuffer.put(verctices.get(i).z);
        }
        vertexBuffer.position(0);

        normalBuffer = ByteBuffer.allocateDirect(nIndices.size() * 3 * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        normalBuffer.position(0);
        for(int i = 0, position; i < vIndices.size(); i++)
        {
            position = (int)(vIndices.get(i).x - 1)*3;
            normalBuffer.position(position);
            normalBuffer.put(normals.get((int)(nIndices.get(i).x - 1)).x);
            normalBuffer.put(normals.get((int)(nIndices.get(i).x - 1)).y);
            normalBuffer.put(normals.get((int)(nIndices.get(i).x - 1)).z);
            position = (int)(vIndices.get(i).y - 1)*3;
            normalBuffer.position(position);
            normalBuffer.put(normals.get((int)(nIndices.get(i).y - 1)).x);
            normalBuffer.put(normals.get((int)(nIndices.get(i).y - 1)).y);
            normalBuffer.put(normals.get((int)(nIndices.get(i).y - 1)).z);
            position = (int)(vIndices.get(i).z - 1)*3;
            normalBuffer.position(position);
            normalBuffer.put(normals.get((int)(nIndices.get(i).z - 1)).x);
            normalBuffer.put(normals.get((int)(nIndices.get(i).z - 1)).y);
            normalBuffer.put(normals.get((int)(nIndices.get(i).z - 1)).z);
        }
        normalBuffer.position(0);

        texCoordBuffer = ByteBuffer.allocateDirect(tIndices.size() * 3 * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        texCoordBuffer.position(0);
        for(int i = 0, position; i < vIndices.size(); i++)
        {
            position = (int)(vIndices.get(i).x - 1) * 2;
            texCoordBuffer.position(position);
            texCoordBuffer.put(texCoords.get((int)(tIndices.get(i).x - 1)).x);
            texCoordBuffer.put(texCoords.get((int)(tIndices.get(i).x - 1)).y);
            position = (int)(vIndices.get(i).y - 1) * 2;
            texCoordBuffer.position(position);
            texCoordBuffer.put(texCoords.get((int)(tIndices.get(i).y - 1)).x);
            texCoordBuffer.put(texCoords.get((int)(tIndices.get(i).y - 1)).y);
            position = (int)(vIndices.get(i).z - 1) * 2;
            texCoordBuffer.position(position);
            texCoordBuffer.put(texCoords.get((int)(tIndices.get(i).z - 1)).x);
            texCoordBuffer.put(texCoords.get((int)(tIndices.get(i).z - 1)).y);
        }
        texCoordBuffer.position(0);

        indexBuffer = ByteBuffer.allocateDirect(vIndices.size() * 3 * 4).order(ByteOrder.nativeOrder()).asShortBuffer();
        indexBuffer.position(0);
        for(int i = 0; i < vIndices.size(); i++)
        {
            indexBuffer.put((short)(vIndices.get(i).x - 1));
            indexBuffer.put((short)(vIndices.get(i).y - 1));
            indexBuffer.put((short)(vIndices.get(i).z - 1));
        }
        indexBuffer.position(0);

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, Assets.vModelShader);
        GLES20.glAttachShader(program, Assets.fModelShader);
        GLES20.glLinkProgram(program);
    }*/

        /*public Model(ArrayList<Vector3> vertices, ArrayList<Vector3> normals, ArrayList<Vector3> uvs, ArrayList<Short> indices)
    {
        textures = new ArrayList<>();
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        Matrix.translateM(matrix, 0, 0, 0, -50);
        Matrix.scaleM(matrix,0, 5f, 5f, 5f);

        vertexBuffer = ByteBuffer.allocateDirect(vertices.size() * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(int i = 0; i < vertices.size(); i++)
        {
            vertexBuffer.put(vertices.get(i).x);
            vertexBuffer.put(vertices.get(i).y);
            vertexBuffer.put(vertices.get(i).z);
        }
        vertexBuffer.flip();

        normalBuffer = ByteBuffer.allocateDirect(normals.size() * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(int i = 0; i < normals.size(); i++)
        {
            normalBuffer.put(normals.get(i).x);
            normalBuffer.put(normals.get(i).y);
            normalBuffer.put(normals.get(i).z);
        }
        normalBuffer.flip();

        texCoordBuffer = ByteBuffer.allocateDirect(uvs.size() * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        for(int i = 0; i < uvs.size(); i++)
        {
            texCoordBuffer.put(uvs.get(i).x);
            texCoordBuffer.put(uvs.get(i).y);
        }
        texCoordBuffer.flip();

        indexBuffer = ByteBuffer.allocateDirect(indices.size() * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
        for(int i = 0; i < indices.size(); i++)
        {
            indexBuffer.put(indices.get(i));
        }
        indexBuffer.flip();

        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, Assets.vModelShader);
        GLES20.glAttachShader(program, Assets.fModelShader);
        GLES20.glLinkProgram(program);
    }*/
    //endregion

    public Model(boolean textured)
    {
        isTextured = textured;
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        color = new float[4];
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, textured ? Assets.vTexturedModelShader : Assets.vModelShader);
        GLES20.glAttachShader(program, textured ? Assets.fTexturedModelShader : Assets.fModelShader);
        GLES20.glLinkProgram(program);
    }

    public void setColor(int color)
    {
        this.color[0] = ((color & 0xff0000) >> 16) / 255f;
        this.color[1] = ((color & 0xff00) >> 8) / 255f;
        this.color[2] = (color & 0xff) / 255f;
        this.color[3] = 1;
    }

    public void Move(float dx, float dy, float dz)
    {
        matrix[12] += dx;
        matrix[13] += dy;
        matrix[14] += dz;
    }
}
