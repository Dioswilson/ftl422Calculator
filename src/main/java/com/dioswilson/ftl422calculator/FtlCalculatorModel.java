package com.dioswilson.ftl422calculator;

import com.dioswilson.ftl422calculator.entities.EnderPearlEntity;
import com.dioswilson.ftl422calculator.entities.Entity;
import com.dioswilson.ftl422calculator.entities.TNTEntity;
import com.dioswilson.ftl422calculator.util.CalculatorResult;
import com.dioswilson.ftl422calculator.util.Vector3D;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FtlCalculatorModel {

    private final EnderPearlEntity referenceEnderPearlEntity = new EnderPearlEntity(0, 185.34881785360997, 0, 0, 0.5100841893612624, 0);
    private final TNTEntity referenceTntEntity = new TNTEntity(1 - (0.5F + 0.98F / 2), 185.5, 1 - (0.5 + 0.98F / 2));//Maybe not actually 0.01

    private final Vector3D northEastTNTPos = new Vector3D((0.375 - 0.98F / 2 + 1), 185.5612500011921, -(0.375 - 0.98F / 2 + 1));//y should be 185.56125
    private final Vector3D southEastTNTPos = new Vector3D((0.375 - 0.98F / 2 + 1), 185.5612500011921, (0.375 - 0.98F / 2 + 1));

    private final Vector3D northWestTNTPos = new Vector3D(-(0.375 - 0.98F / 2 + 1), 185.5612500011921, -(0.375 - 0.98F / 2 + 1));
    private final Vector3D southWestTNTPos = new Vector3D(-(0.375 - 0.98F / 2 + 1), 185.5612500011921, (0.375 - 0.98F / 2 + 1));
//    private final Vector3D northEastTNTPos = new Vector3D(0.88499999046326, 185.5612500011921, -0.8849999904633);//y should be 185.56125
//    private final Vector3D southEastTNTPos = new Vector3D(0.88499999046326, 185.5612500011921, 0.88499999046326);
//
//    private final Vector3D northWestTNTPos = new Vector3D(-0.88499999046326, 185.5612500011921, -0.88499999046326);
//    private final Vector3D southWestTNTPos = new Vector3D(-0.88499999046326, 185.5612500011921, 0.88499999046326);


    public FtlCalculatorModel() {
    }

    public List<CalculatorResult> getAcceptableResults(int alignerX, int alignerZ, double destX, double destZ, int maxTnt) {
        List<CalculatorResult> results = new ArrayList<>();
        alignerX += 1;//Positioning pearl

        EnderPearlEntity initEnderPearlEntity = new EnderPearlEntity(referenceEnderPearlEntity);
        initEnderPearlEntity.incrementPosition(alignerX, 0, alignerZ);
        TNTEntity initTntEntity = new TNTEntity(referenceTntEntity);
        initTntEntity.incrementPosition(alignerX, 0, alignerZ);

        double angle = getAngle(alignerX, alignerZ, destX, destZ);
        int quadrant = getQuadrant(angle);//I don't want to handle exception

        Vector3D bluePos = getBluePos(quadrant);
        bluePos.add(alignerX, alignerZ);
        Vector3D redPos = getRedPos(quadrant);
        redPos.add(alignerX, alignerZ);


        Vector3D pearlBlueExplosion = getBaseExplosionVector(initEnderPearlEntity, bluePos);
        Vector3D pearlRedExplosion = getBaseExplosionVector(initEnderPearlEntity, redPos);

        int maxRed = 0;
        int maxBlue = 0;

        List<Payload> payloads = new ArrayList<>();
        for (int redTnt = maxTnt; redTnt >= 0; redTnt--) {
            for (int blueTnt = maxTnt; blueTnt >= 0; blueTnt--) {
                Vector3D propulsionVector = pearlBlueExplosion.getMultiplied(blueTnt);
                propulsionVector.add(pearlRedExplosion.getMultiplied(redTnt));
                if (isAcceptableAngle(propulsionVector, angle, maxTnt)) {
                    Pair<Double, Integer> distTicksPair = getTicksTillDest(initEnderPearlEntity, pearlBlueExplosion, pearlRedExplosion, blueTnt, redTnt, destX, destZ);
                    double dist = distTicksPair.getKey();
                    int ticks = distTicksPair.getValue();
                    if (ticks != 0) {
                        Payload payload = new Payload(propulsionVector.getAngle(), blueTnt, redTnt, dist, ticks);

                        if (!payloads.contains(payload)) {
                            payloads.add(payload);//This is too big, have to make it smaller, add checks
                        }
                        else if (payloads.get(payloads.indexOf(payload)).dist() > payload.ticksToDest()) {
                            payloads.remove(payload);//Maybe slow
                            payloads.add(payload);
                        }
                        if (redTnt > maxRed) {
                            maxRed = redTnt;
                        }
                        if (blueTnt > maxBlue) {
                            maxBlue = blueTnt;
                        }
                    }
                }
            }
        }

        maxRed *= 1.12;
        maxBlue *= 1.12;

        if (maxRed > maxTnt) {
            maxRed = maxTnt;
        }
        if (maxBlue > maxTnt) {
            maxBlue = maxTnt;
        }

        Vector3D tntBlueExplosion = getBaseExplosionVector(initTntEntity, bluePos);
        Vector3D tntRedExplosion = getBaseExplosionVector(initTntEntity, redPos);

        for (int redTnt = maxRed; redTnt >= 0; redTnt--) {
            for (int blueTnt = maxBlue; blueTnt >= 0; blueTnt--) {
                Vector3D propulsionVector = tntBlueExplosion.getMultiplied(blueTnt);
                propulsionVector.add(tntRedExplosion.getMultiplied(redTnt));
                if (isAcceptableAngle(propulsionVector, angle, maxTnt)) {
                    for (Payload payload : payloads) {//Should first check  angle with reference, then make tighter comparison with actual one
                        if (isAcceptableAngle(propulsionVector, payload.angle(), maxTnt )) {//*2 is to make it tighter
                            Pair<Double, Integer> distTicksPair = getTicksTillDest(initTntEntity, tntBlueExplosion, tntRedExplosion, blueTnt, redTnt, destX, destZ);

                            int tntTick = distTicksPair.getValue();
                            if (tntTick == payload.ticksToDest) {
                                CalculatorResult result = new CalculatorResult(Math.sqrt(payload.dist), Math.sqrt(distTicksPair.getKey()),
                                        payload.ticksToDest, tntTick, payload.blueTnt, payload.redTnt, blueTnt, redTnt, quadrant);

                                if (!results.contains(result)) {
                                    results.add(result);
                                }
                                else if (results.get(results.indexOf(result)).tntDistance() > result.tntDistance()) {
                                    results.remove(result);
                                    results.add(result);
                                }
                            }
                            //ACA había algo subóptimo
                            //Maybe checkear si con los mismos ticks hay distinta distancia
                            //Maybe check similar ticks
                        }
                    }
                }
            }
        }

        return results;
    }

    public List<Pair<Vector3D, Vector3D>> simulateMovement(int alignerX, int alignerZ, int pearlBlueTnt, int pearlRedTnt, int tntBlueTnt, int tntRedTnt, int quadrant) {//TODO: Naming
        List<Pair<Vector3D, Vector3D>> positions = new ArrayList<>();

        alignerX += 1;//Positioning pearl

        EnderPearlEntity initEnderPearlEntity = new EnderPearlEntity(referenceEnderPearlEntity);
        initEnderPearlEntity.incrementPosition(alignerX, 0, alignerZ);
        TNTEntity initTntEntity = new TNTEntity(referenceTntEntity);
        initTntEntity.incrementPosition(alignerX, 0, alignerZ);

        Vector3D bluePos = getBluePos(quadrant);
        bluePos.add(initEnderPearlEntity.posX, initEnderPearlEntity.posZ);
        Vector3D redPos = getRedPos(quadrant);
        redPos.add(initEnderPearlEntity.posX, initEnderPearlEntity.posZ);


        List<Vector3D> pealPositions = getAllPositions(initEnderPearlEntity, pearlBlueTnt, pearlRedTnt, bluePos, redPos);
        List<Vector3D> tntPositions = getAllPositions(initTntEntity, tntBlueTnt, tntRedTnt, bluePos, redPos);


        Vector3D lastTntPos = null;//Maybe not too safe

        for (int i = 0; i < pealPositions.size(); i++) {
            Vector3D pearlPos = pealPositions.get(i);
            Vector3D tntPos = lastTntPos;
            if (i <= 15 && i < tntPositions.size()) {
                tntPos = tntPositions.get(i);
                lastTntPos = tntPos;
            }
            positions.add(new Pair<>(pearlPos, tntPos));
        }

        return positions;
    }

    private List<Vector3D> getAllPositions(Entity ogEntity, int blueTnt, int redTnt, Vector3D bluePos, Vector3D redPos) {
        Entity entity = ogEntity.copy();

        List<Vector3D> positions = new ArrayList<>();

        Vector3D blueExplosion = getBaseExplosionVector(entity, bluePos);
        Vector3D redExplosion = getBaseExplosionVector(entity, redPos);

        Vector3D propulsionVector = blueExplosion.getMultiplied(blueTnt);
        propulsionVector.add(redExplosion.getMultiplied(redTnt));

        entity.impulse(propulsionVector);
        boolean isOnGround = false;
        if (entity instanceof TNTEntity) {
            isOnGround = true;
        }
        while (entity.posY > 128) {
            positions.add(new Vector3D(entity.posX, entity.posY, entity.posZ));
            entity.move(isOnGround);
            isOnGround = false;
        }

        return positions;
    }


    private Pair<Double, Integer> getTicksTillDest(Entity ogEntity, Vector3D blueExplosion, Vector3D redExplosion, int blueTnt, int redTnt, double destX, double destZ) {
        Entity entity = ogEntity.copy();

        Vector3D explosionVector = blueExplosion.getMultiplied(blueTnt);
        explosionVector.add(redExplosion.getMultiplied(redTnt));
//        double distance = entity.getDistance(destX, entity.posY, destZ); //Maybe to 128
        double distance = entity.squareDistance(destX, destZ); //Maybe to 128
        int ticks = 0;
        double prevDistance = Double.MAX_VALUE;

        entity.impulse(explosionVector);
        boolean isOnGround = false;
        if (entity instanceof TNTEntity) {
            isOnGround = true;
        }
        while (distance < prevDistance) {
            prevDistance = distance;

            entity.move(isOnGround);
            isOnGround = false;
            distance = entity.squareDistance(destX, destZ);

            ticks++;
            if (entity.posY < 128) {//Don't really like this
                ticks = 1;
                break;
            }

        }
        return new Pair<>(prevDistance, ticks - 1);
    }

    private boolean isAcceptableAngle(Vector3D vector, double angle, int maxTnt) {
        double angleTolerance = 575 / (double) maxTnt;//Super manual
        double vectorAngle = vector.getAngle();
        return vectorAngle > angle - angleTolerance && vectorAngle < angle + angleTolerance;
    }

    private Vector3D getBaseExplosionVector(Entity ogEntity, Vector3D pos) {
        Entity entity = ogEntity.copy();

        Vector3D explosionVector = new Vector3D();

        double d12 = entity.getDistance(pos.getX(), pos.getY(), pos.getZ()) / 8.0; //distance /8

        double d5 = entity.posX - pos.getX(); //Distance X
        double d7 = entity.posY + (double) entity.getEyeHeight() - pos.getY();//Distance Y to eye height
        double d9 = entity.posZ - pos.getZ(); //Distance Z
        double d13 = (float) Math.sqrt(d5 * d5 + d7 * d7 + d9 * d9); //Distance vector to eye height (Foot for TNT)

        d5 = d5 / d13;
        d7 = d7 / d13;
        d9 = d9 / d13;

        double d11 = 1.0 - d12;

        explosionVector.add(d5 * d11, d7 * d11, d9 * d11);

        return explosionVector;
    }


    private Vector3D getBluePos(int quadrant) {
        Vector3D bluePos = null;
        switch (quadrant) {
            case 0:
                bluePos = new Vector3D(northWestTNTPos);
                break;
            case 1:
                bluePos = new Vector3D(southEastTNTPos);
                break;
            case 2, 3:
                bluePos = new Vector3D(southWestTNTPos);
                break;
        }
        return bluePos;
    }

    private Vector3D getRedPos(int quadrant) {
        Vector3D redPos = null;
        switch (quadrant) {
            case 0, 1:
                redPos = new Vector3D(northEastTNTPos);
                break;
            case 2:
                redPos = new Vector3D(northWestTNTPos);
                break;
            case 3:
                redPos = new Vector3D(southEastTNTPos);
                break;
        }
        return redPos;
    }

    private int getQuadrant(double angle) throws IllegalArgumentException {
        if (angle >= -45 && angle <= 45) {
            return 0;//South
        }
        else if (angle >= -135 && angle <= -45) {
            return 1;//West
        }
        else if (angle >= 45 && angle <= 135) {
            return 2;//East
        }
        else if ((angle >= 135 && angle <= 180) || (angle >= -180 && angle <= -135)) {
            return 3;//North
        }
        else {
            throw new IllegalArgumentException("Angle must be between -180 and 180");
        }

    }

    private double getAngle(int alignerX, int alignerZ, double destX, double destZ) {
        return Math.toDegrees(Math.atan2(destX - alignerX, destZ - alignerZ));//Note: Might have to change it to rads
    }

    private record Payload(double angle, int blueTnt, int redTnt, double dist, int ticksToDest) {
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Payload payload)) {
                return false;
            }
            return ticksToDest == payload.ticksToDest;
        }

        @Override
        public int hashCode() {
            return Objects.hash(ticksToDest);
        }


    }
}

