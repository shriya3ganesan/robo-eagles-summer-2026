package org.firstinspires.ftc.teamcode.training;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
@Disabled
@TeleOp
public class training1 extends LinearOpMode {
DcMotor Motor1;
Servo Servo1;
//Servo Trigger;

    @Override
    public void runOpMode() throws InterruptedException {
        Motor1 = hardwareMap.get(DcMotor.class, "MotorTest1");
        Servo1 = hardwareMap.get(Servo.class, "ServoTest1");
        Servo1.setPosition(0);

        waitForStart();
        if (isStopRequested()) return;

        while (!isStopRequested() && opModeIsActive()) {
            if (gamepad1.a){
                Motor1.setPower(0.5);
            }
            else if (gamepad1.b){
                Motor1.setPower(0.8);
            }
            else if (gamepad1.x){
                Motor1.setPower(1);
            }
            else if (gamepad1.y){
                Motor1.setPower(0.3);
            }
            else {
                Motor1.setPower(0);
            }

            if (gamepad1.dpad_up){
                Servo1.setPosition(0.1);
            } else if (gamepad1.dpad_right){
                Servo1.setPosition(0.3);
            } else if (gamepad1.dpad_down){
                Servo1.setPosition(0.6);
            } else if (gamepad1.dpad_left){
                Servo1.setPosition(1);
            }

        }
        }
    }
