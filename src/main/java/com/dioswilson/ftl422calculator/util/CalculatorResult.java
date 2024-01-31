package com.dioswilson.ftl422calculator.util;

import java.util.Objects;

public record CalculatorResult(double pearlDistance, double tntDistance, int pearlTicks, int tntTicks, int bluePearl,
                               int redPearl,
                               int blueTnt, int redTnt, int quadrant) implements Comparable<CalculatorResult> {

    @Override
    public int compareTo(CalculatorResult o) {
        //TODO: Improve ordering
        short returnVal = 0;
        if (o.pearlDistance() + o.tntDistance() > pearlDistance() + tntDistance()) {
            returnVal = -1;
        }
        else if (o.pearlDistance() + o.tntDistance() < pearlDistance() + tntDistance()) {
            returnVal = 1;
        }
        return returnVal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CalculatorResult result)) {
            return false;
        }
        return pearlTicks == result.pearlTicks;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pearlTicks);
    }

    @Override
    public String toString() {
        return String.format("Pearl: Distance: %.2f Ticks: %d BlueTnt: %d RedTnt: %d\t|\tTNT: Distance: %.2f Ticks: %d BlueTnt: %d RedTnt: %d",
                pearlDistance(), pearlTicks(), bluePearl(), redPearl(),
                tntDistance(), tntTicks(), blueTnt(), redTnt());
    }
}

