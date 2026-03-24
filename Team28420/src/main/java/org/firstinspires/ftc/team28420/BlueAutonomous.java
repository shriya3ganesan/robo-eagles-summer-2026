package org.firstinspires.ftc.team28420;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.team28420.config.CameraConf;
import org.firstinspires.ftc.team28420.config.ShooterConf;
import org.firstinspires.ftc.team28420.module.Actions;
import org.firstinspires.ftc.team28420.types.AprilTag;

@Autonomous(name = "AUTO MAIN BLUE", group = "New Actions")
public class BlueAutonomous extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Actions act = new Actions(hardwareMap, telemetry);

        ElapsedTime elapsedTime = new ElapsedTime();
        int counter = 1;
        boolean shootingDone = false;

        act.init();
        act.setDefaultAutoMotif("PPG");

        waitForStart();

        act.afterStart();
        elapsedTime.reset();
        ShooterConf.TARGET_MOTIF = null;
        while (opModeIsActive()) {
            if (ShooterConf.TARGET_MOTIF == null) {
                act.setMotif();
                act.alignRevolverToTarget();
            }

            telemetry.addData("scanned motif", ShooterConf.TARGET_MOTIF);
            telemetry.addData("elapsed time", elapsedTime.milliseconds());

            if (elapsedTime.milliseconds() <= 5000) {
                act.move(act.getRatiosForApriltag(AprilTag.GREEN, 10, 70));
            } else if (elapsedTime.milliseconds() <= 5250) {
                act.move(act.getRatios(0, 0, -0.5));
            } else if (elapsedTime.milliseconds() <= 10000) {
                act.move(act.getRatiosForApriltag(AprilTag.BLUE, 2, CameraConf.RANGE_TO_TAG));
            } else if (elapsedTime.milliseconds() <= 22200) {
                act.move(act.getRatios(0, 0, 0));
            } else if (elapsedTime.milliseconds() <= 23500) {
                act.move(act.getRatios(-0.5, 0.5, 0));
            } else {
                act.move(act.getRatios(0, 0, 0));
            }


            if (elapsedTime.milliseconds() >= 10000 && elapsedTime.milliseconds() <= 12000) {
                act.prepareForShoot(1);
                telemetry.addLine("4");
            } else if (elapsedTime.milliseconds() >= 12000 && !shootingDone) {
                telemetry.addLine("5");
                if (act.isShootable()) {
                    if (act.shoot()) {
                        counter++;
                    }
                }
                if (counter > 3) {
                    shootingDone = true;
                    act.prepareForShoot(0);
                }
            }
            telemetry.addData("counter", counter);
            act.updateShooter();
            act.log();
            telemetry.update();
        }
    }
}
