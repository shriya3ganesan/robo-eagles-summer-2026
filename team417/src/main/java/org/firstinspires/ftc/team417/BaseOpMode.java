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
import org.firstinspires.ftc.team417.roadrunner.RobotAction;

/**
 * This class contains all of the base logic that is shared between all of the TeleOp and
 * Autonomous logic. All TeleOp and Autonomous classes should derive from this class.
 */
abstract public class BaseOpMode extends LinearOpMode {

    public DcMotorEx launcher = null;
    public CRServo leftFeeder = null;
    public CRServo rightFeeder = null;
    public static final double STOP_SPEED = 0.0; //We send this power to the servos when we want them to stop.
    public static double FEED_TIME_SECONDS = 0.15; //The feeder servos run this long when a shot is requested.

    public static double FULL_SPEED = 1.0; //We send this power to the servos when we want them to feed an artifact to the launcher
    public static double LAUNCHER_HIGH_MAX_VELOCITY = 2000; //high target velocity + 50 (will need adjusting)
    public static double LAUNCHER_HIGH_TARGET_VELOCITY = 1950;
    public static double LAUNCHER_HIGH_MIN_VELOCITY = 1900;

    public static double LAUNCHER_LOW_MAX_VELOCITY = 1175; //low target velocity + 50 (will need adjusting)
    public static double LAUNCHER_LOW_TARGET_VELOCITY = 1125;
    public static double LAUNCHER_LOW_MIN_VELOCITY = 1075;

    public static double LAUNCHER_SORTER_MAX_VELOCITY = 550; //sorter target velocity + 50 (will need adjusting)
    public static double LAUNCHER_SORTER_TARGET_VELOCITY = 500;
    public static double LAUNCHER_SORTER_MIN_VELOCITY = 450;

    public double ROBOT_WIDTH = 0;

    public double ROBOT_LENGTH = 0;
    public static double LAUNCHER_REV_MAX_VELOCITY = -300;
    public static double LAUNCHER_REV_TARGET_VELOCITY = -250;
    public static double LAUNCHER_REV_MIN_VELOCITY = -200;
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
        SPIN_UP_REV,
        LAUNCH,
        LAUNCHING,
    }

    public LaunchState launchState;
    public void initHardware() {
        launchState = LaunchState.IDLE;

        /*
        * Initialize the hardware variables. Note that the strings used here as parameters
        * to 'get' must correspond to the names assigned during the robot configuration
        * step.
        */
        // leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        // rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        launcher = hardwareMap.get(DcMotorEx.class, "motLauncher");
        leftFeeder = hardwareMap.get(CRServo.class, "servoBLaunchFeed");
        rightFeeder = hardwareMap.get(CRServo.class, "servoFLaunchFeed");

        // Reversed direction of launcher for DevBot because motor is on the other side (compared to FastBot)
        if (MecanumDrive.isDevBot) {

            launcher.setDirection(DcMotorEx.Direction.REVERSE);
            ROBOT_LENGTH = 18.5;
            ROBOT_WIDTH = 18;
            redLed = null;
            greenLed = null;

        }
        else if(MecanumDrive.isFastBot) {
            ROBOT_WIDTH = 16;
            ROBOT_LENGTH = 17;
            //redLed = hardwareMap.get(LED.class, "redLed");   Uncomment one we get LEDs
            //greenLed = hardwareMap.get(LED.class, "greenLed");
            //redLed.on();
            //greenLed.off();
        }

        /*
         * To drive forward, most robots need the motor on one side to be reversed,
         * because the axles point in opposite directions. Pushing the left stick forward
         * MUST make robot go forward. So adjust these two lines based on your first test drive.
         * Note: The settings here assume direct drive on left and right wheels. Gear
         * Reduction or 90 Deg drives may require direction flips
         */
        // leftDrive.setDirection(DcMotor.Direction.REVERSE);
        // rightDrive.setDirection(DcMotor.Direction.FORWARD);

        /*
         * Here we set our launcher to the RUN_USING_ENCODER runmode.
         * If you notice that you have no control over the velocity of the motor, it just jumps
         * right to a number much higher than your set point, make sure that your encoders are plugged
         * into the port right beside the motor itself. And that the motors polarity is consistent
         * through any wiring.
         */
        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        /*
         * Setting zeroPowerBehavior to BRAKE enables a "brake mode". This causes the motor to
         * slow down much faster when it is coasting. This creates a much more controllable
         * drivetrain. As the robot stops much quicker.
         */
        // leftDrive.setZeroPowerBehavior(BRAKE);
        // rightDrive.setZeroPowerBehavior(BRAKE);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /*
         * set Feeders to an initial value to initialize the servo controller
         */
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);

        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(300, 0, 0, 10));

        /*
         * Much like our drivetrain motors, we set the left feeder servo to reverse so that they
         * both work to feed the ball into the robot.
         */
        leftFeeder.setDirection(DcMotorSimple.Direction.REVERSE);

        /*
         * Tell the driver that initialization is complete.
         */
        telemetry.addData("Status", "Initialized");
    }
    class LaunchAction extends RobotAction {
        public boolean run(double ElapsedTime) {
            leftFeeder.setPower(FULL_SPEED);
            rightFeeder.setPower(FULL_SPEED);
            if (ElapsedTime < 1) {
                leftFeeder.setPower(STOP_SPEED);
                rightFeeder.setPower(STOP_SPEED);
                return false;
            }
            else {
                return true;
            }
        }


    }
    class SpinUpAction extends RobotAction {
        public boolean run(double ElapsedTime) {
            launcher.setVelocity(LAUNCHER_LOW_TARGET_VELOCITY);
            if(ElapsedTime < 1) {
                return true;
            }
            else {
                return false;
            }
        }
    }

    public void launch(boolean shotRequested) {
        switch (launchState) {
            case IDLE:
                if (shotRequested) {
                    if (doHighLaunch) {
                        launchState = LaunchState.SPIN_UP_HIGH;
                    } else if (doSort) {
                        launchState = LaunchState.SPIN_UP_SORT;
                    } else if (doReverse) {
                        launchState = LaunchState.SPIN_UP_REV;
                    } else {
                        launchState = LaunchState.SPIN_UP_LOW;
                    }
                }
                break;

            case SPIN_UP_SORT:
                launcher.setVelocity(LAUNCHER_SORTER_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_SORTER_MIN_VELOCITY && launcher.getVelocity() < LAUNCHER_SORTER_MAX_VELOCITY) {
                    launchState = LaunchState.LAUNCH;
                }
                break;
            case SPIN_UP_REV:
                launcher.setVelocity(LAUNCHER_REV_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_REV_MIN_VELOCITY && launcher.getVelocity() < LAUNCHER_REV_MAX_VELOCITY) {
                    launchState = LaunchState.LAUNCH;
                }
                break;
            case SPIN_UP_LOW:
                launcher.setVelocity(LAUNCHER_LOW_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_LOW_MIN_VELOCITY && launcher.getVelocity() < LAUNCHER_LOW_MAX_VELOCITY) {
                    if (redLed != null && greenLed != null) {
                        redLed.off();
                        greenLed.on();
                    }
                    launchState = LaunchState.LAUNCH;

                }
                break;
            case SPIN_UP_HIGH:
                launcher.setVelocity(LAUNCHER_HIGH_TARGET_VELOCITY);
                if (launcher.getVelocity() > LAUNCHER_HIGH_MIN_VELOCITY && launcher.getVelocity() < LAUNCHER_HIGH_MAX_VELOCITY) {
                    if (redLed != null && greenLed != null) {
                        redLed.off();
                        greenLed.on();
                    }
                    launchState = LaunchState.LAUNCH;
                }
            case LAUNCH:
                leftFeeder.setPower(FULL_SPEED);
                rightFeeder.setPower(FULL_SPEED);
                feederTimer.reset();
                launchState = LaunchState.LAUNCHING;
                break;
            case LAUNCHING:
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

