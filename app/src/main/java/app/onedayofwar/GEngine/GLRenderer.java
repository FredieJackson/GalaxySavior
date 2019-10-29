package app.onedayofwar.GEngine;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import app.onedayofwar.GEngine.Meshes.Line;
import app.onedayofwar.GEngine.Meshes.Model;
import app.onedayofwar.GEngine.Meshes.Rectangle;
import app.onedayofwar.GEngine.Meshes.Sprite;
import app.onedayofwar.GEngine.Text.Glyph;
import app.onedayofwar.GEngine.Text.TextFont;

/**
 * Created by Slava on 08.01.2015.
 */
public class GLRenderer implements GLSurfaceView.Renderer
{
    private GLSurface glSurface;
    private Loader loader;

    private float[] tmpMatrix;
    private float[] vpMatrix;
    private float[] projectionMatrix;
    private float[] vpMatrixGUI;
    private float[] tmpVector;

    private long startTime;
    private long sleepTime;
    private float eTime = 0.016f;

    private int arraysHandler[];

    private Line line;
    private Rectangle rect;

    public GLRenderer(GLSurface glSurface)
    {
        this.glSurface = glSurface;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        loader = new Loader(glSurface.getResources().getAssets());
        arraysHandler = new int[3];
        line = new Line();
        rect = new Rectangle();
        tmpMatrix = new float[16];
        vpMatrix = new float[16];
        projectionMatrix = new float[16];
        vpMatrixGUI = new float[16];
        tmpVector = new float[4];
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        glSurface.Initialize(loader, width, height);
        float[] projectionMatrixGUI = new float[16];
        float[] viewMatrixGUI = new float[16];
        Matrix.perspectiveM(projectionMatrix, 0, 45, width*1f/height, 1f, 1000);
        Matrix.orthoM(projectionMatrixGUI, 0, 0, width, 0, height, 1, -1);
        Matrix.setLookAtM(viewMatrixGUI, 0, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f);
        Matrix.multiplyMM(vpMatrixGUI, 0, projectionMatrixGUI, 0, viewMatrixGUI, 0);

        GLES20.glClearColor(0.12f, 0.56f, 1, 1);
        //GLES20.glClearColor(0, 0, 0, 1);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
    }

    public void prepareMatrices(float[] viewMatrix)
    {
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl)
    {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        glSurface.Update();
        glSurface.Draw(this);
        /*switch(GLES20.glGetError())
        {
            case GLES20.GL_NO_ERROR:
                Log.i("OPENGL", "No Error");
                break;
            case GLES20.GL_INVALID_ENUM:
                Log.i("OPENGL", "Invalid Enum");
                break;
            case GLES20.GL_INVALID_VALUE:
                Log.i("OPENGL", "Invalid Value");
                break;
            case GLES20.GL_INVALID_OPERATION:
                Log.i("OPENGL", "Invalid Operation");
                break;
            case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
                Log.i("OPENGL", "invalid Framebuffer Operation");
                break;
            case GLES20.GL_OUT_OF_MEMORY:
                Log.i("OPENGL", "Out of Memory");
                break;
        }*/
    }

    public void DrawLine(float xb, float yb, float zb, float xe, float ye, float ze, int color)
    {
        line.setShape(xb, yb, zb, xe, ye, ze, color);
        GLES20.glUseProgram(line.program);
        arraysHandler[0] = GLES20.glGetAttribLocation(line.program, "aPosition");
        GLES20.glEnableVertexAttribArray(arraysHandler[0]);
        GLES20.glVertexAttribPointer(arraysHandler[0], 3, GLES20.GL_FLOAT, false, 3 * 4, line.vertexBuffer);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(line.program, "uMVPMatrix"), 1, false, vpMatrixGUI, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(line.program, "uColor"), 1, line.colorBuffer);
        GLES20.glDrawArrays(GLES20.GL_LINES, 0, 2);
        GLES20.glDisableVertexAttribArray(arraysHandler[0]);
    }

    public void DrawRectangle(float x, float y, float z, int width, int height, int color, boolean isFilled)
    {
        rect.SetShape(x, y, z, width, height, color, isFilled);
        GLES20.glUseProgram(rect.program);
        arraysHandler[0] = GLES20.glGetAttribLocation(rect.program, "aPosition");
        GLES20.glEnableVertexAttribArray(arraysHandler[0]);
        GLES20.glVertexAttribPointer(arraysHandler[0], 2, GLES20.GL_FLOAT, false, 2 * 4, rect.vertexBuffer);
        Matrix.multiplyMM(tmpMatrix, 0, vpMatrixGUI, 0, rect.matrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(rect.program, "uMVPMatrix"), 1, false, tmpMatrix, 0);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(rect.program, "uColor"), 1, rect.colorBuffer);
        GLES20.glDrawArrays(isFilled ? GLES20.GL_TRIANGLE_FAN : GLES20.GL_LINE_LOOP, 0, 4);
        GLES20.glDisableVertexAttribArray(arraysHandler[0]);
    }

    public void DrawSprite(Sprite sprite)
    {
        GLES20.glUseProgram(sprite.program);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, sprite.getTexture());
        arraysHandler[0] = GLES20.glGetAttribLocation(sprite.program, "aPosition");
        GLES20.glEnableVertexAttribArray(arraysHandler[0]);
        GLES20.glVertexAttribPointer(arraysHandler[0], 2, GLES20.GL_FLOAT, false, 2 * 4, sprite.vertexBuffer);
        arraysHandler[1] = GLES20.glGetAttribLocation(sprite.program, "aTexCoord");
        GLES20.glEnableVertexAttribArray (arraysHandler[1]);
        GLES20.glVertexAttribPointer(arraysHandler[1], 2, GLES20.GL_FLOAT, false, 2 * 4, sprite.uvBuffer);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(sprite.program, "useColorFilter"), sprite.color[3] == 0 ? 0 : 1);
        GLES20.glUniform4fv(GLES20.glGetUniformLocation(sprite.program, "uColor"), 1, sprite.color, 0);
        GLES20.glUniform1i(GLES20.glGetUniformLocation(sprite.program, "uTexture"), GLES20.GL_TEXTURE0);
        Matrix.multiplyMM(tmpMatrix, 0, vpMatrixGUI, 0, sprite.matrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(sprite.program, "uMVPMatrix"), 1, false, tmpMatrix, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        GLES20.glDisableVertexAttribArray(arraysHandler[0]);
        GLES20.glDisableVertexAttribArray(arraysHandler[1]);
    }

    public void DrawModel(Model model, float[] viewMatrix, float[] lightPosition)
    {
        GLES20.glUseProgram(model.program);

        if(model.isTextured)
        {
            for (int i = 0; i < model.textures.length; i++)
            {
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, model.textures[i]);

                arraysHandler[2] = GLES20.glGetAttribLocation(model.program, "aTexCoord");
                GLES20.glEnableVertexAttribArray (arraysHandler[2]);
                GLES20.glVertexAttribPointer (arraysHandler[2], 2, GLES20.GL_FLOAT, false, 2 * 4, model.texCoordBuffer);


                GLES20.glUniform1i(GLES20.glGetUniformLocation (model.program, "uTexture" ), GLES20.GL_TEXTURE0);
            }
        }

        arraysHandler[0] = GLES20.glGetAttribLocation(model.program, "aPosition");
        GLES20.glEnableVertexAttribArray(arraysHandler[0]);
        GLES20.glVertexAttribPointer(arraysHandler[0], 3, GLES20.GL_FLOAT, false, 3 * 4, model.vertexBuffer);

        arraysHandler[1] = GLES20.glGetAttribLocation(model.program, "aNormal");
        GLES20.glEnableVertexAttribArray(arraysHandler[1]);
        GLES20.glVertexAttribPointer(arraysHandler[1], 3, GLES20.GL_FLOAT, false, 3 * 4, model.normalBuffer);

        /*colorHandle = GLES20.glGetAttribLocation(program, "aColor");
        GLES20.glEnableVertexAttribArray(colorHandle);
        GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, 4 * 4, colorBuffer);*/

        Matrix.multiplyMM(tmpMatrix,0, viewMatrix, 0, model.matrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(model.program, "uMVMatrix"), 1, false, tmpMatrix, 0);
        Matrix.multiplyMM(tmpMatrix, 0, vpMatrix, 0, model.matrix, 0);
        GLES20.glUniformMatrix4fv(GLES20.glGetUniformLocation(model.program, "uMVPMatrix"), 1, false, tmpMatrix, 0);
        Matrix.multiplyMV(tmpVector, 0, viewMatrix, 0, lightPosition, 0);
        GLES20.glUniform3fv(GLES20.glGetUniformLocation(model.program, "uLightPosition"), 1, tmpVector, 0);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, model.indexBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, model.indexBuffer);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glDisableVertexAttribArray(arraysHandler[0]);
        GLES20.glDisableVertexAttribArray(arraysHandler[1]);
        //GLES20.glDisableVertexAttribArray(texCoordHandle);
    }

    public void DrawText(String text, TextFont font, float x, float y, int color, int size)
    {
        font.sprite.matrix[0] = size * 1f / font.TEXTURE_SIZE;
        font.sprite.matrix[5] = size * 1f / font.TEXTURE_SIZE;
        font.sprite.matrix[12] = x;
        font.sprite.matrix[13] = y + size /2;
        font.sprite.setColorFilter(color);
        for(int i = 0; i < text.length(); i++)
        {
            for(int j = 0; j < font.glyphs.size(); j++)
            {
                if(text.charAt(i) == '\n')
                {
                    font.sprite.matrix[12] = x;
                    font.sprite.Move(0, -font.sprite.getHeight());
                    break;
                }
                if(font.glyphs.get(j).id == (int)text.charAt(i))
                {
                    Glyph glyph = font.glyphs.get(j);
                    font.sprite.setShape(glyph.x, glyph.y, glyph.width, glyph.height);
                    font.sprite.Move(font.sprite.getWidth()/2, 0);
                    DrawSprite(font.sprite);
                    font.sprite.Move(font.sprite.getWidth()/2 + font.TEXTURE_SIZE * 1f / size, 0);
                    break;
                }
            }
        }
    }
}
