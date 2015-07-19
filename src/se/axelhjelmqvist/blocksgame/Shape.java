package se.axelhjelmqvist.blocksgame;

/**
 * Class that represents a tetromino shape in the game. Holds
 * a recursive reference to a possible "future" represenation
 * of the Shape, that is used to check if a move of the blocks
 * that represents the shape is possible.
 */

public class Shape {
    public int[] bitmap;

    public int x, y;

    private Shape future;

    /**
     * Initializes a new instance of the Shape object, given
     * certain values.
     */
    public Shape(int type, int orientation, int flippation) {
        x = 0;
        y = 0;
        bitmap = new int[3 * 5];
        ShapeTool.setShape(this, type, 0);
        ShapeTool.flipShape(this, flippation);
        ShapeTool.rotateShape(this, orientation);
        future = new Shape();
    }

    /**
     * Initializes a new Shape instance using default values. 
     */
    private Shape() {
        this.bitmap = new int[3 * 5];
        this.x = 0;
        this.y = 0;
    }

    /**
     * Returns a possible future instance of the Shape object.
     * It can later be retained or discarded.
     */
    public Shape getFuture(int x, int y) {
        future.x = this.x + x;
        future.y = this.y + y;
        for (int i = 0; i < 3 * 5; ++i) {
            future.bitmap[i] = bitmap[i];
        }
        return future;
    }

    /**
     * Returns a possible future instance of the Shape object
     * using some interesting new properties such as an updated
     * position, flip (mirroring) or rotation, however it will still
     * be the same "type" of shape.
     * It can later be retained or discarded.
     */
    public Shape getFuture(int x, int y, int flip, int rotation) {
        future.x = this.x + x;
        future.y = this.y + y;
        for (int i = 0; i < 3 * 5; ++i) {
            future.bitmap[i] = bitmap[i];
        }
        if (flip >= 0) {
            ShapeTool.flipShape(future, flip);
        }
        if (rotation != 0) {
            ShapeTool.rotateShape(future, rotation);
        }
        return future;
    }

    /**
     * Invoked to retain the current "future" Shape object.
     */
    public void keepFuture() {
        this.x = future.x;
        this.y = future.y;
        for (int i = 0; i < 3 * 5; ++i) {
            bitmap[i] = future.bitmap[i];
        }
    }
}
