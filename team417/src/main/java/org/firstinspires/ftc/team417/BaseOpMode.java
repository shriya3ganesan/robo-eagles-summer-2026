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

