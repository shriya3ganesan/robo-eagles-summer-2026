package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class TurretServo {
    private Servo turretServo;

    //private final double TURRET_START_POSITION = 0.0; // nominally 0 degrees, may need to be tuned based on mounting angle of servo
    private final double TURRET_START_POSITION = 0.5; // pick halfway point to start?
    private double currentTurretAngle;

    public void init (HardwareMap hwMap) {
        turretServo = hwMap.get(Servo.class,"turret_servo");
        currentTurretAngle = TURRET_START_POSITION; // default to "0"
        resetTurret(); // default it to "0" degrees
    }

    /// Set turret servo back to "0" position
    public void resetTurret() {
        // Set feeders to a preset value to stop the servos.
        currentTurretAngle = TURRET_START_POSITION;
        turretServo.setPosition(TURRET_START_POSITION);
    }

    /// Adjust turret servo to new position
    public void changeTurretByDegrees(double deltaDegrees) {

        // try this
        currentTurretAngle = currentTurretAngle + (deltaDegrees / 500);
        if (currentTurretAngle < 0 || currentTurretAngle > 1) {
            resetTurret();
        }

        // TODO: change currentTurretAngle (range: 0 - 1.0) based on input delta angle as measured by the camera (range: -180 - 180)
        // set the turret to the new angle
        turretServo.setPosition(currentTurretAngle);
    }

    public void incrementTurretPosition() {
        currentTurretAngle = currentTurretAngle + .05;
        turretServo.setPosition(currentTurretAngle);
    }

    public void decrementTurretPosition() {
        currentTurretAngle = currentTurretAngle - .05;
        turretServo.setPosition(currentTurretAngle);
    }

    public double getCurrentPosition() {
        return currentTurretAngle;
    }
}
