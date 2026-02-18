package org.firstinspires.ftc.teamcode.Autos; // make sure this aligns with class location

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import  com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.Helperfunctions.Fullfieldshootingvalues;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.Spindex;
import org.firstinspires.ftc.teamcode.subsystems.Turret;

@Autonomous(name = "simpleGoalBlueAuto")

public class simpleGoalBlueAuto extends OpMode {

    Intake intake;
    Spindex spindex;
    Turret turret;
    Fullfieldshootingvalues shootingvalues;




    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;
    private final Pose startPose = new Pose(20.3495, 122.64077, Math.toRadians(143)); // Start Pose of our robot.
    private final Pose shootPose = new Pose(41.32038834951456, 102.17475, Math.toRadians(143));
    private final Pose endPose = new Pose(42.077, 130.3300, Math.toRadians(270));// Scoring Pose of our robot. It is facing the goal at a 135 degree angle.
     // Lowest (Third Set) of Artifacts from the Spike Mark.
    private Path scorePosition;
    private PathChain goOffline;

    public void buildPaths() {
        scorePosition = new Path(new BezierLine(startPose, shootPose));
        scorePosition.setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading());


        goOffline = follower.pathBuilder()
                .addPath(new BezierLine(shootPose,endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
                .build();






    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(scorePosition);
                setPathState(1);
                break;
            case 1:
                intake.forwardIntakeDirection();
                spindex.setSpindexPower(-0.4);
                intake.shootBalls();

            /* You could check for
            - Follower State: "if(!follower.isBusy()) {}"
            - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
            - Robot Position: "if(follower.getPose().getX() > 36) {}"
            */
                if (pathTimer.getElapsedTimeSeconds() > 5){

                    setPathState(2);
                }

                break;
            case 2:
                if(pathTimer.getElapsedTimeSeconds() > 5){
                    follower.followPath(goOffline);
                    setPathState(3);
                }


                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the pickup1Pose's position */

                break;
            case 3:
                /* This case checks the robot's position and will wait until the robot position is close (1 inch away) from the scorePose's position */
                if (!follower.isBusy()) {
                    /* Score Sample */

                    /* Since this is a pathChain, we can have Pedro hold the end point while we are grabbing the sample */

                    setPathState(-1);
                }
                break;

        }
    }

    /**
     * These change the states of the paths and actions. It will also reset the timers of the individual switches
     **/
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    /**
     * This is the main loop of the OpMode, it will run repeatedly after clicking "Play".
     **/
    @Override
    public void loop() {

        // These loop the movements of the robot, these must be called continuously in order to work
        turret.aimTurretOriginal(follower.getPose().getX(),follower.getPose().getY(),follower.getPose().getHeading());
        follower.update();
        autonomousPathUpdate();
        turret.setHoodAngle(shootingvalues.hoodanglelut(follower.getPose().getX(), follower.getPose().getY()));
        turret.setFlyWheelSpeed(shootingvalues.flywheelspeedlut(follower.getPose().getX(), follower.getPose().getY()));



        // Feedback to Driver Hub for debugging
        telemetry.addData("path state", pathState);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();
    }

    /**
     * This method is called once at the init of the OpMode.
     **/
    @Override
    public void init() {

        intake = new Intake(hardwareMap);
        spindex = new Spindex (hardwareMap);
        turret = new Turret(hardwareMap, "blue",90);
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

        shootingvalues = new Fullfieldshootingvalues("blue");


        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);

    }

    /**
     * This method is called continuously after Init while waiting for "play".
     **/
    @Override
    public void init_loop() {
    }

    /**
     * This method is called once at the start of the OpMode.
     * It runs all the setup actions, including building paths and starting the path system
     **/
    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    /**
     * We do not use this because everything should automatically disable
     **/
    @Override
    public void stop() {
    }
}

