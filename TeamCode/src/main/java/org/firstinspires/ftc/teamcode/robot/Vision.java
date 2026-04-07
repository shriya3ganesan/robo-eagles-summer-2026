package org.firstinspires.ftc.teamcode.robot;


import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.robotcore.util.Range;



public class Vision {

    private RobotHardware robot;

    // Lock-on constants
    public static double TAG_LOCK_KP = 0.05;
    public static double TAG_LOCK_TOLERANCE = 0.7;
    private static final double TAG_LOCK_MIN_POWER = 0.00;
    private static final double TAG_LOCK_MAX_POWER = 0.4;

    // State
    public boolean hasTarget   = false;
    public boolean isLockedOn  = false;
    private double  distance    = 0.0;

    public Vision(RobotHardware robot) {
        this.robot = robot;
    }

    public void init() {
        robot.limelight.start();
        robot.limelight.pipelineSwitch(0);
    }

    /**
     * Call every loop. Reads limelight and updates internal state.
     * @return the LLResult so TeleOp can use it for telemetry if needed
     */
    public LLResult update() {
        LLResult result = robot.limelight.getLatestResult();
        hasTarget = result != null && result.isValid();

        if (hasTarget) {
            distance = distanceCalc(result.getTa());
        }

        return result;
    }

    /**
     * Call with the driver's manual yaw input.
     * If lock-on is active and a target is visible, returns a corrected yaw.
     * Otherwise returns the manual yaw unchanged.
     */
    public double getYaw(double manualYaw, boolean lockOnButton, LLResult result) {
        if (lockOnButton && hasTarget && result != null) {
            double tx = result.getTx();
            isLockedOn = Math.abs(tx) < TAG_LOCK_TOLERANCE;

            if (isLockedOn) {
                return 0.0;
            } else {
                double correction = Range.clip(tx * TAG_LOCK_KP, -TAG_LOCK_MAX_POWER, TAG_LOCK_MAX_POWER);
                if (Math.abs(correction) < TAG_LOCK_MIN_POWER) {
                    correction = TAG_LOCK_MIN_POWER * Math.signum(correction);
                }
                return correction;
            }
        } else {
            isLockedOn = false;
            return manualYaw;

        }
    }

    public double distanceCalc(double ta) {
        return 72.06169 * Math.pow(ta, -0.509117);
    }

    public boolean hasTarget()  { return hasTarget; }
    public boolean isLockedOn() { return isLockedOn; }
    public double  getDistance(){ return distance; }
}