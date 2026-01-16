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
public class BlueAutoLong extends OpMode {
    MecanumDrive drive = new MecanumDrive();
    Intake intake = new Intake();
    Launcher launcher = new Launcher();
    AprilTagsWebcam aprilTagWebcam = new AprilTagsWebcam();
    TurretServo turret = new TurretServo();
    LEDIndicator led = new LEDIndicator();
    int numMissingTagReads = 0;

    enum State {
        FIND_TAG,
        DELAY_START,
        DELAY,
        SPIN_UP,
        LAUNCHING,
        MOVE_FORWARD,
        FINISHED
    }
    State state = State.FIND_TAG;
    ElapsedTime driveTimer = new ElapsedTime();

    @Override
    public void init() {
        drive.init(hardwareMap);
        intake.init(hardwareMap);
        launcher.init(hardwareMap);
        aprilTagWebcam.init(hardwareMap, telemetry);
        turret.init(hardwareMap);
        led.init(hardwareMap);

        state = State.FIND_TAG;
    }

    @Override
    public void loop() {
        telemetry.addData("Current state", state);
        doAprilTag();
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(20);

        switch (state) {
            case FIND_TAG:
                if(id24 != null){
                    state = State.SPIN_UP;
                }
                break;
            case SPIN_UP:
                double speedError = launcher.getLaunchSpeedError();
                double angleError = turret.getAngleError();
                if (speedError < 100 && angleError < 2){
                    state = State.LAUNCHING;
                }
                break;
            case LAUNCHING:
                driveTimer.reset();
                while (driveTimer.seconds() < 3) {
                    intake.startIntake();
                    launcher.loadBall();
                }
                intake.stopIntake();
                launcher.resetFeeder();
                state = State.MOVE_FORWARD;
                break;
            case MOVE_FORWARD:
                driveTimer.reset();
                while (driveTimer.seconds() < 1) {
                    drive.drive(0.3, 0.0, 0.0);
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
        AprilTagDetection id24 = aprilTagWebcam.getTagBySpecificId(20); // TAG ID 24 is the red goal
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