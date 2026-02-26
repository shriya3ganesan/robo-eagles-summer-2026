package org.firstinspires.ftc.teamcode;

import static android.os.SystemClock.sleep;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@TeleOp(name="Mecanum Drive Example", group="Iterative Opmode")
public class MecanumDrive extends LinearOpMode {


    public DcMotor leftForward  = null;
    public DcMotor leftBack  = null;
    public DcMotor rightForward  = null;
    public DcMotor rightBack  = null;
    public DcMotor launch = null;
    public DcMotor collect = null;
    public DcMotor collect2 = null;
    public DcMotor collect3 = null;



    @Override
    public void runOpMode() throws InterruptedException {



        leftForward = hardwareMap.get(DcMotor.class, "left_Forward");
        leftBack = hardwareMap.get(DcMotor.class, "left_Back");

        rightForward = hardwareMap.get(DcMotor.class, "right_Forward");
        rightBack = hardwareMap.get(DcMotor.class, "right_Back");

        launch = hardwareMap.get(DcMotor.class, "launch");

        collect = hardwareMap.get(DcMotor.class, "collect");

        collect2 = hardwareMap.get(DcMotor.class, "collect2");

        collect3 = hardwareMap.get(DcMotor.class, "collect3");

        leftForward.setDirection(DcMotor.Direction.REVERSE);
        leftBack.setDirection(DcMotor.Direction.REVERSE);


        rightForward.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        launch.setDirection(DcMotor.Direction.REVERSE);

        collect.setDirection(DcMotor.Direction.FORWARD);

        collect2.setDirection(DcMotor.Direction.FORWARD);

        collect3.setDirection(DcMotor.Direction.FORWARD);



        telemetry.addData(">", "Robot Ready.  Press START.");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {



            double drive = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double twist = gamepad1.right_stick_x;
            float launch = gamepad1.left_trigger;
            float unlaunch = gamepad1.right_trigger;
            boolean collect = gamepad1.left_bumper;
            boolean collect2 = gamepad1.right_bumper;
            boolean collect3 = gamepad1.x;

            double[] speeds = {
                    (drive + strafe + twist),
                    (drive - strafe - twist),
                    (drive - strafe + twist),
                    (drive + strafe - twist)
            };


            double max = Math.abs(speeds[0]);
            for (int i = 0; i < speeds.length; i++) {
                if (max < Math.abs(speeds[i])) max = Math.abs(speeds[i]);
            }


            if (max > 1) {
                for (int i = 0; i < speeds.length; i++) speeds[i] /= max;
            }


            leftForward.setPower(speeds[0]);
            rightForward.setPower(speeds[1]);
            leftBack.setPower(speeds[2]);
            rightBack.setPower(speeds[3]);

        }
    }

}