package se.axelhjelmqvist.blocksgame;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * Class that handles the logic of gameplay of the blocksgame. Holds
 * a representation of the "field" or board of blocks that are currently
 * in play aswell as the current shape that the player can control.
 * Timing and positioning (representation) of blocks falls within this
 * responsibility.
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

    /**
     * Constructor of the BlocksgameEngine. Setups and initalizes what is
     * required to handle the logic of the game.
     */
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

    /**
     * Enables/disables fastdrop of tiles. This usually happens
     * if the drop-button is continuously pressed.
     */
    public void fastDrop(boolean status) {
        fastDrop = status;
    }

    /**
     * Enables/disables the move-left action. This can be continuously
     * activated to enable fast movement of the current block in play.
     */
    public void moveLeft(boolean status) {
        fastLeft = status;
        if (fastLeft) {
            repeatLeft = 0.4f;
            translateShape(-1);
        }
    }

    /**
     * Enables/disables the move-right action. This can be continuously
     * activated to enable fast movement of the current block in play.
     */
    public void moveRight(boolean status) {
        fastRight = status;
        if (fastRight) {
            repeatRight = 0.4f;
            translateShape(1);
        }
    }

    /**
     * Rotates the current tile. This is not automatically repeated
     * if the input is continuously activated.
     */
    public void rotateTile() {
        int[] tileCoords = assembleCoords(shape.getFuture(0, 0, -1, 3));
        int oobDirection = detectOutOfBounds(tileCoords);
        if ((oobDirection != 1 && oobDirection != 2 && oobDirection != 4)
                && detectCollission(tileCoords) == 0) {
            shape.keepFuture();
            SoundPlayer.getInstance().playSound(0);
        }
    }

    /**
     * Updates the gamestate based on elapsed time. This handles the timing
     * of auto-repeat actions in the game (that are activated if an input is
     * continuously activated).
     * @param dTime
     */
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

    /**
     * Helper method to clear the gameplay field to its initial state.
     */
    private void resetField() {
        for (int i  = 0; i < field.length; ++i) {
            field[i] = 0;
        }
    }

    /**
     * Method that eliminates a number of rows that are given as the first argument.
     * The rows are given as indices of the rows. The rows must be given in ascending
     * order or the indices will change as rows are removed. The remainder of
     * rows are moved downwards from the top to fill the removed row.
     */
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

    /**
     * This translates the indices/coordinates of a shape/set of blocks
     * into the coordinates of the gameplay board. These are returned
     * as an integer array that is reused for each call (to preclude
     * GC-induced stutter).
     */
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

    /**
     * This method detects a collission with the gameplay board
     * and a shape, given its coordinates as an integer array.
     */
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

    /**
     * This method detects an out of bounds condition (that a gameplay piece
     * is outside of the visible gameplay board) of a shape, given its coordiantes
     * translated in relation the gameboard origin as an integer array.
     */
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

    /**
     * This method detects an out of bounds condition in relation to the horizontal
     * sides of the gameplay board of a shape, given its coordiantes
     * translated in relation the gameboard origin as an integer array.
     */
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

    /**
     * This method detects an out of bounds condition in relation to the vertical
     * sides of the gameplay board of a shape, given its coordiantes
     * translated in relation the gameboard origin as an integer array.
     */
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

    /**
     * Method that blits the given shape into the gameplay board.
     */
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

    /**
     * Inserts into the given integer array, a number of indices indicating
     * which rows contains no gaps, ie. are completed or full. These rows
     * can then be removed given that the number returned is greater than zero.
     */
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

    /**
     * Performs the required logical operations that are needed to move the
     * shape that is currently in play downwards for one step. This includes
     * performing operations such as removing completed lines and
     * invoking a method to calculate scoring and give points.
     */
    private void moveDown() {
        int[] collissionCandidates = assembleCoords(shape.getFuture(0, 1));
        if (detectOutOfBoundsVertical(collissionCandidates) == 4 ||
                detectCollission(collissionCandidates) != 0) {
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

    /**
     * Method that is used for when a shape needs to be moved horizontally
     * in relation to the gampelay board.
     */
    private void translateShape(int steps) {
        Shape future = shape.getFuture(steps, 0);
        int[] tileCoords = assembleCoords(future);;
        if (detectOutOfBoundsHorizontal(tileCoords) == 0 &&
                detectCollission(tileCoords) == 0) {
            shape.keepFuture();
            SoundPlayer.getInstance().playSound(0);
        }
    }

    /**
     * Swap the offscreen preview so that it becomes the tile that is
     * currently in play, and calculates a new random tile to be visible
     * in the preview field.
     */
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
        int color = Blocksgame.getRand(0, ScoreKeeper.COLORS.length / 3);
        ShapeTool.setShape(nextShape, Blocksgame.getRand(0, ShapeTool.LAYOUTS.length / (3 * 5)), color);
        ShapeTool.rotateShape(nextShape, 1); // To fit the layout
        int flip = Blocksgame.getRand(0, 4);
        if (flip < 2) ShapeTool.flipShape(nextShape, flip);
    }

    /**
     * Draws the tile that is currently in play, the tile that is currently visible in the
     * preview box, aswell as the tiles that are currently in play using a Canvas object.
     */
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
