package org.firstinspires.ftc.teamcode.flywheel;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Kp Tuner", group = "Flywheel")
public class KpTuner extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 28.0;
    private static final double KP_STEP = 0.00001;

    // Set kS and kV to the values found with KsTuner and KvTuner before tuning kP.
    private double kS = 0.05;
    private double kV = 0.0003;
    // kP adds corrective power based on how far actual RPM is from target RPM.
    private double kP = 0.0;
    private double targetRpm = 2000.0;

    private DcMotorEx flywheel;
    // Previous button states turn held buttons into one adjustment or preset change.
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

        double currentRpm = ticksPerSecondToRpm(flywheel.getVelocity());
        double error = targetRpm - currentRpm;

        double feedforward = 0.0;
        if (targetRpm != 0.0) {
            // Feedforward should do most of the work; kP only cleans up remaining error.
            feedforward = (kS * Math.signum(targetRpm)) + (kV * targetRpm);
        }

        // Positive error means the flywheel is slow, so proportional output adds power.
        double pidOutput = kP * error;
        double totalOutput = Range.clip(feedforward + pidOutput, -1.0, 1.0);
        flywheel.setPower(totalOutput);

        telemetry.addData("Target RPM", "%.1f", targetRpm);
        telemetry.addData("kP", "%.6f", kP);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addData("RPM", "%.1f", currentRpm);
        telemetry.addData("Feedforward", "%.3f", feedforward);
        telemetry.addData("PID Output", "%.3f", pidOutput);
        telemetry.addData("Total Output", "%.3f", totalOutput);
        telemetry.addData("Controls", "D-pad kP, A/B/Y presets, X stop");
    }

    private void updateControls() {
        // Tune kP upward until response is quick, but not so high that RPM oscillates.
        if (gamepad1.dpad_up && !previousDpadUp) {
            kP += KP_STEP;
        }
        if (gamepad1.dpad_down && !previousDpadDown) {
            kP = Math.max(0.0, kP - KP_STEP);
        }

        // Preset speeds help test whether kP behaves well across the shooter range.
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
