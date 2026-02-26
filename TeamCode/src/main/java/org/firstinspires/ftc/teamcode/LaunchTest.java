package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Disabled
@TeleOp(name="RobotInOneWeek", group="Iterative Opmode")
public class LaunchTest extends LinearOpMode {


    public DcMotor launchMotor = null;


    @Override
    public void runOpMode() throws InterruptedException {


        launchMotor = hardwareMap.get(DcMotor.class, "launch");


        launchMotor.setDirection(DcMotor.Direction.REVERSE);


        telemetry.addData(">", "Robot Ready.  Press START.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {


            double launch = gamepad1.left_trigger;


            if (launch > .1) {
                launchMotor.setPower(launch);
            } else {
                launchMotor.setPower(0);
            }


            if (gamepad1.b) {
                launchMotor.setPower(.1);
                sleep(3000);
                launchMotor.setPower(0);
            }


        }
    }
}