package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagsWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.LEDIndicator;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.TurretServo;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous
public class RedPedroPathingClose extends OpMode {
    MecanumDrive drive = new MecanumDrive();
    Intake intake = new Intake();
    Launcher launcher = new Launcher();
    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();
    TurretServo turret = new TurretServo();
    LEDIndicator led = new LEDIndicator();
    int numMissingTagReads = 0;
    private Follower follower;


    private final Pose startPose = new Pose(124.6829268292683, 122.73170731707317, Math.toRadians(36)); // Start Pose of our robot.
    private final Pose launchingPose = new Pose(78.82926829268293, 82.14634146341466, Math.toRadians(45)); // Where our robot launches from
    private final Pose pickupReady1Pose = new Pose(98.21951219512195, 83.53658536585364, Math.toRadians(0)); // Ready to pick up closest row of balls
    private final Pose pickup1Pose = new Pose(125.17073170731707, 83.58536585365854, Math.toRadians(0)); // Pick up closest row of balls

    private Path startToLaunching;
    private PathChain launchingToPickupReady1, pickupReady1ToPickup1, pickup1ToLaunching, launchingToStart;

    public void buildPaths() {

        //This sets up our first path where we back up
        startToLaunching = new Path(new BezierLine(startPose, launchingPose));
        startToLaunching.setLinearHeadingInterpolation(startPose.getHeading(), launchingPose.getHeading());

        //these next several section set up the rest of the paths, created in the PathChain
        launchingToPickupReady1 = follower.pathBuilder()
                .addPath(new BezierLine(launchingPose, pickupReady1Pose))
                .setLinearHeadingInterpolation(launchingPose.getHeading(), pickupReady1Pose.getHeading())
                .build();

        pickupReady1ToPickup1 = follower.pathBuilder()
                .addPath(new BezierLine(pickupReady1Pose, pickup1Pose))
                .setLinearHeadingInterpolation(pickupReady1Pose.getHeading(), pickup1Pose.getHeading())
                .build();

        pickup1ToLaunching = follower.pathBuilder()
                .addPath(new BezierLine(pickup1Pose, launchingPose))
                .setLinearHeadingInterpolation(pickup1Pose.getHeading(), launchingPose.getHeading())
                .build();

        launchingToStart =  follower.pathBuilder()
                .addPath(new BezierLine(launchingPose, startPose))
                .setLinearHeadingInterpolation(launchingPose.getHeading(), startPose.getHeading())
                .build();
    }

    enum State {
        GO_TO_LAUNCH_1,
        WAIT_TO_FINISH_PATH_1,
        FIND_TAG_1,
        SPIN_UP_1,
        LAUNCHING_1,
        PREPARE_TO_INTAKE_POSE_1,
        INTAKE_1,
        GO_TO_LAUNCH_2,
        WAIT_TO_FINISH_PATH_2,
        FIND_TAG_2,
        SPIN_UP_2,
        LAUNCHING_2,
        GO_BACK_TO_START_POSE,
        FINISHED
    }

    RedPedroPathingClose.State state;
    ElapsedTime driveTimer = new ElapsedTime();


    @Override
    public void init() {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        launcher.init(hardwareMap);
        aprilTagWebcam.init(hardwareMap, telemetry);
        turret.init(hardwareMap);
        led.init(hardwareMap);

        follower = Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setStartingPose(startPose);
        follower.setMaxPower(.65);

        state = State.GO_TO_LAUNCH_1;
    }

    public void loop() {

        follower.update();


        // Feedback to Driver Hub for debugging
        telemetry.addData("Current state", state);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();

        // if (in spin up, launch, find tag, etc
        if(state == State.FIND_TAG_1 ||
                state == State.SPIN_UP_1 ||
                state == State.LAUNCHING_1)
        {
            doAprilTag();
        }
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(586);

        switch (state) {
            case GO_TO_LAUNCH_1:
                follower.followPath(startToLaunching);
                state = State.WAIT_TO_FINISH_PATH_1;
                break;
            case WAIT_TO_FINISH_PATH_1:
                if(!follower.isBusy()){
                    state = State.FIND_TAG_1;
                }
                break;
            case FIND_TAG_1:
                if(id24 != null){
                    state = State.SPIN_UP_1;
                }
                break;
            case SPIN_UP_1:
                double speedError = launcher.getLaunchSpeedError();
                double angleError = turret.getAngleError();
                if (speedError < 50){
                    state = State.LAUNCHING_1;
                    driveTimer.reset();
                }
                break;
            case LAUNCHING_1:
                if (driveTimer.seconds() < 3) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                else {
                    intake.stopIntake();
                    //launcher.setMotorVelocity();
                    launcher.stopLauncher();
                    launcher.resetFeeder();
                    //Launcher.LaunchState = Launcher.LaunchState.IDLE;
                    state = State.PREPARE_TO_INTAKE_POSE_1;
                    driveTimer.reset();
                }
                break;
            case PREPARE_TO_INTAKE_POSE_1:
                if(!follower.isBusy()){
                    follower.followPath(launchingToPickupReady1, true);
                    state = State.INTAKE_1;
                }
                break;
            case INTAKE_1:
                if(!follower.isBusy()){
                    follower.followPath(pickupReady1ToPickup1);
                    intake.startIntake();
                    state = State.GO_TO_LAUNCH_2;
                }
                break;
            case GO_TO_LAUNCH_2:
                if(!follower.isBusy()){
                    follower.followPath(pickup1ToLaunching);
                    intake.stopIntake();
                    state = State.GO_BACK_TO_START_POSE;
                }
                break;
            case WAIT_TO_FINISH_PATH_2:
                if(!follower.isBusy()){
                    state = State.FIND_TAG_2;
                }
                break;
            case FIND_TAG_2:
                if(id24 != null){
                    state = State.SPIN_UP_2;
                }
                break;
            case SPIN_UP_2:
                speedError = launcher.getLaunchSpeedError();
                angleError = turret.getAngleError();
                if (speedError < 100){
                    state = State.LAUNCHING_2;
                    driveTimer.reset();
                }
                break;
            case LAUNCHING_2:
                if (driveTimer.seconds() < 3) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                else {
                    intake.stopIntake();
                    launcher.resetFeeder();
                    Launcher.LaunchState = Launcher.LaunchState.IDLE;
                    launcher.stopLauncher();
                    state = State.GO_BACK_TO_START_POSE;
                    driveTimer.reset();
                }
            case GO_BACK_TO_START_POSE:
                if(!follower.isBusy()){
                    follower.followPath(launchingToStart);
                    state = State.FINISHED;
                }
                break;
            case FINISHED:
                break;

            default:
                break;

        }


        }


    private void doAprilTag() {
        //Update the vision portal
        aprilTagWebcam.update();
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(586); // TAG ID 24 is the red goal
        aprilTagWebcam.displayDetectionTelemetry(id24);
        // NOTE: we will need a separate OPMODE (otherwise identical) that sets the target TAGID to BLUE (#20)
        if (id24 != null && id24.ftcPose != null) {
            numMissingTagReads = 0;
            double angleToTag = id24.ftcPose.bearing;
            //turret.changeTurretByDegrees(angleToTag);

            double distanceToGoalCM = id24.ftcPose.range;
            launcher.setMotorVelocityForDistance(distanceToGoalCM);
            led.setLEDGreen();
            // NOTE: use this after distance vs speed has been measured and calibrated
            //launcher.setMotorVelocityForDistance(distanceToGoalCM);
        } else if (numMissingTagReads < 100) {
            numMissingTagReads++;
            led.setLEDBlue();
        } else {
            // if we can't see the target/            // default back to neutral/default
            //turret.resetTurret();
            // and turn launch motors off
            launcher.stopLauncher();
            turret.resetTurret();
            led.setLEDRed();
        }
    }
}



