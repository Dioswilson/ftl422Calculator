package com.dioswilson.ftl422calculator.util;

public class Vector3D {
    private double x;
    private double y;
    private double z;

    public Vector3D() {
        this(0, 0, 0);
    }

    public Vector3D(Vector3D vector) {
        this(vector.x, vector.y, vector.z);
    }

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void add(Vector3D vector) {
        this.x += vector.x;
        this.y += vector.y;
        this.z += vector.z;
    }

    public void add(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    public void add(double x, double z) {
        this.x += x;
        this.z += z;
    }

    public Vector3D getMultiplied(double factor) {
        return new Vector3D(x * factor, y * factor, z * factor);
    }

    public double getAngle() {
        return Math.toDegrees(Math.atan2(x, z));
    }

    @Override
    public String toString() {
        return String.format("(X:%.2f, Y:%.2f, Z:%.2f)", x, y, z);
    }
}
