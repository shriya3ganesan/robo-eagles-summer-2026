/*
Copyright (c) 2024 Limelight Vision

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of FIRST nor the names of its contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.team417.apriltags;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.team417.CompetitionAuto;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * This class is used to detect AprilTags using the Limelight3A Vision Sensor.
 *
 * @see <a href="https://limelightvision.io/">Limelight</a>
 *
 * Notes on configuration:
 *
 *   The device presents itself, when plugged into a USB port on a Control Hub as an ethernet
 *   interface.  A DHCP server running on the Limelight automatically assigns the Control Hub an
 *   ip address for the new ethernet interface.
 *
 *   Since the Limelight is plugged into a USB port, it will be listed on the top level configuration
 *   activity along with the Control Hub Portal and other USB devices such as webcams.  Typically
 *   serial numbers are displayed below the device's names.  In the case of the Limelight device, the
 *   Control Hub's assigned ip address for that ethernet interface is used as the "serial number".
 *
 *   Tapping the Limelight's name, transitions to a new screen where the user can rename the Limelight
 *   and specify the Limelight's ip address.  Users should take care not to confuse the ip address of
 *   the Limelight itself, which can be configured through the Limelight settings page via a web browser,
 *   and the ip address the Limelight device assigned the Control Hub and which is displayed in small text
 *   below the name of the Limelight on the top level configuration screen.
 */
public class LimelightDetector implements Closeable {
    /**
     * The variable to store our instance of the AprilTag processor.
     */
    private Limelight3A limelight;

    /**
     * The variable for how long ago the detection last changed.
     */
    private double timeOfLastChange;

    /**
     * The variable for how many detections were and what the ID was in the last cycle.
     */
    private int lastDetectionsSize = -1;
    private int lastId = -1;

    /**
     * Initialize the AprilTag processor.
     */
    public LimelightDetector(HardwareMap hardwareMap) {
        // Create the AprilTag processor.
        limelight = hardwareMap.get(Limelight3A.class, "limelight");

        limelight.pipelineSwitch(7);

        limelight.start();
    }

    /**
     * Default is no verbosity.
     */
    public Pattern detectPatternAndTelemeter(CompetitionAuto.Alliance alliance, Telemetry telemetry) {
        return detectPatternAndTelemeter(alliance, telemetry, false);
    }

    /**
     * Add telemetry about AprilTag detections.
     */
    public Pattern detectPatternAndTelemeter(CompetitionAuto.Alliance alliance, Telemetry telemetry, boolean verbose) {
        LLResult result = limelight.getLatestResult();

        if (verbose) {
            LLStatus status = limelight.getStatus();
            telemetry.addData("Name", "%s",
                    status.getName());
            telemetry.addData("LL", "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
                    status.getTemp(), status.getCpu(),(int)status.getFps());
            telemetry.addData("Pipeline", "Index: %d, Type: %s",
                    status.getPipelineIndex(), status.getPipelineType());

            if (result.isValid()) {
                // Access general information
                Pose3D botpose = result.getBotpose();
                double captureLatency = result.getCaptureLatency();
                double targetingLatency = result.getTargetingLatency();
                double parseLatency = result.getParseLatency();
                telemetry.addData("LL Latency", captureLatency + targetingLatency);
                telemetry.addData("Parse Latency", parseLatency);
                telemetry.addData("PythonOutput", java.util.Arrays.toString(result.getPythonOutput()));

                telemetry.addData("tx", result.getTx());
                telemetry.addData("txnc", result.getTxNC());
                telemetry.addData("ty", result.getTy());
                telemetry.addData("tync", result.getTyNC());

                telemetry.addData("Botpose", botpose.toString());


                // Access fiducial results
                List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                telemetry.addData("# AprilTags Detected", fiducialResults.size());

                for (LLResultTypes.FiducialResult fr : fiducialResults) {
                    telemetry.addData("AprilTags", "ID: %d, Family: %s, X: %.2f, Y: %.2f, Pose: %s", fr.getFiducialId(), fr.getFamily(), fr.getTargetXDegrees(), fr.getTargetYDegrees(), fr.getRobotPoseFieldSpace());
                }
            } else {
                telemetry.addData("Limelight", "No data available");
            }
        }

        // Get the pattern:
        Pattern pattern = detectPattern(alliance, result);

        String patternDisplay;

        // The `\\u...` are escape sequences for green and purple circle emojis.
        // \uD83D\uDFE3 -> Purple circle
        // \uD83D\uDFE2 -> Green circle
        // \u26AA -> White circle
        switch (pattern) {
            case PPG:
                patternDisplay = "\uD83D\uDFE3\uD83D\uDFE3\uD83D\uDFE2";
                break;
            case PGP:
                patternDisplay = "\uD83D\uDFE3\uD83D\uDFE2\uD83D\uDFE3";
                break;
            case GPP:
                patternDisplay = "\uD83D\uDFE2\uD83D\uDFE3\uD83D\uDFE3";
                break;
            default:
                patternDisplay = "\u26AA\u26AA\u26AA";
                break;
        }

        double elapsedTime = (System.currentTimeMillis() / 1000.0) - timeOfLastChange;

        // Summarize the most important detection info in one line:
        switch (alliance) {
            case RED:
                telemetry.addLine(String.format("%s (%d IDs, leftmost ID = %d for %f sec.)",
                        patternDisplay, lastDetectionsSize, lastId, elapsedTime));
                break;
            case BLUE:
                telemetry.addLine(String.format("%s (%d IDs, rightmost ID = %d for %f sec.)",
                        patternDisplay, lastDetectionsSize, lastId, elapsedTime));
                break;
        }

        return pattern;
    }   // end method telemetryAprilTag()

    /**
     * We want the Limelight to get its own result as default.
     */
    public Pattern detectPattern(CompetitionAuto.Alliance alliance) {
        return  detectPattern(alliance, null);
    }

    /**
     * Detect the correct color pattern and return it.
     */
    public Pattern detectPattern(CompetitionAuto.Alliance alliance, LLResult result) {
        if (result == null)
            result = limelight.getLatestResult();

        List<LLResultTypes.FiducialResult> currentDetections = new ArrayList<>();

        if (result.isValid())
            currentDetections = result.getFiducialResults();

        // Remove all AprilTags that don't have ID 21, 22, or 23
        // (This is because the obelisk only has AprilTags with IDs 21, 22, and 23)
        // (The remaining IDs, 20 and 24, are for localization only)
        currentDetections.removeIf(detection ->
                detection.getFiducialId() != 21
                        && detection.getFiducialId() != 22
                        && detection.getFiducialId() != 23
        );

        // FiducialResult objects contain the x (left) and y (up) degrees relative to the robot.
        //  When we're on the red alliance, we want the leftmost valid
        //  AprilTag, and when we're on the blue alliance, we want the rightmost valid AprilTag.
        //  This is because, in our near position, we see two AprilTags on the obelisk: the front
        //  AprilTag and the side AprilTag closest to our color goal.
        // For more information about the info the AprilTagDetection object contains, see this link:
        //  https://ftc-docs.firstinspires.org/en/latest/apriltag/understanding_apriltag_detection_values/understanding-apriltag-detection-values.html
        LLResultTypes.FiducialResult detection = null;
        switch (alliance) {
            case RED:
                // Set detection to the leftmost (min x degrees) detection relative to the robot
                // If there are no detections, set it to null
                detection = currentDetections.stream()
                        .min(Comparator.comparingDouble(LLResultTypes.FiducialResult::getTargetXDegrees)).orElse(null);
                break;
            case BLUE:
                // Set detection to the rightmost (max x degrees) detection relative to the robot
                // If there are no detections, set it to null
                detection = currentDetections.stream()
                        .max(Comparator.comparingDouble(LLResultTypes.FiducialResult::getTargetXDegrees)).orElse(null);
        }

        if (detection == null) {
            lastId = -1;
            timeOfLastChange = System.currentTimeMillis() / 1_000.0;
            return Pattern.UNKNOWN;
        }

        int currentDetectionsSize = currentDetections.size();

        if (lastDetectionsSize != currentDetectionsSize || detection.getFiducialId() != lastId) {
            timeOfLastChange = System.currentTimeMillis() / 1_000.0;
        }

        lastDetectionsSize = currentDetectionsSize;
        lastId = detection.getFiducialId();

        switch (detection.getFiducialId()) {
            case 21:
                return Pattern.GPP;
            case 22:
                return Pattern.PGP;
            case 23:
                return Pattern.PPG;
            default:
                return Pattern.UNKNOWN;
        }
    }

    /**
     * Detect the pose of the robot with the AprilTag.
     */
    public Pose2D detectRobotPose() {
        LLResult result = limelight.getLatestResult();

        if (result.isValid()) {
            List<LLResultTypes.FiducialResult> currentDetections = result.getFiducialResults();

            // FiducialResult objects contain the x (left) and y (up) degrees relative to the robot.
            //  We want the AprilTag that is as straight on as possible,
            //  that is, the lowest absolute value x.
            // For more information about the info the AprilTagDetection object contains, see this link:
            //  https://ftc-docs.firstinspires.org/en/latest/apriltag/understanding_apriltag_detection_values/understanding-apriltag-detection-values.html

            LLResultTypes.FiducialResult detection = currentDetections.stream()
                    .min(Comparator.comparingDouble(aprilTagDetection ->
                            Math.abs(aprilTagDetection.getTargetXDegrees()))).orElse(null);

            Pose3D pose = detection.getRobotPoseFieldSpace();

            return new Pose2D(
                    pose.getPosition().unit,
                    pose.getPosition().x,
                    pose.getPosition().y,
                    AngleUnit.RADIANS,
                    pose.getOrientation().getYaw(AngleUnit.RADIANS));
        }

        return null;
    };

    /**
     * Release the resources taken up by the vision portal.
     */
    @Override
    public void close() {
        limelight.stop();
    }
}
