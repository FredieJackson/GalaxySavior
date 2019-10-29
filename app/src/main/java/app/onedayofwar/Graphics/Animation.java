package app.onedayofwar.Graphics;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;


/**
 * Created by Slava on 14.02.2015.
 */
public class Animation
{
    private int frames;
    private int currentFrame;
    private int speed;
    private float tick;
    private int width;
    private int height;
    private int start;
    public float[] textureMatrix;
    private boolean isLooped;
    private boolean isStart;

    private FloatBuffer vertexBuffer;

    private final int program;
    private int positionHandle;

    private float[] uvs;
    private FloatBuffer uvBuffer;

    private int textureID;

    private final int vertexCount = 8 / COORDS_PER_VERTEX;

    private final int vertexStride = COORDS_PER_VERTEX * 4;

    static final int COORDS_PER_VERTEX = 2;

    private float spriteCoords[];

    private float[] color;

    public Animation(Bitmap bitmap, int frames, int latency, int start, boolean isLooped)
    {
        this.frames = frames;
        this.speed = latency;
        this.width = bitmap.getWidth()/frames;
        this.height = bitmap.getHeight();
        this.isLooped = isLooped;
        this.start = start;
        isStart = false;
        tick = 0;
        currentFrame = 0;
        textureMatrix = new float[16];
        Matrix.setIdentityM(textureMatrix, 0);

        color = new float[4];
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
        GLES20.glAttachShader(program, GLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, Shaders.vertexAnimation));

        // add the fragment shader to program
        GLES20.glAttachShader(program,  GLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, Shaders.fragmentSprite));

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(program);

        LoadTexture(bitmap);
    }

    public void Update(float eTime)
    {
        if(isStart)
        {
            if (tick >= speed)
            {
                if (currentFrame == frames - 1)
                {
                    if(!isLooped)
                    {
                        isStart = false;
                    }
                    currentFrame = start;
                    Matrix.setIdentityM(textureMatrix, 0);
                }
                Matrix.translateM(textureMatrix, 0, 1f/frames, 0, 0);
                tick = 0;
                currentFrame++;
            }
            else
            {
                tick += eTime * 10000;
            }
        }
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

        // Pass the projection and view transformation to the shader
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "uMVPMatrix"), 1, false, mvpMatrix, 0);

        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(program, "texMat"), 1, false, textureMatrix, 0);

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
                        1.0f / frames, 1.0f,
                        1.0f / frames, 0.0f};

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

    public void Start()
    {
        isStart = true;
    }

    public boolean IsStart()
    {
        return isStart;
    }
}
