package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Nora TeleOp")
public class Nora extends OpMode {
    DcMotorEx motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class, "motor");
    }

    @Override
    public void loop() {
        int nora = 8;
        int shay = 12;
        int seamus = shay + nora;

        telemetry.addData("Number", seamus);


        motor.setPower(seamus * gamepad1.left_stick_y);
    }
}
