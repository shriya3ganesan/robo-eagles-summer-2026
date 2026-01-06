package org.firstinspires.ftc.teamcode;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Trajectory;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
/*
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.lang.Math;

@Autonomous(name="DiagnalTwoPointAuto")
public class DiagnalTwoPointAuto extends LinearOpMode {
    @Override
    public void runOpMode() {
        // Initialize the standard SampleMecanumDrive
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);

        // Set the starting pose at (0,0) with heading 0
        drive.setPoseEstimate(new Pose2d(0, 0, 0));

        // Wait for start button
        waitForStart();
        if (isStopRequested()) return;

        // --- Trajectory 1: Move straight along X to (6,0) ---
        Trajectory traj1 = drive.trajectoryBuilder(drive.getPoseEstimate())
                .lineTo(new Vector2d(6, 0))
                .build();

        drive.followTrajectory(traj1);

        // --- Trajectory 2: Move diagonally to (-3,4) ---
        Trajectory traj2 = drive.trajectoryBuilder(drive.getPoseEstimate())
                .lineTo(new Vector2d(-3, 4))
                .build();

        drive.followTrajectory(traj2);

        // Optionally, print final pose
        Pose2d finalPose = drive.getPoseEstimate();
        telemetry.addData("Final X", finalPose.getX());
        telemetry.addData("Final Y", finalPose.getY());
        telemetry.addData("Final Heading (deg)", Math.toDegrees(finalPose.getHeading()));
        telemetry.update();
    }
}
*/