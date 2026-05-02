package org.firstinspires.ftc.teamcode;

/**
 * VisualPathPIDController
 *
 * A standard Proportional-Integral-Derivative controller implemented for
 * two-dimensional (X, Y) coordinate tracking.
 *
 * Usage:
 * Initialize with gain constants (kP, kI, kD), then call calculate()
 * periodically with current and target positions to get the correction output.
 */
public class VisualPathPIDController {
    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private double kP, kI, kD;

    private double integralX = 0;
    private double integralY = 0;

    private double lastErrorX = 0;
    private double lastErrorY = 0;

    private long lastTime;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * @param kP Proportional gain.
     * @param kI Integral gain.
     * @param kD Derivative gain.
     */
    public VisualPathPIDController(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        lastTime = System.currentTimeMillis();
    }

    // -------------------------------------------------------------------------
    // Control Logic
    // -------------------------------------------------------------------------

    /**
     * Calculates the correction output for X and Y coordinates based on the 
     * PID algorithm.
     *
     * @param currentX Current X position.
     * @param currentY Current Y position.
     * @param targetX Target X position.
     * @param targetY Target Y position.
     * @return An array containing {outputX, outputY}.
     */
    public double[] calculate(double currentX, double currentY, double targetX, double targetY) {
        long now = System.currentTimeMillis();
        double dt = (now - lastTime) / 1000.0;
        lastTime = now;

        double errorX = targetX - currentX;
        double errorY = targetY - currentY;

        integralX += errorX * dt;
        integralY += errorY * dt;

        double derivativeX = (errorX - lastErrorX) / dt;
        double derivativeY = (errorY - lastErrorY) / dt;

        lastErrorX = errorX;
        lastErrorY = errorY;

        double outputX = kP * errorX + kI * integralX + kD * derivativeX;
        double outputY = kP * errorY + kI * integralY + kD * derivativeY;

        return new double[]{outputX, outputY};
    }

    /**
     * Resets the accumulated integral and last error values.
     */
    public void reset() {
        integralX = 0;
        integralY = 0;
        lastErrorX = 0;
        lastErrorY = 0;
    }
}