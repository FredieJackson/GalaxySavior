package app.onedayofwar.GEngine;


import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import app.onedayofwar.GEngine.Meshes.Model;
import app.onedayofwar.GEngine.Text.TextFont;
import app.onedayofwar.GEngine.Text.Texture;
import app.onedayofwar.Utils.IO.XMLParser;


/**
 * Created by Slava on 13.03.2015.
 */
public class Loader
{
    public AssetManager assets;

    public Loader(AssetManager assets)
    {
        this.assets = assets;
        LoadAssets();
    }

    //region LoadAssets
    public void LoadAssets()
    {
        Assets.vAnimationShader = LoadShader(GLES20.GL_VERTEX_SHADER, "Shaders/Sprite/vAnimation");
        Assets.vSpriteShader = LoadShader(GLES20.GL_VERTEX_SHADER, "Shaders/Sprite/v");
        Assets.fSpriteShader = LoadShader(GLES20.GL_FRAGMENT_SHADER, "Shaders/Sprite/f");
        Assets.vModelShader = LoadShader(GLES20.GL_VERTEX_SHADER, "Shaders/Model/v");
        Assets.fModelShader = LoadShader(GLES20.GL_FRAGMENT_SHADER, "Shaders/Model/f");
        Assets.vTexturedModelShader = LoadShader(GLES20.GL_VERTEX_SHADER, "Shaders/TexturedModel/v");
        Assets.fTexturedModelShader = LoadShader(GLES20.GL_FRAGMENT_SHADER, "Shaders/TexturedModel/f");
        Assets.vRectangleShader = LoadShader(GLES20.GL_VERTEX_SHADER, "Shaders/Rectangle/v");
        Assets.fRectangleShader = LoadShader(GLES20.GL_FRAGMENT_SHADER, "Shaders/Rectangle/f");

        Assets.gsFont = new TextFont(LoadTexture("fonts/gs.png"), "fonts/gs.xml", new XMLParser(assets));

        Assets.gsColor = Color.argb(255, 0, 138, 240);

        Assets.space = LoadTexture("campaign/space/space.jpg");
        Assets.planetStroke = LoadTexture("campaign/space/planets/stroke.png");
        Assets.playerSkin = LoadTexture("campaign/space/player.png");
        Assets.botSkin = LoadTexture("campaign/space/AI.png");
        Assets.btnRegion = LoadTexture("shipmenu.png");

        Assets.btnStartGame = LoadTexture("button/Igrat.png");
        Assets.btnSingleGame = LoadTexture("button/Odinochnaya_igra.png");
        Assets.btnBluetoothGame = LoadTexture("button/Bluetooth.png");
        Assets.btnCampaing = LoadTexture("button/Kampania.png");
        Assets.btnQuickBattle = LoadTexture("button/Igrat.png");
        Assets.btnBack = LoadTexture("button/Nazad.png");
        Assets.btnNewGame = LoadTexture("button/Novaya_igra.png");
        Assets.btnLoadGame = LoadTexture("button/Zagruzit_igru.png");

        Assets.btnCancel = LoadTexture("button/Otmena.png");
        Assets.btnAttack = LoadTexture("button/Atakovat.png");
        Assets.btnOK = LoadTexture("button/Ok.png");
        Assets.btnShoot = LoadTexture("button/Ogon.png");
        Assets.btnTurn = LoadTexture("button/Povernut_2.png");
        Assets.btnPanelClose = LoadTexture("button/Vpered.png");
        Assets.btnFlag = LoadTexture("button/flag.png");

        Assets.btnMove = LoadTexture("button/Letet.png");
        Assets.btnEndTurn = LoadTexture("button/Konets_khoda.png");
        Assets.btnPlus = LoadTexture("button/Plus.png");
        Assets.btnMinus = LoadTexture("button/Minus.png");
        Assets.btnExport = LoadTexture("button/Export.png");
        Assets.btnImport = LoadTexture("button/Import.png");
        Assets.btnCreate = LoadTexture("button/Sozdat.png");
        Assets.btnFactory = LoadTexture("button/Zavod.png");
        Assets.btnBuildings = LoadTexture("button/Zdania.png");
        Assets.btnBuild = LoadTexture("button/Stroit.png");
        Assets.btnInfo = LoadTexture("button/Info.png");
        Assets.btnArmy = LoadTexture("button/Armia.png");
        Assets.btnSArmy = LoadTexture("button/Kosmos.png");
        Assets.btnGArmy = LoadTexture("button/Zemlya.png");
        Assets.btnOil = LoadTexture("button/Neft.png");
        Assets.btnNanosteel = LoadTexture("button/Nanostal.png");
        Assets.btnCredits = LoadTexture("button/Kredity.png");
        Assets.btnQBack = LoadTexture("button/Nazad_Kvadrat.png");
        Assets.btnPVO = LoadTexture("button/PVO.png");
        Assets.btnGlare = LoadTexture("button/Zasvet.png");
        Assets.btnReload = LoadTexture("button/Perezaryadka.png");
        Assets.btnResources = LoadTexture("button/Resursya.png");

        Assets.btnEasyGame = LoadTexture("button/Legko.png");
        Assets.btnNormalGame = LoadTexture("button/Normalno.png");
        Assets.btnHardGame = LoadTexture("button/Trudno.png");
        Assets.btnGodGame = LoadTexture("button/Bog.png");

        Assets.robotIcon = LoadTexture("unit/icon/Robot.png");
        Assets.robotImage = LoadTexture("unit/image/robot_big.png");
        Assets.robotStroke = LoadTexture("unit/stroke/robot_stroke.png");

        Assets.ifvImage = LoadTexture("unit/image/ifv_big.png");
        Assets.ifvIcon = LoadTexture("unit/icon/Ivf.png");
        Assets.ifvStroke = LoadTexture("unit/stroke/ifv_stroke.png");

        Assets.rocketImage = LoadTexture("unit/image/rocket_big.png");
        Assets.rocketIcon = LoadTexture("unit/icon/Rocket.png");
        Assets.rocketStroke = LoadTexture("unit/stroke/rocket_stroke.png");

        Assets.tankImage = LoadTexture("unit/image/tank_big.png");
        Assets.tankIcon = LoadTexture("unit/icon/Tank.png");
        Assets.tankStroke = LoadTexture("unit/stroke/tank_stroke.png");

        Assets.turretImage = LoadTexture("unit/image/turret_big.png");
        Assets.turretIcon = LoadTexture("unit/icon/Turret.png");
        Assets.turretStroke = LoadTexture("unit/stroke/turret_stroke.png");

        Assets.sonderImage = LoadTexture("unit/image/sonder_big.png");
        Assets.sonderIcon = LoadTexture("unit/icon/Sonder.png");
        Assets.sonderStroke = LoadTexture("unit/stroke/sonder_stroke.png");

        Assets.akiraImage = LoadTexture("unit/image/akira_big.png");
        Assets.akiraIcon = LoadTexture("unit/icon/Akira.png");
        Assets.akiraStroke = LoadTexture("unit/stroke/akira_stroke.png");

        Assets.battleshipImage = LoadTexture("unit/image/storm.png");
        Assets.battleshipIcon = LoadTexture("unit/icon/Storm.png");
        Assets.battleshipStroke = LoadTexture("unit/stroke/storm_stroke.png");

        Assets.bioshipImage = LoadTexture("unit/image/bioship_big.png");
        Assets.bioshipIcon = LoadTexture("unit/icon/Bioship.png");
        Assets.bioshipStroke = LoadTexture("unit/stroke/bioship_stroke.png");

        Assets.birdOfPreyImage = LoadTexture("unit/image/bird_big.png");
        Assets.birdOfPreyIcon = LoadTexture("unit/icon/Bird.png");
        Assets.birdOfPreyStroke = LoadTexture("unit/stroke/bird_stroke.png");

        Assets.defaintImage = LoadTexture("unit/image/defaint_big.png");
        Assets.defaintIcon = LoadTexture("unit/icon/Defaint.png");
        Assets.defaintStroke = LoadTexture("unit/stroke/defaint_stroke.png");

        Assets.r2d2Image = LoadTexture("unit/image/r2d2_big.png");
        Assets.r2d2Icon = LoadTexture("unit/icon/Droid.png");
        Assets.r2d2Stroke = LoadTexture("unit/stroke/r2d2_stroke.png");

        //Assets.grid = loader.LoadTexture("field/grid/normal_green_5x5.png");
        //Assets.gridIso = loader.LoadTexture("field/grid/iso_5x5.png");
        //Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.2f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
        //Assets.isoGridCoeff = (int)((screenHeight * (1 - 2 * 0.4f)) / 30) * 30 / (double)Assets.gridIso.getHeight();

        Assets.grid = LoadTexture("field/grid/Normal_setka.png");
        Assets.gridIso = LoadTexture("field/grid/Iso_setka.png");

        Assets.isoGridCoeff = Assets.gridCoeff;

        Assets.signMiss = LoadTexture("field/mark/Tochka.png");
        Assets.signMissIso = LoadTexture("field/mark/crater.png");
        Assets.signHit = LoadTexture("field/mark/Krestik.png");
        Assets.signFlag = LoadTexture("field/mark/flag.png");
        Assets.signGlare = LoadTexture("field/mark/Plyusik.png");

        Assets.groundBackground = LoadTexture("desert.jpg");
        Assets.spaceBackground = LoadTexture("spacebackground.jpg");
        Assets.monitor = LoadTexture("monitor.png");

        Assets.bullet = LoadTexture("unit/bullet/Pulya.png");
        Assets.miniRocket = LoadTexture("unit/bullet/Raketa.png");

        Assets.explosion = LoadTexture("animation/land_explosion.png");
        Assets.airExplosion = LoadTexture("animation/air_explosion.png");
        Assets.fire = LoadTexture("animation/fire2.png");

        /*Assets.spaceCoeff = 1.0d * screenHeight / Assets.space.getHeight();
        Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.15f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
        Assets.btnCoeff = screenHeight * 0.17f / Assets.btnAttack.getHeight();
        Assets.monitorHeightCoeff = (double)screenHeight / 1080;
        Assets.monitorWidthCoeff = (double)screenWidth / 1920;
        Assets.iconCoeff = ((screenHeight - 70) / 6d) / Assets.sonderIcon.getHeight();
        Assets.bgHeightCoeff = screenHeight *1f/ Assets.groundBackground.getHeight();
        Assets.bgWidthCoeff = screenWidth *1f/ Assets.groundBackground.getWidth();*/
    }
    //endregion

    public int LoadShader(int type, String path)
    {
        int shader = GLES20.glCreateShader(type);
        try {
            AssetManager assetManager = assets;
            BufferedReader in = new BufferedReader(new InputStreamReader(assetManager.open(path)));
            String line;
            String shaderCode = "";
            while ((line = in.readLine()) != null) { shaderCode += line; }
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
        } catch (IOException e) { throw new RuntimeException("Couldn't load shader from asset '" + path + "'"); }
        return shader;
    }

    public Texture LoadTexture(String fileName)
    {
        InputStream in = null;
        Bitmap bitmap = null;
        try
        {
            in = assets.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
                throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        }
        catch (IOException e)
        {
            throw new RuntimeException("Couldn't load bitmap from asset '" + fileName + "'");
        }
        finally
        {
            if (in != null)
            {
                try { in.close(); }
                catch (IOException e) {}
            }
        }
        int[] texturenames = new int[1];
        GLES20.glGenTextures(1, texturenames, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[0]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bitmap, 0);
        bitmap.recycle();
        return new Texture(texturenames[0], bitmap.getWidth(), bitmap.getHeight());
    }

    //region Old Model Loading
    /*public Model LoadModel(String fileName)
    {
        ArrayList<Vector3> vertices = new ArrayList<>();
        ArrayList<Vector3> normals = new ArrayList<>();
        ArrayList<Vector3> texCoords = new ArrayList<>();
        ArrayList<Vector3> vIndices = new ArrayList<>();
        ArrayList<Vector3> nIndices = new ArrayList<>();
        ArrayList<Vector3> tIndices = new ArrayList<>();

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(assets.open(fileName)));
            String line;
            String[] tmp;
            char type;
            while ((line = in.readLine()) != null) {
                if(line.length() < 2)
                    continue;
                type = line.charAt(1);
                if (line.charAt(0) == 'v')
                {
                    line = line.substring(2, line.length());
                    line = line.trim();
                    tmp = line.split(" ");
                    switch(type)
                    {
                        case 'n':
                            normals.add(new Vector3(Float.parseFloat(tmp[0]), Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2])));
                            break;
                        case 't':
                            texCoords.add(new Vector3(Float.parseFloat(tmp[0]), Float.parseFloat(tmp[1]), 0));
                            break;
                        case ' ':
                            vertices.add(new Vector3(Float.parseFloat(tmp[0]), Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2])));
                            break;
                    }
                }
                else if (line.charAt(0) == 'f')
                {
                    String[] buff;
                    line = line.substring(2, line.length());
                    line = line.trim();
                    tmp = line.split(" ");
                    float[] tmpV = new float[3];
                    float[] tmpN = new float[3];
                    float[] tmpTC = new float[3];
                    for(int i = 0; i < tmp.length; i++)
                    {
                        buff = tmp[i].split("/");
                        tmpV[i] = Float.parseFloat(buff[0]);
                        if(!buff[1].isEmpty()) tmpTC[i] = Float.parseFloat(buff[1]);
                        tmpN[i] = Float.parseFloat(buff[2]);
                    }
                    vIndices.add(new Vector3(tmpV));
                    tIndices.add(new Vector3(tmpTC));
                    nIndices.add(new Vector3(tmpN));
                }
            }
        }catch(IOException e){}

        return new Model(vertices, normals, texCoords, vIndices, nIndices, tIndices);
    }*/

    /*public Model LoadModel(String fileName, int gsm)
    {
        ArrayList<Vector3> vertices = new ArrayList<>();
        ArrayList<Vector3> normals = new ArrayList<>();
        ArrayList<Vector3> uv = new ArrayList<>();
        ArrayList<Short> indices = new ArrayList<>();

        try {
            AssetManager assetManager = renderer.res.getAssets();
            InputStreamReader is = new InputStreamReader(assetManager.open(fileName));
            BufferedReader in = new BufferedReader(is);
            String line;
            String[] tmp;
            while ((line = in.readLine()) != null) {
                if(line.length() < 2)
                    continue;
                switch(line.charAt(0))
                {
                    case 'v':
                        line = line.substring(1, line.length());
                        line = line.trim();
                        tmp = line.split(" ");
                        vertices.add(new Vector3(Float.parseFloat(tmp[0]), Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2])));
                        break;
                    case 't':
                        line = line.substring(1, line.length());
                        line = line.trim();
                        tmp = line.split(" ");
                        uv.add(new Vector3(Float.parseFloat(tmp[0]), Float.parseFloat(tmp[1]), 0));
                        break;
                    case 'n':
                        line = line.substring(1, line.length());
                        line = line.trim();
                        tmp = line.split(" ");
                        normals.add(new Vector3(Float.parseFloat(tmp[0]), Float.parseFloat(tmp[1]), Float.parseFloat(tmp[2])));
                        break;
                    case 'i':
                        line = line.substring(1, line.length());
                        line = line.trim();
                        indices.add(Short.parseShort(line));
                        break;
                }
            }
        }catch(IOException e){}

        return new Model(vertices, normals, uv, indices);
    }*/
    //endregion

    public Model LoadModel(String fileName, boolean textured)
    {
        Model model = new Model(textured);
        try
        {
            fileName = "Models/" + fileName;
            BufferedReader in = new BufferedReader(new InputStreamReader(assets.open(fileName + "/m.gsm")));
            String line;
            String[] tmp;
            int tc = 0;
            while ((line = in.readLine()) != null)
            {
                if(line.length() < 2)
                    continue;
                switch(line.charAt(0))
                {
                    case '$':
                        switch(line.charAt(1))
                        {
                            case 'v':
                                model.vertexBuffer = ByteBuffer.allocateDirect(Integer.parseInt(line.substring(2)) * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                                model.texCoordBuffer = ByteBuffer.allocateDirect(Integer.parseInt(line.substring(2)) * 2 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                                model.normalBuffer = ByteBuffer.allocateDirect(Integer.parseInt(line.substring(2)) * 3 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
                                break;
                            case 'i':
                                model.indexBuffer = ByteBuffer.allocateDirect(Integer.parseInt(line.substring(2)) * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
                                break;
                            case 't':
                                model.textures = new int[Integer.parseInt(line.substring(2))];
                                break;
                        }
                        break;
                    case '@':
                        model.textures[tc] = LoadTexture(fileName + '/' + line.substring(1)).getId();
                        tc++;
                        break;
                    case 'v':
                        tmp = line.substring(1, line.length()).trim().split(" ");
                        for(int i = 0; i < tmp.length; i++)
                            model.vertexBuffer.put(Float.parseFloat(tmp[i]));
                        break;
                    case 't':
                        tmp = line.substring(1, line.length()).trim().split(" ");
                        for(int i = 0; i < tmp.length; i++)
                            model.texCoordBuffer.put(Float.parseFloat(tmp[i]));
                        break;
                    case 'n':
                        tmp = line.substring(1, line.length()).trim().split(" ");
                        for(int i = 0; i < tmp.length; i++)
                            model.normalBuffer.put(Float.parseFloat(tmp[i]));
                        break;
                    case 'i':
                        model.indexBuffer.put(Short.parseShort(line.substring(1, line.length()).trim()));
                        break;
                }
            }
            model.vertexBuffer.flip();
            model.texCoordBuffer.flip();
            model.normalBuffer.flip();
            model.indexBuffer.flip();
        }catch(IOException e){ throw new RuntimeException("Couldn't load model from asset '" + fileName + "'"); }
        return model;
    }
}
