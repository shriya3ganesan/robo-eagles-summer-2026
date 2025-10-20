package org.firstinspires.ftc.team417;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

/**
 * This class contains all of the base logic that is shared between all of the TeleOp and
 * Autonomous logic. All TeleOp and Autonomous classes should derive from this class.
 */
abstract public class BaseOpMode extends LinearOpMode {

    public DcMotorEx launcher = null;
    public CRServo leftFeeder = null;
    public CRServo rightFeeder = null;
    public static final double STOP_SPEED = 0.0; //We send this power to the servos when we want them to stop.
    public static double FEED_TIME_SECONDS = 0; //The feeder servos run this long when a shot is requested.

    public static double FEED_TIME_LOW = 0.15;
    public static double FEED_TIME_SORT = 0.07;


    public static double rememberVelocity = 0;

    public static double FULL_SPEED = 1.0; //We send this power to the servos when we want them to feed an artifact to the launcher
    public static double SLOW_REV_SPEED = -0.15; //speed for the constant reverse rotation
    public static double REV_SPEED = -1.0;//speed used for the reverse launch function
    public static double LAUNCHER_HIGH_MAX_VELOCITY = 2000; //high target velocity + 50 (will need adjusting)
    public static double LAUNCHER_HIGH_TARGET_VELOCITY = 1950;
    public static double LAUNCHER_HIGH_MIN_VELOCITY = 1900;

    public static double LAUNCHER_LOW_MAX_VELOCITY = 1175; //low target velocity + 50 (will need adjusting)
    public static double LAUNCHER_LOW_TARGET_VELOCITY = 1125;
    public static double LAUNCHER_LOW_MIN_VELOCITY = 1075;

    public static double LAUNCHER_SORTER_MAX_VELOCITY = 550; //sorter target velocity + 50 (will need adjusting)
    public static double LAUNCHER_SORTER_TARGET_VELOCITY = 500;
    public static double LAUNCHER_SORTER_MIN_VELOCITY = 450;


    public static double LAUNCHER_REV_TARGET_VELOCITY = -250;
    boolean doHighLaunch = false;
    boolean doSort = false;
    boolean doReverse = false;

    public LED redLed;
    public LED greenLed;

    ElapsedTime feederTimer = new ElapsedTime();

    public enum LaunchState {
        IDLE,
        SPIN_UP_HIGH,
        SPIN_UP_LOW,
        SPIN_UP_SORT,
        LAUNCH,
        LAUNCHING,
    }

    public LaunchState launchState;
    public void initHardware() {

        // leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        // rightDrive = hardwareMap.get(DcMotor.class, "right_drive");

        // initialize flywheel motor and feeder servos
        launcher = hardwareMap.get(DcMotorEx.class, "motLauncher");
        leftFeeder = hardwareMap.get(CRServo.class, "servoBLaunchFeed");
        rightFeeder = hardwareMap.get(CRServo.class, "servoFLaunchFeed");

        // Reversed direction of launcher for DevBot because motor is on the other side (compared to FastBot)
        if (MecanumDrive.isDevBot) {
            launcher.setDirection(DcMotorEx.Direction.REVERSE);
            redLed = null;
            greenLed = null;

        }
        if (false) {
            redLed = hardwareMap.get(LED.class, "redLed");
            greenLed = hardwareMap.get(LED.class, "greenLed");
            redLed.on();
            greenLed.off();
        }


        /*
         * Here we set our launcher to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // set the flywheel to a braking behavior so it slows down faster when left trigger is pressed
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // set the feeder servos to an initial value to init the servo controller
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);

        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

        //set the left feeder servo to rotate in reverse, so that the servos spin in the same relative direction
        leftFeeder.setDirection(DcMotorSimple.Direction.REVERSE);


        //  Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }


    public void launch(boolean shotRequested) {
        switch (launchState) {
            case IDLE: // The default launch state. When a launchmode is selected, the flywheel revs up and waits here for shotRequested
                if (shotRequested) {
                    if (doHighLaunch) {
                        launchState = LaunchState.SPIN_UP_HIGH;
                    } else if (doSort) {
                        launchState = LaunchState.SPIN_UP_SORT;
                        FEED_TIME_SECONDS = FEED_TIME_SORT;
                    } else {
                        launchState = LaunchState.SPIN_UP_LOW;
                        FEED_TIME_SECONDS = FEED_TIME_LOW;
                    }
                }
                break;

            case SPIN_UP_SORT: //if sorting launchmode is selected and shotRequested is true, check that the flywheel is in the correct velocity range (450 - 500 rpm)
                launcher.setVelocity(LAUNCHER_SORTER_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_SORTER_MIN_VELOCITY && launcher.getVelocity() < LAUNCHER_SORTER_MAX_VELOCITY) {
                    launchState = LaunchState.LAUNCH;
                }
                break;

            case SPIN_UP_LOW: //if low launchmode is selected and shotRequested is true, check that the flywheel is in the correct velocity range (1075 - 1175 rpm)
                launcher.setVelocity(LAUNCHER_LOW_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_LOW_MIN_VELOCITY && launcher.getVelocity() < LAUNCHER_LOW_MAX_VELOCITY) {
                    if (redLed != null && greenLed != null) {
                        redLed.off();
                        greenLed.on();
                    }
                    launchState = LaunchState.LAUNCH;

                }
                break;
            case SPIN_UP_HIGH: //if high launchmode is selected and shotRequested is true, check that the flywheel is in the correct velocity range (1900 - 2000 rpm)
                launcher.setVelocity(LAUNCHER_HIGH_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_HIGH_MIN_VELOCITY && launcher.getVelocity() < LAUNCHER_HIGH_MAX_VELOCITY) {
                    if (redLed != null && greenLed != null) {
                        redLed.off();
                        greenLed.on();
                    }
                    launchState = LaunchState.LAUNCH;
                }
            case LAUNCH: //when shotRequested, start the feeder servos to init launch
                leftFeeder.setPower(FULL_SPEED);
                rightFeeder.setPower(FULL_SPEED);
                feederTimer.reset();
                launchState = LaunchState.LAUNCHING;
                break;
            case LAUNCHING: //wait until feedTimer surpasses FEED_TIME_SECONDS, then stop the feeder servos.
                if (feederTimer.seconds() > FEED_TIME_SECONDS) {
                    launchState = LaunchState.IDLE;
                    leftFeeder.setPower(STOP_SPEED);
                    rightFeeder.setPower(STOP_SPEED);
                }
                if (redLed != null && greenLed != null) {
                    redLed.off();
                    greenLed.on();
                }
                break;
        }
    }
}

