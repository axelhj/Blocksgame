package se.axelhjelmqvist.blocksgame;

/**
 * Class that has some method to manipulate blocks-shapes,
 * for example rotation or creating a new bit-pattern to represent
 * a certain type of Shape. This is not embedded in the Shape object
 * since that class is supposed to help with managing the current state
 * of the object. Therefor this object is used with static methods instead
 * for the pure "geometrical" operations that can be applied to the shape.
 */
public class ShapeTool {

    /**
     * The various possible layouts or kinds or types of shapes
     * are encoded into this integer array. The mirrored or
     * rotated shapes are available by manipulating a certain
     * shape using the various methods of the ShapeTool object, for instance.
     */
    public final static int[] LAYOUTS = new int[] {
            // straight
            0, 0, 0,//origin, rotation method
            -1, 0, 1, //x, y, color
            0, 0, 1,
            1, 0, 1,
            2, 0, 1,
            // box
            0, 0, 0,
            0, 0, 1,
            1, 0, 1,
            0, 1, 1,
            1, 1, 1,
            // diagonal
            0, 0, 0,
            -1, 0, 1,
            0, 0, 1,
            0, 1, 1,
            1, 1, 1,
            // top
            0, 0, 0,
            0, 1, 1,
            -1, 0, 1,
            0, 0, 1,
            1, 0, 1,
            // angle
            0, 0, 0,
            -1, 1, 1,
            -1, 0, 1,
            0, 0, 1,
            1, 0, 1
    };

    /**
     * Instance of an array that is used to save results of operations on
     * the shapes without allocating new array objects.
     */
    private static float[] result = new float[2];

    /**
     * Rotate a point about an origin. The result is not returned but
     * is saved to the result argument.
     */
    public static void rotatePoint(double theta, double x, double y, double originX, double originY, float[] result) {
        result[0] = (float)((Math.cos(theta) * (x - originX) - Math.sin(theta) * (y - originY)) + originX);
        result[1] = (float)((Math.sin(theta) * (x - originX) + Math.cos(theta) * (y - originY)) + originY);
    }

    /**
     * Make a shape into a certain type. Type and color can be specified.
     */
    public static void setShape(Shape shape, int type, int color) {
        int[] bitmap = shape.bitmap;
        if (type >= (ShapeTool.LAYOUTS.length / (3 * 5))) {
            type = 0;
        }
        int offset = type * (3 * 5);
        for (int i = 0; i < 3 * 5; ++i) {
            bitmap[i] = ShapeTool.LAYOUTS[offset + i];
        }
        for (int i = 3; i < 3 * 5; i+=3) {
            bitmap[i + 2] += color;
        }
    }

    /**
     * Flip or mirror a shape object about a horizontal or vertical axis.
     */
    public static void flipShape(Shape shape, int axis) {
        int[] bitmap = shape.bitmap;
        int originX = bitmap[0];
        for (int i = 3; i < 3 * 5; i += 3) {
            bitmap[i + 0] = ((bitmap[i + 0] - originX) * -1) + 1;
        }
        bitmap[0] += 1;
    }

    /**
     * Rotate a shape ninety degrees about it's origin, and repeat the procedure
     * for steps number of times.
     */
    public static void rotateShape(Shape shape, int steps) {
        int[] bitmap = shape.bitmap;
        double degrees = -90 * steps;
        double radians = degrees * (Math.PI / 180.0);
        for (int i = 3; i < 3 * 5; i += 3) {
            rotatePoint(radians, bitmap[i + 0], bitmap[i + 1], bitmap[0], bitmap[1], result);
            bitmap[i + 0] = (int)result[0];
            bitmap[i + 1] = (int)result[1];
        }
    }
}
