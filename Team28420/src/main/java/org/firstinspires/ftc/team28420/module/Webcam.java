package org.firstinspires.ftc.team28420.module;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.VisionProcessor;

import java.util.concurrent.TimeUnit;

public class Webcam {

    public static class WebcamConfig {
        public static int EXPOSURE_MS = 4;
        public static int GAIN = 270;
    }

    private final VisionPortal visionPortal;

    public Webcam(HardwareMap hMap, VisionProcessor... visionProcessors) {
        visionPortal = new VisionPortal.Builder().setCamera(hMap.get(WebcamName.class, "Webcam 1")).setCameraResolution(new Size(640, 480)).setStreamFormat(VisionPortal.StreamFormat.MJPEG).enableLiveView(false).setAutoStopLiveView(true).addProcessors(visionProcessors).build();
    }

    public void setup() {
        while (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        setManualExposure();

        FtcDashboard.getInstance().startCameraStream(visionPortal, 30);
    }

    private void setManualExposure() {
        if (visionPortal == null) return;

        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl != null) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
            exposureControl.setExposure(WebcamConfig.EXPOSURE_MS, TimeUnit.MILLISECONDS);
        }

        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        if (gainControl != null) {
            gainControl.setGain(WebcamConfig.GAIN);
        }
    }
}
