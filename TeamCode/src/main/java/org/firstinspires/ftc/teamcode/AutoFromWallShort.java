package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="Robot: Robot1RedAutoGoal", group="Robot")

public class AutoFromWallShort extends LinearOpMode {



    @Override
    public void runOpMode() throws InterruptedException {

        AutoRobot1 robot1 = new AutoRobot1(this);
        robot1.init(hardwareMap );

        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses START)
        waitForStart();





               robot1.encoderDrive(.2, 12, 12, 20);
    }



}


