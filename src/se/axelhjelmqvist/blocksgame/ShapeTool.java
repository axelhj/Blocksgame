package se.axelhjelmqvist.blocksgame;

/**
 * Created with IntelliJ IDEA.
 * User: Axel
 * Date: 2013-10-05
 * Time: 14:48
 * To change this template use File | Settings | File Templates.
 */
public class ShapeTool {

    final static int[] layouts = new int[] {
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

    private static float[] result = new float[2];

    public static void rotatePoint(double theta, double x, double y, double originX, double originY, float[] result) {
        result[0] = (float)((Math.cos(theta) * (x - originX) - Math.sin(theta) * (y - originY)) + originX);
        result[1] = (float)((Math.sin(theta) * (x - originX) + Math.cos(theta) * (y - originY)) + originY);
    }

    public static void setShape(Shape shape, int type, int color) {
        int[] bitmap = shape.bitmap;
        if (type >= (ShapeTool.layouts.length / (3 * 5))) {
            type = 0;
        }
        int offset = type * (3 * 5);
        for (int i = 0; i < 3 * 5; ++i) {
            bitmap[i] = ShapeTool.layouts[offset + i];
        }
        for (int i = 3; i < 3 * 5; i+=3) {
            bitmap[i + 2] += color;
        }
        return;
    }

    public static void flipShape(Shape shape, int axis) {
        int[] bitmap = shape.bitmap;
        int originX = bitmap[0];
        for (int i = 3; i < 3 * 5; i += 3) {
            bitmap[i + 0] = ((bitmap[i + 0] - originX) * -1) + 1;
        }
        bitmap[0] += 1;
    }

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
