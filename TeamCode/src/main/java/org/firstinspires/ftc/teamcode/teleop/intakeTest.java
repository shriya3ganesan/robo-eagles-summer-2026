package org.firstinspires.ftc.teamcode.teleop;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.Intake;

@TeleOp
@Config

public class intakeTest extends OpMode {
    private Intake intake;

    double intakeLposition;
    double intakeRposition;

    public void init(){
        intake = new Intake(hardwareMap);
    }
    public void loop(){
        if (gamepad1.left_bumper){
            intakeLposition+=0.01;
        }
        if (gamepad1.right_bumper){
            intakeLposition-=0.01;
        }
        telemetry.addData("intakeLposition", intakeLposition);

        if (gamepad1.a){
            intakeRposition+=0.01;
        }
        if (gamepad1.b){
            intakeRposition-=0.01;
        }
        telemetry.addData("intakeRposition", intakeRposition);
        intake.setIntakeLPosition(intakeLposition);
        intake.setIntakeRPosition(intakeRposition);
    }
}
