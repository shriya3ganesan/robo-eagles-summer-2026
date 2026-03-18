package org.firstinspires.ftc.team28420;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.team28420.config.CameraConf;
import org.firstinspires.ftc.team28420.config.GamepadConf;
import org.firstinspires.ftc.team28420.config.ShooterConf;
import org.firstinspires.ftc.team28420.module.Actions;
import org.firstinspires.ftc.team28420.types.AprilTag;

@TeleOp(name = "Shooter test", group = "New Actions")
public class ShooterSpeedTest extends LinearOpMode {
    private boolean dpad_active = false;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        Actions act = new Actions(hardwareMap, telemetry);

        act.init();

        waitForStart();

        act.afterStart();
//        act.updateHeading();
        ShooterConf.TARGET_MOTIF = null;
        while (opModeIsActive()) {
            if (ShooterConf.TARGET_MOTIF == null) {
                act.setMotif();
            }

            telemetry.addData("scanned motif", ShooterConf.TARGET_MOTIF);

            if (gamepad1.right_trigger > 0.2) {
//                act.updateHeading();
                act.move(act.getRatiosForApriltag(AprilTag.BLUE, -2, CameraConf.RANGE_TO_TAG));
            } else if (gamepad1.right_bumper) {
                act.move(act.getRatiosLookApriltag(AprilTag.BLUE, 0, CameraConf.RANGE_TO_TAG));
            } else {
                act.move(act.getRatios(act.getCubic(act.withDeathzone(gamepad1.left_stick_x, GamepadConf.LEFT_DEAD_ZONE)), -1 * act.getCubic(act.withDeathzone(gamepad1.left_stick_y, GamepadConf.LEFT_DEAD_ZONE)), act.getCubic(act.withDeathzone(gamepad1.right_stick_x, GamepadConf.RIGHT_DEAD_ZONE))));
            }

            if (gamepad2.triangle) {
                act.toggleShooterManualControl(false);
                gamepad2.setLedColor(0, 255, 0, -1);
                gamepad2.rumble(0);
            }
            if (gamepad2.circle) {
                act.resetRevolverTicks();
                act.toggleShooterManualControl(false);
                gamepad2.setLedColor(0, 255, 0, -1);
                gamepad2.rumble(0);
            }

            if (gamepad2.dpad_left && !dpad_active) {
                act.revolverRotate(-60);
                gamepad2.setLedColor(255, 0, 0, -1);
                gamepad2.rumble(-1);
                dpad_active = true;
            }
            if (gamepad2.dpad_right && !dpad_active) {
                act.revolverRotate(60);
                gamepad2.setLedColor(255, 0, 0, -1);
                gamepad2.rumble(-1);
                dpad_active = true;
            }

            if (gamepad2.dpad_up && !dpad_active) {
                act.revolverRotate(-2);
                gamepad2.setLedColor(255, 0, 0, -1);
                gamepad2.rumble(-1);
                dpad_active = true;
            }
            if (gamepad2.dpad_down && !dpad_active) {
                act.revolverRotate(2);
                gamepad2.setLedColor(255, 0, 0, -1);
                gamepad2.rumble(-1);
                dpad_active = true;
            }


            if (!gamepad2.dpad_right && !gamepad2.dpad_left) dpad_active = false;
            if (gamepad1.left_bumper) {
                if (gamepad1.left_trigger > 0.5) {
                    act.setDribblerVelocityCoefficient(-0.5f);
                } else act.setDribblerVelocityCoefficient(1);
            } else act.setDribblerVelocityCoefficient(0);

            if (gamepad2.right_trigger > 0.4) {
                act.setShooterVelocityCoefficient(gamepad2.right_trigger * gamepad2.right_trigger);
            } else act.setShooterVelocityCoefficient(0);


            if (gamepad2.right_bumper) {
                act.shoot();
            }

            act.updateShooter();

            if (gamepad1.dpad_up) {
                act.park();
            }

            act.log();

            telemetry.update();
        }
    }

}
