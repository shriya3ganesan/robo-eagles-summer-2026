package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.sun.tools.doclint.Entity;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagsWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.LEDIndicator;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.TurretServo;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
@TeleOp
public class BlueTeleOp  extends OpMode {
    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();
    Launcher launcher = new Launcher();
    MecanumDrive drive = new MecanumDrive();
    Intake intake = new Intake();
    TurretServo turret = new TurretServo();
    LEDIndicator led = new LEDIndicator();
    int numMissingTagReads = 0;


    @Override
    public void init() {
        aprilTagWebcam.init(hardwareMap, telemetry);
        launcher.init(hardwareMap);
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        turret.init(hardwareMap);
        led.init(hardwareMap);
        // turret.init(hardwareMap);
    }

    @Override
    public void loop() {
        //Update the vision portal
        aprilTagWebcam.update();
        AprilTagDetection id20 = aprilTagWebcam.getTagBySpecificId(20); // TAG ID 20 is the blue goal
        aprilTagWebcam.displayDetectionTelemetry(id20);
        // NOTE: we will need a separate OPMODE (otherwise identical) that sets the target TAGID to BLUE (#20)
        if (id20 != null && id20.ftcPose != null) {
            numMissingTagReads = 0;
            double angleToTag = id20.ftcPose.bearing;
            turret.changeTurretByDegrees(angleToTag);

            double distanceToGoalCM = id20.ftcPose.range;
            launcher.setMotorVelocityForDistance(distanceToGoalCM);
            led.setLEDGreen();
            // NOTE: use this after distance vs speed has been measured and calibrated
            //launcher.setMotorVelocityForDistance(distanceToGoalCM);
        } else if (numMissingTagReads < 100){
            numMissingTagReads++;
            led.setLEDBlue();
        } else {
            // if we can't see the target/            // default back to neutral/default
            //turret.resetTurret();
            // and turn launch motors off
            launcher.stopLauncher();
            turret.resetTurret();
            led.setLEDRed();
        }

        // these are manual test methods to assist with tuning the target launch motor velocity at measured distances
        if (gamepad2.leftStickButtonWasPressed()) {
            //      launcher.incrementLaunchSpeed();
        } else if (gamepad2.rightStickButtonWasPressed()) {
            //      launcher.decrementLaunchSpeed();
        }
        //  launcher.setMotorVelocity();


        if (gamepad2.right_trigger > .5) {
            //     if (!launcher.getTriggerActive()) {
            // TODO: maybe also check to see that launcher measured velocities are within 10%(?) of target velocity
            //         launcher.triggerFeeder();
            launcher.loadBall();

        } else {
            launcher.resetFeeder();
        }

        if (gamepad2.leftStickButtonWasPressed()) {
            launcher.startLauncher();
            telemetry.addLine("Left Stick Was Pressed");
        } else if (gamepad2.rightStickButtonWasPressed()) {
            launcher.stopLauncher();
        }

        if (gamepad2.aWasPressed()) {
            launcher.incrementLaunchSpeed();
        } else if (gamepad2.bWasPressed()) {
            launcher.decrementLaunchSpeed();
        }

        if (gamepad2.xWasPressed()) {
            turret.incrementTurretPosition();
        } else if (gamepad2.yWasPressed()) {
            turret.decrementTurretPosition();
        }

        //For Intake (test if same buttons works)
        if (gamepad1.right_trigger !=0 ) {
            intake.startIntake();
        } else if (gamepad1.left_trigger !=0) {
            intake.reverseIntake();
        } else {
            intake.stopIntake();
        }

        // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
        // Note: pushing left stick forward gives negative value
        drive.drive(-gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        // update launcher state machine
        //launcher.updateState();
        launcher.setMotorVelocity();




        //telemetry.addData("Distance to goal: ", distanceToGoalCM);
        // telemetry.addLine("Feeder active: " + launcher.getTriggerActive());
        telemetry.addLine("Missed Tag Reads: " + numMissingTagReads);
        telemetry.addLine("Target Velocity: " + launcher.getTargetLaunchSpeed());
        telemetry.addLine("Right Velocity: " + launcher.getLowerVelocity());
        telemetry.addLine("Left Velocity: " + launcher.getUpperVelocity());
        telemetry.addData("State: ", launcher.getState());
        String turretPositionStr = String.format("%.2f",turret.getCurrentPosition());
        telemetry.addLine("Turret Position: " + turretPositionStr);
        /*
        String leftStickX = String.format("%.2f",gamepad1.left_stick_x);
        telemetry.addLine("Left Stick X:" + leftStickX);
        String leftStickY = String.format("%.2f",gamepad1.left_stick_y);
        telemetry.addLine("Left Stick X:" + leftStickY);
        String rightStickX = String.format("%.2f",gamepad1.right_stick_x);
        telemetry.addLine("Left Stick X:" + rightStickX);
        */

    }
}