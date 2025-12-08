package org.firstinspires.ftc.team417;

import com.acmerobotics.roadrunner.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.team417.apriltags.LimelightDetector;
import org.firstinspires.ftc.team417.roadrunner.MecanumDrive;

/**
 * This class contains all of the base logic that is shared between all of the TeleOp and
 * Autonomous logic. All TeleOp and Autonomous classes should derive from this class.
 */
abstract public class BaseOpMode extends LinearOpMode {
    LimelightDetector detector;
    MecanumDrive drive;

    // Resets the robot pose only if the robot is not moving
    public void tryResetRobotPose() {
        if (isZero(drive.poseVelocity.linearVel.x)
                && isZero(drive.poseVelocity.linearVel.y)
                && isZero(drive.poseVelocity.angVel)
        ) {

            Pose2d pose = detector.detectRobotPose();

            if (pose != null) {
                drive.setPose(pose);
            }
        }
    }

    // Sees if a number is within one one-hundredths of zero
    private static boolean isZero(double z) {
        return Math.abs(z) < 0.01;
    }
}


