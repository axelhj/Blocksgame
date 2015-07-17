package se.axelhjelmqvist.blocksgame;

import android.graphics.Rect;

/**
 * Created with IntelliJ IDEA.
 * User: Axel
 * Date: 2013-10-06
 * Time: 19:23
 * To change this template use File | Settings | File Templates.
 */

public class Vector2 {
    public float x, y;

    public Vector2() {
        this(0, 0);
    }

    public Vector2(Vector2 copy) {
        this(copy.x, copy.y);
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void move(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void move(Vector2 distance) {
        this.x += distance.x; this.y += distance.y;
    }

    public void offset(float x, float y) {
        this.x += x;
        this.y += y;
    }

    public void offset(Vector2 distance) {
        this.x += distance.x;
        this.y += distance.y;
    }

    public void offsetTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void offsetTo(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    public void offsetLengthTo(float newLength) {
        this.normalize();
        this.scaleBy(newLength);
    }

    public void scaleBy(float factor) {
        x = x * factor;
        y = y * factor;
    }

    public float getLength() {
        double xsq = Math.abs((double)(x * x));
        double ysq = Math.abs((double)(y * y));

        float result = (float)Math.sqrt(xsq + ysq);
        return Float.isNaN(result) ? 0.0f : result;
    }

    public float direction() { //return value in radians
        return (float)Math.atan2((double)x, (double)y);
    }

    public void normalize() {
        float length = getLength();
        if (length != 0.0f) {
            this.x = x / length;
            this.y = y / length;
        }
        else { this.x = 0.0f; this.y = 0.0f; }
        return;
    }

    public Vector2 normalized() {
        Vector2 result = new Vector2(x, y);
        result.normalize();
        return result;
    }

    public Rect getAsRect() {
        return new Rect((int)x, (int)y, 0, 0);
    }

    public void makeZero() {
        x = 0;
        y = 0;
    }

    @Override
    public String toString() {
        return "Vect2 " + x + " : " + y;
    }
}
