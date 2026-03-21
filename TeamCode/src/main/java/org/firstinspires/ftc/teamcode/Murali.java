package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name="Murali Opmode")
public class Murali extends OpMode {
    DcMotorEx motor;

    @Override
    public void init() {
        motor = hardwareMap.get(DcMotorEx.class,"motor");
    }

    @Override
    public void loop() {


        int declaration = 9;
        int MurMur = 12;
        int tiger = 67;
        int Williamsburg = 89;
        Williamsburg = tiger + MurMur + declaration;

        motor.setPower(Williamsburg);
        telemetry.addData("Letter", declaration);
        }
}