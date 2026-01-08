package org.firstinspires.ftc.teamcode.robot;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import android.graphics.Color;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import java.util.List;
import java.util.Locale;

public class VisionController {
    private final RobotHardware robot;
    private final Telemetry telemetry;
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private final double MIN_INTAKE_COLOR_DETECTION_DISTANCE = 14.0; // Minimum distance for detecting color in MM

    // Constructor
    public VisionController(RobotHardware RoboRoar) {
        this.robot = RoboRoar;
        this.telemetry = RoboRoar.telemetry;
    }

    // Vision setup
    public void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setCameraPose(robot.cameraPosition, robot.cameraOrientation)
                .build();

        visionPortal = new VisionPortal.Builder()
                .setCamera(robot.camera)
                .addProcessor(aprilTag)
                .build();
    }

    public AprilTagProcessor getAprilTag() {
        return aprilTag;
    }

    public VisionPortal getVisionPortal() {
        return visionPortal;
    }

    public int[] findTagPattern(AprilTagProcessor aprilTag) {
        if (aprilTag == null || aprilTag.getDetections().isEmpty()) {
            return new int[]{0, 0, 0, 0};
        }
        for (AprilTagDetection detection : aprilTag.getDetections()) {
            switch (detection.id) {
                case 21: return new int[]{21, 2, 1, 1}; // ID 21: GPP
                case 22: return new int[]{22, 1, 2, 1}; // ID 22: PGP
                case 23: return new int[]{23, 1, 1, 2}; // ID 23: PPG
            }
        }
        return new int[]{0, 0, 0, 0};
    }

    public Pose getRobotPoseFromCamera() {
        if (aprilTag == null || aprilTag.getDetections().isEmpty()) return null;
        else {
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            for (AprilTagDetection detection : currentDetections) {
                if (detection.metadata != null) {
                    if (!detection.metadata.name.contains("Obelisk")) {
                        double x = ((detection.robotPose.getPosition().y) + 72 + 3);
                        double y = ((-detection.robotPose.getPosition().x) + 72 - 10);
                        double heading = detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES);
                        return new Pose(x, y, Math.toRadians(heading));
                    }
                }
            }
            return null;
        }
    }

    public void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        for (AprilTagDetection detection : currentDetections) {
            if (detection.metadata != null) {
                telemetry.addLine(String.format(Locale.US,"\n==== (ID %d) %s", detection.id, detection.metadata.name));
                if (!detection.metadata.name.contains("Obelisk")) {
                    telemetry.addLine(String.format(Locale.US,"Field XYZ %6.1f %6.1f %6.1f  (inch)",
                            detection.robotPose.getPosition().x,
                            detection.robotPose.getPosition().y,
                            detection.robotPose.getPosition().z));
                    telemetry.addLine(String.format(Locale.US,"Field PRY %6.1f %6.1f %6.1f  (deg)",
                            detection.robotPose.getOrientation().getPitch(AngleUnit.DEGREES),
                            detection.robotPose.getOrientation().getRoll(AngleUnit.DEGREES),
                            detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));
                    telemetry.addLine(String.format(Locale.US,"Pedro XYH %6.1f %6.1f %6.1f  (inch inch deg)",
                            ((detection.robotPose.getPosition().y)+72),
                            ((-detection.robotPose.getPosition().x)+72),
                            detection.robotPose.getOrientation().getYaw(AngleUnit.DEGREES)));
                }
            } else {
                telemetry.addLine(String.format(Locale.US,"\n==== (ID %d) Unknown", detection.id));
                telemetry.addLine(String.format(Locale.US,"Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
            }
        }
    }

    public void aprilTagTelemetry() {
        if (aprilTag != null && !aprilTag.getDetections().isEmpty()) {
            AprilTagDetection tag = aprilTag.getDetections().get(0);
            telemetry.addData("Tag ", "ID: %d | X: %.1f | Y: %.1f | Heading: %.1f°",
                    tag.id, tag.ftcPose.x, tag.ftcPose.y, tag.ftcPose.yaw);
        }
    }
    public int artifactColor() {
        return isFinalColor(isColor(robot.sensorL), isColor(robot.sensorR));
    }
    private int isFinalColor(int ls, int rs){
        int finalColor = 0;
        if (ls == 1 || rs == 1){
            finalColor = 1; // Purple
        } else if (ls == 2 || rs == 2) {
            finalColor = 2; // Green
        }
        return finalColor;
    }
    private int isColor(RevColorSensorV3 colorSensor){
        float[] hsvValues = new float[3];
        int r = colorSensor.red();
        int g = colorSensor.green();
        int b = colorSensor.blue();
        // Scale RGB to 0–255 and convert to HSV
        Color.RGBToHSV(r * 255 / 800, g * 255 / 800, b * 255 / 800, hsvValues);
        float hue = hsvValues[0];  // Hue (0 to 360)
        float sat = hsvValues[1];  // Saturation (0 to 1)
        float val = hsvValues[2];  // Value (0 to 1)
        // Get distance
        double distance = colorSensor.getDistance(DistanceUnit.MM);
        // Color classification
        int detectedColor;
        if (distance < MIN_INTAKE_COLOR_DETECTION_DISTANCE) {
            if (isGreen(hue, sat, val)) {
                detectedColor = 2; // Green
            } else if (isPurple(hue, sat, val)) {
                detectedColor = 1; // Purple
            } else {
                detectedColor = 0;
            }
        } else {
            detectedColor = 0;
        }
        return detectedColor;
    }
    private boolean isGreen(float hue, float sat, float val) {
        return hue >= 80 && hue <= 190 && sat > 0.3 && val > 0.2;
    }
    private boolean isPurple(float hue, float sat, float val) {
        return (hue >= 200 && hue <= 300) && sat > 0.3 && val > 0.2;
    }

    public void sensorTelemetry() {
        telemetry.addData("Intake Color", artifactColor());
    }
}