package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "BoboFettBlueWall", group = "Autonomous")
public class BoboFettBlueWall extends LinearOpMode {


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
        robot2.encoderDrive(0.5, -3, -3, 5);
        robot2.turnToPID(21,3);
        flywheel.flyWheelAuto(2050,5);
        robot2.turnToPID(0,3);
        robot2.encoderDrive(0.5, -12, -12, 5);
        robot2.turnToPID(90,3);
        flywheel.Finger.setPosition(.5);
        flywheel.BottomCollection.setPower(9);
        flywheel.TopCollection.setPower(9);
        robot2.encoderDrive(0.4, -27, -27, 4);
        robot2.encoderDrive(0.5, 25, 25, 10);
        robot2.turnToPID(0,3);
        flywheel.BottomCollection.setPower(0);
        flywheel.TopCollection.setPower(0);
        robot2.encoderDrive(0.5, 9.5, 9.5, 10);
        robot2.turnToPID(24.5,3);
        flywheel.Finger.setPosition(.3);
        flywheel.flyWheelAuto(2050,6);
        robot2.encoderDrive(0.5, -5, -5, 5);







        //flywheel.flyWheelAuto(1740,5);
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