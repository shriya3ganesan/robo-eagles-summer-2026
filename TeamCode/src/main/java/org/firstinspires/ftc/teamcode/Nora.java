package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Nora TeleOp")
public class Nora extends OpMode {
    @Override
    public void init() {

    }

    @Override
    public void loop() {

        int nora = 9;
        int shay = 12;
        int seamus = shay + nora;

        telemetry.addData("Number", seamus);
    }
}

