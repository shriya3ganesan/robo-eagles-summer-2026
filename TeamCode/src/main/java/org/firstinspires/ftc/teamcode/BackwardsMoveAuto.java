package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="BackMove")
@Disabled
public class BackwardsMoveAuto extends LinearOpMode {

    @Override
    public void runOpMode() {

        DcMotor FL = hardwareMap.get(DcMotor.class, "FL"); // local hardware mapping
        DcMotor FR = hardwareMap.get(DcMotor.class, "FR");
        DcMotor BL = hardwareMap.get(DcMotor.class, "BL");
        DcMotor BR = hardwareMap.get(DcMotor.class, "BR");

        FL.setDirection(DcMotor.Direction.FORWARD); //so I don't have to think about
        BL.setDirection(DcMotor.Direction.FORWARD); //inverting later
        FR.setDirection(DcMotor.Direction.REVERSE); //should generally do whenever motors
        BR.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {


            FL.setPower(1); //tells the motors how fast to go
            BL.setPower(-1);
            FR.setPower(-1);
            BR.setPower(1);
            sleep(500);
            FL.setPower(0); //tells the motors how fast to go
            BL.setPower(0);
            FR.setPower(0);
            BR.setPower(0);
            break;


        }
    }
}
