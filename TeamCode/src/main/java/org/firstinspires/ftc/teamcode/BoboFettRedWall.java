package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

@Autonomous(name = "BoboFettRedWall", group = "Autonomous")
public class BoboFettRedWall extends LinearOpMode {

    AutoRobot2 robot2;
    Flywheel flywheel;



    @Override
    public void  runOpMode() {
        flywheel = new Flywheel(this);
        robot2 = new AutoRobot2(this);
        robot2.init(hardwareMap);
        flywheel.init(hardwareMap);
        telemetry.update();

        waitForStart();
        robot2.encoderDrive(0.5, -3, -3, 1);
        robot2.turnToPID(-21,1);
        flywheel.flyWheelAuto(2200,5);
        robot2.turnToPID(0,1);
        robot2.encoderDrive(0.5, -10.5, -10.5, 1);
        robot2.turnToPID(0,1);
        robot2.turnToPID(-88,1);
        flywheel.Finger.setPosition(.5);
        flywheel.BottomCollection.setPower(1);
        flywheel.TopCollection.setPower(1);
        robot2.encoderDrive(0.5, -27, -27, 1);
        robot2.encoderDrive(0.5, 25, 25, 1);
        robot2.turnToPID(0,3);
        robot2.encoderDrive(0.5, 9.5, 9.5, 1);
        flywheel.BottomCollection.setPower(0);
        flywheel.TopCollection.setPower(0);
        robot2.turnToPID(-21.5,2);
        flywheel.Finger.setPosition(.3);
        flywheel.flyWheelAuto(2200,5);
        robot2.turnToPID(0,2);
        robot2.encoderDrive(0.5, -23, -23, 1);
        robot2.turnToPID(-87,3);
        flywheel.Finger.setPosition(.5);
        flywheel.BottomCollection.setPower(1);
        flywheel.TopCollection.setPower(1);
        robot2.encoderDrive(0.5, -25, -25, 1);
        robot2.encoderDrive(0.5, 20, 20, 1);
        robot2.turnToPID(0,1);
        robot2.encoderDrive(0.5, 29, 29, 1);
        flywheel.BottomCollection.setPower(0);
        flywheel.TopCollection.setPower(0);
        robot2.turnToPID(-21,1);
        flywheel.Finger.setPosition(.3);
        flywheel.flyWheelAuto(2200,5);
        robot2.encoderDrive(0.5, -15, -15, 1);











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