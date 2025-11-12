package org.firstinspires.ftc.team417;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;
import org.firstinspires.ftc.team417.roadrunner.RobotAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains all of the base logic that is shared between all of the TeleOp and
 * Autonomous logic. All TeleOp and Autonomous classes should derive from this class.
 */
abstract public class BaseOpMode extends LinearOpMode {

    //fastbot hardware variables
    public DcMotorEx launcher = null;

    public CRServo leftFeeder = null;
    public CRServo rightFeeder = null;

    //fastbot constants
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

    public double ROBOT_WIDTH = 0;
    public double ROBOT_LENGTH = 0;

    public static double LAUNCHER_REV_TARGET_VELOCITY = -250;


    public LED redLed;
    public LED greenLed;

    ElapsedTime feederTimer = new ElapsedTime();

    public String CURRENT_LAUNCHSTATE = "IDLE";
    public enum LaunchState {
        IDLE,
        HIGH,
        LOW,
        SORT,
        LAUNCH,
        LAUNCHING,
    }

    public LaunchState launchState;

    /*---------------------------------------------------------------*/
    //slowbot hardware

    public Servo drum = null;

    //slowbot constants

    final double intakeslot0 = -1;
    final double intakeslot1 = -0.2;
    final double intakeslot2 = 0.6;
    final double launcherslot0 = 0.2;
    final double launcherslot1 = 1;
    final double launcherslot2 = -0.6;

    int [] intakePositions = {0, 1, 2};

    ArrayList<Double> blah;
    final List<Double> INTAKE_POSITIONS
            = new ArrayList<>(Arrays.asList(-1.0, -0.2, 0.6));
    int [] launcherPositions = {0, 1, 2};
    //double intakeSpeed = gamepad2.left_stick_x;
    double Multiply = 0; //need to change, placeholder
    public void initHardware() {

        // Reversed direction of launcher for DevBot because motor is on the other side (compared to FastBot)


            //add slowbot initialization code here
            drum = hardwareMap.get(Servo.class, "drum");
            //launcher = hardwareMap.get(DcMotorEx.class, "motLauncher");




        //  Tell the driver that initialization is complete.
        telemetry.addData("Status", "Initialized");
    }



    public void drumLogic () {

    }
}

