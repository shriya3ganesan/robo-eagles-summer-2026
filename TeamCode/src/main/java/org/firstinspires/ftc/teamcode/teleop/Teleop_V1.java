package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.Telemetry;

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
        frontLeftDrive.setDirection(DcMotorEx.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorEx.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotorEx.Direction.FORWARD);
        backRightDrive.setDirection(DcMotorEx.Direction.FORWARD);
    }

    @Override
    public void loop() {
        double axial = gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        frontLeftDrive.setPower((axial + lateral + yaw) * 0.5);
        backLeftDrive.setPower((axial - lateral - yaw) * 0.50);
        frontRightDrive.setPower((axial - lateral + yaw) * 0.5);
        backRightDrive.setPower((axial + lateral - yaw) * 0.5);

        telemetry.addData("Current axial", axial);
        telemetry.addData("Current lateral", lateral);
        telemetry.addData("Current yaw", yaw);
        telemetry.addData("Average Power", (frontLeftDrive.getPower()
                + frontLeftDrive.getPower()
                + backLeftDrive.getPower()
                + backRightDrive.getPower()) / 4);
    }

}

