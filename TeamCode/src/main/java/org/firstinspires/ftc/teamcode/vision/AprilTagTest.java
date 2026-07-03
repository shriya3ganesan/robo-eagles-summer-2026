package org.firstinspires.ftc.teamcode.vision;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagGameDatabase;
import org.firstinspires.ftc.vision.apriltag.AprilTagLibrary;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;

@TeleOp(name = "AprilTag Test")
public class AprilTagTest extends LinearOpMode {

    // This name must exactly match the webcam name in the Driver Station robot configuration.
    private static final String WEBCAM_NAME = "Webcam 1";

    // Measure the black AprilTag square on your printed tag and update this value if needed.
    // The FTC SDK needs the real tag size to calculate distance and angle values.
    private static final double CUSTOM_TAG_SIZE_CM = 5.0;

    // The code below creates pose data for custom tag IDs in this range.
    // Raise LAST_CUSTOM_TAG_ID if your printed tag has a larger ID number.
    private static final int FIRST_CUSTOM_TAG_ID = 0;
    private static final int LAST_CUSTOM_TAG_ID = 50;

    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        // AprilTagProcessor does the actual tag detection.
        // The builder lets us choose drawing options, tag sizes, and output units.
        aprilTag = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)
                .setTagLibrary(createTagLibrary())
                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
                .build();

        // Lower decimation can detect tags farther away, but it may run slower.
        // A value of 2 is a good starting point for testing.
        aprilTag.setDecimation(2);

        // VisionPortal connects the webcam to the AprilTag processor.
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, WEBCAM_NAME))
                .addProcessor(aprilTag)
                .build();

        telemetry.addLine("Camera ready");
        telemetry.addData("Tag size", "%.1f cm", CUSTOM_TAG_SIZE_CM);
        telemetry.update();

        waitForStart();

        // Keep updating Driver Station telemetry while the OpMode is running.
        while (opModeIsActive()) {
            telemetryAprilTag();
            telemetry.update();
            sleep(20);
        }

        // Release the camera when the OpMode ends.
        visionPortal.close();
    }

    private AprilTagLibrary createTagLibrary() {
        // The tag library tells the SDK which tag IDs exist and how big each tag is.
        // Without this metadata, the SDK may see a tag but cannot calculate ftcPose.
        AprilTagLibrary.Builder tagLibraryBuilder = new AprilTagLibrary.Builder()
                .setAllowOverwrite(true);

        // Add simple metadata for custom printed tags.
        // These tags will all use CUSTOM_TAG_SIZE_CM.
        for (int id = FIRST_CUSTOM_TAG_ID; id <= LAST_CUSTOM_TAG_ID; id++) {
            tagLibraryBuilder.addTag(id, "Custom Tag " + id, CUSTOM_TAG_SIZE_CM, DistanceUnit.CM);
        }

        // Also include the official FTC game tags and sample tags.
        tagLibraryBuilder.addTags(AprilTagGameDatabase.getCurrentGameTagLibrary());
        tagLibraryBuilder.addTags(AprilTagGameDatabase.getSampleTagLibrary());

        return tagLibraryBuilder.build();
    }

    private void telemetryAprilTag() {
        // getDetections() returns every tag currently visible to the camera.
        List<AprilTagDetection> detections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", detections.size());

        for (AprilTagDetection detection : detections) {
            // ftcPose contains the useful robot-style numbers:
            // X/Y/Z distance, pitch/roll/yaw, range, bearing, and elevation.
            if (detection.metadata != null && detection.ftcPose != null) {
                telemetry.addLine(String.format("\n==== ID %d: %s", detection.id, detection.metadata.name));
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f cm",
                        detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f deg",
                        detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f cm/deg/deg",
                        detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
            } else {
                telemetry.addLine(String.format("\n==== ID %d: no pose", detection.id));
                telemetry.addLine("Add this ID to the tag library with the real tag size.");
            }

            // Center and corners are pixel locations in the camera image.
            // These still work even if pose data is missing.
            telemetry.addLine(String.format("Center %6.0f %6.0f px", detection.center.x, detection.center.y));

            for (int i = 0; i < detection.corners.length; i++) {
                telemetry.addLine(String.format("Corner %d %6.0f %6.0f px",
                        i, detection.corners[i].x, detection.corners[i].y));
            }
        }
    }
}
