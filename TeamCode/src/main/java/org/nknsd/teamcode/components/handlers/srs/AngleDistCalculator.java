package org.nknsd.teamcode.components.handlers.srs;

import org.nknsd.teamcode.components.utility.SensorGridPoint;

public class AngleDistCalculator {
    private static final double sensorOffset = 8;

    public static double calculateHeadingAngle(SensorGridPoint point) {
        double x = point.getX(), y = point.getY() + sensorOffset;
        return Math.atan(x/y);
    }

    public static double calculateDistance(SensorGridPoint point) {
        double x = point.getX(), y = point.getY();
        return Math.sqrt(x * x + y * y);
    }
}

