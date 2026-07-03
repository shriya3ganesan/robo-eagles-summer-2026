package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Flywheel PIDF Control", group = "Flywheel")
public class FlywheelPIDFControl extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 28.0;
    private static final double MAX_TARGET_RPM = 3000.0;
    // Telemetry reports stable once RPM error is within this tolerance.
    private static final double STABLE_ERROR_RPM = 75.0;
    // Smaller values smooth acceleration more; larger values follow target changes faster.
    private static final double TARGET_SMOOTHING = 0.10;

    // Replace these with the final values found using FF_Tuner and FFP_Tuner.
    private static final double KF = 0.0;
    private static final double KP = 0.0;

    private DcMotorEx flywheel;

    // targetRpm is the driver command; smoothedTargetRpm is the ramped value used by control.
    private double targetRpm = 0.0;
    private double smoothedTargetRpm = 0.0;

    private boolean previousA;
    private boolean previousB;
    private boolean previousY;
    private boolean previousX;

    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        updateTargetRpm();

        // Smooth target changes for gentler acceleration and more stable control.
        smoothedTargetRpm += (targetRpm - smoothedTargetRpm) * TARGET_SMOOTHING;

        double currentRpm = ticksPerSecondToRpm(flywheel.getVelocity());
        double error = smoothedTargetRpm - currentRpm;

        // Feedforward estimates the needed power; proportional feedback corrects RPM error.
        double feedforward = KF * smoothedTargetRpm;
        double proportional = KP * error;
        double motorPower = Range.clip(feedforward + proportional, -1.0, 1.0);

        flywheel.setPower(motorPower);

        boolean stable = Math.abs(error) < STABLE_ERROR_RPM;

        telemetry.addData("Target RPM", "%.1f", targetRpm);
        telemetry.addData("Current RPM", "%.1f", currentRpm);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addData("kF", "%.6f", KF);
        telemetry.addData("kP", "%.6f", KP);
        telemetry.addData("Motor Power", "%.3f", motorPower);
        telemetry.addData("Stable", stable ? "YES" : "NO");
        telemetry.addData("Controls", "Right stick live target, A/B/Y RPM, X stop");
    }

    private void updateTargetRpm() {
        // Push right stick up to increase target RPM live.
        if (Math.abs(gamepad1.right_stick_y) > 0.05) {
            targetRpm = -gamepad1.right_stick_y * MAX_TARGET_RPM;
        }

        // Preset buttons are quicker and more repeatable than joystick input.
        if (gamepad1.a && !previousA) {
            targetRpm = 1000.0;
        }
        if (gamepad1.b && !previousB) {
            targetRpm = 2000.0;
        }
        if (gamepad1.y && !previousY) {
            targetRpm = 3000.0;
        }
        if (gamepad1.x && !previousX) {
            targetRpm = 0.0;
        }

        targetRpm = Range.clip(targetRpm, -MAX_TARGET_RPM, MAX_TARGET_RPM);

        previousA = gamepad1.a;
        previousB = gamepad1.b;
        previousY = gamepad1.y;
        previousX = gamepad1.x;
    }

    private double ticksPerSecondToRpm(double ticksPerSecond) {
        // RPM = encoder ticks/sec * 60 / ticks per revolution.
        return ticksPerSecond * 60.0 / TICKS_PER_REVOLUTION;
    }
}
