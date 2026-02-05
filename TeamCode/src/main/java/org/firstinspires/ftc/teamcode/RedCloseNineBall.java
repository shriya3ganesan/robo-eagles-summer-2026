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
public class RedCloseNineBall extends OpMode {
    MecanumDrive drive = new MecanumDrive();
    Intake intake = new Intake();
    Launcher launcher = new Launcher();
    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();
    TurretServo turret = new TurretServo();
    LEDIndicator led = new LEDIndicator();
    int numMissingTagReads = 0;
    private Follower follower;


    private final Pose startPose = new Pose(124.6829268292683, 122.73170731707317, Math.toRadians(36)); // Start Pose of our robot.
    private final Pose launchingPose = new Pose(92, 92, Math.toRadians(45)); // Where our robot launches from
    private final Pose pickupReady1Pose = new Pose(98, 84, Math.toRadians(0)); // Ready to pick up closest row of balls
    private final Pose pickup1Pose = new Pose(125, 84, Math.toRadians(0)); // Pick up closest row of balls
    private final Pose pickupReady2Pose = new Pose(98, 60, Math.toRadians(0)); //Ready to pick up middle row of balls
    private final Pose pickup2Pose = new Pose(125, 60, Math.toRadians(0)); //Pick up middle row of balls
    private final Pose pickupReady3 = new Pose(98, 38.5, Math.toRadians(0)); //Ready to pick up far balls
    private final Pose endPose = new Pose(125, 38.5, Math.toRadians(0)); //Finish with 3 balls

    private Path startToLaunching;
    private PathChain launchingToPickupReady1, pickupReady1ToPickup1, pickup1ToLaunching, launchingToPickupReady2, pickupReady2ToPickup2, pickup2ToLaunching2, launchingToPickupReady3, pickupReady3ToFinish, finishToLaunching3;

    public void buildPaths() {

        //This sets up our first path where we back up
        startToLaunching = new Path(new BezierLine(startPose, launchingPose));
        startToLaunching.setLinearHeadingInterpolation(startPose.getHeading(), launchingPose.getHeading());

        //these next several sections set up the rest of the paths, created in the PathChain
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

        launchingToPickupReady2 = follower.pathBuilder()
                .addPath(new BezierLine(launchingPose, pickupReady2Pose))
                .setLinearHeadingInterpolation(launchingPose.getHeading(), pickupReady2Pose.getHeading())
                .build();

        pickupReady2ToPickup2 = follower.pathBuilder()
                .addPath(new BezierLine(pickupReady2Pose, pickup2Pose))
                .setLinearHeadingInterpolation(pickupReady2Pose.getHeading(), pickup2Pose.getHeading())
                .build();

        pickup2ToLaunching2 = follower.pathBuilder()
                .addPath(new BezierLine(pickup2Pose, launchingPose))
                .setLinearHeadingInterpolation(pickup2Pose.getHeading(), launchingPose.getHeading())
                .build();

        launchingToPickupReady3 =  follower.pathBuilder()
                .addPath(new BezierLine(launchingPose, pickupReady3))
                .setLinearHeadingInterpolation(launchingPose.getHeading(), pickupReady3.getHeading())
                .build();

        pickupReady3ToFinish = follower.pathBuilder()
                .addPath(new BezierLine(pickupReady3, endPose))
                .setLinearHeadingInterpolation(pickupReady3.getHeading(), endPose.getHeading())
                .build();

        finishToLaunching3 = follower.pathBuilder()
                .addPath(new BezierLine(endPose, launchingPose))
                .setLinearHeadingInterpolation(endPose.getHeading(), launchingPose.getHeading())
                .build();
    }

    public static enum State {
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
        PREPARE_TO_INTAKE_POSE_2,
        INTAKE_2,
        GO_TO_LAUNCH_3,
        WAIT_TO_FINISH_PATH_3,
        FIND_TAG_3,
        SPIN_UP_3,
        LAUNCHING_3,
        PREPARE_TO_INTAKE_POSE_3,
        INTAKE_3,
        GO_TO_LAUNCH_4,
        WAIT_TO_FINISH_PATH_4,
        SPIN_UP_4,
        LAUNCHING_4,
        FIND_TAG_4,
        GO_TO_END_POSE,
        FINISHED,
    }

    State state;
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
        follower.setMaxPower(1);

        state = State.GO_TO_LAUNCH_1;
    }

    public void loop() {

        follower.update();


        // Feedback to Driver Hub for debugging
        telemetry.addData("Current state", state);
        telemetry.addLine("Target Velocity: " + launcher.getTargetLaunchSpeed());
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.update();

        // if (in spin up, launch, find tag, etc
        if(state == State.FIND_TAG_1 ||
                state == State.SPIN_UP_1 ||
                state == State.LAUNCHING_1 ||
                state == State.FIND_TAG_2 ||
                state == State.SPIN_UP_2 ||
                state == State.LAUNCHING_2 ||
                state == State.FIND_TAG_3 ||
                state == State.SPIN_UP_3 ||
                state == State.LAUNCHING_3 ||
                state == State.FIND_TAG_4 ||
                state == State.SPIN_UP_4 ||
                state == State.LAUNCHING_4)
        {
            doAprilTag();
        }
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(24);

        switch (state) {
            case GO_TO_LAUNCH_1:
                follower.followPath(startToLaunching);
                state = State.WAIT_TO_FINISH_PATH_1;

                ///SET LAUNCHERS TO START SPINNING HERE, FIND THE IDEAL VELOCITY FOR OUR LAUNCH POSITION

                break;
            case WAIT_TO_FINISH_PATH_1:
                launcher.presetMotorVelocity(1000);
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
                if (driveTimer.seconds() < 1.5) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                else {
                    intake.stopIntake();
                    //launcher.setMotorVelocity();
                    launcher.stopLauncher();
                    launcher.resetFeeder();

                    //SET BOTH LAUNCHERS TO 0 VELOCITY HERE

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
                    follower.followPath(pickupReady1ToPickup1, .4, false);
                    intake.startIntake();
                    state = State.GO_TO_LAUNCH_2;
                }
                break;
            case GO_TO_LAUNCH_2:
                if(!follower.isBusy()){

                    //STOP INTAKE HERE TO AVOID OVERFLOWING BALLS BEFORE LAUNCHING

                    //START SPINNING UP BOTH MOTORS HERE TO IDEAL LAUNCH VELOCITY FROM SHOOTING POSITION

                    follower.followPath(pickup1ToLaunching);
                    intake.stopIntake();
                    state = State.WAIT_TO_FINISH_PATH_2;
                }
                break;
            case WAIT_TO_FINISH_PATH_2:
                launcher.presetMotorVelocity(1000);
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
                if (driveTimer.seconds() < 1.5) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                else {
                    intake.stopIntake();
                    launcher.resetFeeder();

                    //SET LAUNCHERS BOTH TO 0 VELOCITY

                    Launcher.LaunchState = Launcher.LaunchState.IDLE;
                    launcher.stopLauncher();
                    state = State.PREPARE_TO_INTAKE_POSE_2;
                    driveTimer.reset();
                }
                break;
            case PREPARE_TO_INTAKE_POSE_2:
                if(!follower.isBusy()){
                    follower.followPath(launchingToPickupReady2, true);
                    state = State.INTAKE_2;
                }
                break;
            case INTAKE_2:
                if(!follower.isBusy()){
                    follower.followPath(pickupReady2ToPickup2, .4, false);
                    intake.startIntake();
                    state = State.GO_TO_LAUNCH_3;
                }
                break;
            case GO_TO_LAUNCH_3:
                if(!follower.isBusy()){
                    intake.stopIntake();
                    follower.followPath(pickup2ToLaunching2);
                    state = State.WAIT_TO_FINISH_PATH_3;
                }
                break;
            case WAIT_TO_FINISH_PATH_3:
                if(!follower.isBusy()){
                    launcher.presetMotorVelocity(1000);
                    state = State.FIND_TAG_3;
                }
                break;
            case FIND_TAG_3:
                if(id24 != null){
                    state = State.SPIN_UP_3;
                }
                break;
            case SPIN_UP_3:
                speedError = launcher.getLaunchSpeedError();
                angleError = turret.getAngleError();
                if (speedError < 100){
                    state = State.LAUNCHING_3;
                    driveTimer.reset();
                }
                break;
            case LAUNCHING_3:
                if (driveTimer.seconds() < 1.5) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                else {
                    intake.stopIntake();
                    launcher.resetFeeder();
                    Launcher.LaunchState = Launcher.LaunchState.IDLE;
                    launcher.stopLauncher();
                    state = State.PREPARE_TO_INTAKE_POSE_3;
                    driveTimer.reset();
                }
                break;
            case PREPARE_TO_INTAKE_POSE_3:
                if(!follower.isBusy()){
                    follower.followPath(launchingToPickupReady3, true);
                    state = State.INTAKE_3;
                }

            case INTAKE_3:
                if(!follower.isBusy()){
                    intake.startIntake();
                    follower.followPath(pickupReady3ToFinish);
                    driveTimer.reset();
                    state = State.GO_TO_LAUNCH_4;
                }
                break;

            case GO_TO_LAUNCH_4:
                if(!follower.isBusy()){
                    intake.stopIntake();
                    follower.followPath(finishToLaunching3);
                    state = State.WAIT_TO_FINISH_PATH_4;
                }
                break;
            case WAIT_TO_FINISH_PATH_4:
                if(!follower.isBusy()){
                    launcher.presetMotorVelocity(1000);
                    state = State.FIND_TAG_4;
                }
                break;
            case FIND_TAG_4:
                if(id24 != null){
                    state = State.SPIN_UP_4;
                }
                break;
            case SPIN_UP_4:
                speedError = launcher.getLaunchSpeedError();
                angleError = turret.getAngleError();
                if (speedError < 100){
                    driveTimer.reset();
                    state = State.LAUNCHING_4;

                }
                break;
            case LAUNCHING_4:
                if (driveTimer.seconds() < 1.5) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                else {
                    intake.stopIntake();
                    launcher.resetFeeder();
                    Launcher.LaunchState = Launcher.LaunchState.IDLE;
                    launcher.stopLauncher();
                    state = State.GO_TO_END_POSE;
                    driveTimer.reset();
                }
                break;
            case GO_TO_END_POSE:
                if(!follower.isBusy()){
                    intake.startIntake();
                    follower.followPath(launchingToPickupReady3);
                    driveTimer.reset();
                    state = State.FINISHED;
                }
                break;

            case FINISHED:
                if(!follower.isBusy()) {
                        if(driveTimer.seconds() > 2)
                            intake.stopIntake();
                }
                break;

            default:
                break;

        }


        }


    private void doAprilTag() {
        //Update the vision portal
        aprilTagWebcam.update();
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(24); // TAG ID 24 is the red goal
        //aprilTagWebcam.displayDetectionTelemetry(id24);
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



