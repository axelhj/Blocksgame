package se.axelhjelmqvist.blocksgame;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.media.AudioManager;

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

    public void onResume() {
        surfaceView.startThread();
        super.onResume();
    }

    public void onPause() {
        surfaceView.stopThread();
        super.onPause();
    }

    public void onBackPressed() {
        float currentTime = (float)SystemClock.uptimeMillis() / 1000.0f;
        if (currentTime - backPressed < 1.5f) {
            finish();
        } else {
            backPressed = currentTime;
        }
    }
}
