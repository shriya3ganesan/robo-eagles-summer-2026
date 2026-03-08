package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.euler.Constant.LEFT_MOTOR;
import static org.firstinspires.ftc.teamcode.euler.Constant.RIGHT_MOTOR;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.euler.driver.Driver;

@Autonomous(preselectTeleOp = "EulerTeleop",group = "Euler")
public class EulerAutonomous extends LinearOpMode {
    DcMotor leftMotor,rightMotor;
    Driver myRobotDriver;

    void initialize(){
        leftMotor = hardwareMap.get(DcMotor.class, LEFT_MOTOR);
        rightMotor = hardwareMap.get(DcMotor.class, RIGHT_MOTOR);
        myRobotDriver = new Driver(leftMotor, rightMotor);
    }

    @Override
    public void runOpMode() {
        initialize();

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            shoot();
            intake.run();
            // avance
            myRobotDriver.drive(1,1);
            sleep(500);
            // tourne à g
            myRobotDriver.drive(0,1);
            sleep(200);
            // avance au mur
            myRobotDriver.drive(1,1);
            sleep(500);
            // recule vers le centre
            myRobotDriver.drive(-1,-1);
            sleep(500);
            shoot();

        }
    }
}
