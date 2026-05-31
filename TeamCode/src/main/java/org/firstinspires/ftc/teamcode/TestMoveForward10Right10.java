package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.configurables.annotations.Configurable;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Configurable
@Autonomous(name = "TestMoveForward10Right10", group = "Autonomous")
public class TestMoveForward10Right10 extends OpMode {

    // -------------------------------------------------------------------------
    // Pedro Pathing
    // -------------------------------------------------------------------------
    private Follower follower;
    private TelemetryManager telemetryM;
    private PathChain pathChain;
    private int pathState = 0;

    // =========================================================================
    // INIT
    // =========================================================================
    @Override
    public void init() {
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(0, 0, 0));
        follower.update();

        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        // Build both paths upfront:
        // Step 1: move 10 units forward  (y + 10)
        // Step 2: move 10 units right    (x + 10)
        pathChain = follower.pathBuilder()
                .addPath(new Path(new BezierLine(
                        new Pose(0, 0, 0),
                        new Pose(0, 10, 0)   // forward 10
                )))
                .setConstantHeadingInterpolation(0)
                .addPath(new Path(new BezierLine(
                        new Pose(0, 10, 0),
                        new Pose(10, 10, 0)  // right 10
                )))
                .setConstantHeadingInterpolation(0)
                .build();

        telemetry.addData("Status", "Initialized — ready to run");
        telemetry.update();
    }

    // =========================================================================
    // START
    // =========================================================================
    @Override
    public void start() {
        follower.followPath(pathChain, true); // true = hold position at end
        pathState = 1;
    }

    // =========================================================================
    // LOOP
    // =========================================================================
    @Override
    public void loop() {
        follower.update();
        telemetryM.update();

        switch (pathState) {
            case 1:
                // Still following the path chain
                if (!follower.isBusy()) {
                    pathState = 2; // done
                }
                break;

            case 2:
                // Finished — hold position
                break;
        }

        // -----------------------------------------------------------------
        // TELEMETRY
        // -----------------------------------------------------------------
        telemetry.addData("Path State",     pathState == 1 ? "Moving" : "Done");
        telemetry.addData("X",              "%.2f", follower.getPose().getX());
        telemetry.addData("Y",              "%.2f", follower.getPose().getY());
        telemetry.addData("Heading (deg)",  "%.1f", Math.toDegrees(follower.getPose().getHeading()));
        telemetry.update();
    }

} // end class MecanumAuto