package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
@Disabled
@Autonomous(name="Robot: Robot1RedFromWall", group="Robot")

public class Robot1RedFromWall extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {

        AutoRobot1 robot1 = new AutoRobot1(this);
        robot1.init(hardwareMap );

        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();


        // Wait for the game to start (driver presses START)
        waitForStart();




        robot1.encoderDrive(.5, 110, 110, 10);
        robot1.encoderDrive(.5, 68, -68, 10);
        robot1.launch3();

    }



}


