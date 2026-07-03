package org.firstinspires.ftc.teamcode.flywheel;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Kv Tuner", group = "Flywheel")
public class KvTuner extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 28.0;
    private static final double KV_STEP = 0.00001;

    // Set kS to the value found with KsTuner before tuning kV.
    private double kS = 0.05;
    // kV converts a desired RPM into the approximate motor power needed to hold that speed.
    private double kV = 0.0;
    private double targetRpm = 2000.0;

    private DcMotorEx flywheel;
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

        // kP is disabled here. Tune kV until actual RPM is close at each preset.
        double power = 0.0;
        if (targetRpm != 0.0) {
            // Feedforward model: power = static friction term + velocity term.
            power = (kS * Math.signum(targetRpm)) + (kV * targetRpm);
        }
        power = Range.clip(power, -1.0, 1.0);
        flywheel.setPower(power);

        double currentRpm = ticksPerSecondToRpm(flywheel.getVelocity());
        double error = targetRpm - currentRpm;

        telemetry.addData("kS", "%.4f", kS);
        telemetry.addData("kV", "%.6f", kV);
        telemetry.addData("Target RPM", "%.1f", targetRpm);
        telemetry.addData("Actual RPM", "%.1f", currentRpm);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addData("Motor Power", "%.3f", power);
        telemetry.addData("Controls", "D-pad kV, A/B/Y presets, X stop");
    }

    private void updateControls() {
        // D-pad adjusts kV in small steps so you can watch RPM/error settle on telemetry.
        if (gamepad1.dpad_up && !previousDpadUp) {
            kV += KV_STEP;
        }
        if (gamepad1.dpad_down && !previousDpadDown) {
            kV = Math.max(0.0, kV - KV_STEP);
        }

        // Presets make it easy to verify that one kV works across low, medium, and high RPM.
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
