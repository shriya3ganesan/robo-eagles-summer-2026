package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name="Teleop_V1")
public class Teleop_V1 extends OpMode {
    DcMotorEx frontLeftDrive;
    DcMotorEx backLeftDrive;
    DcMotorEx frontRightDrive;
    DcMotorEx backRightDrive;

    @Override
    public void init() {
        frontLeftDrive = hardwareMap.get(DcMotorEx.class, "frontLeftDrive");
        backLeftDrive = hardwareMap.get(DcMotorEx.class, "backLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotorEx.class, "frontRightDrive");
        backRightDrive = hardwareMap.get(DcMotorEx.class, "backRightDrive");
        frontLeftDrive.setDirection(DcMotorEx.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotorEx.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotorEx.Direction.FORWARD);
        backRightDrive.setDirection(DcMotorEx.Direction.FORWARD);
    }

    @Override
    public void loop() {
        double Power = gamepad1.right_stick_y * 0.25;

        frontLeftDrive.setPower(Power);
        backLeftDrive.setPower(Power);
        frontRightDrive.setPower(Power);
        backRightDrive.setPower(Power);

        telemetry.addData("Current Power", Power);
    }
}
