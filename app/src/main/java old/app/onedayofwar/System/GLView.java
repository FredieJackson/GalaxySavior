package app.onedayofwar.System;

import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.util.Log;

import app.onedayofwar.Activities.MainActivity;
import app.onedayofwar.Battle.System.BattleView;
import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Campaign.System.GameView;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.GLRenderer;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.Graphics.TextFont;

/**
 * Created by Slava on 29.03.2015.
 */
public class GLView extends GLSurfaceView
{
    private MainActivity activity;
    private GLRenderer renderer;
    private int screenWidth;
    private int screenHeight;

    public GLView(MainActivity activity, int width, int height)
    {
        super(activity);
        this.activity = activity;
        this.screenWidth = width;
        this.screenHeight = height;
        setEGLContextClientVersion(2);
        renderer = new GLRenderer(this);
        setRenderer(renderer);
        setOnTouchListener(renderer);
    }

    public void LoadAssets(Graphics graphics)
    {
        Assets.gsFont = new TextFont(graphics.LoadTexture("fonts/gs.png"), "fonts/gs.xml", new XMLParser(activity.getAssets()));
        Assets.gsColor = Color.argb(255, 0,138,240);

        Assets.space = graphics.LoadTexture("campaign/space/space.jpg");
        Assets.planetStroke = graphics.LoadTexture("campaign/space/planets/stroke.png");
        Assets.playerSkin = graphics.LoadTexture("campaign/space/player.png");
        Assets.botSkin = graphics.LoadTexture("campaign/space/AI.png");
        Assets.btnRegion = graphics.LoadTexture("shipmenu.png");

        Assets.btnStartGame = graphics.LoadTexture("button/Igrat.png");
        Assets.btnSingleGame = graphics.LoadTexture("button/Odinochnaya_igra.png");
        Assets.btnBluetoothGame = graphics.LoadTexture("button/Bluetooth.png");
        Assets.btnCampaing = graphics.LoadTexture("button/Kampania.png");
        Assets.btnQuickBattle = graphics.LoadTexture("button/Igrat.png");
        Assets.btnBack = graphics.LoadTexture("button/Nazad.png");
        Assets.btnNewGame = graphics.LoadTexture("button/Novaya_igra.png");
        Assets.btnLoadGame = graphics.LoadTexture("button/Zagruzit_igru.png");

        Assets.btnCancel = graphics.LoadTexture("button/Otmena.png");
        Assets.btnAttack = graphics.LoadTexture("button/Atakovat.png");
        Assets.btnOK = graphics.LoadTexture("button/Ok.png");
        Assets.btnShoot = graphics.LoadTexture("button/Ogon.png");
        Assets.btnTurn = graphics.LoadTexture("button/Povernut_2.png");
        Assets.btnPanelClose = graphics.LoadTexture("button/Vpered.png");
        Assets.btnFlag = graphics.LoadTexture("button/flag.png");

        Assets.btnMove = graphics.LoadTexture("button/Letet.png");
        Assets.btnEndTurn = graphics.LoadTexture("button/Konets_khoda.png");
        Assets.btnPlus = graphics.LoadTexture("button/Plus.png");
        Assets.btnMinus = graphics.LoadTexture("button/Minus.png");
        Assets.btnExport = graphics.LoadTexture("button/Export.png");
        Assets.btnImport = graphics.LoadTexture("button/Import.png");
        Assets.btnCreate = graphics.LoadTexture("button/Sozdat.png");
        Assets.btnFactory = graphics.LoadTexture("button/Zavod.png");
        Assets.btnBuildings = graphics.LoadTexture("button/Zdania.png");
        Assets.btnBuild = graphics.LoadTexture("button/Stroit.png");
        Assets.btnInfo = graphics.LoadTexture("button/Info.png");
        Assets.btnArmy = graphics.LoadTexture("button/Armia.png");
        Assets.btnSArmy = graphics.LoadTexture("button/Kosmos.png");
        Assets.btnGArmy = graphics.LoadTexture("button/Zemlya.png");
        Assets.btnOil = graphics.LoadTexture("button/Neft.png");
        Assets.btnNanosteel = graphics.LoadTexture("button/Nanostal.png");
        Assets.btnCredits = graphics.LoadTexture("button/Kredity.png");
        Assets.btnQBack = graphics.LoadTexture("button/Nazad_Kvadrat.png");
        Assets.btnPVO = graphics.LoadTexture("button/PVO.png");
        Assets.btnGlare = graphics.LoadTexture("button/Zasvet.png");
        Assets.btnReload = graphics.LoadTexture("button/Perezaryadka.png");
        Assets.btnResources = graphics.LoadTexture("button/Resursya.png");

        Assets.robotIcon = graphics.LoadTexture("unit/icon/Robot.png");
        Assets.robotImage = graphics.LoadTexture("unit/image/robot_big.png");
        Assets.robotStroke = graphics.LoadTexture("unit/stroke/robot_stroke.png");

        Assets.ifvImage = graphics.LoadTexture("unit/image/ifv_big.png");
        Assets.ifvIcon = graphics.LoadTexture("unit/icon/Ivf.png");
        Assets.ifvStroke = graphics.LoadTexture("unit/stroke/ifv_stroke.png");

        Assets.rocketImage = graphics.LoadTexture("unit/image/rocket_big.png");
        Assets.rocketIcon = graphics.LoadTexture("unit/icon/Rocket.png");
        Assets.rocketStroke = graphics.LoadTexture("unit/stroke/rocket_stroke.png");

        Assets.tankImage = graphics.LoadTexture("unit/image/tank_big.png");
        Assets.tankIcon = graphics.LoadTexture("unit/icon/Tank.png");
        Assets.tankStroke = graphics.LoadTexture("unit/stroke/tank_stroke.png");

        Assets.turretImage = graphics.LoadTexture("unit/image/turret_big.png");
        Assets.turretIcon = graphics.LoadTexture("unit/icon/Turret.png");
        Assets.turretStroke = graphics.LoadTexture("unit/stroke/turret_stroke.png");

        Assets.sonderImage = graphics.LoadTexture("unit/image/sonder_big.png");
        Assets.sonderIcon = graphics.LoadTexture("unit/icon/Sonder.png");
        Assets.sonderStroke = graphics.LoadTexture("unit/stroke/sonder_stroke.png");

        Assets.akiraImage = graphics.LoadTexture("unit/image/akira_big.png");
        Assets.akiraIcon = graphics.LoadTexture("unit/icon/Akira.png");
        Assets.akiraStroke = graphics.LoadTexture("unit/stroke/akira_stroke.png");

        Assets.battleshipImage = graphics.LoadTexture("unit/image/storm.png");
        Assets.battleshipIcon = graphics.LoadTexture("unit/icon/Storm.png");
        Assets.battleshipStroke = graphics.LoadTexture("unit/stroke/storm_stroke.png");

        Assets.bioshipImage = graphics.LoadTexture("unit/image/bioship_big.png");
        Assets.bioshipIcon = graphics.LoadTexture("unit/icon/Bioship.png");
        Assets.bioshipStroke = graphics.LoadTexture("unit/stroke/bioship_stroke.png");

        Assets.birdOfPreyImage = graphics.LoadTexture("unit/image/bird_big.png");
        Assets.birdOfPreyIcon = graphics.LoadTexture("unit/icon/Bird.png");
        Assets.birdOfPreyStroke = graphics.LoadTexture("unit/stroke/bird_stroke.png");

        Assets.defaintImage = graphics.LoadTexture("unit/image/defaint_big.png");
        Assets.defaintIcon = graphics.LoadTexture("unit/icon/Defaint.png");
        Assets.defaintStroke = graphics.LoadTexture("unit/stroke/defaint_stroke.png");

        Assets.r2d2Image =  graphics.LoadTexture("unit/image/r2d2_big.png");
        Assets.r2d2Icon = graphics.LoadTexture("unit/icon/Droid.png");
        Assets.r2d2Stroke = graphics.LoadTexture("unit/stroke/r2d2_stroke.png");


        //Assets.grid = graphics.LoadTexture("field/grid/normal_green_5x5.png");
        //Assets.gridIso = graphics.LoadTexture("field/grid/iso_5x5.png");
        //Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.2f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
        //Assets.isoGridCoeff = (int)((screenHeight * (1 - 2 * 0.4f)) / 30) * 30 / (double)Assets.gridIso.getHeight();

        Assets.grid = graphics.LoadTexture("field/grid/Normal_setka.png");
        Assets.gridIso = graphics.LoadTexture("field/grid/Iso_setka.png");
        Assets.gridCoeff = (int)((screenHeight * (1 - 2 * 0.15f)) / 30) * 30 / (double)Assets.gridIso.getHeight();
        Assets.isoGridCoeff = Assets.gridCoeff;

        Assets.signMiss = graphics.LoadTexture("field/mark/Tochka.png");
        Assets.signMissIso = graphics.LoadTexture("field/mark/crater.png");
        Assets.signHit = graphics.LoadTexture("field/mark/Krestik.png");
        Assets.signFlag = graphics.LoadTexture("field/mark/flag.png");
        Assets.signGlare = graphics.LoadTexture("field/mark/glare_green.png");

        Assets.groundBackground = graphics.LoadTexture("desert.jpg");
        Assets.spaceBackground = graphics.LoadTexture("desert.jpg");
        Assets.monitor = graphics.LoadTexture("monitor.png");

        Assets.bullet = graphics.LoadTexture("unit/bullet/Pulya.png");
        Assets.miniRocket = graphics.LoadTexture("unit/bullet/Raketa.png");

        Assets.explosion = graphics.LoadTexture("animation/land_explosion.png");
        Assets.airExplosion = graphics.LoadTexture("animation/air_explosion.png");
        Assets.fire = graphics.LoadTexture("animation/fire2.png");

        Assets.spaceCoeff = 1.0d * screenHeight / Assets.space.getHeight();
        Assets.btnCoeff = screenHeight * 0.17f / Assets.btnAttack.getHeight();
        Assets.monitorHeightCoeff = (double)screenHeight / 1080;
        Assets.monitorWidthCoeff = (double)screenWidth / 1920;
        Assets.iconCoeff = ((screenHeight - 70) / 6d) / Assets.sonderIcon.getHeight();
        Assets.bgHeightCoeff = screenHeight *1f/ Assets.groundBackground.getHeight();
        Assets.bgWidthCoeff = screenWidth *1f/ Assets.groundBackground.getWidth();

        renderer.changeScreen(new MainView(this));
    }

    public void gotoMainMenu()
    {
        renderer.GoMenu();
    }

    public void changeScreen(ScreenView screen)
    {
        renderer.changeScreen(screen);
    }

    public void goBack()
    {
        renderer.GoBack();
    }

    public MainActivity getActivity()
    {
        return activity;
    }

    public void StartCampaign(DBController dbController, boolean isNewGame)
    {
        changeScreen(new GameView(this, dbController, isNewGame));
        activity.gameState = MainActivity.GameState.CAMPAIGN;
    }

    public void StartBattle(Planet planet, char type, boolean isYourTurn)
    {
        BattleView battleView = new BattleView(this, planet, type, isYourTurn);
        if(type == 'b')
        {
            battleView.btController = activity.getBtController();
        }
        Log.i("BT", "Start battle");
        changeScreen(battleView);
        activity.gameState = MainActivity.GameState.BATTLE;
    }

    public void moveCamera(float dx, float dy)
    {
        renderer.moveCamera(dx, dy);
    }

    public void setCamera(float x, float y)
    {
        renderer.setCamera(x, y);
    }

    public float getCameraX()
    {
        return renderer.getCameraX();
    }

    public float getCameraY()
    {
        return renderer.getCameraY();
    }

    public int getScreenHeight()
    {
        return screenHeight;
    }

    public int getScreenWidth()
    {
        return screenWidth;
    }
}
