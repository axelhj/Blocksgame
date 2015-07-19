package se.axelhjelmqvist.blocksgame;

import java.util.ArrayList;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * SoundPlayer class. Loads and plays a few sound effects.
 * This class is a Singleton object. 
 */
public class SoundPlayer {
    private static SoundPlayer instance = null;

    private ArrayList<Integer> media;

    private SoundPool soundPool;

    private boolean isEnabled;

    /**
     * Constructor of the soundplayer objetc that initializes
     * the fields. This constructor is private.
     */
    private SoundPlayer() {
        media = new ArrayList<Integer>();
        soundPool = null;
        isEnabled = false;
    }

    /**
     * Returns the single instance of the SoundPlayer object.
     * If no object exists, one is created.
     */
    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }

    /**
     * Plays a sound that corresponds to a secret id.
     */
    public void playSound(int id) {
        if (isEnabled && id < media.size()) {
            soundPool.play(media.get(id), 0.5f, 0.5f, 0, 0, 1.0f);
        }
    }

    /**
     * Set a value to decide if the SoundPlayer is enabled.
     */
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled && soundPool != null;
    }

    /**
     * Get a value indicating if the SoundPlayer is enabled.
     */
    public boolean getIsEnabled() {
        return isEnabled;
    }

    /**
     * Enable or disable playback of sounds, if it is not enabled it will be
     * and vice-versa.
     */
    public void toggleIsEnabled() {
        isEnabled = !isEnabled;
    }

    /**
     * Load the sound resources. If the resources are not loaded they will
     * not be played.
     */
    public void loadResources(Context context) {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        media.add(soundPool.load(context, R.raw.fx0, 1));
        media.add(soundPool.load(context, R.raw.fx1, 1));
        media.add(soundPool.load(context, R.raw.fx2, 1));
    }
}
