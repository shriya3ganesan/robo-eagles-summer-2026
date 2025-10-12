package org.firstinspires.ftc.team417;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.PoseVelocity2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.team417.roadrunner.Drawing;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

/**
 * This class exposes the competition version of TeleOp. As a general rule, add code to the
 * BaseOpMode class rather than here so that it can be shared between both TeleOp and Autonomous.
 */
@TeleOp(name="TeleOp", group="Competition")
@Config
public class CompetitionTeleOp extends BaseOpMode {

    double FASTDRIVE_SPEED = 1.0;
    double SLOWDRIVE_SPEED = 0.5;


    @Override
    public void runOpMode() {
        Pose2d beginPose = new Pose2d(0, 0, 0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, telemetry, gamepad1, beginPose);

        launchState = LaunchState.IDLE;

        initHardware(); //initialize hardware and telemetry; found in BaseOpMode

        // Wait for Start to be pressed on the Driver Hub!
        waitForStart();

        while (opModeIsActive()) {
            telemetry.addLine("Running TeleOp!");

            // Set the drive motor powers according to the gamepad input:
            drive.setDrivePowers(new PoseVelocity2d(
                    new Vector2d(
                            -gamepad1.left_stick_y * doSLOWMODE(),
                            -gamepad1.left_stick_x * doSLOWMODE()

                    ),
                    -gamepad1.right_stick_x


            ));

            // Update the current pose:
            drive.updatePoseEstimate();

            // 'packet' is the object used to send data to FTC Dashboard:
            TelemetryPacket packet = MecanumDrive.getTelemetryPacket();

            // Do the work now for all active Road Runner actions, if any:
            drive.doActionsWork(packet);

            // Draw the robot and field:
            packet.fieldOverlay().setStroke("#3F51B5");
            Drawing.drawRobot(packet.fieldOverlay(), drive.pose);
            MecanumDrive.sendTelemetryPacket(packet);

            if (gamepad2.y) { //high speed
                launcher.setVelocity(LAUNCHER_HIGH_TARGET_VELOCITY);
                doHighLaunch = true;
                doSort = false;
                doReverse = false;
            } else if (gamepad2.a) { //slow speed
                launcher.setVelocity(LAUNCHER_LOW_TARGET_VELOCITY);
                doHighLaunch = false;
                doSort = false;
                doReverse = false;
            } else if (gamepad2.x) { // sort speed
                launcher.setVelocity(LAUNCHER_SORTER_TARGET_VELOCITY);
                doHighLaunch = false;
                doSort = true;
                doReverse = false;
            } else if (gamepad2.b) { // reverse
                launcher.setVelocity(LAUNCHER_REV_TARGET_VELOCITY);
                doHighLaunch = false;
                doSort = false;
                doReverse = true;
            } else if (gamepad2.left_bumper) { // stop flywheel
                launcher.setVelocity(STOP_SPEED);
            }

            /*
             * Now we call our "Launch" function.
             */
            launch(gamepad2.rightBumperWasPressed());

            /*
             * Show the state and motor powers
             */
            telemetry.addData("State", launchState);
            // telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
            telemetry.addData("motorSpeed", launcher.getVelocity());
            telemetry.addData("reverse", doReverse);
            telemetry.addData("highLaunch", doHighLaunch);
            telemetry.addData("sort", doSort);

            telemetry.update();
        }
    }



    public double doSLOWMODE(){
        if (gamepad1.left_stick_button) {
            return SLOWDRIVE_SPEED;
        } else {
            return FASTDRIVE_SPEED;
        }
    }
}
