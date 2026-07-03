package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "X Drive Train")
public class XDriveTrain extends OpMode {
    /*
     * An X-drive uses four omni wheels mounted at angles.
     *
     * Controls in this OpMode:
     * - Left stick Y: drive forward and backward
     * - Left stick X: strafe left and right
     * - Right stick X: rotate left and right
     */

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    @Override
    public void init() {
        mapHardware();
        configureDriveMotors();

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        double drive = -gamepad1.left_stick_y;
        double strafe = gamepad1.left_stick_x;
        double turn = gamepad1.right_stick_x;

        double frontLeftPower = drive + strafe + turn;
        double frontRightPower = drive - strafe - turn;
        double backLeftPower = drive - strafe + turn;
        double backRightPower = drive + strafe - turn;

        double maxPower = getMaxMagnitude(
                frontLeftPower,
                frontRightPower,
                backLeftPower,
                backRightPower
        );

        if (maxPower > 1.0) {
            frontLeftPower /= maxPower;
            frontRightPower /= maxPower;
            backLeftPower /= maxPower;
            backRightPower /= maxPower;
        }

        setDrivePower(frontLeftPower, frontRightPower, backLeftPower, backRightPower);
        showTelemetry(drive, strafe, turn, frontLeftPower, frontRightPower, backLeftPower, backRightPower);
    }

    private void mapHardware() {
        // These names must match the Driver Station robot configuration.
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
    }

    private void configureDriveMotors() {
        // Reverse the right side so positive power means forward on both sides.
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.REVERSE);

        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private double getMaxMagnitude(double frontLeftPower, double frontRightPower,
                                   double backLeftPower, double backRightPower) {
        double maxPower = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower));
        maxPower = Math.max(maxPower, Math.abs(backLeftPower));
        return Math.max(maxPower, Math.abs(backRightPower));
    }

    private void setDrivePower(double frontLeftPower, double frontRightPower,
                               double backLeftPower, double backRightPower) {
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }

    private void showTelemetry(double drive, double strafe, double turn,
                               double frontLeftPower, double frontRightPower,
                               double backLeftPower, double backRightPower) {
        telemetry.addData("Status", "Running");
        telemetry.addData("Drive", "%.2f", drive);
        telemetry.addData("Strafe", "%.2f", strafe);
        telemetry.addData("Turn", "%.2f", turn);
        telemetry.addData("Front Left Power", "%.2f", frontLeftPower);
        telemetry.addData("Front Right Power", "%.2f", frontRightPower);
        telemetry.addData("Back Left Power", "%.2f", backLeftPower);
        telemetry.addData("Back Right Power", "%.2f", backRightPower);
    }
}
