package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.mechanisms.Launcher;
import org.firstinspires.ftc.teamcode.mechanisms.TurretServo;


@TeleOp
@Disabled
public class ServoTest extends OpMode {
    //private Servo servo;
    //Launcher launcher = new Launcher();
    TurretServo turret = new TurretServo();

    //private final int FEED_TIME_MILLISECONDS = 200; //The feeder servo runs this long when a shot is requested.
    //private final double FEED_START_POSITION = 0.0; // nominally 0 degrees, may need to be tuned based on mounting angle of servo
    //private final double FEED_POSITION = 0.5; // nominally 90 degrees, may need to increase it slightly

    //ElapsedTime feederTimer = new ElapsedTime();

    @Override
    public void init() {
        //servo = hardwareMap.get(Servo.class,"launch_feeder");

        //launcher.init(hardwareMap);
        turret.init(hardwareMap);

        // Set left feeder servo to reverse so both servos work to feed ball into robot.
        //servo.setDirection(Servo.Direction.REVERSE);
        //servo.setPosition (0.0); // default it to "0" degrees
    }

    @Override
    public void loop() {

        if (gamepad2.b) {
            //launcher.loadBall();
            turret.changeTurretByDegrees(30);
        } else if (gamepad2.y) {
            //launcher.resetFeeder();
            turret.changeTurretByDegrees(-30);
        }
/*
        if (gamepad2.b) {
            servo.setPosition (0.5);
        } else if (gamepad2.y) {
            servo.setPosition (0.0);
        }
         */
    }
}
