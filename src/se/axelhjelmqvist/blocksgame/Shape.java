package se.axelhjelmqvist.blocksgame;

/**
 * Created with IntelliJ IDEA.
 * User: Axel
 * Date: 2013-10-06
 * Time: 00:04
 * To change this template use File | Settings | File Templates.
 */

public class Shape {
    public int[] bitmap;

    public int x, y;

    private Shape future;

    public Shape(int type, int orientation, int flippation) {
        x = 0;
        y = 0;
        bitmap = new int[3 * 5];
        ShapeTool.setShape(this, type, 0);
        ShapeTool.flipShape(this, flippation);
        ShapeTool.rotateShape(this, orientation);
        future = new Shape();
    }
    private Shape() {
        this.bitmap = new int[3 * 5];
        this.x = 0;
        this.y = 0;
    }
    public Shape getFuture(int x, int y) {
        future.x = this.x + x;
        future.y = this.y + y;
        for (int i = 0; i < 3 * 5; ++i) {
            future.bitmap[i] = bitmap[i];
        }
        return future;
    }
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

    public void keepFuture() {
        this.x = future.x;
        this.y = future.y;
        for (int i = 0; i < 3 * 5; ++i) {
            bitmap[i] = future.bitmap[i];
        }
    }
}
