package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.WhiteBalanceControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.opencv.ColorBlobLocatorProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Autonomous(name = "Color Detection Starter", group = "Vision")
public class ColorDetectionOpMode extends LinearOpMode {

    // ---------------------------------------------------------------
    // Vision objects
    // ---------------------------------------------------------------
    private VisionPortal visionPortal;
    private ColorBlobLocatorProcessor colorProcessor;

    @Override
    public void runOpMode() {

        // ===========================================================
        // 1. BUILD THE COLOR PROCESSOR
        //    Tells the SDK what color to look for and how to find it.
        // ===========================================================
        colorProcessor = new ColorBlobLocatorProcessor.Builder()
                .setTargetColorRange(ColorRange.RED)   // <-- change to your target color
                .setContourMode(ColorBlobLocatorProcessor.ContourMode.EXTERNAL_ONLY)
                .build();

        // ===========================================================
        // 2. BUILD THE VISION PORTAL
        //    Connects the C270 webcam and attaches the processor.
        //    "Webcam 1" must match your Configure Robot name exactly.
        // ===========================================================
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(colorProcessor)
                .build();

        // ===========================================================
        // 3. WAIT FOR THE CAMERA TO FINISH STARTING UP
        //    Camera controls are only available after streaming begins.
        // ===========================================================
        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addLine("Waiting for camera...");
            telemetry.update();
            sleep(50);
        }

        // ===========================================================
        // 4. LOCK EXPOSURE AND WHITE BALANCE (C270 specific)
        //    Prevents auto-adjustments mid-match that would break
        //    color detection consistency.
        // ===========================================================

        // Lock exposure to Manual at 250ms
        // Note: C270 range is 0-1000, but goes dark above 655
        ExposureControl exposureControl =
                visionPortal.getCameraControl(ExposureControl.class);
        exposureControl.setMode(ExposureControl.Mode.Manual);
        exposureControl.setExposure(250, TimeUnit.MILLISECONDS);

        // Lock white balance to a neutral daylight value
        WhiteBalanceControl wbControl =
                visionPortal.getCameraControl(WhiteBalanceControl.class);
        wbControl.setMode(WhiteBalanceControl.Mode.MANUAL);
        wbControl.setWhiteBalanceTemperature(4000);

        // Show camera is ready
        telemetry.addLine("Camera ready. Press Start.");
        telemetry.update();

        // ===========================================================
        // 5. WAIT FOR MATCH START
        // ===========================================================
        waitForStart();

        // ===========================================================
        // 6. MAIN LOOP - Read color blobs and act on them
        // ===========================================================
        while (opModeIsActive()) {
            telemetry.update();
        }
    }
}
