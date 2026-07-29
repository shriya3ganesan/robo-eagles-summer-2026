package org.firstinspires.ftc.teamcode.Exercises;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class DrivetrainBlank {

    // Drivetrain motors
    private final DcMotor frontLeft;
    private final DcMotor frontRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final IMU imu;

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

        imu = opMode.hardwareMap.get(IMU.class, "imu");

        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);

        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        setRunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

   //sets power to left and right
    public void setPower(double leftPower, double rightPower) {
        frontLeft.setPower(leftPower);
        backLeft.setPower(leftPower);
        frontRight.setPower(rightPower);
        backRight.setPower(rightPower);

    }


    //set power same
    public void setAllPower(double power) {

        setPower(power, power);

    }

    //stop motors
    public void stop() {
        setAllPower(0);

    }

    //drive for a certain time
    public void driveForTime(double power, long milliseconds) {

        resetTime();

        //set power, start moving, move for a certain amt of time, stop motors

        while(runtime.milliseconds() <= milliseconds) {
            setAllPower(power);
        }

        stop();

        setAllPower(power);
        while (runtime.milliseconds() <= milliseconds) {

        }
        stop();

        setAllPower(power);
        opMode.sleep(milliseconds);
        stop();

    }

    //turn for a certain time
    public void turnForTime(double power, long milliseconds) {

        while (runtime.milliseconds() <= milliseconds) {
            setPower(power, -power);
        }

        stop();
    }

    //drive a certain amt of inches
    public void driveInches(double inches, double power, double timeoutMs) {

        //figure out how many encoder ticks corresponds to inches
        double ticks = inchesToTicks(inches);
        //resetting encoders
        resetEncoders();
        resetTime();
        //set power to motors
        //keep driving until we're at inches
        //stop robot

        while(getAverageEncoderPosition() <= ticks && runtime.milliseconds() <= timeoutMs) {
            setAllPower(power);
        }
        stop();

    }

    public void turnDegrees(double degrees, double power, boolean isRight) {

        //ideas for how to account for negative degrees
        /*double scaledPower = 0;
        if(isRight) {
            scaledPower = -power;
        }
        else {
            scaledPower = power;
        }

        while(getHeading() <= degrees) {
            setPower(scaledPower, -scaledPower);
        }*/


    }

    //get the encoder position of all motors
    private double getAverageEncoderPosition() {


        return (frontLeft.getCurrentPosition() + frontRight.getCurrentPosition()
        + backRight.getCurrentPosition() + backLeft.getCurrentPosition()) / 4;
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

    public double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }
}