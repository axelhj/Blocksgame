package se.axelhjelmqvist.blocksgame;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * ScoreKeeper tracks and presents the current score.
 */
public class ScoreKeeper {
    /**
     * Color values that are used when presenting the score.
     */
    public final static int[] COLORS = new int[] {
            255, 0, 255,
            255, 255, 0,
            0, 255, 255,
            255, 0, 0,
            0, 0, 255,
            0, 255, 0,
            0, 255, 127
    };

    float posX, posY;

    char[] messageString;

    char[] text;

    public int currentScore, currentTopScore, lastRoundScore;

    public int levelMultiplier;

    public int scoreCount;

    private boolean viewTop;

    public Paint paint;

    /**
     * Constructor of the scorekeeper object. Requires
     * position values for where on screen the score should
     * be drawn.
     */
    public ScoreKeeper(int x, int y) {
        posX = x;
        posY = y;
        text = new char[100];
        messageString = ":    :         :".toCharArray();
        currentTopScore = lastRoundScore = 0;
        viewTop = false;
        paint = new Paint();
        paint.setTextSize(30);
        reset();
    }

    /**
     * Resets the current score but not the highscore.
     */
    private void reset() {
        currentScore = scoreCount = 0;
        levelMultiplier = 1;
        updateText();
    }

    /**
     * Returns the textstring that presents the current score.
     */
    public char[] getText(){
        return text;
    }

    /**
     * Update the current textstring based on the current score.
     */
    private void updateText() {
        for (int i = 0; i < text.length; ++i) {
            if (i < messageString.length) {
                text[i] = messageString[i];
            } else {
                text[i] = ' ';
            }
        }
        String string = String.valueOf(levelMultiplier);
        for (int i = 0; i < string.length(); ++i) {
            text[i + 1] = string.charAt(i);
        }
        string = String.valueOf(currentScore);
        for (int i = 0; i < string.length(); ++i) {
            text[i + 6] = string.charAt(i);
        }
        string = String.valueOf(viewTop ? currentTopScore : lastRoundScore);
        for (int i = 0; i < string.length(); ++i) {
            text[i + 16] = string.charAt(i);
        }
    }

    /**
     * Invoked when the current game round is over (eg. the player lost).
     */
    public void endRound() {
        lastRoundScore = currentScore;
        if (currentScore > currentTopScore) {
            currentTopScore = currentScore;
        }
        reset();
    }

    /**
     * Enables/disables the visibility of the score on screen
     */
    public void toggleScore() {
        viewTop = !viewTop;
        updateText();
    }

    /**
     * This method needs to be called each time the player scores.
     */
    public void score(int lines) {
        if (lines == 0) return;
        int baseScore = 100+levelMultiplier;
        currentScore += baseScore*levelMultiplier*(lines*(lines/2+1));
        ++scoreCount;
        if (scoreCount/levelMultiplier > 5) ++levelMultiplier;
        updateText();
    }

    /**
     * Draw the current score using the Canvas object.
     */
    public void draw(Canvas canvas) {
        paint.setARGB(255, 63, 63, 63);
        canvas.drawText(text, 0, 4, posX, posY, paint);
        int offset = (scoreCount % (COLORS.length / 3)) * 3;
        paint.setARGB(255, COLORS[offset + 0], COLORS[offset + 1], COLORS[offset + 2]);
        canvas.drawText(text, 5, 14 - 5, posX + 50, posY, paint);
        paint.setARGB(255, viewTop ? 255 : 200, 200, 200);
        canvas.drawText(text, 15, 30, posX + 180, posY, paint);
    }
}
