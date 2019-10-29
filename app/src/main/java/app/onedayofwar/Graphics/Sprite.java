package app.onedayofwar.Graphics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * Created by Slava on 05.03.2015.
 */
public class Sprite
{
    private int width;
    private int height;

    private FloatBuffer vertexBuffer;

    private final int program;
    private int positionHandle;
    private int matrixMVPHandle;

    private float[] uvs;
    private FloatBuffer uvBuffer;

    private int textureID;

    private final int vertexCount = 8 / COORDS_PER_VERTEX;

    private final int vertexStride = COORDS_PER_VERTEX * 4;

    static final int COORDS_PER_VERTEX = 2;

    private float spriteCoords[];

    private float[] color;

    public Sprite(Bitmap bitmap)
    {
        color = new float[4];
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        spriteCoords = new float[8];
        spriteCoords[0] = -width/2;
        spriteCoords[1] = height/2;
        spriteCoords[2] = -width/2;
        spriteCoords[3] = -height/2;
        spriteCoords[4] = width/2;
        spriteCoords[5] = -height/2;
        spriteCoords[6] = width/2;
        spriteCoords[7] = height/2;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(spriteCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(spriteCoords);
        vertexBuffer.position(0);

        // create empty OpenGL ES Program
        program = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.vertexSprite));

        // add the fragment shader to program
        GLES20.glAttachShader(program,  GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.fragmentSprite));

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);

        LoadTexture(bitmap);
    }

    void Draw(float[] mvpMatrix)
    {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

        // Add program to OpenGL ES environment
        GLES20.glUseProgram(program);

        // get handle to vertex shader's vPosition member
        positionHandle = GLES20.glGetAttribLocation(program, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(positionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

        // Get handle to texture coordinates location
        int mTexCoordLoc = GLES20.glGetAttribLocation(program, "a_texCoord");

        // Enable generic vertex attribute array
        GLES20.glEnableVertexAttribArray ( mTexCoordLoc );

        GLES20.glUniform1i(GLES20.glGetUniformLocation(program, "useColorFilter"), color[3] == 0 ? 0 : 1);

        GLES20.glUniform4fv(GLES20.glGetUniformLocation(program, "vColor"), 1, color, 0);

        // Prepare the texturecoordinates
        GLES20.glVertexAttribPointer ( mTexCoordLoc, 2, GLES20.GL_FLOAT, false, 0, uvBuffer);

        // Set the sampler texture unit to 0, where we have saved the texture.
        GLES20.glUniform1i (GLES20.glGetUniformLocation (program, "s_texture" ), 0);

        // get handle to shape's transformation matrix
        matrixMVPHandle = GLES20.glGetUniformLocation(program, "uMVPMatrix");

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(matrixMVPHandle, 1, false, mvpMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(positionHandle);
    }

    private void LoadTexture(Bitmap bmp)
    {
        // Create our UV coordinates.
        uvs = new float[]
        {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f};

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        // Generate Textures, if more needed, alter these numbers.
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);

        textureID = texturenames[0];

        // Bind texture to texturename
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        // Set wrapping mode
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        // We are done using the bitmap so we should recycle it.
        bmp.recycle();
    }

    public void setColorFilter(int color)
    {
        this.color[0] = ((color & 0xff0000) >> 16) / 255;
        this.color[1] = ((color & 0xff00) >> 8) / 255;
        this.color[2] = (color & 0xff) / 255;
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

}
