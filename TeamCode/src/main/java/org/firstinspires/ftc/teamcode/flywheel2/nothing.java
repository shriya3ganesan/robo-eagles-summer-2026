package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "nothing", group = "Flywheel")
public class nothing extends OpMode {
    private static final double TICKS_PER_REVOLUTION = 28.0;

    // Change this number to the RPM you want the flywheel to hold.
    private static final double TARGET_RPM = 4000.0;

    private DcMotorEx flywheel;

    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        flywheel.setVelocity(rpmToTicksPerSecond(TARGET_RPM));

        double currentRpm = ticksPerSecondToRpm(flywheel.getVelocity());
        double error = TARGET_RPM - currentRpm;

        telemetry.addData("Target RPM", "%.1f", TARGET_RPM);
        telemetry.addData("Current RPM", "%.1f", currentRpm);
        telemetry.addData("Error", "%.1f", error);
        telemetry.addData("Target ticks/sec", "%.1f", rpmToTicksPerSecond(TARGET_RPM));
    }

    private double ticksPerSecondToRpm(double ticksPerSecond) {
        // RPM = encoder ticks/sec * 60 / ticks per revolution.
        return ticksPerSecond * 60.0 / TICKS_PER_REVOLUTION;
    }

    private double rpmToTicksPerSecond(double rpm) {
        // Encoder ticks/sec = RPM * ticks per revolution / 60.
        return rpm * TICKS_PER_REVOLUTION / 60.0;
    }
}
