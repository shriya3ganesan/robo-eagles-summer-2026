package org.firstinspires.ftc.teamcode;

import android.util.Size;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.firstinspires.ftc.teamcode.Tuning.*;
import static org.firstinspires.ftc.teamcode.Tuning.changes;
import static org.firstinspires.ftc.teamcode.Tuning.stopRobot;
import static org.firstinspires.ftc.teamcode.Tuning.telemetryM;

@Autonomous(name = "AprilTag Tracker V2")
public class AprilTagTrackerV2 extends OpMode {

    // Adjust these numbers to suit your robot.
    final double DESIRED_DISTANCE = 15.0; // Keep in inches! (Usually 10-15 inches for backdrops)

    final double SPEED_GAIN  =  0.02;
    final double STRAFE_GAIN =  0.015;
    final double TURN_GAIN   =  0.01;

    final double MAX_AUTO_SPEED  = 0.5;
    final double MAX_AUTO_STRAFE = 0.5;
    final double MAX_AUTO_TURN   = 0.3;

    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;

    private static final boolean USE_WEBCAM = true;
    private static final int DESIRED_TAG_ID = 6;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;
    private AprilTagDetection desiredTag = null;
    private final ArrayList<Double> velocities = new ArrayList<>();
    public static double DISTANCE = 48;
    public static double RECORD_NUMBER = 10;

    private boolean end;

    // Variable to ensure exposure optimization only attempts setup once
    private boolean exposureConfigured = false;


    @Override
    public void init() {

        follower = Constants.createFollower(hardwareMap);

        initAprilTag();

        frontLeftDrive  = hardwareMap.get(DcMotor.class, "leftFront");
        frontRightDrive = hardwareMap.get(DcMotor.class, "rightFront");
        backLeftDrive   = hardwareMap.get(DcMotor.class, "leftBack");
        backRightDrive  = hardwareMap.get(DcMotor.class, "rightBack");

        // Single motor reverse layout adjusted to your chassis configuration
        frontLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);

        exposureConfigured = false; // Reset setup flag

        telemetry.addData("Status", "Hardware Initialized safely.");
        telemetry.update();
    }

    @Override
    public void init_loop() {
        // Safe Check: Attempt exposure adjustments only when the video pipeline begins streaming frames
        if (USE_WEBCAM && !exposureConfigured) {
            if (visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING) {
                setManualExposure(6, 50); // Safe to call directly; loops no longer required
                exposureConfigured = true;
            } else {
                telemetry.addLine("Camera Status: Warming up stream pipeline...");
            }
        }

        boolean targetFound = false;
        double  drive       = 0;
        double  strafe      = 0;
        double  turn        = 0;

        desiredTag = null;

        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                if ((DESIRED_TAG_ID < 0) || (detection.id == DESIRED_TAG_ID)) {
                    targetFound = true;
                    desiredTag = detection;
                    break;
                } else {
                    telemetry.addData("Skipping", "Tag ID %d is not desired", detection.id);
                }
            } else {
                telemetry.addData("Unknown", "Tag ID %d has empty metadata context", detection.id);
            }
        }

        if (targetFound) {
            telemetry.addData("\n>", "Target Found and Locked. Press START to Begin Approaching.\n");
            telemetry.addData("Found", "ID %d (%s)", desiredTag.id, desiredTag.metadata.name);
            telemetry.addData("AprilTag Relative x", "%5.1f inches", desiredTag.ftcPose.x);
            telemetry.addData("AprilTag Relative y", "%3.0f inches", desiredTag.ftcPose.y);
            telemetry.addData("AprilTag Range", "%3.0f inches", desiredTag.ftcPose.range);

            double FieldX   = (desiredTag.ftcPose.x + 129.1227) ;
            double FieldY = (-desiredTag.ftcPose.y + 126.3925);
            follower.setStartingPose(new Pose(FieldX, FieldY));

            telemetry.addData("FieldX", "%3.0f inches", FieldX);
            telemetry.addData("FieldY", "%3.0f inches", FieldY);

        } else {
            telemetry.addLine("\n> Scanning For Target...");
            drive  = 0;
            turn   = 0.2; // Pivot at 20% power while searching to limit hardware shake
            strafe = 0;
        }

        telemetry.update();
        moveRobot(drive, strafe, turn);
    }

    public void start() {
        for (int i = 0; i < RECORD_NUMBER; i++) {
            velocities.add(0.0);
        }
        follower.startTeleopDrive(true);
        follower.update();
        end = false;
    }

    public void loop() {
        // Keep Pedro Pathing's internal coordinate math updating
        follower.update();

        if (!end) {
            // Check if the robot has driven past the target distance threshold
            if (Math.abs(follower.getPose().getX()) > (DISTANCE + 72)) {
                end = true;
                stopRobot(); // Safe hard stop function from Tuning
            } else {
                // Keep driving straight ahead at full speed
                follower.setTeleOpDrive(1, 0, 0, true);
            }
        } else {
            // Extra safety guard: ensure the drivetrain holds zero power once finished
            stopRobot();
        }

        // Output basic, clean status information directly to your Driver Station screen
        telemetry.addData("Current X Position", follower.getPose().getX());
        telemetry.addData("Target X Distance", (DISTANCE + 72));
        telemetry.addData("Is Finished", end);
        telemetry.update();
    }
    public void moveRobot(double drive, double strafe, double turn) {
        double[] speeds = {
                (drive + strafe + turn),
                (drive - strafe - turn),
                (drive - strafe + turn),
                (drive + strafe - turn)
        };

        double max = Math.abs(speeds[0]);
        for (double speed : speeds) {
            if (max < Math.abs(speed)) max = Math.abs(speed);
        }

        if (max > 1) {
            for (int i = 0; i < speeds.length; i++) speeds[i] /= max;
        }

        frontLeftDrive.setPower(speeds[0]);
        frontRightDrive.setPower(speeds[1]);
        backLeftDrive.setPower(speeds[2]);
        backRightDrive.setPower(speeds[3]);
    }

    private void initAprilTag() {
        AprilTagLibrary testLibrary = new AprilTagLibrary.Builder()
                .addTag(6, "FIH", 6.8125, DistanceUnit.INCH)
                .addTag(24, "RED", 6.5, DistanceUnit.INCH)
                .addTag(20, "BLUE", 6.5, DistanceUnit.INCH)
                .build();

        aprilTag = new AprilTagProcessor.Builder()
                .setDrawTagOutline(true)
                .setTagFamily(AprilTagProcessor.TagFamily.TAG_36h11)
                .setTagLibrary(testLibrary)
                .setLensIntrinsics(243.0, 243.0, 320.0, 240.0)
                .build();

        aprilTag.setDecimation(3);

        VisionPortal.Builder builder = new VisionPortal.Builder();
        if (USE_WEBCAM) {
            builder.setCamera(hardwareMap.get(WebcamName.class, "NeverGonnaGiveYouUp"));
        } else {
            builder.setCamera(BuiltinCameraDirection.BACK);
        }

        builder.setCameraResolution(new Size(640, 480));
        builder.enableLiveView(true);
        builder.setStreamFormat(VisionPortal.StreamFormat.YUY2);
        builder.setAutoStopLiveView(false);
        builder.addProcessor(aprilTag);

        visionPortal = builder.build();
        visionPortal.setProcessorEnabled(aprilTag, true);
    }

    /*
      Asynchronous safe manual exposure config for standard OpMode frameworks
    */
    private void setManualExposure(int exposureMS, int gain) {
        if (visionPortal == null) return;

        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
        }
        exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);

        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(gain);
    }
}