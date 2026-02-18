package org.firstinspires.ftc.teamcode.Autos;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;


@Autonomous

public class sampleAutoPathing extends OpMode {

    private Follower follower;
    private Timer pathTimer, opModeTimer;

    public enum PathState{
        //STARTPosition -->EndPosition

        DRIVE_STARTPOSITION_SHOOTPOSITION,
        SHOOTPRELOAD,
    }

    PathState pathState;
    private final Pose startPose = new Pose(20.81553,122.8737, Math.toRadians (138));
    private final Pose shootPose = new Pose (48.07766990291262,94.6796,Math.toRadians(138));
    private PathChain driveStartPosShootPos;

    public void buildPaths(){
        //put in coordinates for start pose and end pose
        driveStartPosShootPos = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading()).build();
    }
    public void statePathUpdate(){
        switch(pathState){
            case  DRIVE_STARTPOSITION_SHOOTPOSITION:
                follower.followPath(driveStartPosShootPos,true);
               setPathState(PathState.SHOOTPRELOAD);
                break;
            case SHOOTPRELOAD:

                if (!follower.isBusy()){
                    telemetry.addLine("Done State Machine");
                    follower.holdPoint(shootPose);

                }
                break;

            default:
                telemetry.addLine("No state COmmanded");
                break;

        }
    }
    public void setPathState(PathState newState){
        pathState=newState;
        pathTimer.resetTimer();
    }



    @Override
    public void init(){
        pathState=PathState.DRIVE_STARTPOSITION_SHOOTPOSITION;
        pathTimer = new Timer();
        opModeTimer = new Timer();

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setPose(startPose);

    }

    public void start(){
        opModeTimer.resetTimer();
        setPathState(pathState);

    }
    @Override
    public void loop(){
        follower.update();
        statePathUpdate();
        telemetry.addData("pathState", pathState.toString());
    }
}
