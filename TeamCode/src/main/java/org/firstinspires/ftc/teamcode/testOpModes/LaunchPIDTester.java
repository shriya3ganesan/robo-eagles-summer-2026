package org.firstinspires.ftc.teamcode.testOpModes;


import android.os.Build;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.internal.files.DataLogger;
import org.firstinspires.ftc.teamcode.mechanisms.AprilTagsWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.LEDIndicator;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.TurretServo;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;

@TeleOp(name = "Launch PID Tuner", group = "Testing")
public class LaunchPIDTester extends OpMode {

    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();
    Launcher launcher = new Launcher();
    MecanumDrive drive = new MecanumDrive();
    Intake intake = new Intake();
    TurretServo turret = new TurretServo();
    LEDIndicator led = new LEDIndicator();
    int numMissingTagReads = 0;

    double[] stepsize = new double[]{10, 1, .1, .01, .001, .0001};
    // PIDFCoefficients pidf = new PIDFCoefficients(300, 0, 0.001, 10); // current values for reference

    char[] pids = new char[] {'P', 'I', 'D', 'F'};

    int currentStepIndex = 0;
    int currentPIDEditIndex = 0;


    double[] test_PIDF = new double[] {0,0,0,0};


    DataLogger dl;


    @Override
    public void init() {
        aprilTagWebcam.init(hardwareMap, telemetry);
        launcher.init(hardwareMap);
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        turret.init(hardwareMap);
        led.init(hardwareMap);

        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String formatedTime = timeFormat.format(calendar.getTime());
        try {
            dl = new DataLogger(formatedTime + "_LaunchPIDLogger.txt");

            dl.addHeaderLine("P", "I", "D", "F", "Target Velocity", "Measured Velocity");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        dl.close();
    }

    @Override
    public void loop() {

        //Update the vision portal
        aprilTagWebcam.update();
        AprilTagDetection anyTag = aprilTagWebcam.getTagBySpecificId(0); // TAG ID 0 returns any tag
        aprilTagWebcam.displayDetectionTelemetry(anyTag);

        if (anyTag != null && anyTag.ftcPose != null) {
            numMissingTagReads = 0;
            double angleToTag = anyTag.ftcPose.bearing;
            turret.changeTurretByDegrees(angleToTag);

            double distanceToGoalCM = anyTag.ftcPose.range;
            launcher.setMotorVelocityForDistance(distanceToGoalCM);
        } else if (numMissingTagReads < 100){
            numMissingTagReads++;
        } else {
            // if we can't see the target
            // default back to neutral/default
            // and turn launch motors off
            launcher.stopLauncher();
            turret.resetTurret();
        }

        double speedError = launcher.getLaunchSpeedError();
        double angleError = turret.getAngleError();
        if(numMissingTagReads >= 100) {
            led.setLEDRed();
        } else if (anyTag != null && anyTag.ftcPose != null) {
            if (speedError < 50 && angleError < 2) {
                led.setLEDGreen();
            } else {
                led.setLEDBlue();
            }
        }
        // set LED to yellow? Or something else to indicate we don't have 100 missed reads, but aren't facing the tag now
        // if we turn quick enough, no guarantee that we will get an angle error...
        // maybe just > 10 missedTagReads? that would indicate that the tag reads are sketchy even if facing it
        else if(numMissingTagReads > 10) { // || angleError > 5
            led.setLEDRed();
        }

        // gamepad2.dpadDownWasPressed() - decrement sizestep index (min 0)
        // gamepad2.dpadUpWasPressed() - increment sizestep index (max array.length)
        // gamepad2.dpadLeftWasPressed() - decrement P/I/D/F selection (min 0)
        // gamepad2.dpadRightWasPressed() - increment P/I/D/F selection (max 3)

        // gamepad2.aWasPressed() - decrement P/I/D/F
        // gamepad2.yWasPressed() - increment P/I/D/F

        if(gamepad2.dpadDownWasPressed()) {
            // lower bound of 0
            currentStepIndex = Math.max(0, currentStepIndex - 1);
        }
        if(gamepad2.dpadUpWasPressed()) {
            // upper bound of last element in array
            currentStepIndex = Math.min(stepsize.length-1, currentStepIndex+1);
        }
        if(gamepad2.dpadLeftWasPressed()) {
            // lower bound of 0
            currentPIDEditIndex = Math.max(0, currentPIDEditIndex - 1);
        }
        if(gamepad2.dpadRightWasPressed()) {
            // upper bound of last element in array
            currentPIDEditIndex = Math.min(pids.length-1, currentPIDEditIndex+1);
        }

        if(gamepad2.aWasPressed()) {
            // no upper bound
            test_PIDF[currentPIDEditIndex] += stepsize[currentStepIndex];
        }
        if(gamepad2.yWasPressed()) {
            // lower bound of 0
            test_PIDF[currentPIDEditIndex] = Math.max(0, test_PIDF[currentPIDEditIndex] - stepsize[currentStepIndex]);
        }

        launcher.SetNewPIDValues(test_PIDF[0], test_PIDF[1], test_PIDF[2], test_PIDF[3]);


        if (gamepad2.right_trigger != 0) {
            launcher.loadBall();
        } else {
            launcher.resetFeeder();
        }


        //For Intake driver can turn it on for intake, or shooter can turn it on with feeder
        if (gamepad1.right_trigger !=0 || gamepad2.right_trigger != 0) {
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
        launcher.setMotorVelocity();


        try {
            double target = launcher.getTargetLaunchSpeed();
            double current = (launcher.getLowerVelocity() + launcher.getUpperVelocity())/2;
            dl.addDataLine(test_PIDF[0], test_PIDF[1], test_PIDF[2], test_PIDF[3], target, current);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        telemetry.addLine("< > changes P I D F ");
        telemetry.addLine("^ v changes step size");
        telemetry.addLine("Editing: " + pids[currentPIDEditIndex] + " +/-" + stepsize[currentStepIndex]);

        telemetry.addLine("P: " + test_PIDF[0]);
        telemetry.addLine("I: " + test_PIDF[1]);
        telemetry.addLine("D: " + test_PIDF[2]);
        telemetry.addLine("F: " + test_PIDF[3]);


        telemetry.addLine("Target Velocity: " + launcher.getTargetLaunchSpeed());
        double averageVelocity = (launcher.getLowerVelocity() + launcher.getUpperVelocity())/2.0;
        double targetError = launcher.getTargetLaunchSpeed() - averageVelocity;
        telemetry.addLine("Velocity Error: " + targetError);

        telemetry.addLine("");
        telemetry.addLine("Stop OpMode to write to file...");

    }


}
