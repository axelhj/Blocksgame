package se.axelhjelmqvist.blocksgame;

import android.graphics.Rect;

/**
 * A vector class that supports two-dimensional vector operations.
 */
public class Vector2 {
    public float x, y;

    /**
     * Constructor. Creates a zero length vector.
     */
    public Vector2() {
        this(0, 0);
    }

    /**
     * Constructor. Creates a vector that is identical to another vector.
     * Copies the vector that is supplied as its argument.
     */
    public Vector2(Vector2 copy) {
        this(copy.x, copy.y);
    }

    /**
     * Constructor. Creates a vector using the supplied argument values.
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Move the vector. Offsets its value according to the arguments.
     */
    public void move(float x, float y) {
        this.x += x;
        this.y += y;
    }

    /**
     * Move the vector. Offsets its value according to the argument.
     */
    public void move(Vector2 distance) {
        this.x += distance.x; this.y += distance.y;
    }

    /**
     * Offset the vector. Same as move.
     */
    public void offset(float x, float y) {
        this.x += x;
        this.y += y;
    }

    /**
     * Offset the vector. Same as move.
     * @param distance
     */
    public void offset(Vector2 distance) {
        this.x += distance.x;
        this.y += distance.y;
    }

    /**
     * Offset the vector to a specific position. Reinitializes the vector.
     */
    public void offsetTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Offset the vector to a specific position. Reinitializes the vector.
     */
    public void offsetTo(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Set the vector length while retaining the vector direction.
     */
    public void offsetLengthTo(float newLength) {
        this.normalize();
        this.scaleBy(newLength);
    }

    /**
     * Multiply the individual vector components by the argument value.
     */
    public void scaleBy(float factor) {
        x = x * factor;
        y = y * factor;
    }

    /**
     * Get the length of the vector.
     */
    public float getLength() {
        double xsq = Math.abs((double)(x * x));
        double ysq = Math.abs((double)(y * y));
        float result = (float)Math.sqrt(xsq + ysq);
        return Float.isNaN(result) ? 0.0f : result;
    }

    /**
     * Get the vector direction as a radian degree.
     */
    public float direction() {
        return (float)Math.atan2((double)x, (double)y);
    }

    /**
     * Normalize the vector. For a nonzero-length vector, its
     * length will be one while the direction is retained.
     */
    public void normalize() {
        float length = getLength();
        if (length != 0.0f) {
            this.x = x / length;
            this.y = y / length;
        }
        else {
            this.x = 0.0f;
            this.y = 0.0f;
        }
    }

    /**
     * Returns a normalized copy of the vector. For a nonzero-length vector, its
     * length will be one while the direction is retained.
     */
    public Vector2 normalized() {
        Vector2 result = new Vector2(x, y);
        result.normalize();
        return result;
    }

    /**
     * Get a rectangle of zero width and height, which has a
     * top left corner that is equal to the values of the
     * x and y components of the vector.
     */
    public Rect getAsRect() {
        return new Rect((int)x, (int)y, 0, 0);
    }

    /**
     * Set the vector length to zero (x- and y-components will be zero).
     */
    public void makeZero() {
        x = 0;
        y = 0;
    }

    /**
     * Return a string represenation of the vector.
     */
    @Override
    public String toString() {
        return "Vect2 " + x + " : " + y;
    }
}
