package org.firstinspires.ftc.teamcode.flywheel;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Ks Tuner", group = "Flywheel")
public class KsTuner extends OpMode {
    // GoBILDA 5202/5203 bare motor encoders commonly report 28 ticks per motor shaft revolution.
    private static final double TICKS_PER_REVOLUTION = 28.0;
    // The RPM value is only used for sign/error display here; kS does not scale with RPM.
    private static final double TARGET_RPM = 2000.0;
    private static final double KS_STEP = 0.005;

    private DcMotorEx flywheel;
    // kS is the minimum power needed to overcome static friction and start/keep motion.
    private double kS = 0.0;
    // Previous button states let each press change kS once instead of every loop while held.
    private boolean previousDpadUp;
    private boolean previousDpadDown;

    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        updateKsFromGamepad();

        // kV and kP are disabled for this test. Only static feedforward is used.
        // Increase kS until the flywheel just begins spinning reliably.
        double power = Range.clip(kS * Math.signum(TARGET_RPM), -1.0, 1.0);
        flywheel.setPower(power);

        // DcMotorEx.getVelocity() returns encoder ticks per second, so convert it for readable telemetry.
        double currentRpm = ticksPerSecondToRpm(flywheel.getVelocity());
        double error = TARGET_RPM - currentRpm;

        telemetry.addData("Target RPM", "%.1f", TARGET_RPM);
        telemetry.addData("kS", "%.4f", kS);
        telemetry.addData("Motor Power", "%.3f", power);
        telemetry.addData("RPM", "%.1f", currentRpm);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addData("Controls", "D-pad up/down adjusts kS");
    }

    private void updateKsFromGamepad() {
        if (gamepad1.dpad_up && !previousDpadUp) {
            kS += KS_STEP;
        }
        if (gamepad1.dpad_down && !previousDpadDown) {
            kS = Math.max(0.0, kS - KS_STEP);
        }

        previousDpadUp = gamepad1.dpad_up;
        previousDpadDown = gamepad1.dpad_down;
    }

    private double ticksPerSecondToRpm(double ticksPerSecond) {
        // RPM = encoder ticks/sec * 60 / ticks per revolution.
        return ticksPerSecond * 60.0 / TICKS_PER_REVOLUTION;
    }
}
