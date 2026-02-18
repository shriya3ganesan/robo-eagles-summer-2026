
package org.firstinspires.ftc.teamcode.Autos;
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

@Autonomous(name = "simple Far Blue Auto")

public class simpleFarBlueAuto extends OpMode {

    Intake intake;
    Spindex spindex;
    Turret turret;
    Fullfieldshootingvalues shootingvalues;



    private Follower follower;
    private Timer pathTimer, actionTimer, opmodeTimer;

    private int pathState;
    private final Pose startPose = new Pose(56.4660194174757, 8.23300, Math.toRadians(90)); // Start Pose of our robot.
    private final Pose shootPose = new Pose(56.8549, 34.01672, Math.toRadians(90));
    // Lowest (Third Set) of Artifacts from the Spike Mark.
    private Path scorePosition;

    public void buildPaths() {
        scorePosition = new Path(new BezierLine(startPose, shootPose));
        scorePosition.setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading());







    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(scorePosition);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()){
                    setPathState(-1);
                }
            /* You could check for
            - Follower State: "if(!follower.isBusy()) {}"
            - Time: "if(pathTimer.getElapsedTimeSeconds() > 1) {}"
            - Robot Position: "if(follower.getPose().getX() > 36) {}"
            */

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
        turret.setHoodAngle(shootingvalues.hoodanglelut(follower.getPose().getX(), follower.getPose().getY()));
        turret.setFlyWheelSpeed(shootingvalues.flywheelspeedlut(follower.getPose().getX(), follower.getPose().getY()));

        follower.update();
        autonomousPathUpdate();

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
        shootingvalues = new Fullfieldshootingvalues("blue");
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();

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


