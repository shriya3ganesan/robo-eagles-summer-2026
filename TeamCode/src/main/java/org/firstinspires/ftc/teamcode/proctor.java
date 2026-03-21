package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name="Proctor 0pMode")
public class proctor extends OpMode {
    DcMotorEx pen;
    @Override
    public void init() {

        pen = hardwareMap.get(DcMotorEx.class, "motor");

    }


    @Override
    public void loop() {

        int sketchy_van_guy = 10;
        int explosive_barcode_scanner = 55;

        int pop_corn = sketchy_van_guy + explosive_barcode_scanner;

        telemetry.addData("Number",pop_corn);

        pen.setPower(pop_corn/65);
    }
}
