package org.firstinspires.ftc.teamcode.Exercises.Ani;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

public class DrivetrainBlank {

    // Drivetrain motors
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;

    // Gives this class access to sleep(), telemetry, and opModeIsActive()
    private final LinearOpMode opMode;
    private ElapsedTime runtime = new ElapsedTime();

   //encoder constants
    private static final double TICKS_PER_MOTOR_REVOLUTION = 537.7;
    private static final double DRIVE_GEAR_RATIO = 1.0;
    private static final double WHEEL_DIAMETER_INCHES = 4.0;

    private static final double TICKS_PER_INCH =
            (TICKS_PER_MOTOR_REVOLUTION * DRIVE_GEAR_RATIO)
                    / (Math.PI * WHEEL_DIAMETER_INCHES);

    //constructor (used for initialization)
    public DrivetrainBlank(LinearOpMode opMode) {
        this.opMode = opMode;

        frontLeft = opMode.hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = opMode.hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = opMode.hardwareMap.get(DcMotor.class, "backLeft");
        backRight = opMode.hardwareMap.get(DcMotor.class, "backRight");


        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

   //sets power to left and right
    public void setPower(double leftPower, double rightPower) {

    }

    //set power same
    public void setAllPower(double power) {


    }

    //stop motors
    public void stop() {


    }

    //drive for a certain time
    public void driveForTime(double power, long milliseconds) {

    }

    //turn for a certain time
    public void turnForTime(double power, long milliseconds) {

    }

    //drive a certain amt of inches
    public void driveInches(double inches, double power, double timeoutms) {


    }

    //get the encoder position of all motors
    private double getAverageEncoderPosition() {

        return 0;
    }
    /*
     * Return true while at least one motor is moving
     * toward its target.
     */

    private double ticksToInches(double ticks) {
        return ticks / TICKS_PER_INCH;
    }

    private int inchesToTicks(double inches) {
        return (int) Math.round(inches * TICKS_PER_INCH);
    }

    private void resetEncoders() {

        setRunMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }


    private void setRunMode(DcMotor.RunMode mode) {
        frontLeft.setMode(mode);
        frontRight.setMode(mode);
        backLeft.setMode(mode);
        backRight.setMode(mode);
    }


    private void setZeroPowerBehavior(
            DcMotor.ZeroPowerBehavior behavior
    ) {
        frontLeft.setZeroPowerBehavior(behavior);
        frontRight.setZeroPowerBehavior(behavior);
        backLeft.setZeroPowerBehavior(behavior);
        backRight.setZeroPowerBehavior(behavior);
    }

    private void resetTime () {
        runtime.reset();
    }
}