package org.firstinspires.ftc.teamcode;

import androidx.annotation.NonNull;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name="adam k. opMode")
public class  adam extends OpMode {
DcMotorEx bread_motor;

    @Override
   public void init() {
    bread_motor = hardwareMap.get(DcMotorEx.class,"motor");
    }

    @Override
    public void loop() {
        int shayshay = 9;
        int mochi = 3;
        int wise_tree = shayshay + mochi;

        telemetry.addData("number", wise_tree);

        bread_motor.setPower(wise_tree*gamepad1.left_stick_y);
    }

}

