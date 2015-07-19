package se.axelhjelmqvist.blocksgame;

import android.os.SystemClock;

/**
 * A timer that is used to calculate timing values for a game-loop.
 * Will account for time that the game was paused in its calculations.
 */
public class Timer {
    float initialTime, currentTime, lastTime;

    float deltaTime, pauseTime, totalPauseTime;

    public boolean pausing = false;

    /**
     * Constructor of the timer. Sets the initial time,
     * ie. starts the timer.
     */
    public Timer() {
        initialTime = getTime();
    }

    /**
     * Update the timer. This should be called once before, during or
     * after every gameloop update.
     */
    public void update() {
        lastTime = currentTime;
        currentTime = getTime();
        deltaTime = currentTime - lastTime;
        if (deltaTime < 0.0f) {
            deltaTime = 0.0f;
        }
    }

    /**
     * Gets the current syste time (system clock uptime value).
     */
    private float getTime() {
        return (float)SystemClock.uptimeMillis() / 1000.0f;
    }

    /**
     * Gets the total time since the timer was created. The amount of
     * time that was elapsed during paused is subtracted.
     */
    public float getTotal() {
        return (currentTime - initialTime) - totalPauseTime;
    }

    /**
     * Gets the delta of time that elapsed during the last two consectuive calls to
     * update, not counting time elapsed when paused.
     */
    public float getDelta() {
        return pausing ? 0.0f : deltaTime;
    }

    /**
     * Set the timer to paused mode. The time elapsed when paused is not
     * included in the value returned by the getTotal method. 
     */
    public void pause() {
        if (!pausing) {
            pauseTime = getTime();
            pausing = true;
        }
    }

    /**
     * Resume after pausing to start increasing the total time elapsed value.
     */
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