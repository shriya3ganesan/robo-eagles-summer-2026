package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name="ServoTestOpMode")
@Config

public class ServoTestOpMode extends LinearOpMode {

    private Servo DrumServo;
    private Servo FiringPinServo;

    public static double servoOffSet = 0;
    public static double firingpinmax = 0.95;


    @Override
    public void runOpMode() {
        DrumServo = hardwareMap.get(Servo.class, "DrumServo");
        FiringPinServo = hardwareMap.get(Servo.class, "FiringPinServo");

        double targetdrumangle = 0;
        double targetfiringpinangle = 0;
        boolean firing = false;

        DrumServo.setPosition(0);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // sets the three angles
            if (gamepad2.a) {
                targetfiringpinangle = firingpinmax;
            } else {
                targetfiringpinangle = .98;// these values are all placeholders
                targetdrumangle = gamepad2.x ? servoOffSet+.09 ://Firing angles
                                  gamepad2.y ? servoOffSet+.42 :
                                  gamepad2.b ? servoOffSet+.76 :
                                  gamepad1.x ? servoOffSet+.27 ://loading angles
                                  gamepad1.y ? servoOffSet+.6 :
                                  gamepad1.b ? servoOffSet+.92 :
                                  targetdrumangle;

                //.27 - .42   0  -   1
                //.6 - .76    1   -   2
                //.92 - .9    2   -    0
            }
            DrumServo.setPosition(targetdrumangle);
            FiringPinServo.setPosition(targetfiringpinangle);

            servoOffSet += gamepad1.dpadUpWasPressed() ? .01 : 0;
            servoOffSet -= gamepad1.dpadDownWasPressed() ? .01 : 0;
            servoOffSet += gamepad1.dpadRightWasPressed() ? .1 : 0;
            servoOffSet -= gamepad1.dpadLeftWasPressed() ? .1 : 0;

            telemetry.addData("servo offset angle", servoOffSet);
            telemetry.addData("servoangle", targetdrumangle);
            telemetry.addData("servoangle", targetfiringpinangle);
            telemetry.update();
        }
    }
}
