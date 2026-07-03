package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class M2_TankDrive extends OpMode {
    private static final int HEX_TARGET_POSITION = 100; // Example target position for the hex motors
    private static final double HEX_POWER = 0.5;
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

        expandLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        expandRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        expandLeft.setTargetPosition(0);
        expandRight.setTargetPosition(0);
        expandLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        expandRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        double y = gamepad2.left_stick_y;
        double x = gamepad2.left_stick_x;

        double backLeftPower = y + x;
        double backRightPower = y - x;


        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        if (gamepad1.triangle) {
            moveHexMotorsTo(HEX_TARGET_POSITION);
        } else if (gamepad1.circle) {
            moveHexMotorsTo(0);
        }

        if (gamepad1.cross) {
            intakeOn = true;
        } else if (gamepad1.square) {
            intakeOn = false;
        }
        intake.setPower(intakeOn ? INTAKE_POWER : 0);

        telemetry.addData("Status", "Running");
        telemetry.addData("Left Stick X", x);
        telemetry.addData("Left Stick Y", y);
        telemetry.addData("Hex Left Position", expandLeft.getCurrentPosition());
        telemetry.addData("Hex Right Position", expandRight.getCurrentPosition());
        telemetry.addData("Hex Target", expandLeft.getTargetPosition());
        telemetry.addData("Intake On", intakeOn);
    }

    private void moveHexMotorsTo(int targetPosition) {
        expandLeft.setTargetPosition(targetPosition);
        expandRight.setTargetPosition(targetPosition);
        expandLeft.setPower(HEX_POWER);
        expandRight.setPower(HEX_POWER);
    }
}
