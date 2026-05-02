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
d
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class VisualPathMovementController extends LinearOpMode {
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    private IMU imu;

    private VisualPathMouseReader mouse;
    private VisualPathVirtualField field;
    
    private double targetX;
    private double targetY;
    private double targetHeading;

    private double maxSpeed = 1.0;
    private double tolerance = 10.0;

    public VisualPathMovementController(DcMotor frontLeftDrive, DcMotor frontRightDrive, DcMotor backLeftDrive, DcMotor backRightDrive, IMU imu) {
        this.frontLeftDrive = frontLeftDrive;
        this.frontRightDrive = frontRightDrive;
        this.backLeftDrive = backLeftDrive;
        this.backRightDrive = backRightDrive;
        this.imu = imu;
        this.targetX = 0;
        this.targetY = 0;
        this.targetHeading = 0;
    }

    public void runDrivetrain() {
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
    }

    public void stopDrivetrain() {
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);
    }

    public void runToPosition(double x, double y, double heading) {
        this.targetX = x;
        this.targetY = y;
        this.targetHeading = heading;

        while (Math.abs(field.getRobotPosition()[0] - targetX) > tolerance || Math.abs(field.getRobotPosition()[1] - targetY) > tolerance || Math.abs(orientation.firstAngle - targetHeading) > tolerance) {
            runDrivetrain();
        }

        stopDrivetrain();
    }

    public void cleanup() {
        stopDrivetrain();
        mouse.stop();
    }

    public double clampSpeed(double speed, double bound) {return Math.max(-bound, Math.min(bound, speed));}
}