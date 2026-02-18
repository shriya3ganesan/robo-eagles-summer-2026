package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Limelight {

    Limelight3A limelight;

    LLResult result;

    public Limelight (HardwareMap hardwareMap){
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // This sets how often we ask Limelight for data (100 times per second)
        limelight.start();

    }
    public void switchPipelines(int x){
        limelight.pipelineSwitch(x);
    }

    public String detectMotif(LLResult result){
return"b";
    }

    public void updateLimelight(){
        result = limelight.getLatestResult();
    }
}
