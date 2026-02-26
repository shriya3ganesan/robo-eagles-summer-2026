package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;
@Disabled
@TeleOp(name="Robot: TeleOp POV",group ="Robot")
public class FirstFTCProgram extends LinearOpMode {
    public DcMotor leftForward   = null;
    public DcMotor  rightForward  = null;
    public DcMotor leftBack   = null;
    public DcMotor  rightBack  = null;
    public DcMotor  arm  = null;
    public DcMotor  rightLift  = null;
    public DcMotor  leftLift  = null;

    public Servo leftClaw    = null;
    public Servo rightClaw   = null;

    public Servo    dump   = null;

    double clawOffset = 0;

    public static final double MID_SERVO   =  0.5 ;

    public static final double CLAW_SPEED  = 0.04 ;
    @Override
    public void runOpMode() throws InterruptedException {

        double left;
        double right;
        double drive;
        double turn;
        double max;
        double armPower;

        leftForward  = hardwareMap.get(DcMotor.class, "left_Forward");
        rightForward = hardwareMap.get(DcMotor.class, "right_Forward");

        leftBack  = hardwareMap.get(DcMotor.class, "left_Back");
        rightBack = hardwareMap.get(DcMotor.class, "right_Back");

        rightLift  = hardwareMap.get(DcMotor.class, "right_Lift");
        leftLift = hardwareMap.get(DcMotor.class, "left_Lift");

        arm = hardwareMap.get(DcMotor.class, "arm");


        leftClaw  = hardwareMap.get(Servo.class, "left_hand");
        rightClaw = hardwareMap.get(Servo.class, "right_hand");
        leftClaw.setPosition(MID_SERVO);
        //rightClaw.setPosition(MID_SERVO);
        dump = hardwareMap.get(Servo.class, "dump");
        dump.setPosition(MID_SERVO);

        leftForward.setDirection(DcMotor.Direction.REVERSE);
        rightForward.setDirection(DcMotor.Direction.FORWARD);

        rightLift.setDirection(DcMotor.Direction.REVERSE);
        leftLift.setDirection(DcMotor.Direction.FORWARD);

        rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        rightLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        arm.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData(">", "Robot Ready.  Press START.");    //
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            drive = -gamepad1.left_stick_y;
            turn  =  gamepad1.left_stick_x;
            armPower = gamepad1.right_stick_y *.5;
            boolean ClawOpen = gamepad1.left_bumper;
            boolean ClawClose = gamepad1.right_bumper;
            boolean LiftPower = gamepad1.dpad_up;
            boolean DownPower = gamepad1.dpad_down;
            boolean Dump = gamepad1.x;
            boolean UnDump = gamepad1.a;

            // Combine drive and turn for blended motion.
            left  = drive + turn;
            right = drive - turn;

            // Normalize the values so neither exceed +/- 1.0
            max = Math.max(Math.abs(left), Math.abs(right));
            if (max > 1.0)
            {
                left /= max;
                right /= max;
            }

            // Output the safe vales to the motor drives.
            leftForward.setPower(left);
            rightForward.setPower(right);
            leftBack.setPower(left);
            rightBack.setPower(right);

            arm.setPower(armPower);
            if (ClawClose)
                clawOffset += CLAW_SPEED;
            else if (ClawOpen)
                clawOffset -= CLAW_SPEED;

            // Move both servos to new position.  Assume servos are mirror image of each other.
            clawOffset = Range.clip(clawOffset, -0.5, 0.5);

            leftClaw.setPosition(MID_SERVO + clawOffset);
            //rightClaw.setPosition(MID_SERVO - clawOffset);

            double linearPower = (double)(1450 - leftLift.getCurrentPosition()) /1075;
            if (LiftPower) {
                if (leftLift.getCurrentPosition() < 1020){
                    rightLift.setPower(.4);
                    leftLift.setPower(.4);
                }
                   else if (leftLift.getCurrentPosition() >= 1020) {
                       rightLift.setPower(linearPower);
                    leftLift.setPower(linearPower);
                }
            }
            else if (DownPower && leftLift.getCurrentPosition() > 0) {
                rightLift.setPower(-.4);
                leftLift.setPower(-.4);
            }
            else {
                rightLift.setPower(0);
                leftLift.setPower(0);
            }
            if(Dump) {
                dump.setPosition(.75);
            }
            else if(UnDump) {
                dump.setPosition(.36);
            }
            telemetry.addData("left",  "%.2f", left);
            telemetry.addData("right", "%.2f", right);
            telemetry.addData("rightLiftPosition", rightLift.getCurrentPosition());
            telemetry.addData("leftLiftPosition", leftLift.getCurrentPosition());
            telemetry.addData("linearPower",linearPower);
            telemetry.update();

            sleep(50);
        }


    }

}

