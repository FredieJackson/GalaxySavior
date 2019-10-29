package app.onedayofwar.Campaign.System;

import android.graphics.Color;
import android.view.MotionEvent;

import app.onedayofwar.Battle.BattleElements.BattlePlayer;
import app.onedayofwar.Battle.Mods.SingleBattle;
import app.onedayofwar.Campaign.Space.Planet;
import app.onedayofwar.Graphics.Assets;
import app.onedayofwar.Graphics.Graphics;
import app.onedayofwar.Graphics.ScreenView;
import app.onedayofwar.System.GLView;
import app.onedayofwar.UI.Button;

/**
 * Created by Slava on 30.03.2015.
 */
public class PlanetView implements ScreenView
{
    GLView glView;
    Planet planet;
    Button attackBtn;

    public PlanetView(GLView glView, Planet planet)
    {
        this.glView = glView;
        this.planet = planet;
        attackBtn = new Button(Assets.btnFinishInstallation, 50, 50, false);
        attackBtn.Scale(Assets.btnCoeff);
    }

    @Override
    public void Initialize(Graphics graphics)
    {

    }

    @Override
    public void Update(float eTime)
    {

    }

    @Override
    public void Draw(Graphics graphics)
    {
        attackBtn.Draw(graphics);
        graphics.DrawText("Size: " + planet.getFieldSize(), Assets.arialFont, 200, 50, Color.GREEN, 50);
        graphics.DrawText("Oil: " + planet.oil, Assets.arialFont, 200, 150, Color.GREEN, 50);
        graphics.DrawText("Nano Steel: " + planet.nanoSteel, Assets.arialFont, 200, 250, Color.GREEN, 50);
    }

    @Override
    public void OnTouch(MotionEvent event)
    {
        BattlePlayer.fieldSize = 15;
        BattlePlayer.unitCount = planet.getGroundGuards().clone();
        SingleBattle.difficulty = (byte)(Math.random()*40);
        glView.StartBattle('c', Math.random() < 0.3);
    }

    @Override
    public void Resume()
    {

    }
}
