package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagsWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp
public class RedTeleOp extends OpMode {

    MecanumDrive drive = new MecanumDrive();
    Launcher launcher = new Launcher();
    Intake intake = new Intake();
    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();

    @Override
    public void init() {
        aprilTagWebcam.init(hardwareMap, telemetry);
        drive.init(hardwareMap);
        launcher.init(hardwareMap);
        intake.init(hardwareMap);
    }

    @Override
    public void loop() {
        //Update the vision portal
        aprilTagWebcam.update();
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(24); // TAG ID 24 is the red goal
        aprilTagWebcam.displayDetectionTelemetry(id24);
        // NOTE: we will need a separate OPMODE (otherwise identical) that sets the target
        //TAGID to BLUE (#??)
        double distanceToGoalCM = 0;

        if (id24 != null) {
            double angleToTag = id24.ftcPose.yaw;
            //turret.changeTurretByDegrees(angleToTag);

            distanceToGoalCM = id24.ftcPose.range;
            // NOTE: use this after distance vs speed has been measured and calibrated
            // launcher.setMotorVelocityForDistance(distanceToGoalCM);
        } else {
            // if we can't see the target
            // default back to neutral/default
            // turret.resetTurret();
            // and turn launch motors off
            launcher.stopLauncher();
        }
        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        // Note: pushing left stick forward gives negative value
        drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        // The user has control of the speed of launcher motor without automatically
        // queuing a shot.
        if (gamepad2.leftStickButtonWasPressed()) {
            launcher.startLauncher();
        }
        else if (gamepad2.rightStickButtonWasPressed()) {
            launcher.stopLauncher();
        }

        //For Intake (test if same buttons works)
        if (gamepad2.a) {
            intake.startIntake();
        } else if (gamepad2.x) {
            intake.stopIntake();
        }

        if (gamepad2.b) {
            launcher.loadBall();
        }

        // update launcher state machine
        launcher.updateState();

        telemetry.addData("Distance to goal: ", distanceToGoalCM);
        //telemetry.addLine("Feeder active: " + launcher.getTriggerActive());
        telemetry.addLine("Target Velocity: " + launcher.getTargetLaunchSpeed());
        telemetry.addLine("Lower Velocity: " + launcher.getLowerVelocity());
        telemetry.addLine("Upper Velocity: " + launcher.getUpperVelocity());
        telemetry.addData("State", launcher.getState());
    }
}
