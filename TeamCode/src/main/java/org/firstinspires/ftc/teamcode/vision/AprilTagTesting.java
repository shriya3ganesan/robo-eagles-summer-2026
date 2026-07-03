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

@TeleOp(name = "AprilTag2", group = "Vision")
public class AprilTagTesting extends LinearOpMode {

    /*
     * Big picture:
     * This TeleOp opens the webcam, looks for AprilTags, and prints each
     * detected tag's position to telemetry on the Driver Station.
     *
     * AprilTags work best when the camera knows the real size of the printed
     * tag. That size lets the FTC SDK estimate distance and rotation.
     */

    /*
     * This name must match the webcam name in the Robot Configuration exactly.
     * If the configuration says "Webcam 1", this must also say "Webcam 1".
     */
    private static final String WEBCAM_NAME = "Webcam 1";

    /*
     * Measure only the black square of the printed AprilTag, not the whole paper.
     * The SDK uses this size to calculate distance, so an incorrect size gives
     * incorrect position numbers.
     */
    private static final double CUSTOM_TAG_SIZE_CM = 16.0;

    /*
     * This code supports every custom AprilTag ID from FIRST_CUSTOM_TAG_ID through
     * LAST_CUSTOM_TAG_ID. For example, ID 37 works because it is between 0 and 100.
     */
    private static final int FIRST_CUSTOM_TAG_ID = 0;
    private static final int LAST_CUSTOM_TAG_ID = 100;

    // Finds AprilTags in each camera frame.
    private AprilTagProcessor aprilTag;

    // Opens the camera and sends frames to the AprilTag processor.
    private VisionPortal visionPortal;

    @Override
    public void runOpMode() {

        /*
         * Step 1: Create the AprilTag processor.
         *
         * This is the part that detects AprilTags from the camera image.
         * The Builder pattern lets us choose settings first, then call build()
         * to create the finished processor.
         */
        aprilTag = new AprilTagProcessor.Builder()
                // Draw visual helpers on the camera stream.
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .setDrawTagOutline(true)

                // Tell the processor which tag IDs exist and how large they are.
                .setTagLibrary(createTagLibrary())

                // Make all distance outputs centimeters and all angle outputs degrees.
                .setOutputUnits(DistanceUnit.CM, AngleUnit.DEGREES)
                .build();

        /*
         * Optional:
         * Decimation changes processing speed vs detection distance.
         * Lower number = better far detection, but slower.
         * Higher number = faster, but worse far detection.
         */
        aprilTag.setDecimation(2);

        /*
         * Step 2: Create the VisionPortal.
         *
         * VisionPortal opens the camera and sends frames to the AprilTag processor.
         */
        visionPortal = new VisionPortal.Builder()
                // Look up the webcam from the Robot Configuration.
                .setCamera(hardwareMap.get(WebcamName.class, WEBCAM_NAME))

                // Run AprilTag detection on this camera's frames.
                .addProcessor(aprilTag)
                .build();

        /*
         * Step 3: INIT loop.
         *
         * Stay here before pressing START.
         * This helps the Camera Stream button appear in the Driver Hub menu.
         * opModeInInit() is true after INIT and before START.
         */
        while (opModeInInit()) {
            telemetry.addLine("Camera Ready");
            telemetry.addData("Camera State", visionPortal.getCameraState());
            telemetry.addData("Tag Size", "%.1f cm", CUSTOM_TAG_SIZE_CM);
            telemetry.addLine("Open 3-dot menu for Camera Stream before START");
            telemetry.update();

            sleep(50);
        }

        /*
         * Step 4: Main loop.
         *
         * After pressing START, continuously show AprilTag values.
         * opModeIsActive() stays true until STOP is pressed.
         */
        while (opModeIsActive()) {
            telemetryAprilTag();
            telemetry.update();

            sleep(50);
        }

        /*
         * Step 5: Close the camera when the OpMode ends.
         *
         * This frees the webcam so another OpMode can use it later.
         */
        visionPortal.close();
    }

    /*
     * This creates the AprilTag library.
     *
     * A tag library tells FTC:
     * - which tag IDs exist
     * - what each tag is called
     * - how physically large each tag is
     *
     * Without correct tag size, distance/range can be wrong.
     */
    private AprilTagLibrary createTagLibrary() {
        /*
         * Step A: Start building a library of tags.
         *
         * A Builder lets us add many tags first, then create one finished library.
         * setAllowOverwrite(true) lets later tags replace earlier tags with the
         * same ID if there is ever a duplicate.
         */
        AprilTagLibrary.Builder tagLibraryBuilder = new AprilTagLibrary.Builder()
                .setAllowOverwrite(true);

        /*
         * Step B: Add your team's custom tags.
         *
         * Add your own custom tags.
         * This adds ID 0 to ID 100.
         * Each tag needs an ID, a name, a real-world size, and the unit for that size.
         */
        for (int id = FIRST_CUSTOM_TAG_ID; id <= LAST_CUSTOM_TAG_ID; id++) {
            tagLibraryBuilder.addTag(
                    id,
                    "Custom Tag " + id,
                    CUSTOM_TAG_SIZE_CM,
                    DistanceUnit.CM
            );
        }

        /*
         * Step C: Add official FTC game tags.
         *
         * Add official FTC game tags.
         * This lets your robot detect official field AprilTags too.
         * The FTC SDK already knows the IDs and sizes for these.
         */
        tagLibraryBuilder.addTags(AprilTagGameDatabase.getCurrentGameTagLibrary());

        /*
         * Step D: Add FTC sample tags.
         *
         * Add FTC sample tags.
         * Useful if you are testing with sample AprilTags.
         */
        tagLibraryBuilder.addTags(AprilTagGameDatabase.getSampleTagLibrary());

        // Build and return the finished tag library.
        return tagLibraryBuilder.build();
    }

    /*
     * This method prints AprilTag values to Driver Hub telemetry.
     */
    private void telemetryAprilTag() {
        /*
         * Step 1: Ask the processor for all tags visible in the current frame.
         * The list can be empty if the camera does not see any AprilTags.
         */
        List<AprilTagDetection> detections = aprilTag.getDetections();

        telemetry.addLine("Camera Ready");
        telemetry.addLine();

        if (detections.size() == 0) {
            telemetry.addLine("No AprilTag detected");
            telemetry.addLine();
        }

        /*
         * Step 2: Go through each detected tag one at a time.
         * A for-each loop is useful here because we do the same telemetry
         * printing steps for every tag in the list.
         */
        for (AprilTagDetection detection : detections) {

            /*
             * Step 3: Check whether the tag has enough information for pose data.
             *
             * metadata != null means the tag exists in the library.
             * ftcPose != null means FTC calculated position/rotation data.
             * If either is missing, the camera saw the tag but cannot give full pose data.
             */
            if (detection.metadata != null && detection.ftcPose != null) {

                telemetry.addLine(String.format("==== (ID %d) %s",
                        detection.id,
                        detection.metadata.name));

                // XYZ is the tag's position compared to the camera.
                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f (cm)",
                        detection.ftcPose.x,
                        detection.ftcPose.y,
                        detection.ftcPose.z));

                // PRY is the tag's rotation: pitch, roll, and yaw.
                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f (deg)",
                        detection.ftcPose.pitch,
                        detection.ftcPose.roll,
                        detection.ftcPose.yaw));

                // RBE is often easiest for driving: range, bearing, and elevation.
                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f (cm, deg, deg)",
                        detection.ftcPose.range,
                        detection.ftcPose.bearing,
                        detection.ftcPose.elevation));

                telemetry.addLine();

            } else {
                /*
                 * Step 4: Handle tags without pose data.
                 * This usually means the tag ID or tag size is missing from
                 * the AprilTag library.
                 */
                telemetry.addLine(String.format("==== (ID %d) no pose data", detection.id));
                telemetry.addLine("Tag ID not in library or tag size missing");
                telemetry.addLine();
            }
        }

        /*
         * Step 5: Print a short key so the telemetry labels make sense.
         * This is especially helpful while learning what the AprilTag numbers mean.
         */
        telemetry.addLine("key:");
        telemetry.addLine("XYZ = X (Right), Y (Forward), Z (Up) dist.");
        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
        telemetry.addLine("RBE = Range, Bearing & Elevation");
    }
}
