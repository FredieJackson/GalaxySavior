package app.onedayofwar.OldBattle.BattleElements;

/**
 * Created by Slava on 10.01.2015.
 */
public class BattlePlayer
{
    public static int credits;
    public static void Initialize(int credits)
    {
        BattlePlayer.credits = credits;
    }
    public static byte[] unitCount;
    public static byte fieldSize;
    public static boolean isGround;
    public static int level;
}
