package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "AprilTag Detection", group = "Vision")
public class AprilTagDetectionOpMode extends LinearOpMode {

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {
        aprilTag = AprilTagProcessor.easyCreateWithDefaults();

        visionPortal = VisionPortal.easyCreateWithDefaults(
                hardwareMap.get(WebcamName.class, "Webcam 1"),
                aprilTag
        );

        telemetry.addLine("AprilTag camera ready.");
        telemetry.addLine("Press Start to detect tags.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            List<AprilTagDetection> detections = aprilTag.getDetections();

            telemetry.addData("Tags Detected", detections.size());

            for (AprilTagDetection detection : detections) {
                telemetry.addLine();
                telemetry.addData("Tag ID", detection.id);

                if (detection.metadata != null) {
                    telemetry.addData("Name", detection.metadata.name);
                    telemetry.addData("X/Y/Z", "%.1f, %.1f, %.1f in",
                            detection.ftcPose.x,
                            detection.ftcPose.y,
                            detection.ftcPose.z);
                    telemetry.addData("Range", "%.1f in", detection.ftcPose.range);
                    telemetry.addData("Bearing", "%.1f deg", detection.ftcPose.bearing);
                    telemetry.addData("Yaw", "%.1f deg", detection.ftcPose.yaw);
                } else {
                    telemetry.addData("Name", "Unknown tag");
                    telemetry.addData("Center", "%.0f, %.0f px",
                            detection.center.x,
                            detection.center.y);
                }
            }

            telemetry.update();
            sleep(20);
        }

        visionPortal.close();
    }
}
