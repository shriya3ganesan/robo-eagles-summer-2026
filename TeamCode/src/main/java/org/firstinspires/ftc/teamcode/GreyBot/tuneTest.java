package org.firstinspires.ftc.teamcode.GreyBot;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name="tuneTest", group="Linear OpMode")
public class tuneTest extends OpMode {

    public DcMotorEx lancher;
//    public DcMotorEx wheel;

    public double high = 2200;

    public double low = 900;

    public double curTarget = high;

    double f = 12.227;
    double p =1;

    double F = 0;
    double P =0;


    double[] stepSize = {10.0, 1.0, 0.1, 0.01, 0.001};

    int stepIndex = 0;

    public double curvelocity = 0;
    public double error = 0;

    @Override
    public void init(){
        lancher = hardwareMap.get(DcMotorEx.class, "lancher");
//        wheel = hardwareMap.get(DcMotorEx.class, "wheel");

        lancher.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
//        wheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        lancher.setDirection(DcMotorEx.Direction.REVERSE);
//        wheel.setDirection(DcMotor.Direction.REVERSE);

        PIDFCoefficients a = new PIDFCoefficients(p, 0, 0, f);
//        PIDFCoefficients b = new PIDFCoefficients(P, 0, 0, F);

        lancher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, a);
//        wheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, a);
    }
    @Override
    public void loop(){
        if (gamepad1.yWasPressed()) {
            if (curTarget == low){
                curTarget = high;
            }
            else {
                curTarget = low;
            }
        }

        if (gamepad1.bWasPressed()){
            stepIndex = (stepIndex + 1) % stepSize.length;
        }

        if (gamepad1.dpadLeftWasPressed()){
            f += stepSize[stepIndex];
        }
        if (gamepad1.dpadRightWasPressed()){
            f -= stepSize[stepIndex];
        }
        if (gamepad1.dpadUpWasPressed()){
            p += stepSize[stepIndex];
        }
        if (gamepad1.dpadDownWasPressed()){
            p -= stepSize[stepIndex];
        }



        PIDFCoefficients a = new PIDFCoefficients(p, 0, 0, f);

        lancher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, a);
//        wheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, a);

        lancher.setVelocity(curTarget);
//        wheel.setVelocity(curTarget);

        curvelocity = lancher.getVelocity();
        error = curTarget - curvelocity;

//        double curvell = wheel.getVelocity();
//        double errorr = curTarget - curvell;

        telemetry.addData("target volocity" ,curTarget);
        telemetry.addData("current volocity" , "%.2f",curvelocity);
        telemetry.addData("error" , "%.2f", error);
        telemetry.addData("tunning p","%.4f (d-pad U/D)" ,p);
        telemetry.addData("tunning f","%.4f (d-pad L/R)" ,f);
        telemetry.addData("step size","%.4f (b button)" ,stepSize[stepIndex]);
        telemetry.update();
    }
}
