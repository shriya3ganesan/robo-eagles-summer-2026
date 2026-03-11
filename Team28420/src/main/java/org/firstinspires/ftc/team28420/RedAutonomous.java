package org.firstinspires.ftc.team28420;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.team28420.module.Actions;
import org.firstinspires.ftc.team28420.module.Camera;
import org.firstinspires.ftc.team28420.module.Movement;
import org.firstinspires.ftc.team28420.module.shooter.Shooter;
import org.firstinspires.ftc.team28420.types.AprilTag;
import org.firstinspires.ftc.team28420.util.Config;

@Autonomous(name = "AUTO MAIN RED", group = "New Actions")
public class RedAutonomous extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        Config.Etc.telemetry = telemetry;

        Actions act = new Actions(
                new Movement(
                        hardwareMap.get(DcMotorEx.class, Config.WheelBaseConf.LEFT_TOP_MOTOR),
                        hardwareMap.get(DcMotorEx.class, Config.WheelBaseConf.RIGHT_TOP_MOTOR),
                        hardwareMap.get(DcMotorEx.class, Config.WheelBaseConf.LEFT_BOTTOM_MOTOR),
                        hardwareMap.get(DcMotorEx.class, Config.WheelBaseConf.RIGHT_BOTTOM_MOTOR)
                ),
                hardwareMap.get(IMU.class, Config.GyroConf.IMU),
                new Camera(hardwareMap.get(WebcamName.class, Config.CameraConf.WEBCAM)),
                new Shooter(hardwareMap),
                hardwareMap.get(Servo.class, "parkingServo1"),
                hardwareMap.get(Servo.class, "parkingServo2")
        );

        ElapsedTime elapsedTime = new ElapsedTime();
        int counter = 1;
        boolean shootingDone = false;

        act.init();
        act.setDefaultAutoMotif("PPG");
        act.scanAllowed = false;

        waitForStart();

        act.afterStart();
        elapsedTime.reset();
        Config.ShooterConf.TARGET_MOTIF = null;
        while(opModeIsActive()) {
            if (Config.ShooterConf.TARGET_MOTIF == null) {
                act.setMotif();
                act.alignRevolverToTarget();
            }

            telemetry.addData("scanned motif", Config.ShooterConf.TARGET_MOTIF);
            telemetry.addData("elapsed time", elapsedTime.milliseconds());

            if (elapsedTime.milliseconds() <= 5000) {
                act.move(act.getRatiosForApriltag(AprilTag.GREEN, -10, 70));
            }
            else if (elapsedTime.milliseconds() <= 5250) {
                act.move(act.getRatios(0, 0, 0.5));
            }
            else if (elapsedTime.milliseconds() <= 10000) {
                act.move(act.getRatiosForApriltag(AprilTag.RED, 2, Config.CameraConf.RANGE_TO_TAG));
            } else if (elapsedTime.milliseconds() <= 22200) {
                act.move(act.getRatios(0, 0, 0));
            } else if (elapsedTime.milliseconds() <= 23500) {
                act.move(act.getRatios(0.5, 0.5, 0));
            }
            else {
                act.move(act.getRatios(0,0,0));
            }


            if (elapsedTime.milliseconds() >= 10000 && elapsedTime.milliseconds() <= 12000) {
                act.setShooterVelocityCoefficient(1);
                telemetry.addLine("4");
            }
            else if(elapsedTime.milliseconds() >= 12000 && !shootingDone){
                telemetry.addLine("5");
                if(act.isShootable()) {
                    if(act.shoot()) {
                        counter++;
                    }
                }
                if(counter > 3) {
                    shootingDone = true;
                    act.setShooterVelocityCoefficient(0);
                }
            }
            telemetry.addData("counter", counter);
            act.updateShooter();
            act.log();
            telemetry.update();
        }
    }
}
