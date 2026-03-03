package org.firstinspires.ftc.teamcode.NonOpModes.depreciated;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import java.util.List;

@Autonomous(name="LimeLightTesting", group="limelight")
@Disabled
//
// the disabled will make it not show up under the driver station OPmode list
// useful to prevent cluttering after testing
public class LimeLightTesting extends LinearOpMode {

    @Override

    public void runOpMode() {

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        waitForStart();

        while (opModeIsActive()) { // keeps the code running so it doesn't only run once

            LLResult result = limelight.getLatestResult(); //so don't forget LLResult is a variable type

            if (result != null && result.isValid()){ // checks if there is a target and if the target is an actual target
                double tx = result.getTx();  // Horizontal angle to target
                double ty = result.getTy();  // Vertical angle to target
                double area = result.getTa();  // Size of the target

                telemetry.addData("Target Found", true);
                telemetry.addData("Target X Angle", tx);
                telemetry.addData("Target Y Angle", ty);
                telemetry.addData("Target Area", area);
                
                List<LLResultTypes.FiducialResult> tags = result.getFiducialResults(); //get fiducial results basically just tells how many april tags it sees
                //List<LLResultTypes.FiducialResult>: so it makes a list at the size of the # of tags detected and has info on the id and position of the tag

                for (LLResultTypes.FiducialResult tag : tags) {
                    int id = tag.getFiducialId();
                    Pose3D tagPose = tag.getRobotPoseTargetSpace();
                    telemetry.addData("Tag ID", id);
                    telemetry.addData("Tag Pose", tagPose);
                    }
                }

            else {
                telemetry.addLine("no current target");
                }

            telemetry.update();

            }

        limelight.stop(); //stops the limelight

        }
    }

