package org.firstinspires.ftc.team28420;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.team28420.config.CameraConf;
import org.firstinspires.ftc.team28420.config.GamepadConf;
import org.firstinspires.ftc.team28420.config.ShooterConf;
import org.firstinspires.ftc.team28420.module.Actions;
import org.firstinspires.ftc.team28420.types.AprilTag;
import org.firstinspires.ftc.team28420.types.MovementParams;
import org.firstinspires.ftc.team28420.types.PolarVector;

@TeleOp(name = "BLUE MAIN", group = "New Actions")
public class BlueTeleOp extends LinearOpMode {
    private Actions act;
    private boolean dpadPressed = false;

    private void initialize() throws InterruptedException {
        act = new Actions(hardwareMap, telemetry);
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        act.init();
    }

    private void handleTargeting() {
        if (ShooterConf.TARGET_MOTIF == null) {
            act.setMotif();
        }

        telemetry.addData("scanned motif", ShooterConf.TARGET_MOTIF);
    }

    private void handleMovement() {
        if (gamepad1.right_trigger > 0.2) {
            act.move(act.getRatiosForApriltag(AprilTag.BLUE, -2, CameraConf.RANGE_TO_TAG));
        } else if (gamepad1.right_bumper) {
            act.move(act.getRatiosLookApriltag(AprilTag.BLUE, 0, CameraConf.RANGE_TO_TAG));
        } else {
            manualDrive();
        }
    }

    private void manualDrive() {
        double x = act.getCubic(act.withDeathzone(gamepad1.left_stick_x, GamepadConf.LEFT_DEAD_ZONE));
        double y = -1 * act.getCubic(act.withDeathzone(gamepad1.left_stick_y, GamepadConf.LEFT_DEAD_ZONE));
        double rx = act.getCubic(act.withDeathzone(gamepad1.right_stick_x, GamepadConf.RIGHT_DEAD_ZONE));

        act.move(act.getRatios(new MovementParams(
                new PolarVector(x, y).rotate(act.getRobotAngles().getYaw(AngleUnit.RADIANS)), rx)));
    }

    private void indicateReady() {
        gamepad2.setLedColor(0, 255, 0, -1);
        gamepad2.rumble(0);
    }

    private void handleShooter() {
        if (gamepad2.triangle || gamepad2.circle) {
            if (gamepad2.circle) act.resetRevolverTicks();
            act.toggleShooterManualControl(false);
            indicateReady();
        }

        handleRevolverInput();

        float shooterPower = (float) ((gamepad2.right_trigger > 0.4) ? Math.pow(gamepad2.right_trigger, 2) : 0);
        act.prepareForShoot(shooterPower);

        if (gamepad2.right_bumper) act.shoot();
    }

    private void rotateRevolver(int degrees) {
        act.revolverRotate(degrees);
        gamepad2.setLedColor(255, 0, 0, -1);
        gamepad2.rumble(-1);
    }

    private void handleRevolverInput() {
        if (!dpadPressed) {
            if (gamepad2.dpad_left) rotateRevolver(-60);
            if (gamepad2.dpad_right) rotateRevolver(60);
            if (gamepad2.dpad_up) rotateRevolver(-2);
            if (gamepad2.dpad_down) rotateRevolver(2);
        }
        dpadPressed = (gamepad2.dpad_left || gamepad2.dpad_right || gamepad2.dpad_up || gamepad2.dpad_down);
    }

    private void handleIntakeAndParking() {
        if (gamepad1.left_bumper) {
            float power = (gamepad1.left_trigger > 0.5) ? -0.5f : 1.0f;
            act.setDribblerVelocityCoefficient(power);
        } else {
            act.setDribblerVelocityCoefficient(0);
        }

        if (gamepad1.dpad_up) act.park();
    }

    private void handleTurret() {
        if (gamepad2.triangle) {
            act.goTurretToAprilTag(AprilTag.BLUE, gamepad2.right_stick_x * 10);
        } else {
            act.goTurretToGyroAngle(gamepad2.right_stick_x * 10);
        }
    }

    @Override
    public void runOpMode() throws InterruptedException {
        initialize();
        waitForStart();

        act.afterStart();
        ShooterConf.TARGET_MOTIF = null;

        while (opModeIsActive()) {
            act.updateLastAngles();
            act.updateApriltags();

            handleTargeting();
            handleMovement();
            handleShooter();
            handleIntakeAndParking();
            handleTurret();

            if (gamepad2.right_bumper) {
                act.shoot();
            }

            act.updateShooter();
            act.log();
            telemetry.update();
        }
    }

}
