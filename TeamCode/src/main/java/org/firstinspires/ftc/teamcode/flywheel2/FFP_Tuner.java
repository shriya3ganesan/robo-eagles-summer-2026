package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "FFP Tuner", group = "Flywheel")
public class FFP_Tuner extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 28.0;
    private static final double KF_STEP = 0.00001;
    private static final double KP_STEP = 0.00001;

    private DcMotorEx flywheel;

    // kF provides the baseline power for the requested RPM; kP corrects remaining error.
    private double kF = 0.000180;
    private double kP = 0.0;
    private double targetRpm = 2900.0;

    // Previous button states turn held controls into one tuning step per press.
    private boolean previousDpadUp;
    private boolean previousDpadDown;
    private boolean previousDpadLeft;
    private boolean previousDpadRight;
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
        updateControls();

        double currentRpm = ticksPerSecondToRpm(flywheel.getVelocity());
        double error = targetRpm - currentRpm;

        // The feedforward term predicts needed power, then proportional feedback nudges it.
        double feedforwardOutput = kF * targetRpm;
        double pidCorrection = kP * error;
        double finalOutput = Range.clip(feedforwardOutput + pidCorrection, -1.0, 1.0);

        flywheel.setPower(finalOutput);

        telemetry.addData("kF", "%.6f", kF);
        telemetry.addData("kP", "%.6f", kP);
        telemetry.addData("Target RPM", "%.1f", targetRpm);
        telemetry.addData("RPM", "%.1f", currentRpm);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addData("Feedforward Output", "%.3f", feedforwardOutput);
        telemetry.addData("PID Correction", "%.3f", pidCorrection);
        telemetry.addData("Final Output", "%.3f", finalOutput);
        telemetry.addData("Controls", "Up/down kP, left/right kF, A/B/Y RPM, X stop");
    }

    private void updateControls() {
        // Up/down tune proportional correction; left/right tune feedforward.
        if (gamepad1.dpad_up && !previousDpadUp) {
            kP += KP_STEP;
        }
        if (gamepad1.dpad_down && !previousDpadDown) {
            kP = Math.max(0.0, kP - KP_STEP);
        }
        if (gamepad1.dpad_right && !previousDpadRight) {
            kF += KF_STEP;
        }
        if (gamepad1.dpad_left && !previousDpadLeft) {
            kF = Math.max(0.0, kF - KF_STEP);
        }

        // Presets give repeatable targets while checking overshoot and steady-state error.
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

        previousDpadUp = gamepad1.dpad_up;
        previousDpadDown = gamepad1.dpad_down;
        previousDpadLeft = gamepad1.dpad_left;
        previousDpadRight = gamepad1.dpad_right;
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
