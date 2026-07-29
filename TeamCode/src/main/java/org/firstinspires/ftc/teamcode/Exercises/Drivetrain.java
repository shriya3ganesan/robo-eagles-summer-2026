package org.firstinspires.ftc.teamcode.Exercises;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public class Drivetrain {

    // Drivetrain motors
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;

    // Gives this class access to sleep(), telemetry, and opModeIsActive()
    private final LinearOpMode opMode;

    /*
     * Encoder constants
     *
     * Students should replace these values with the specifications
     * for the motors, gear ratio, and wheels on their robot.
     */
    private static final double TICKS_PER_MOTOR_REVOLUTION = 537.7;
    private static final double DRIVE_GEAR_RATIO = 1.0;
    private static final double WHEEL_DIAMETER_INCHES = 4.0;

    private static final double TICKS_PER_INCH =
            (TICKS_PER_MOTOR_REVOLUTION * DRIVE_GEAR_RATIO)
                    / (Math.PI * WHEEL_DIAMETER_INCHES);

    /*
     * Constructor
     *
     * This runs when a Drivetrain object is created.
     */
    public Drivetrain(LinearOpMode opMode) {
        this.opMode = opMode;

        frontLeft = opMode.hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = opMode.hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = opMode.hardwareMap.get(DcMotor.class, "backLeft");
        backRight = opMode.hardwareMap.get(DcMotor.class, "backRight");

        /*
         * Motors on opposite sides of the robot usually need
         * opposite directions.
         *
         * These directions may need to be reversed depending on
         * how the motors are mounted.
         */
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /*
     * Set a different power for the left and right sides.
     *
     * This can be used for driving forward, backward, or turning.
     */
    public void setPower(double leftPower, double rightPower) {
        frontLeft.setPower(leftPower);
        backLeft.setPower(leftPower);

        frontRight.setPower(rightPower);
        backRight.setPower(rightPower);
    }

    /*
     * Set the same power to all four motors.
     */
    public void setAllPower(double power) {
        setPower(power, power);
    }

    /*
     * Stop all four motors.
     */
    public void stop() {
        setAllPower(0);
    }

    /*
     * Move forward or backward for a certain number of milliseconds.
     *
     * Positive power moves forward.
     * Negative power moves backward.
     */
    public void driveForTime(double power, long milliseconds) {
        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        setAllPower(power);
        opMode.sleep(milliseconds);
        stop();
    }

    /*
     * Turn for a certain number of milliseconds.
     *
     * Positive power turns right.
     * Negative power turns left.
     */
    public void turnForTime(double power, long milliseconds) {
        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        setPower(power, -power);
        opMode.sleep(milliseconds);
        stop();
    }

    /*
     * Move a certain number of inches using encoders.
     *
     * Positive inches moves forward.
     * Negative inches moves backward.
     */
    public void driveInches(double inches, double power) {
        int ticks = (int) Math.round(inches * TICKS_PER_INCH);

        resetEncoders();

        setTargetPositions(ticks, ticks, ticks, ticks);

        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);

        // RUN_TO_POSITION normally uses positive power.
        setAllPower(Math.abs(power));

        while (opMode.opModeIsActive() && motorsAreBusy()) {
            opMode.telemetry.addData("Target inches", inches);
            opMode.telemetry.addData(
                    "Front left",
                    "%d / %d",
                    frontLeft.getCurrentPosition(),
                    frontLeft.getTargetPosition()
            );
            opMode.telemetry.addData(
                    "Front right",
                    "%d / %d",
                    frontRight.getCurrentPosition(),
                    frontRight.getTargetPosition()
            );
            opMode.telemetry.update();
        }

        stop();

        setRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /*
     * Turn using encoder ticks.
     *
     * This is not yet measured in degrees because the conversion
     * from degrees to ticks depends on the robot's wheel spacing.
     */
    public void turnTicks(int ticks, double power) {
        resetEncoders();

        /*
         * For a point turn:
         * left wheels move forward
         * right wheels move backward
         */
        setTargetPositions(ticks, -ticks, ticks, -ticks);

        setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        setAllPower(Math.abs(power));

        while (opMode.opModeIsActive() && motorsAreBusy()) {
            opMode.telemetry.addData("Turn target", ticks);
            opMode.telemetry.addData(
                    "Front left position",
                    frontLeft.getCurrentPosition()
            );
            opMode.telemetry.addData(
                    "Front right position",
                    frontRight.getCurrentPosition()
            );
            opMode.telemetry.update();
        }

        stop();

        setRunMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    /*
     * Set the target encoder position for every motor.
     */
    private void setTargetPositions(
            int frontLeftTicks,
            int frontRightTicks,
            int backLeftTicks,
            int backRightTicks
    ) {
        frontLeft.setTargetPosition(frontLeftTicks);
        frontRight.setTargetPosition(frontRightTicks);
        backLeft.setTargetPosition(backLeftTicks);
        backRight.setTargetPosition(backRightTicks);
    }

    /*
     * Return true while at least one motor is moving
     * toward its target.
     */
    private boolean motorsAreBusy() {
        return frontLeft.isBusy()
                || frontRight.isBusy()
                || backLeft.isBusy()
                || backRight.isBusy();
    }

    /*
     * Reset all motor encoders to zero.
     */
    private void resetEncoders() {
        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    /*
     * Change the RunMode of all four motors.
     */
    private void setRunMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }

    /*
     * Change the zero-power behavior of all four motors.
     */
    private void setZeroPowerBehavior(
            DcMotor.ZeroPowerBehavior behavior
    ) {
        frontLeft.setZeroPowerBehavior(behavior);
        frontRight.setZeroPowerBehavior(behavior);
        backLeft.setZeroPowerBehavior(behavior);
        backRight.setZeroPowerBehavior(behavior);
    }
}