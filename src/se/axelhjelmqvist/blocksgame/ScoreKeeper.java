package se.axelhjelmqvist.blocksgame;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created with IntelliJ IDEA.
 * User: Axel
 * Date: 2013-10-09
 * Time: 13:33
 * To change this template use File | Settings | File Templates.
 */
public class ScoreKeeper {
    final static int[] colors = new int[] {
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

    private void reset() {
        currentScore = scoreCount = 0;
        levelMultiplier = 1;
        updateText();
    }

    public char[] getText(){
        return text;
    }

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

    public void endRound() {
        lastRoundScore = currentScore;
        if (currentScore > currentTopScore) currentTopScore = currentScore;
        reset();
    }

    public void toggleScore() { viewTop = !viewTop; updateText(); }

    public void score(int lines) {
        if (lines == 0) return;
        int baseScore = 100+levelMultiplier;
        currentScore += baseScore*levelMultiplier*(lines*(lines/2+1));
        ++scoreCount;
        if (scoreCount/levelMultiplier > 5) ++levelMultiplier;
        updateText();
    }

    public void draw(Canvas canvas) {
        paint.setARGB(255, 63, 63, 63);
        canvas.drawText(text, 0, 4, posX, posY, paint);
        int offset = (scoreCount % (colors.length / 3)) * 3;
        paint.setARGB(255, colors[offset+0], colors[offset+1], colors[offset+2]);
        canvas.drawText(text, 5, 14-5, posX+50, posY, paint);

        paint.setARGB(255, viewTop?255:200, 200, 200);
        canvas.drawText(text, 15, 30, posX+180, posY, paint);
    }
}
