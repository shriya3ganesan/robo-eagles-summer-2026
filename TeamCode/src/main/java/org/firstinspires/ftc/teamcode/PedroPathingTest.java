package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.*;
import com.pedropathing.util.*;
import com.pedropathing.paths.PathChain;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Basic: PedroPathingTest OpMode ChargedCreeper", group = "OpMode")
public class PedroPathingTest extends OpMode {
    private Follower follower;
    private Timer pathTimer, OpModeTimer;
    public enum PathState {
        DRIVE_STARTPOS_SHOOT_POS,
        SHOOT_PRELOAD
    }
    PathState pathState;
    private final Pose startPos = new Pose(0, 0, Math.toRadians(0));
    private final Pose shootPos = new Pose(19.8469823789,0, Math.toRadians(136));
    private PathChain driveStartPosShootPos;
    public void buildPaths() {
        driveStartPosShootPos = follower.pathBuilder()
                .addPath(new BezierLine(startPos, shootPos))
                .setLinearHeadingInterpolation(startPos.getHeading(), shootPos.getHeading())
                .build();
    }
    public void statePathUpdate(){
        switch (pathState) {
            case DRIVE_STARTPOS_SHOOT_POS:
                follower.followPath(driveStartPosShootPos, true);
                setPathState(PathState.SHOOT_PRELOAD);
                break;
            case SHOOT_PRELOAD:
                if (!follower.isBusy()) {

                    telemetry.addLine("Done Path 1");
                }
                break;
            default:
                telemetry.addLine("No state command");
                break;
        }
    }
    public void setPathState(PathState newState){
        pathState = newState;
        pathTimer.resetTimer();
    }
    @Override
    public void init() {
        pathState = PathState.DRIVE_STARTPOS_SHOOT_POS;
        pathTimer = new Timer();
        OpModeTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        // add any other init
        buildPaths();
        follower.setPose(startPos);
    }
    public void start() {
        OpModeTimer.resetTimer();
        setPathState(pathState);
    }

    @Override
    public void loop() {
        follower.update();
        statePathUpdate();

        telemetry.addData("Path State", pathState.toString());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("Path time", pathTimer.getElapsedTimeSeconds());


    }

}
