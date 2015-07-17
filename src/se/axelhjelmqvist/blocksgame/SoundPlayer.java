package se.axelhjelmqvist.blocksgame;

import java.util.ArrayList;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created with IntelliJ IDEA.
 * User: Axel
 * Date: 2013-10-12
 * Time: 22:55
 * To change this template use File | Settings | File Templates.
 */
public class SoundPlayer {
    private static SoundPlayer instance = null;
    private ArrayList<Integer> media;
    private SoundPool soundPool;
    private boolean isEnabled;

    private SoundPlayer() {
        media = new ArrayList<Integer>();
        soundPool = null;
        isEnabled = false;
    }

    public static SoundPlayer getInstance() {
        if (instance == null) {
            instance = new SoundPlayer();
        }
        return instance;
    }

    public void playSound(int id) {
        if (isEnabled && id < media.size()) {
            soundPool.play(media.get(id), 0.5f, 0.5f, 0, 0, 1.0f);
        }
    }
    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled && soundPool != null;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }
    
    public void toggleIsEnabled() {
        isEnabled = !isEnabled;
    }

    public void loadResources(Context context) {
        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        media.add(soundPool.load(context, R.raw.fx0, 1));
        media.add(soundPool.load(context, R.raw.fx1, 1));
        media.add(soundPool.load(context, R.raw.fx2, 1));
    }
}
