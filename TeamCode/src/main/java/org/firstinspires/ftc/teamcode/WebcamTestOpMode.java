package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.sun.tools.doclint.Entity;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagsWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.TurretServo;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
@TeleOp
public class WebcamTestOpMode  extends OpMode {
    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();
    Launcher launcher = new Launcher();

   // TurretServo turret = new TurretServo();

    @Override
    public void init() {
        aprilTagWebcam.init(hardwareMap, telemetry);
        launcher.init(hardwareMap);
       // turret.init(hardwareMap);
    }

    @Override
    public void loop() {
        //Update the vision portal
        aprilTagWebcam.update();
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(24); // TAG ID 24 is the red goal
        aprilTagWebcam.displayDetectionTelemetry(id24);
        // NOTE: we will need a separate OPMODE (otherwise identical) that sets the target TAGID to BLUE (#20)
        if(id24 != null) {
            double angleToTag = id24.ftcPose.yaw;
            //turret.changeTurretByDegrees(angleToTag);

            double distanceToGoalCM = id24.ftcPose.range;
            // NOTE: use this after distance vs speed has been measured and calibrated
            //launcher.setMotorVelocityForDistance(distanceToGoalCM);
        } else {
            // if we can't see the target
            // default back to neutral/default
            // turret.resetTurret();
            // and turn launch motors off
            launcher.stopLauncher();
        }

        // these are manual test methods to assist with tuning the target launch motor velocity at measured distances
        if (gamepad2.leftStickButtonWasPressed()) {
      //      launcher.incrementLaunchSpeed();
        } else if (gamepad2.rightStickButtonWasPressed()) {
      //      launcher.decrementLaunchSpeed();
        }
      //  launcher.setMotorVelocity();


        if (gamepad1.right_trigger > .5) {
       //     if (!launcher.getTriggerActive()) {
                // TODO: maybe also check to see that launcher measured velocities are within 10%(?) of target velocity
       //         launcher.triggerFeeder();
            } else {
                // already triggered, wait for it to reset
            }
        }
       // telemetry.addData("Distance to goal: ", distanceToGoalCM);
       // telemetry.addLine("Feeder active: " + launcher.getTriggerActive());


        //telemetry.addLine("Target Velocity: " + launcher.getTargetLaunchSpeed());
        //telemetry.addLine("Lower Velocity: " + launcher.getLowerVelocity());
        //telemetry.addLine("Upper Velocity: " + launcher.getUpperVelocity());

}
