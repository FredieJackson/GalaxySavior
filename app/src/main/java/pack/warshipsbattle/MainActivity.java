package pack.warshipsbattle;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
    }
    public void ClickBtn1(View view)
    {
        StartGame();
    }
    public void ClickBtn2(View view)
    {

    }
    public void ClickBtn3(View view)
    {

    }
    private void StartGame()
    {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
