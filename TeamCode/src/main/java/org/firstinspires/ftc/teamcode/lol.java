package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "My First TeleOp")
public class lol extends LinearOpMode {

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Ready");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Robot", "Running");
            telemetry.update();
        }
    }
}