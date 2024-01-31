package com.dioswilson.ftl422calculator.entities;

public class EnderPearlEntity extends Entity {

    public EnderPearlEntity(double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        super(posX, posY, posZ, motionX, motionY, motionZ);
    }
    public EnderPearlEntity(Entity entity) {
        super(entity);
    }

    @Override
    public void move(boolean isOnAir) {//Idk, don't care about air
        this.updatePosition();

        this.multiplyMotion(0.99);
        this.motionY -= 0.03;
    }

    @Override
    public float getEyeHeight() {
        return 0.25F*0.85F;
    }


    @Override
    public Entity copy() {
        return new EnderPearlEntity(posX, posY, posZ, motionX, motionY, motionZ);
    }
}
