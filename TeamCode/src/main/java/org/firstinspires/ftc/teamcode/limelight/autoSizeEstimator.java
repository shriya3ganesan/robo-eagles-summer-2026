package org.firstinspires.ftc.teamcode.limelight;
/*
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class autoSizeEstimator {

    // Default Limelight parameters
    private static final double DEFAULT_HORIZONTAL_FOV_DEGREES = 59.6;  // Limelight 3A
    private static final int DEFAULT_HORIZONTAL_RESOLUTION_PX = 960;   // Limelight default resolution

    /**
     * Estimates the real-world width of an object using Limelight data.
     * Automatically reads from NetworkTables for pixel width and area.
     *
     * @return Estimated real-world width in meters.
     *//*
    public static double estimateRealWidthAuto() {
        // Get Limelight NetworkTable
        NetworkTable limelightTable = NetworkTableInstance.getDefault().getTable("limelight");

        // Read bounding box width in pixels (thor = target horizontal side)
        double pixelWidth = limelightTable.getEntry("thor").getDouble(0.0);

        // Optional: Use target area or z distance if available for better distance estimate
        // You can improve this part if you have a better method for measuring distance
        double distanceM = estimateDistanceFromArea(limelightTable.getEntry("ta").getDouble(0.0));

        return estimateRealWidth((int) pixelWidth, DEFAULT_HORIZONTAL_FOV_DEGREES, DEFAULT_HORIZONTAL_RESOLUTION_PX, distanceM);
    }*/

    /**
     * Core estimation logic: real-world width from camera parameters.
     *
     * @param pixelWidth   Width of the target blob in pixels.
     * @param fovDegrees   Horizontal Field of View of the camera in degrees.
     * @param resolutionPx Horizontal resolution of the camera in pixels.
     * @param distanceM    Distance from camera to target in meters.
     * @return Estimated real-world width in meters.
     *//*
    public static double estimateRealWidth(int pixelWidth, double fovDegrees, int resolutionPx, double distanceM) {
        // Calculate angle per pixel
        double anglePerPixel = fovDegrees / resolutionPx;

        // Calculate total angle covered by the target
        double totalAngleDegrees = pixelWidth * anglePerPixel;

        // Use trigonometry to calculate real-world width
        double halfAngleRadians = Math.toRadians(totalAngleDegrees / 2.0);
        return 2 * distanceM * Math.tan(halfAngleRadians);
    }*/

    /**
     * Estimate distance from target area (ta). can be calibrated later
     *
     * @param targetArea The target area (%) as reported by Limelight (0 to 100).
     * @return Estimated distance in meters.
     *//*
    private static double estimateDistanceFromArea(double targetArea) {
        // Simple empirical inverse relationship; real value depends on target size & tuning
        if (targetArea <= 0.0) {
            return 1.0;  // Default/fallback value (1 meter)
        }

        // This constant would depend on real-world calibration with target.
        double calibratedConstant = 2.0; // Tune this based on setup
        return calibratedConstant / Math.sqrt(targetArea);
    }
}
*/