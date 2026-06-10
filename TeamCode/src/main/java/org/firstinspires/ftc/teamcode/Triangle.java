package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "Triangle", group = "Autonomous")
public class Triangle extends LinearOpMode {

    private Follower follower;

    @Override
    public void runOpMode() {

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0, 0, 0));
        follower.update();

        // (0,0) → (40,0)
        Path leg1 = new Path(new BezierLine(
                new Pose(0, 0, 0),
                new Pose(40, 0, 0)
        ));
        leg1.setConstantHeadingInterpolation(0);

        // (40,0) → (20,40)
        Path leg2 = new Path(new BezierLine(
                new Pose(40, 0, 0),
                new Pose(20, 40, 0)
        ));
        leg2.setConstantHeadingInterpolation(0);

        // (20,40) → (0,0)
        Path leg3 = new Path(new BezierLine(
                new Pose(20, 40, 0),
                new Pose(0, 0, 0)
        ));
        leg3.setConstantHeadingInterpolation(0);

        telemetry.addData("Status", "Initialized — ready to run");
        telemetry.update();

        waitForStart();

        // === LEG 1: (0,0) → (40,0) ===
        follower.followPath(leg1, true);
        while (opModeIsActive() && follower.isBusy()) {
            follower.update();
            telemetry.addData("State", "Leg 1: (0,0) → (40,0)");
            telemetry.addData("X", "%.2f", follower.getPose().getX());
            telemetry.addData("Y", "%.2f", follower.getPose().getY());
            telemetry.update();
        }
        follower.breakFollowing();
        sleep(500);

        // === LEG 2: (40,0) → (20,40) ===
        follower.followPath(leg2, true);
        while (opModeIsActive() && follower.isBusy()) {
            follower.update();
            telemetry.addData("State", "Leg 2: (40,0) → (20,40)");
            telemetry.addData("X", "%.2f", follower.getPose().getX());
            telemetry.addData("Y", "%.2f", follower.getPose().getY());
            telemetry.update();
        }
        follower.breakFollowing();
        sleep(500);

        // === LEG 3: (20,40) → (0,0) ===
        follower.followPath(leg3, true);
        while (opModeIsActive() && follower.isBusy()) {
            follower.update();
            telemetry.addData("State", "Leg 3: (20,40) → (0,0)");
            telemetry.addData("X", "%.2f", follower.getPose().getX());
            telemetry.addData("Y", "%.2f", follower.getPose().getY());
            telemetry.update();
        }
        follower.breakFollowing();

        // === DONE ===
        telemetry.addData("State", "Done");
        telemetry.update();
        sleep(30000);
    }
}