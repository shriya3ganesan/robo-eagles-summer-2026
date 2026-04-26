package org.firstinspires.ftc.teamcode;

import android.app.Activity;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.OrientationSensor;

import java.util.HashMap;

import android.hardware.usb.UsbConstants;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name="Force Bearing", group="VisualPath")
public class ForceBearing extends LinearOpMode {
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    private IMU imu;

    @Override
    public void runOpMode() {
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeftDrive");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRightDrive");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRightDrive");

        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
        imu.resetYaw();

        VisualPathMouseReader mouse = new VisualPathMouseReader((Activity) hardwareMap.appContext, 420);
        VisualPathVirtualField field = new VisualPathVirtualField(new double[]{20, 20}, new double[]{0, 0});

        if (!mouse.start()) {
            telemetry.addLine("Mouse not found");
            telemetry.update();
            waitForStart();
            return;
        }

        telemetry.addLine("Using Mouse: " + mouse.getDeviceName());
        telemetry.update();

        double targetX = 0;
        double targetY = 0;

        double maxSpeed = 0.2;

        waitForStart();

        while (opModeIsActive()) {
            Orientation orientation = imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

            targetX += gamepad1.left_stick_x * 500;
            targetY -= gamepad1.left_stick_y * 500;

            int[] delta = mouse.consumeDelta();

            double dy = delta[0];
            double dx = delta[1];

            field.shiftRobotPosition(dx, dy);

            double currentX = field.getRobotPosition()[0];
            double currentY = field.getRobotPosition()[1];

            double xPower = clampSpeed((currentX - targetX) * -0.001, maxSpeed);
            double yPower = clampSpeed((currentY - targetY) * -0.001, maxSpeed);
            double rPower = clampSpeed(orientation.firstAngle * 0.1, maxSpeed);

            frontLeftDrive.setPower(yPower + xPower + rPower);
            frontRightDrive.setPower(yPower - xPower - rPower);
            backLeftDrive.setPower(yPower - xPower + rPower);
            backRightDrive.setPower(yPower + xPower - rPower);

            telemetry.addLine("xPower:" + xPower);
            telemetry.addLine("yPower:" + yPower);
            telemetry.addLine("rPower:" + rPower);
            telemetry.addLine("Yaw:" + orientation.firstAngle);
            telemetry.addLine("xPos:" + currentX);
            telemetry.addLine("yPos:" + currentY);
            telemetry.addLine("xTarget:" + targetX);
            telemetry.addLine("yTarget:" + targetY);
            telemetry.update();
        }

        mouse.stop();
    }

    public double clampSpeed(double speed, double bound) {return Math.max(-bound, Math.min(bound, speed));}
}