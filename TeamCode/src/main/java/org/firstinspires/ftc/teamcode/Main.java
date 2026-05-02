package org.firstinspires.ftc.teamcode;

import android.app.Activity;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

@TeleOp(name="Main", group="VisualPath")
public class Main extends LinearOpMode {
    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor backLeftDrive;
    private DcMotor backRightDrive;

    private IMU imu;

    private VisualPathMouseReader mouse;
    private VisualPathVirtualField field;
    private VisualPathMovementController movementController;

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

        VisualPathMovementController movementController = new VisualPathMovementController(frontLeftDrive, frontRightDrive, backLeftDrive, backRightDrive, mouse, field);

        waitForStart();

        movementController.runToPosition(0, 10000, 0);
        movementController.runToPosition(10000, 0, 0);
        movementController.runToPosition(10000, 10000, 0);
        movementController.runToPosition(0, 0, 0);

        movementController.cleanup();
    }
}