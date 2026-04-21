package org.firstinspires.ftc.team28420.processors;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.team28420.types.AprilTag;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AprilTagWrapper {

    public static class AprilTagWrapperConfig {
        public static Position CAMERA_POSITION = new Position(DistanceUnit.MM, 0.0, 0.0, 0.0, 0);
        public static YawPitchRollAngles CAMERA_ORIENTATION = new YawPitchRollAngles(AngleUnit.DEGREES, 0, 0, 0, 0);
    }

    private final AprilTagProcessor processor;
    private List<AprilTagDetection> lastDetections = new ArrayList<>();

    public AprilTagWrapper() {
        this.processor = new AprilTagProcessor.Builder()
                .setCameraPose(AprilTagWrapperConfig.CAMERA_POSITION, AprilTagWrapperConfig.CAMERA_ORIENTATION)
                .setDrawTagOutline(true)
                .setOutputUnits(DistanceUnit.CM, AngleUnit.RADIANS)
                .build();
    }

    public void setup() {
        this.processor.setDecimation(2);
    }

    public void update() {
        lastDetections = processor.getDetections();
    }

    public VisionProcessor getProcessor() {
        return processor;
    }

    public AprilTagDetection getDetection(AprilTag aprilTag) {
        for (AprilTagDetection detection : getDetections()) {
            if (AprilTag.fromId(detection.id) == aprilTag) {
                return detection;
            }
        }
        return null;
    }

    public void log(Telemetry telemetry) {
        telemetry.addData("apriltags detected", getDetections().size());
        for (AprilTagDetection detection : getDetections()) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format(Locale.ROOT, "\n(ID: %d) %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format(Locale.ROOT, "X %6.1f, Y %6.1f, Z %6.1f", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format(Locale.ROOT, "P %6.1f, R %6.1f, Y %6.1f", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format(Locale.ROOT, "R %6.1f, B %6.1f, E %6.1f", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format(Locale.ROOT, "(ID: %d) UNKNOWN", detection.id));
            }
        }
    }

    private List<AprilTagDetection> getDetections() {
        return lastDetections;
    }
}
