package org.firstinspires.ftc.teamcode.limelight;
/*
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class autoSizeEstimatorB {

    private static final double DEFAULT_HORIZONTAL_FOV_DEGREES = 59.6;  // Limelight 3A
    private static final int DEFAULT_HORIZONTAL_RESOLUTION_PX = 960;

    public static NetworkTable getTable() {
        return NetworkTableInstance.getDefault().getTable("limelight");
    }

    /** Horizontal offset from target center (degrees) *//*
    public static double getTx() {
        return getTable().getEntry("tx").getDouble(0.0);
    }

    /** Vertical offset (degrees) *//*
    public static double getTy() {
        return getTable().getEntry("ty").getDouble(0.0);
    }

    /** Target area (%) *//*
    public static double getTa() {
        return getTable().getEntry("ta").getDouble(0.0);
    }

    public static double estimateRealWidthAuto() {
        NetworkTable limelightTable = getTable();
        double pixelWidth = limelightTable.getEntry("thor").getDouble(0.0);
        double distanceM = estimateDistanceFromArea(limelightTable.getEntry("ta").getDouble(0.0));
        return estimateRealWidth((int) pixelWidth, DEFAULT_HORIZONTAL_FOV_DEGREES, DEFAULT_HORIZONTAL_RESOLUTION_PX, distanceM);
    }

    public static double estimateRealWidth(int pixelWidth, double fovDegrees, int resolutionPx, double distanceM) {
        double anglePerPixel = fovDegrees / resolutionPx;
        double totalAngleDegrees = pixelWidth * anglePerPixel;
        double halfAngleRadians = Math.toRadians(totalAngleDegrees / 2.0);
        return 2 * distanceM * Math.tan(halfAngleRadians);
    }

    private static double estimateDistanceFromArea(double targetArea) {
        if (targetArea <= 0.0) return 1.0;
        double calibratedConstant = 2.0; // tune this for your setup
        return calibratedConstant / Math.sqrt(targetArea);
    }
}
*/