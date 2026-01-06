package org.firstinspires.ftc.teamcode.NonOpModes.depreciated;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="Move")

public class MoveAuto extends LinearOpMode {

    @Override
    public void runOpMode() {

        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront"); // local hardware mapping
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "rightBack");

        leftFront.setDirection(DcMotor.Direction.FORWARD); //so I don't have to think about
        leftBack.setDirection(DcMotor.Direction.FORWARD); //inverting later
        rightFront.setDirection(DcMotor.Direction.REVERSE); //should generally do whenever motors
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {


            leftFront.setPower(-1); //tells the motors how fast to go
            leftBack.setPower(1);
            rightFront.setPower(1);
            rightBack.setPower(-1);
            sleep(500);
            leftFront.setPower(0); //tells the motors how fast to go
            leftBack.setPower(0);
            rightFront.setPower(0);
            rightBack.setPower(0);
            break;


        }
    }
}
