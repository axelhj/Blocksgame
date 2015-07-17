package se.axelhjelmqvist.blocksgame;

import android.os.SystemClock;

public class Timer {
    float initialTime, currentTime, lastTime;
    float deltaTime, pauseTime, totalPauseTime;
    public boolean pausing = false;

    public Timer() {
        initialTime = getTime();
    }

    public void update() {
        lastTime = currentTime;
        currentTime = getTime();
        deltaTime = currentTime - lastTime;
        if (deltaTime < 0.0f) {
            deltaTime = 0.0f;
        }
    }

    private float getTime() {
        return (float)SystemClock.uptimeMillis() / 1000.0f;
    }

    public float getTotal() {
        return (currentTime - initialTime) - totalPauseTime;
    }

    public float getDelta() {
        return pausing ? 0.0f : deltaTime;
    }

    public void pause() {
        if (!pausing) {
            pauseTime = getTime();
            pausing = true;
        }
    }

    public void resume() {
        if (pausing) {  
        	currentTime = getTime();
            float interval = pauseTime-currentTime;
            lastTime += interval;
            totalPauseTime += interval;
            pausing = false;
        }
    }
}