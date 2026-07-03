package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "FF Tuner", group = "Flywheel")
public class FF_Tuner extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 28.0;
    private static final double KF_STEP = 0.00001;

    private DcMotorEx flywheel;

    // kF is a simple feedforward gain: motor power = kF * target RPM.
    private double kF = 0.0;
    private double targetRpm = 2000.0;

    // Previous button states turn held buttons into single-step changes.
    private boolean previousDpadUp;
    private boolean previousDpadDown;
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

        // Feedforward only: P, I, and D are intentionally not used.
        // Increase kF until current RPM is close to target RPM without correction.
        double motorPower = Range.clip(kF * targetRpm, -1.0, 1.0);
        flywheel.setPower(motorPower);

        double currentRpm = ticksPerSecondToRpm(flywheel.getVelocity());
        double error = targetRpm - currentRpm;

        telemetry.addData("Target RPM", "%.1f", targetRpm);
        telemetry.addData("Current RPM", "%.1f", currentRpm);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addData("kF", "%.6f", kF);
        telemetry.addData("Motor Power", "%.3f", motorPower);
        telemetry.addData("Controls", "D-pad up/down kF, A/B/Y RPM, X stop");
    }

    private void updateControls() {
        // D-pad changes kF by a small amount so the shooter can be tuned live.
        if (gamepad1.dpad_up && !previousDpadUp) {
            kF += KF_STEP;
        }
        if (gamepad1.dpad_down && !previousDpadDown) {
            kF = Math.max(0.0, kF - KF_STEP);
        }

        // Presets let you check whether the same kF works at different shooter speeds.
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
