package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Limelight {

    Limelight3A limelight;

    LLResult result;

    public Limelight (HardwareMap hardwareMap){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100);
        limelight.start();
        result = limelight.getLatestResult();// This sets how often we ask Limelight for data (100 times per second)

    }
    public void updateResult(){
        result = limelight.getLatestResult();
    }


    public void switchPipelines(int x){
        limelight.pipelineSwitch(x);
    }

    public String detectMotif(){
        updateResult();
        limelight.pipelineSwitch(7);
        if (result !=null){
            return "PGP";
        }

        limelight.pipelineSwitch(8);
        updateResult();

        if (result != null){
            return "GPP";
        }
        limelight.pipelineSwitch(9);
        updateResult();
        if (result != null){
            return "PPG";
        } else {
            return "null";
        }

    }

    public void updateLimelight(){
        result = limelight.getLatestResult();
    }
}
