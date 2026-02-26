package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
@Disabled
@Autonomous(name="Robot: Robot1RedGoal", group="Robot")

public class AutoRobot1Red extends LinearOpMode {



    @Override
    public void runOpMode() throws InterruptedException {

        AutoRobot1 robot1 = new AutoRobot1(this);
        robot1.init(hardwareMap );

        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();





        robot1.encoderDrive(.5, 45, 45, 10);
        robot1.encoderDrive(.5, -25, 25, 10);
        robot1.launch3();
        robot1.bottomMotor.setPower(1.0);
        robot1.middleMotor.setPower(1.0);
        robot1.encoderDrive(.2, 55, 55, 20);   //Drive to balls for collection

        
    }



}

