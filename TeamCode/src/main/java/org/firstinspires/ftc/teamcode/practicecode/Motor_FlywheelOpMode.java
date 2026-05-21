package org.firstinspires.ftc.teamcode.practicecode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Practice: Flywheel", group = "Practice")
public class Motor_FlywheelOpMode extends LinearOpMode {

    private static final double FLYWHEEL_SPEED = 1.0;

    @Override
    public void runOpMode() {
        Motor_FlywheelController motor = new Motor_FlywheelController(hardwareMap, "flywheel");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        boolean flywheelOn = false;
        boolean lastA = false;

        while (opModeIsActive()) {
            boolean currentA = gamepad1.a;
            if (currentA && !lastA) {
                flywheelOn = !flywheelOn;
            }
            lastA = currentA;

            motor.setPower(flywheelOn ? FLYWHEEL_SPEED : 0);

            telemetry.addData("Flywheel", flywheelOn ? "ON" : "OFF");
            telemetry.addData("Motor Power", "%.2f", motor.getPower());
            telemetry.update();
        }

        motor.stop();
    }
}
