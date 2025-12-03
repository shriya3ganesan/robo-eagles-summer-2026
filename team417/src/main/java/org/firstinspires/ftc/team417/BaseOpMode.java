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
    // Resets the robot pose only if the robot is not moving
    public static void tryResetRobotPose(LimelightDetector detector, MecanumDrive drive) {
        if (isZero(drive.rightBack.getVelocity())
                && isZero(drive.rightFront.getVelocity())
                && isZero(drive.leftBack.getVelocity())
                && isZero(drive.leftFront.getVelocity())) {

            Pose2d pose = detector.detectRobotPose();

            if (pose != null) {
                drive.setPose(pose);
            }
        }
    }

    // Sees if a number is within one one-thousandths of zero
    private static boolean isZero(double x) {
        return Math.abs(x) < 0.001;
    }
}


