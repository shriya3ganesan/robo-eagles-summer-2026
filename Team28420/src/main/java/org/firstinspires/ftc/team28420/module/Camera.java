package org.firstinspires.ftc.team28420.module;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.team28420.types.AprilTag;
import org.firstinspires.ftc.team28420.types.MovementParams;
import org.firstinspires.ftc.team28420.types.PolarVector;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Camera {

    @Config
    public static class CameraConf {
        public static double ANGLE_MAX_VELOCITY = Math.PI;
    }

    private final AprilTagProcessor aprilTag;
    private final VisionPortal visionPortal;

    private List<AprilTagDetection> lastDetections = new ArrayList<>();

    public Camera(HardwareMap hMap) {
        WebcamName webcamName = hMap.get(WebcamName.class, "Webcam 1");

        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setOutputUnits(DistanceUnit.METER, AngleUnit.DEGREES)
                .build();

        VisionProcessor[] processors = {aprilTag, aprilTag};
        visionPortal = new VisionPortal.Builder()
                .setCamera(webcamName)
                .addProcessors(processors)
                .enableLiveView(true)
                .setAutoStopLiveView(true)
                .build();

        ElapsedTime timer = new ElapsedTime();
        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING && timer.seconds() < 3) {
            try { Thread.sleep(7); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        setManualExposure();

        FtcDashboard.getInstance().startCameraStream(visionPortal, 30);
    }

    private void setManualExposure() {
        if (visionPortal == null) return;

        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl != null) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
            exposureControl.setExposure(4, TimeUnit.MILLISECONDS);
        }

        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        if (gainControl != null) {
            gainControl.setGain(270);
        }
    }

    private PolarVector getVectorToPoint(double x, double y, double x0, double y0) {
        double theta = Math.atan2(y - y0, x - x0);
        double abs = Math.hypot(x - x0, y - y0) / 2;

        return new PolarVector(theta, Math.min(abs, 1.0));
    }

    private double getRotateForce(double angleDegrees) {
        return angleDegrees / CameraConf.ANGLE_MAX_VELOCITY * 0.5;
    }

    public MovementParams getMovementParamsToPoint(AprilTagDetection detection, double offsetX, double offsetY) {
        if (detection == null || detection.ftcPose == null) {
            return null;
        }
        PolarVector vector = getVectorToPoint(detection.ftcPose.x, detection.ftcPose.y, offsetX, offsetY);
        double rotateForce = getRotateForce(Math.toRadians(-detection.ftcPose.yaw));
        return new MovementParams(vector, rotateForce);
    }

    public MovementParams getMovementParamsToOffset(AprilTagDetection detection, double offsetX, double offsetY) {
        if (detection == null || detection.ftcPose == null) {
            return null;
        }
        PolarVector vector = getVectorToPoint(detection.ftcPose.x, detection.ftcPose.y, offsetX, offsetY);
        double rotateForce = getRotateForce(Math.toRadians(-detection.ftcPose.bearing));
        return new MovementParams(vector, rotateForce);
    }

    public AprilTagDetection getAprilTagDetection(AprilTag tag) {
        List<AprilTagDetection> currentDetections = new ArrayList<>(lastDetections);
        for (AprilTagDetection detection : currentDetections) {
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