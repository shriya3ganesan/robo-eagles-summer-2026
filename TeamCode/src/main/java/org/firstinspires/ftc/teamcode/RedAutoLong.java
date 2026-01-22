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
public class RedAutoLong extends OpMode {
    MecanumDrive drive = new MecanumDrive();
    Intake intake = new Intake();
    Launcher launcher = new Launcher();
    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();
    TurretServo turret = new TurretServo();
    LEDIndicator led = new LEDIndicator();
    int numMissingTagReads = 0;

    enum State {
        MOVE_FORWARD_1_0,
        ROTATE_RIGHT_1_1,
        FIND_TAG_1_2,
        SPIN_UP_1_3,
        LAUNCHING_1_4,
        MOVE_FORWARD_1_5,
        ROTATE_RIGHT_1_6,
        FORWARD_PICKUP_1_7,
        HOLD_FORWARD_1_8,
        MOVE_BACK_1_9,
        ROTATE_LEFT_1_10,
        MOVE_BACK_1_11,
        FIND_TAG_2_0,
        SPIN_UP_2_1,
        LAUNCHING_2_2,
        ROTATE_RIGHT_2_3,
        MOVE_FORWARD_2_4,
        ROTATE_RIGHT_2_5,
        STRAFE_PICKUP_2_6,
        HOLD_FORWARD_2_7,
        ROTATE_LEFT_2_8,
        MOVE_BACK_2_9,
        ROTATE_LEFT_2_10,
        FIND_TAG_3_0,
        SPIN_UP_3_1,
        LAUNCHING_3_2,
        STRAFE_RIGHT_3_3,

        FINISHED,
    }
    State state = State.MOVE_FORWARD_1_0;
    ElapsedTime driveTimer = new ElapsedTime();

    @Override
    public void init() {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        launcher.init(hardwareMap);
        aprilTagWebcam.init(hardwareMap, telemetry);
        turret.init(hardwareMap);
        led.init(hardwareMap);

        state = State.MOVE_FORWARD_1_0;
    }

    @Override
    public void loop() {
        telemetry.addData("Current state", state);
        doAprilTag();
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(24);


        switch (state) {
            case MOVE_FORWARD_1_0:
                driveTimer.reset();
                while (driveTimer.seconds() < 0.4) {
                    drive.drive(0.3, 0.0, 0.0);
                }
                drive.drive(0,0,0);
                state = State.ROTATE_RIGHT_1_1;
                break;

            case ROTATE_RIGHT_1_1:
                driveTimer.reset();
                while (driveTimer.seconds() < 0.28) {
                    drive.drive(0., 0.0, 0.3);
                }
                drive.drive(0,0,0);
                state = State.FIND_TAG_1_2;
                break;

            case FIND_TAG_1_2:
                if(id24 != null){
                    state = State.SPIN_UP_1_3;
                }
                break;

            case SPIN_UP_1_3:
                double speedError1 = launcher.getLaunchSpeedError();
                double angleError1 = turret.getAngleError();
                if (speedError1 < 100 && angleError1 < 2){
                  state = State.LAUNCHING_1_4;
                }
                break;

            case LAUNCHING_1_4:
                driveTimer.reset();
                while (driveTimer.seconds() < 3) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                intake.stopIntake();
                launcher.resetFeeder();
                state = State.MOVE_FORWARD_1_5;
                break;

            case MOVE_FORWARD_1_5:
                driveTimer.reset();
                while (driveTimer.seconds() < 1.4) {
                    drive.drive(0.3, 0.0, 0.0);
                }
                drive.drive(0,0,0);
                state = State.ROTATE_RIGHT_1_6;
                break;

            case ROTATE_RIGHT_1_6:
                driveTimer.reset();
                while (driveTimer.seconds() < 1) {
                    drive.drive(0,0,0.3);
                }
                drive.drive(0,0,0);
                launcher.lowerLaunch.setVelocity(0);
                launcher.upperLaunch.setVelocity(0);
                state = State.FORWARD_PICKUP_1_7;
                break;

            case FORWARD_PICKUP_1_7:
                driveTimer.reset();
                intake.startIntake();
                if (driveTimer.seconds() < 1.3) {
                    drive.drive(0.4, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    driveTimer.reset();
                    state = RedAutoLong.State.HOLD_FORWARD_1_8;
                                    }
                break;

            case HOLD_FORWARD_1_8:
                if (driveTimer.seconds() < .5) {
                    drive.drive(0.0, 0.0, 0.0);
                }
                else {
                    driveTimer.reset();
                    state = State.MOVE_BACK_1_9;
                }
                break;

            case MOVE_BACK_1_9:
                intake.stopIntake();
                if (driveTimer.seconds() < 1.3) {
                    drive.drive(-0.4, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    driveTimer.reset();
                    state = State.ROTATE_LEFT_1_10;
                }
                break;

            case ROTATE_LEFT_1_10:
                if (driveTimer.seconds() < 0.5) {
                    drive.drive(0.0, 0.0, -0.4);
                }
                else {
                    drive.drive(0,0,0);
                    driveTimer.reset();
                    state = State.MOVE_BACK_1_11;
                }
                break;

            case MOVE_BACK_1_11:
                if (driveTimer.seconds() < 1.1) {
                    drive.drive(-0.4, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    driveTimer.reset();
                    state = State.FIND_TAG_2_0;
                }
                break;

            case FIND_TAG_2_0:
                if(id24 != null){
                    state = State.SPIN_UP_2_1;
                }
                break;

            case SPIN_UP_2_1:
                double speedError2 = launcher.getLaunchSpeedError();
                double angleError2 = turret.getAngleError();
                if (speedError2 < 100 && angleError2 < 2){
                    state = State.LAUNCHING_2_2;
                }
                break;

            case LAUNCHING_2_2:
                driveTimer.reset();
                while (driveTimer.seconds() < 3) {
                    launcher.loadBall();
                    intake.startIntake();
                }
                intake.stopIntake();
                launcher.resetFeeder();
                state = State.ROTATE_RIGHT_2_3;
                break;

            case ROTATE_RIGHT_2_3:
                driveTimer.reset();
                while (driveTimer.seconds() < 0.4) {
                    drive.drive(0., 0.0, 0.3);
                }
                drive.drive(0,0,0);
                state = State.MOVE_FORWARD_2_4;
                break;

            case MOVE_FORWARD_2_4:
                driveTimer.reset();
                while (driveTimer.seconds() < 2.4) {
                    drive.drive(0.4, 0.0, 0.0);
                }
                drive.drive(0,0,0);
                state = State.ROTATE_RIGHT_2_5;
                break;

            case ROTATE_RIGHT_2_5:
                driveTimer.reset();
                while (driveTimer.seconds() < 0.28) {
                    drive.drive(0., 0.0, 0.3);
                }
                drive.drive(0,0,0);
                state = State.STRAFE_PICKUP_2_6;
                break;

            case STRAFE_PICKUP_2_6:
                intake.startIntake();
                if (driveTimer.seconds() < 1) {
                    drive.drive(0.4, 0.4, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = RedAutoLong.State.HOLD_FORWARD_2_7;
                    driveTimer.reset();
                }
                break;

            case HOLD_FORWARD_2_7:
                driveTimer.reset();
                while (driveTimer.seconds() < 0.4) {
                    drive.drive(0.0, 0.0, 0.0);
                }
                drive.drive(0,0,0);
                state = State.ROTATE_LEFT_2_8;
                break;

            case ROTATE_LEFT_2_8:
                if (driveTimer.seconds() < 0.4) {
                    drive.drive(-0.3, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.MOVE_BACK_1_11;
                    driveTimer.reset();
                }
                break;

            case MOVE_BACK_2_9:
                intake.stopIntake();
                if (driveTimer.seconds() < 1.1) {
                    drive.drive(-0.4, 0.0, 0.0);
                }
                else {
                    drive.drive(0,0,0);
                    driveTimer.reset();
                    state = State.ROTATE_LEFT_2_10;
                }
                break;

            case ROTATE_LEFT_2_10:
                if (driveTimer.seconds() < 0.5) {
                    drive.drive(0.0, 0.0, -0.3);
                }
                else {
                    drive.drive(0,0,0);
                    state = State.FIND_TAG_3_0;
                    driveTimer.reset();
                }
                break;

            case FIND_TAG_3_0:
                if(id24 != null){
                    state = State.SPIN_UP_3_1;
                }
                break;

            case SPIN_UP_3_1:
                double speedError3 = launcher.getLaunchSpeedError();
                double angleError3 = turret.getAngleError();
                if (speedError3 < 100 && angleError3 < 2){
                    state = State.LAUNCHING_3_2;
                }
                break;

            case LAUNCHING_3_2:
                driveTimer.reset();
                while (driveTimer.seconds() < 3) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                intake.stopIntake();
                launcher.resetFeeder();
                state = State.STRAFE_RIGHT_3_3;
                break;

            case STRAFE_RIGHT_3_3:
                driveTimer.reset();
                while (driveTimer.seconds() < 0.7) {
                    drive.drive(0., 0.4, 0.0);
                }
                drive.drive(0,0,0);
                state = State.FINISHED;
                break;

            case FINISHED:
                telemetry.addData("Current state", state);
                break;
            default:

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
            turret.changeTurretByDegrees(angleToTag);

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