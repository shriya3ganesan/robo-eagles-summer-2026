package org.firstinspires.ftc.teamcode.teleop;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;

public class aprilTagLimelight extends OpMode {

    private Limelight3A limelight;
    private IMU imu;
    @Override

    public void init(){
        limelight = hardwareMap.get(Limelight3A.class,"limelight");
        limelight.pipelineSwitch(8);
        imu = hardwareMap.get (IMU.class, "imu");
    }
    public void start(){
        limelight.start();
    }
    public void loop(){}

}
