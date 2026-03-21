package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp
public class Muchen extends OpMode {
    DcMotorEx motor;
    @Override
    public void init() {
        int i = 1;
        int fah = 2;

        int sdsd = i + fah;

        telemetry.addData("yo", sdsd);

        motor = hardwareMap.get(DcMotorEx.class, "motor");
    }

    @Override
    public void loop() {

        int wierdthing = 60845;
        int anotherthingy = 409095;

        int bothwierdthings = wierdthing + anotherthingy;

        telemetry.addData("hi this is a thing", bothwierdthings);

        motor.setPower(bothwierdthings/1000000);
    }

}
