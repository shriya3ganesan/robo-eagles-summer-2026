package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "Separate Expand Test", group = "TeleOp")
class TestCopy extends OpMode {
    private static final double DRIVE_BUTTON_POWER = 0.5;
    private static final double EXPAND_POWER = 0.5;
    private static final double INTAKE_POWER = 1.0;

    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor expandLeft;
    private DcMotor expandRight;
    private DcMotor intake;
    private boolean intakeOn;

    @Override
    public void init() {
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        expandLeft = hardwareMap.get(DcMotor.class, "expandLeft");
        expandRight = hardwareMap.get(DcMotor.class, "expandRight");
        intake = hardwareMap.get(DcMotor.class, "intake");

        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        expandLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        expandLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        expandRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        expandLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        expandRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        double y = -gamepad2.left_stick_y;
        double x = gamepad2.left_stick_x;

        double backLeftPower;
        double backRightPower;

        if (gamepad2.dpad_up) {
            backLeftPower = DRIVE_BUTTON_POWER;
            backRightPower = DRIVE_BUTTON_POWER;
        } else if (gamepad2.dpad_down) {
            backLeftPower = -DRIVE_BUTTON_POWER;
            backRightPower = -DRIVE_BUTTON_POWER;
        } else {
            backLeftPower = y + x;
            backRightPower = y - x;
        }

        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        expandLeft.setPower(getMotorPower(gamepad1.dpad_up, gamepad1.dpad_down));
        expandRight.setPower(getMotorPower(gamepad1.triangle, gamepad1.circle));

        if (gamepad1.cross) {
            intakeOn = true;
        } else if (gamepad1.square) {
            intakeOn = false;
        }
        intake.setPower(intakeOn ? INTAKE_POWER : 0);

        telemetry.addData("Status", "Running");
        telemetry.addData("Drive Left Power", backLeftPower);
        telemetry.addData("Drive Right Power", backRightPower);
        telemetry.addData("Left Expand Power", expandLeft.getPower());
        telemetry.addData("Right Expand Power", expandRight.getPower());
        telemetry.addData("Left Expand Position", expandLeft.getCurrentPosition());
        telemetry.addData("Right Expand Position", expandRight.getCurrentPosition());
        telemetry.addData("Intake On", intakeOn);
    }

    private double getMotorPower(boolean forwardButton, boolean backwardButton) {
        if (forwardButton) {
            return EXPAND_POWER;
        } else if (backwardButton) {
            return -EXPAND_POWER;
        }

        return 0;
    }
}
