package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


@Autonomous(name = "BoboFettRedGoal", group = "Autonomous")
public class BoboFettRedGoal extends LinearOpMode {

    AutoRobot2 robot2;
    Flywheel flywheel;



    @Override
    public void runOpMode() {
        flywheel = new Flywheel(this);
        robot2 = new AutoRobot2(this);
        flywheel.init(hardwareMap);
        robot2.init(hardwareMap);

        telemetry.update();

        waitForStart();
        robot2.turnToPID(7,1);
        robot2.encoderDrive(0.4, 30, 30, 5);
        flywheel.flyWheelAuto(1700,4);
        robot2.turnToPID(-50,3);
        flywheel.TopCollection.setPower(9);
        flywheel.BottomCollection.setPower(9);
        flywheel.Finger.setPosition(.5);
        robot2.encoderDrive(0.3, -15, -15, 5);
        robot2.turnToPID(-33,1);
        robot2.encoderDrive(0.3, -10, -10, 5);
        robot2.encoderDrive(0.3, 10, 10, 5);
        robot2.turnToPID(-90,1);
        robot2.encoderDrive(0.4, 15, 15, 5);
        flywheel.TopCollection.setPower(0);
        flywheel.BottomCollection.setPower(0);
        robot2.turnToPID(2.5,2.5);
        flywheel.Finger.setPosition(.3);
        flywheel.flyWheelAuto(1550,4);
        robot2.turnToPID(45,1);
        robot2.encoderDrive(0.3, -5, -5, 5);










        //flywheel.flyWheelAuto(2150,5);
        // robot2.encoderDrive(0.5, -8, -8, 5);
//        robot2.encoderDrive(0.5, 12, -12, 5);
//        opModeSleep(500);
        //  telemetry.update();
    }


    private void opModeSleep(long milliseconds) {
        try {
            sleep(milliseconds);
        } catch (Exception e) {
            telemetry.update();
        }
    }
}
