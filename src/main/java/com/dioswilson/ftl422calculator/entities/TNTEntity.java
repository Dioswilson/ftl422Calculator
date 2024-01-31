package com.dioswilson.ftl422calculator.entities;

public class TNTEntity extends Entity {
    public TNTEntity(double posX, double posY, double posZ) {
        super(posX, posY, posZ, 0, 0, 0);
    }

    public TNTEntity(double posX, double posY, double posZ, double motionX, double motionY, double motionZ) {
        super(posX, posY, posZ, motionX, motionY, motionZ);
    }

    public TNTEntity(Entity entity) {
        super(entity);
    }

    @Override
    public void move(boolean isOnGround) {

        if (isOnGround) {
            this.motionY = 0;
        }
        else {
            this.motionY -= 0.03999999910593033;
        }

        this.updatePosition();

        this.multiplyMotion(0.9800000190734863D);

        if (isOnGround) {
            this.motionX *= 0.699999988079071D;
            this.motionZ *= 0.699999988079071D;
            this.motionY *= -0.5D;
        }
    }

    @Override
    public float getEyeHeight() {
        return 0;
    }

    @Override
    public Entity copy() {
        return new TNTEntity(posX, posY, posZ, motionX, motionY, motionZ);
    }

}
