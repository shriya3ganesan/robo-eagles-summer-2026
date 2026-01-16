//This code assumes a battery voltage output of 13V

package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.mechanisms.AprilTagsWebcam;
import org.firstinspires.ftc.teamcode.mechanisms.Intake;
import org.firstinspires.ftc.teamcode.mechanisms.LEDIndicator;
import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.mechanisms.TurretServo;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@Autonomous
public class RedAutoClose extends OpMode {
    MecanumDrive drive = new MecanumDrive();
    Intake intake = new Intake();
    Launcher launcher = new Launcher();
    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();
    TurretServo turret = new TurretServo();
    LEDIndicator led = new LEDIndicator();
    int numMissingTagReads = 0;

    enum State {
        START_TIMER_FIRST_TIME,
        FIND_TAG,
        BACK_UP,
        SPIN_UP,
        LAUNCHING,
        ROTATE_RIGHT,
        MOVE_RIGHT,
        MOVE_FORWARD,
        HOLD_FORWARD,
        MOVE_BACKWARD,
        MOVE_LEFT,
        ROTATE_LEFT,
        FIND_TAG_2,
        SPIN_UP_2,
        LAUNCHING_2,
        ROTATE_RIGHT_2,
        MOVE_FORWARD_9,
        MOVE_RIGHT_2,
        MOVE_FORWARD_2,
        HOLD_FORWARD_2,
        MOVE_BACKWARD_2,
        MOVE_LEFT_2,
        ROTATE_LEFT_2,
        FIND_TAG_3,
        LAUNCHING_3,
        MOVE_RIGHT_3,
        FINISHED,
    }
    State state = State.FIND_TAG;
    ElapsedTime driveTimer = new ElapsedTime();

    private void ShootUsingVelocity(){
        launcher.lowerLaunch.setVelocity(1000);
        launcher.upperLaunch.setVelocity(1000);
    }


    @Override
    public void init() {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        launcher.init(hardwareMap);
        aprilTagWebcam.init(hardwareMap, telemetry);
        turret.init(hardwareMap);
        led.init(hardwareMap);

        state = State.START_TIMER_FIRST_TIME;
    }

    @Override
    public void loop() {
        telemetry.addData("Current state", state);
        // if (in spin up, launch, find tag, etc
        if(state == State.FIND_TAG ||
            state == State.FIND_TAG_2 ||
            state == State.LAUNCHING ||
            state == State.LAUNCHING_2 ||
            state == State.SPIN_UP ||
            state == State.SPIN_UP_2)
        {
            doAprilTag();
        }
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(24);

        switch (state) {
            case START_TIMER_FIRST_TIME:
                state = State.BACK_UP;
                driveTimer.reset();
                break;
            case BACK_UP:
                if (driveTimer.seconds() < 1.5) {
                    drive.drive(-0.4, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.FIND_TAG;
                }
                break;
            case FIND_TAG:
                if(id24 != null){
                    state = State.SPIN_UP;
                }
                break;
            case SPIN_UP:
                double speedError = launcher.getLaunchSpeedError();
                double angleError = turret.getAngleError();
                if (speedError < 50){
                    state = State.LAUNCHING;
                    driveTimer.reset();
                }
                break;
            case LAUNCHING:
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
                    state = State.ROTATE_RIGHT;
                    driveTimer.reset();
                }
                break;
            case ROTATE_RIGHT:
                if (driveTimer.seconds() < 0.33) {
                    drive.drive(0.0, 0.0, 0.4);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.MOVE_RIGHT;
                    driveTimer.reset();
                }
                break;
            case MOVE_RIGHT:
                if (driveTimer.seconds() < .7) {
                    drive.drive(0.0, 0.5, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.MOVE_FORWARD;
                    driveTimer.reset();
                }
                break;
            case MOVE_FORWARD:
                intake.startIntake();
                if (driveTimer.seconds() < 1.15) {
                    drive.drive(0.5, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.HOLD_FORWARD;
                    driveTimer.reset();
                }
                break;
            case HOLD_FORWARD:
                if (driveTimer.seconds() < .5) {
                    drive.drive(0.0, 0.0, 0.0);
                }
                else {
                    state = State.MOVE_BACKWARD;
                    driveTimer.reset();
                }
                break;
            case MOVE_BACKWARD:
                intake.stopIntake();
                if (driveTimer.seconds() < 1.25) {
                    drive.drive(-0.5, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.ROTATE_LEFT;
                    driveTimer.reset();
                }
                break;
            case MOVE_LEFT:
                // skipping this state
                if (driveTimer.seconds() < .7) {
                    drive.drive(0.0, -0.5, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.ROTATE_LEFT;
                    driveTimer.reset();
                }
                break;
            case ROTATE_LEFT:
                if (driveTimer.seconds() < 0.46) {
                    drive.drive(0.0, 0.0, -0.4);
                }
                else {
                    drive.drive(0,0,0);
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
                    state = State.ROTATE_RIGHT_2;
                    driveTimer.reset();
                }
                break;
            case ROTATE_RIGHT_2:
                if (driveTimer.seconds() < .41) {
                    drive.drive(0.0, 0.0, 0.4);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.MOVE_FORWARD_9;
                    driveTimer.reset();
                }
                break;
            case MOVE_FORWARD_9:
                if (driveTimer.seconds() < .3) {
                    drive.drive(0.4, 0.0, 0.0);
                }
                else {
                    drive.drive(0, 0, 0);
                    state = State.MOVE_RIGHT_2;
                }
                break;
            case MOVE_RIGHT_2:
                if (driveTimer.seconds() < 2.12) {
                    drive.drive(0.0, 0.4, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    driveTimer.reset();
                    state = State.MOVE_FORWARD_2;
                                    }
                break;
            case MOVE_FORWARD_2:
                intake.startIntake();
                if (driveTimer.seconds() < 1) {
                    drive.drive(0.4, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.HOLD_FORWARD_2;
                    driveTimer.reset();
                }
                break;
            case HOLD_FORWARD_2:
                if (driveTimer.seconds() < .5) {
                    drive.drive(0.0, 0.0, 0.0);
                }
                else {
                    state = State.MOVE_BACKWARD_2;
                    driveTimer.reset();
                }
                break;
            case MOVE_BACKWARD_2:
                intake.stopIntake();
                if (driveTimer.seconds() < 1.1) {
                    drive.drive(-0.4, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.MOVE_LEFT_2;
                    driveTimer.reset();
                }
                break;
            case MOVE_LEFT_2:
                // skipping this state
                if (driveTimer.seconds() < 1.3) {
                    drive.drive(0.0, -0.5, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.ROTATE_LEFT_2;
                    driveTimer.reset();
                }
                break;
            case ROTATE_LEFT_2:
                if (driveTimer.seconds() < 0.42) {
                    drive.drive(0.0, 0.0, -0.4);
                }
                else {
                    drive.drive(0,0,0);
                    driveTimer.reset();
                    state = State.LAUNCHING_3;
                }
                break;

            case LAUNCHING_3:
                if (driveTimer.seconds() < 1.1) {
                    launcher.lowerLaunch.setVelocity(1000);
                    launcher.upperLaunch.setVelocity(1000);
                }
                else if (driveTimer.seconds() < 4) {

                    intake.startIntake();
                    launcher.loadBall();
                }
                else {
                    intake.stopIntake();
                    launcher.resetFeeder();
                    launcher.stopLauncher();
                    launcher.lowerLaunch.setVelocity(0);
                    launcher.upperLaunch.setVelocity(0);
                    state = State.MOVE_RIGHT_3;
                    driveTimer.reset();
                }
                break;

            case MOVE_RIGHT_3:
                if (driveTimer.seconds() < 1) {
                    drive.drive(0.0, 0.4, 0.0);
                }
                else if (driveTimer.seconds() < 1.4) {
                    drive.drive(0.0, 0.4, 0.4);
                }
                else if (driveTimer.seconds() < 2.4) {
                    drive.drive(0.0, 0.4, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.FINISHED;
                }

                break;
            case FINISHED:
                telemetry.addData("Current state", state);
                break;
            default:
                break;
        }

    }
    private void doAprilTag(){
        //Update the vision portal
        aprilTagWebcam.update();
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(24); // TAG ID 24 is the red goal
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
        } else if (numMissingTagReads < 100){
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
