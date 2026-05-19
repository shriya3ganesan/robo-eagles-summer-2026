package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheel {
    private DcMotorEx m1;

    private double gearRatio = 1;

    private double encoderCPM = 20;

     private double kV = 0.017 ,kS = 0.056,kP = 0.0005;

     public void init(HardwareMap hwMap) {
         m1 = hwMap.get(DcMotorEx.class,"LiftR");
         m1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
         m1.setDirection(DcMotorSimple.Direction.REVERSE);
     }

     public void setMotorPower(double power) {
         m1.setPower(power);
     }
     public void stopMotorPOwer() {
         m1.setPower(0);
     }
     public void setMotorRPM(double targetRPM) {
         //call this evry loop
         double error = targetRPM -getRPM();
         double ff = (kV * targetRPM) + kS ;
         double fb = error * kP;
         double power = ff + fb;

         setMotorPower(power);
     }


     public double getTicksPerSec() {
         return m1.getVelocity();
     }

     public  double getRPM() {
         return ((getTicksPerSec()/encoderCPM) * 60) / gearRatio;
     }

}
