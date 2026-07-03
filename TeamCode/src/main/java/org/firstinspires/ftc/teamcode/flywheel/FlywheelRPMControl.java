package org.firstinspires.ftc.teamcode.flywheel;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Flywheel RPM Control", group = "Flywheel")
public class FlywheelRPMControl extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 28.0;
    private static final double MAX_TARGET_RPM = 3000.0;
    // If error is inside this band, telemetry reports the flywheel as ready/stable.
    private static final double STABLE_ERROR_RPM = 75.0;
    // Smaller values ramp target changes more slowly; larger values react faster.
    private static final double SMOOTHING_ALPHA = 0.10;

    // Replace these with the final values found from the tuner OpModes.
    private static final double KS = 0.05;
    private static final double KV = 0.0003;
    private static final double KP = 0.0001;

    private DcMotorEx flywheel;
    // targetRpm is the driver's requested speed; smoothedTargetRpm is what control follows.
    private double targetRpm;
    private double smoothedTargetRpm;
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

        // Smooth target changes so the flywheel accelerates without abrupt power jumps.
        smoothedTargetRpm += (targetRpm - smoothedTargetRpm) * SMOOTHING_ALPHA;

        double currentRpm = ticksPerSecondToRpm(flywheel.getVelocity());
        double error = smoothedTargetRpm - currentRpm;

        double output = 0.0;
        if (smoothedTargetRpm != 0.0) {
            // Final controller = static feedforward + velocity feedforward + proportional correction.
            output = (KS * Math.signum(smoothedTargetRpm)) + (KV * smoothedTargetRpm) + (KP * error);
        }

        double motorPower = Range.clip(output, -1.0, 1.0);
        flywheel.setPower(motorPower);

        boolean stable = Math.abs(error) < STABLE_ERROR_RPM;

        telemetry.addData("Target RPM", "%.1f", targetRpm);
        telemetry.addData("Smoothed Target RPM", "%.1f", smoothedTargetRpm);
        telemetry.addData("Current RPM", "%.1f", currentRpm);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addData("Motor Power", "%.3f", motorPower);
        telemetry.addData("Stable", stable ? "YES" : "NO");
        telemetry.addData("Controls", "Right stick live target, A/B/Y presets, X stop");
    }

    private void updateTargetRpm() {
        // Right stick controls target RPM live. Push up for higher RPM.
        double joystickTarget = -gamepad1.right_stick_y * MAX_TARGET_RPM;
        if (Math.abs(gamepad1.right_stick_y) > 0.05) {
            targetRpm = joystickTarget;
        }

        // Buttons provide repeatable target speeds for testing and match play.
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
