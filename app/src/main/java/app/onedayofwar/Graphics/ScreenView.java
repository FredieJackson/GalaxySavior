package app.onedayofwar.Graphics;

/**
 * Created by Slava on 14.03.2015.
 */
public interface ScreenView
{
    abstract public void Initialize(Graphics graphics);
    abstract public void Update(float eTime);
    abstract public void Draw(Graphics graphics);

}
