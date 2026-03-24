package org.firstinspires.ftc.team28420.module;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.team28420.config.CameraConf;
import org.firstinspires.ftc.team28420.types.AprilTag;
import org.firstinspires.ftc.team28420.types.MovementParams;
import org.firstinspires.ftc.team28420.types.PolarVector;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Camera {

    private final AprilTagProcessor aprilTag;
    private final VisionPortal visionPortal;

    private List<AprilTagDetection> lastDetections = new ArrayList<>();

    public Camera(HardwareMap hMap) throws InterruptedException {
        WebcamName webcamName = hMap.get(WebcamName.class, CameraConf.WEBCAM);

        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setOutputUnits(DistanceUnit.METER, AngleUnit.RADIANS)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .addProcessor(aprilTag)
                .enableLiveView(true)
                .setAutoStopLiveView(true)
                .build();

        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            Thread.sleep(20);
        }

        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        exposureControl.setMode(ExposureControl.Mode.Manual);
        exposureControl.setExposure(5, TimeUnit.MILLISECONDS);

        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(250);

        FtcDashboard.getInstance().startCameraStream(visionPortal, 30);
    }

    private PolarVector getVectorToPoint(double x, double y, double x0, double y0) {
        double theta = Math.atan2(y - y0, x - x0);
        double abs = Math.hypot(x - x0, y - y0) / 75;
        return new PolarVector(theta, abs > 1 ? 1 : abs);

    }

    private double getRotateForce(double angle) {
        return angle / CameraConf.ANGLE_MAX_VELOCITY * 0.5;
    }

    public MovementParams getMovementParamsToPoint(AprilTagDetection detection, double offsetX, double offsetY) {
        if (detection == null) {
            return new MovementParams(new PolarVector(0, 0), 0);
        }
        PolarVector vector = getVectorToPoint(detection.ftcPose.x, detection.ftcPose.y, offsetX, offsetY);
        double rotateForce = getRotateForce(Math.toRadians(-detection.ftcPose.yaw));
        return new MovementParams(vector, rotateForce);
    }

    public MovementParams getMovementParamsToOffset(AprilTagDetection detection, double offsetX, double offsetY) {
        if (detection == null) {
            return new MovementParams(new PolarVector(0, 0), 0);
        }
        PolarVector vector = getVectorToPoint(detection.ftcPose.x, detection.ftcPose.y, offsetX, offsetY);
        double rotateForce = getRotateForce(Math.toRadians(-detection.ftcPose.bearing));
        return new MovementParams(vector, rotateForce);
    }

    public AprilTagDetection getAprilTagDetection(AprilTag tag) {
        for (AprilTagDetection detection : lastDetections) {
            if (AprilTag.fromId(detection.id) == tag) {
                return detection;
            }
        }
        return null;
    }

    public void updateApriltags() {
        lastDetections = aprilTag.getDetections();
    }

    public void log(Telemetry telemetry) {
        telemetry.addLine("=== CAMERA ===");
        telemetry.addData("# apriltags detected", lastDetections.size());

        for (AprilTagDetection detection : lastDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format(Locale.ROOT, "\n(ID: %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format(Locale.ROOT, "X %6.1f, Y %6.1f, Z %6.1f", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format(Locale.ROOT, "P %6.1f, R %6.1f, Y %6.1f", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format(Locale.ROOT, "R %6.1f, B %6.1f, E %6.1f", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format(Locale.ROOT, "(ID: %d) UNKNOWN", detection.id));
            }
        }
        telemetry.addLine("=== CAMERA ===");
    }

    public void close() {
        visionPortal.close();
    }
}