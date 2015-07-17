package se.axelhjelmqvist.blocksgame;

/**
 * Created with IntelliJ IDEA.
 * User: Axel
 * Date: 2013-10-05
 * Time: 10:13
 * To change this template use File | Settings | File Templates.
 */

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;
import android.view.MotionEvent;

import java.util.Random;

public class Blocksgame
        implements View.OnTouchListener {
    private static Random rand = new Random();

    public static float GAME_WIDTH, GAME_HEIGHT;

    private Bitmap bg, tile;

    private BlocksgameEngine blocksgameEngine;

    private int buttonLeft, buttonRight, buttonRotate, buttonDown;

    private int buttonPause, buttonSound, buttonToggleScore;

    private Input input;

    private Timer timer;

    public static int getRand(int min, int max) {
        return min + rand.nextInt(max - min);
    }

    public Blocksgame(Context context, int gameWidth, int gameHeight) {
        GAME_WIDTH = gameWidth;
        GAME_HEIGHT = gameHeight;
        input = new Input();
        timer = new Timer();
        buttonRotate = input.addButton(0, 0.0625f * GAME_HEIGHT, GAME_WIDTH, 0.3125f * GAME_HEIGHT);
        buttonLeft = input.addButton(0, 0.375f * GAME_HEIGHT, 0.5f * GAME_WIDTH, 0.375f * GAME_HEIGHT);
        buttonRight = input.addButton(0.5f * GAME_WIDTH, 0.375f * GAME_HEIGHT, 0.5f * GAME_WIDTH, 0.375f * GAME_HEIGHT);
        buttonDown = input.addButton(0, 0.75f * GAME_HEIGHT, GAME_WIDTH, 0.375f * GAME_HEIGHT);
        buttonPause = input.addButton(0.896f * GAME_WIDTH, 0, 0.1f * GAME_WIDTH, 0.0625f * GAME_HEIGHT);
        buttonSound = input.addButton(0.79f * GAME_WIDTH, 0, 0.1f * GAME_WIDTH, 0.0625f * GAME_HEIGHT);
        buttonToggleScore = input.addButton(0.354f * GAME_WIDTH, 0, 0.3125f * GAME_WIDTH, 0.0625f * GAME_HEIGHT);
    }

    public Bitmap getBitmap(int id, Context context) {
        return ((BitmapDrawable)context.getResources().getDrawable(id)).getBitmap();
    }

    public void loadResources(Context context) {
        SoundPlayer.getInstance().loadResources(context);
        bg = getBitmap(R.drawable.bg_t, context);
        tile = getBitmap(R.drawable.br_t, context);
        int[] colors = ScoreKeeper.colors;
        Bitmap[] sprites = new Bitmap[colors.length / 3 + 1];
        sprites[0] = null;
        int offset = 0;
        for (int i = 1; i < (colors.length / 3) + 1; ++i) {
            sprites[i] = tile.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(sprites[i]);
            offset = (i - 1) * 3;
            canvas.drawARGB(63, colors[offset + 0], colors[offset + 1], colors[offset + 2]);
        }
        blocksgameEngine = new BlocksgameEngine(10, 20, sprites);
    }

    public void update() {
        float deltaTime = timer.getDelta();
        Input.EventData event = input.getEvent();
        while (event != null && !timer.pausing) {
            if (event.state == 1) {
                if (event.button == buttonLeft) {
                    blocksgameEngine.moveLeft(true);
                } else if (event.button == buttonRight) {
                    blocksgameEngine.moveRight(true);
                } else if (event.button == buttonDown) {
                    blocksgameEngine.fastDrop(true);
                } else if (event.button == buttonRotate) {
                    blocksgameEngine.rotateTile();
                } else if (event.button == buttonPause) {
                    timer.pause();
                } else if (event.button == buttonToggleScore) {
                    blocksgameEngine.score.toggleScore();
                } else if (event.button == buttonSound) {
                    SoundPlayer.getInstance().toggleIsEnabled();
                }
            } else if (event.state == 0) {
                if (event.button == buttonLeft) {
                    blocksgameEngine.moveLeft(false);
                } else if (event.button == buttonRight) {
                    blocksgameEngine.moveRight(false);
                } else if (event.button == buttonDown) {
                    blocksgameEngine.fastDrop(false);
                }
            }
            event = input.getEvent();
        }
        while (event != null && timer.pausing && event.state == 1) {
            if (event.button == buttonPause) {
                timer.resume();
            } else if (event.button == buttonSound) {
                SoundPlayer.getInstance().toggleIsEnabled();
            }
            event = input.getEvent();
        }
        blocksgameEngine.update(deltaTime);
        input.updateState();
        timer.update();
        try {
            Thread.sleep(33);
        } catch (InterruptedException ie) {
            /* Ignored */
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(bg, null, new Rect(0, 0, (int)GAME_WIDTH, (int)GAME_HEIGHT), null);
        if (timer.pausing) {
            canvas.drawARGB(40, 255, 63, 63);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
                /* Ignored */
            }
        }
        blocksgameEngine.draw(canvas);
    }

    public boolean onTouch(View view, MotionEvent evt) {
        if (input.handleEvent(evt)) {
            view.performClick();
        }
        return true;
    }
}
