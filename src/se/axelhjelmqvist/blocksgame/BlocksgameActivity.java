package se.axelhjelmqvist.blocksgame;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.media.AudioManager;

/**
 * The main activity of the tetris game. The root object of the game hierarchy.
 */
public class BlocksgameActivity
        extends Activity {
    BlocksgameSurfaceView surfaceView;

    float backPressed;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            finish();
            return;
        }
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        backPressed = 0.0f;
        surfaceView = new BlocksgameSurfaceView(getApplicationContext());
        setContentView(surfaceView);
    }

    /**
     * Called whenever the activity enters the the foreground visible state
     * of the system - it is visible and accepts input.
     */
    public void onResume() {
        surfaceView.startThread();
        super.onResume();
    }

    /**
     * Called whenever the activity loses the the foreground visible state
     * of the system - it may still be visible after this call but can not
     * accept input and may be destroyed if the memory is running low, etc.
     */
    public void onPause() {
        surfaceView.stopThread();
        super.onPause();
    }

    /**
     * Method that is invoked whenever the back-key is pressed. If the back-key
     * is pressed twice in a sufficiently short amount of time, the app will close.
     * This is not a very nice way to handle the scenario of ending the game but
     * it will have to do for now.
     */
    public void onBackPressed() {
        float currentTime = (float)SystemClock.uptimeMillis() / 1000.0f;
        if (currentTime - backPressed < 1.5f) {
            finish();
        } else {
            backPressed = currentTime;
        }
    }
}
