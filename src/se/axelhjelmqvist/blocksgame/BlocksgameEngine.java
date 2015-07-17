package se.axelhjelmqvist.blocksgame;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Created with IntelliJ IDEA.
 * User: Axel
 * Date: 2013-10-04
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public class BlocksgameEngine {
    Shape shape, nextShape;

    Bitmap[] sprites;

    int sizeX, sizeY;

    int posX, posY;

    int nextPosX, nextPosY;

    int brickWidth, brickHeight;

    int[] field;

    int[] workingField;

    boolean fastLeft, fastRight, fastDrop;

    float repeatLeft, repeatRight;

    float timeAccum;

    ScoreKeeper score;

    public BlocksgameEngine(int sizeX, int sizeY, Bitmap[] sprites) {
        this.sprites = sprites;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        brickWidth = (int)(0.073f * Blocksgame.GAME_WIDTH);
        brickHeight = (int)(0.044f * Blocksgame.GAME_HEIGHT);
        posX = (int)(0.073f * Blocksgame.GAME_WIDTH);
        posY = (int)(0.0625f * Blocksgame.GAME_HEIGHT);
        nextPosX = (int)(0.77f * Blocksgame.GAME_WIDTH);
        nextPosY = (int)(0.12f * Blocksgame.GAME_HEIGHT);
        field = new int[sizeX * sizeY];
        workingField = new int[4 * 2];
        shape = new Shape(0, 0, 0);
        nextShape = new Shape(0, 0, 0);
        nextTile();
        timeAccum = 0.0f;
        repeatLeft = repeatRight = 0.0f;
        fastLeft = fastRight = fastDrop = false;
        score = new ScoreKeeper((int)(0.0833f * Blocksgame.GAME_WIDTH), (int)(0.0375f * Blocksgame.GAME_HEIGHT));
    }

    public void fastDrop(boolean status) {
        fastDrop = status;
    }

    public void moveLeft(boolean status) {
        fastLeft = status;
        if (fastLeft) {
            repeatLeft = 0.4f;
            translateShape(-1);
        }
    }

    public void moveRight(boolean status) {
        fastRight = status;
        if (fastRight) {
            repeatRight = 0.4f;
            translateShape(1);
        }
    }

    public void rotateTile() {
        int[] tileCoords = assembleCoords(shape.getFuture(0, 0, -1, 3));
        int oobDirection = detectOutOfBounds(tileCoords);
        if ((oobDirection != 1 && oobDirection != 2 && oobDirection != 4)
                && detectCollission(tileCoords) == 0) {
            shape.keepFuture();
            SoundPlayer.getInstance().playSound(0);
        }
    }

    public void update(float dTime) {
        timeAccum += dTime;
        float baseSpeed = 0.5f;
        float dropInterval = baseSpeed - ((float)(score.levelMultiplier * 2) / 60.0f);
        if (fastDrop) {
            dropInterval /= 4.0f;
        }
        if (timeAccum >= dropInterval) {
            timeAccum = 0.0f;
            moveDown();
        }
        if (fastLeft) {
            if (repeatLeft < 0) {
                translateShape(-1);
                repeatLeft = 0.1f;
            }
            repeatLeft -= dTime;
        } else if (fastRight) {
            if (repeatRight < 0) {
                translateShape(1);
                repeatRight = 0.1f;
            }
            repeatRight -= dTime;
        }
    }

    private void resetField() {
        for (int i  = 0; i < field.length; ++i) {
            field[i] = 0;
        }
    }

    private void eliminateRows(int[] rows, int nrRows) {
        for (int i = 0; i < nrRows; ++i) {
            if (rows[i] == sizeY) {
                continue;
            }
            for (int j = 1; j <= rows[i]*sizeX; ++j) {
                field[rows[i] * sizeX - j + sizeX] = field[rows[i]* sizeX - j];
            }
            for (int k = 0 ; k < sizeX; ++k) {
                field[k] = 0;
            }
        }
    }

    private int[] assembleCoords(Shape shape) {
        for (int i = 0; i < workingField.length; ++i) {
            workingField[i] = 0;
        }
        int[] coords = workingField;
        int currentIndex = 0;
        for (int i = 3; i < 15; i += 3) {
            if (shape.bitmap[i + 2] != 0) {
                coords[currentIndex + 0] = shape.x + shape.bitmap[i + 0];
                coords[currentIndex + 1] = shape.y + shape.bitmap[i + 1];
                currentIndex += 2;
            }
        }
        if (currentIndex < 2 * (4 - 1)) {
            coords[currentIndex + 0] = Integer.MIN_VALUE;
        }
        return coords;
    }

    private int detectCollission(int[] coords) {
        for (int i = 0; i < 4 * 2; i += 2) {
            if (coords[i + 0] == Integer.MIN_VALUE) {
                return 0;
            }
            if (coords[i + 0] < 0 || coords[i + 0] >= sizeX ||
                    coords[i + 1] < 0 || coords[i + 1] >= sizeY) {
                continue;
            }
            // Detected a collission
            if (field[coords[i + 0] + coords[i + 1] * sizeX] != 0) {
                return 1;
            }
        }
        return 0;
    }

    private int detectOutOfBounds(int[] coords) {
        for (int i = 0; i < 4 * 2; i += 2) {
            // Reached the end of list-token
            if (coords[i + 0] == Integer.MIN_VALUE) {
                return 0;
            }
            // Out of bounds to the left
            if (coords[i + 0] < 0) {
                return 1;
            }
            // Out of bounds to the right
            if (coords[i + 0] >= sizeX) {
                return 2;
            }
            // Out of bounds at the top
            if (coords[i + 1] < 0) {
                return 3;
            }
            // Out of bounds at the bottom
            if (coords[i + 1] >= sizeY) {
                return 4;
            }
        }
        return 0;
    }

    private int detectOutOfBoundsVertical(int[] coords) {
        for (int i = 0; i < 4 * 2; i += 2) {
            // Reached the end of list-token
            if (coords[i+0] == Integer.MIN_VALUE) {
                return 0;
            }
            // Out of bounds at the top
            if (coords[i + 1] < 0) {
                return 3;
            }
            // Out of bounds at the bottom
            if (coords[i + 1] >= sizeY) {
                return 4;
            }
        }
        return 0;
    }
    private int detectOutOfBoundsHorizontal(int[] coords) {
        for (int i = 0; i < 4 * 2; i += 2) {
            // Reached the end of list-token
            if (coords[i + 0] == Integer.MIN_VALUE) {
                return 0;
            }
            // Out of bounds to the left
            if (coords[i + 0] < 0) {
                return 1;
            }
            // Out of bounds to the right
            if (coords[i + 0] >= sizeX) {
                return 2;
            }
        }
        return 0;
    }

    private void insertShape(Shape shape) {
        int tileX = 0, tileY = 0, fieldIndex = 0;
        for (int i = 3; i < 3 * 5; i += 3) {
            tileX = shape.x + shape.bitmap[i + 0];
            tileY = shape.y + shape.bitmap[i + 1];
            fieldIndex = tileX + tileY * sizeX;
            if (shape.bitmap[i + 2] != 0 &&
                    (fieldIndex >= 0 && fieldIndex < sizeX * sizeY)) {
                field[fieldIndex] = shape.bitmap[i + 2];
            }
        }
    }

    private int checkScore(int[] result) {
        int resultCounter = 0;
        if (shape.y < 0) {
            return 0;
        }
        int low = shape.y, high = shape.y, intermediate = 0;
        for (int i = 0; i < 4; ++i) {
            intermediate = shape.bitmap[3 * (i + 1) + 1] + shape.y;
            if (intermediate < low) {
                low = intermediate - 1;
            }
            if (intermediate > high) {
                high = intermediate;
            }
        }
        for (int i = low; i <= high && i >= 0 && i < sizeY; ++i) {
            boolean rowComplete = true;
            for (int j = 0; j < sizeX; ++j) {
                if (field[i * sizeX + j] == 0) {
                    rowComplete = false;
                }
            }
            if (rowComplete) {
                result[resultCounter] = i;
                ++resultCounter;
            }
        }
        return resultCounter;
    }

    private void moveDown() {
        int[] collissionCandidates = assembleCoords(shape.getFuture(0, 1));
        if (detectOutOfBoundsVertical(collissionCandidates) == 4 ||
            detectCollission(collissionCandidates) != 0)
        {
            insertShape(shape);
            for (int i = 0; i < 4; ++i) workingField[i] = 0;
            int[] completeRows = workingField;
            int count = checkScore(completeRows);
            eliminateRows(completeRows, count);
            // Count is score
            score.score(count);
            SoundPlayer.getInstance().playSound((count > 0) ? 1 : 2);
            if (shape.y < 0) {
                // It is possible to save the highscore at this point
                score.endRound();
                resetField();
            }
            nextTile();
        }
        else {
            shape.keepFuture();
        }
    }

    private void translateShape(int steps) {
        Shape future = shape.getFuture(steps, 0);
        int[] tileCoords = assembleCoords(future);;
        if (detectOutOfBoundsHorizontal(tileCoords) == 0 &&
                detectCollission(tileCoords) == 0) {
            shape.keepFuture();
            SoundPlayer.getInstance().playSound(0);
        }
    }

    private void nextTile() {
        // Swap the shapes
        int[] bitmap = shape.bitmap;
        shape.bitmap = nextShape.bitmap;
        nextShape.bitmap = bitmap;

        // Update the shape that is currently in play
        ShapeTool.rotateShape(shape, Blocksgame.getRand(0, 3));

        // Reset to offscreen position
        int lowestBlock = Integer.MIN_VALUE;
        for (int i = 3; i < 3 * 5; i += 3) {
            if (nextShape.bitmap[i + 1] > lowestBlock) {
                lowestBlock = i;
            }
        }
        shape.y = -1 * lowestBlock + 1;
        shape.x = (sizeX / 2) - 1;

        // Update next shape
        int color = Blocksgame.getRand(0, ScoreKeeper.colors.length / 3);
        ShapeTool.setShape(nextShape, Blocksgame.getRand(0, ShapeTool.layouts.length / (3 * 5)), color);
        ShapeTool.rotateShape(nextShape, 1); // To fit the layout
        int flip = Blocksgame.getRand(0, 4);
        if (flip < 2) ShapeTool.flipShape(nextShape, flip);
    }

    public void draw(Canvas canvas) {
        int x = 0, y= 0, i = 0, color = 0;
        // Draw playing-tile
        for (i = 3; i < 3*5; i+=3) {
            x = brickWidth*(shape.x+shape.bitmap[i + 0])+posX;
            y = brickHeight*(shape.y+shape.bitmap[i + 1])+posY;
            color = shape.bitmap[i + 2];
            if (color != 0 && shape.y+shape.bitmap[i + 1] >= 0) {
                canvas.drawBitmap(sprites[color], null, new Rect(x, y, x + brickWidth, y + brickHeight), null);
            }
        }

        // Draw next-tile
        int offsetX = 1, offsetY = 2;
        for (i = 3; i < 3 * 5; i+=3) {
            x = brickWidth*(nextShape.bitmap[i + 0] + offsetX) + nextPosX;
            y = brickHeight*(nextShape.bitmap[i + 1] + offsetY) + nextPosY;
            color = nextShape.bitmap[i + 2];
            if (color != 0){
                canvas.drawBitmap(sprites[color], null, new Rect(x, y, x + brickWidth, y + brickHeight), null);
            }
        }
        // Draw playfield
        for (i = 0; i < sizeX*sizeY; ++i) {
            color = field[i];
            if (color != 0) {
                x = brickWidth * (i % sizeX) + posX;
                y = brickHeight * (i / sizeX) + posY;
                canvas.drawBitmap(sprites[color], null, new Rect(x, y, x + brickWidth, y + brickHeight), null);
            }
        }
        score.draw(canvas);
    }

}
