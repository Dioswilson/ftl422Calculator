package com.dioswilson.ftl422calculator.entities;

import com.dioswilson.ftl422calculator.util.Vector3D;

public abstract class Entity {
    //Don't really want to use Vector3D, want public fields
    public double posX;
    public double posY;
    public double posZ;
    protected double motionX;
    protected double motionY;
    protected double motionZ;


    public Entity(double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
    }

    public Entity(Entity entity) {
        this.posX = entity.posX;
        this.posY = entity.posY;
        this.posZ = entity.posZ;
        this.motionX = entity.motionX;
        this.motionY = entity.motionY;
        this.motionZ = entity.motionZ;
    }

    public abstract void move(boolean isOnAir);//Maybe move multiple at once

    public abstract float getEyeHeight();

    public abstract Entity copy();

    public void move() {
        move(false);
    }

    public void multiplyMotion(double factor) {
        motionX *= factor;
        motionY *= factor;
        motionZ *= factor;
    }


    protected void updatePosition() {
        posX += motionX;
        posY += motionY;
        posZ += motionZ;
    }

    public void incrementPosition(double x, double y, double z) {
        posX += x;
        posY += y;
        posZ += z;
    }


    public double squareDistance(double x, double z) {
        double d0 = this.posX - x;
        double d2 = this.posZ - z;
        return d0 * d0 + d2 * d2;
    }

    public float getDistance(double x, double y, double z) {//Wtf minecraft, why float
        double d0 = this.posX - x;
        double d1 = this.posY - y;
        double d2 = this.posZ - z;
        return (float)Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    public void impulse(Vector3D vector) {
        motionX += vector.getX();
        motionY += vector.getY();
        motionZ += vector.getZ();
    }


}

