package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;


public class AutoRobot1 {
    public DcMotor leftForward = null;
    public DcMotor rightForward = null;
    public DcMotor leftBack = null;
    public DcMotor rightBack = null;
    public DcMotor launchMotor = null;
    public DcMotor middleMotor = null;
    public DcMotor topMotor = null;
    public DcMotor bottomMotor = null;


    private ElapsedTime runtime = new ElapsedTime();


    static final double FORWARD_SPEED = 1.0;
    static final double TURN_SPEED = 0.5;

    static final double COUNTS_PER_MOTOR_REV = 537.7;
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 4.1;
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * Math.PI);
    LinearOpMode opMode;
    public AutoRobot1(LinearOpMode OP) {
    opMode = OP;
    }

    public void init(HardwareMap hardwareMap){
        leftForward = hardwareMap.get(DcMotor.class, "left_Forward");
        rightForward = hardwareMap.get(DcMotor.class, "right_Forward");

        leftBack = hardwareMap.get(DcMotor.class, "left_Back");
        rightBack = hardwareMap.get(DcMotor.class, "right_Back");

        launchMotor = hardwareMap.get(DcMotor.class, "launch");

        middleMotor = hardwareMap.get(DcMotor.class, "collect");
        topMotor = hardwareMap.get(DcMotor.class, "collect2");
        bottomMotor = hardwareMap.get(DcMotor.class, "collect3");

        launchMotor.setDirection(DcMotor.Direction.FORWARD);

        leftForward.setDirection(DcMotor.Direction.REVERSE);
        rightForward.setDirection(DcMotor.Direction.FORWARD);

        leftBack.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        middleMotor.setDirection(DcMotor.Direction.REVERSE);
        topMotor.setDirection(DcMotor.Direction.REVERSE);
        bottomMotor.setDirection(DcMotor.Direction.REVERSE);

        leftForward.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightForward.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        launchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        middleMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        topMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bottomMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftForward.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightForward.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        launchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        middleMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        topMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bottomMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftForward.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightForward.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



    }
    public void encoderDrive(double speed,
                             double leftInches, double rightInches,
                             double timeoutS) {
        int LeftFrontTarget;

        int RightFrontTarget;
        int LeftBackTarget;
        int RightBackTarget;


        // Ensure that the OpMode is still active
        if (opMode.opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            LeftFrontTarget = leftForward.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            RightFrontTarget = rightForward.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);

            LeftBackTarget = leftBack.getCurrentPosition() + (int) (leftInches * COUNTS_PER_INCH);
            RightBackTarget = rightBack.getCurrentPosition() + (int) (rightInches * COUNTS_PER_INCH);
            leftForward.setTargetPosition(LeftFrontTarget);
            rightForward.setTargetPosition(RightFrontTarget);

            leftBack.setTargetPosition(LeftBackTarget);
            rightBack.setTargetPosition(RightBackTarget);



            // Turn On RUN_TO_POSITION
            leftForward.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightForward.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            leftBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightBack.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            // reset the timeout time and start motion.


            runtime.reset();
            leftForward.setPower(Math.abs(speed));
            rightForward.setPower(Math.abs(speed));

            leftBack.setPower(Math.abs(speed));
            rightBack.setPower(Math.abs(speed));

            // keep looping while we are still active, and there is time left, and both motors are running.
            // Note: We use (isBusy() && isBusy()) in the loop test, which means that when EITHER motor hits
            // its target position, the motion will stop.  This is "safer" in the event that the robot will
            // always end the motion as soon as possible.
            // However, if you require that BOTH motors have finished their moves before the robot continues
            // onto the next step, use (isBusy() || isBusy()) in the loop test.
            while (opMode.opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (leftForward.isBusy() && rightForward.isBusy() && leftBack.isBusy()  ))//rightBack.isBusy())) {

            {
                // Display it for the driver.
                opMode.telemetry.addData("Running to", " %7d :%7d", LeftFrontTarget, RightFrontTarget);
                opMode.telemetry.addData("Running to", " %7d :%7d", LeftBackTarget, RightBackTarget);
                opMode.telemetry.addData("Currently at", " at %7d :%7d",
                        leftForward.getCurrentPosition(), rightForward.getCurrentPosition());
                opMode.telemetry.addData("Currently at", " at %7d :%7d",
                        leftBack.getCurrentPosition(), rightBack.getCurrentPosition());


                opMode.telemetry.update();
            }

            // Stop all motion;
            leftForward.setPower(0);
            rightForward.setPower(0);

            leftBack.setPower(0);
            rightBack.setPower(0);

            leftForward.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightForward.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            leftBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            rightBack.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


            // Turn off RUN_TO_POSITION
            leftForward.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightForward.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            opMode.sleep(250);   // optional pause after each move.
        }



    }

    public void launch1() {
        int targetPos = (launchMotor.getCurrentPosition() + (int) (1425.1));
        launchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launchMotor.setPower(1);


        while (opMode.opModeIsActive() && launchMotor.getCurrentPosition() < (targetPos)) {
            opMode.telemetry.addData("Launch pos", launchMotor.getCurrentPosition());
            opMode.telemetry.update();

        }
        ;

        launchMotor.setPower(0);
        opMode.telemetry.addLine("Done Launching");
        opMode.telemetry.update();

    }
    public void launch3(){
        launch1();
        loading();
        launch1();
        loading();
        topMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        topMotor.setPower(1);
        opMode.sleep(1000);
        launch1();
        opMode.telemetry.addLine("doneShooting");
        opMode.telemetry.update();
        topMotor.setPower(0);
    }
    public void loading(){
        topMotor.setTargetPosition(topMotor.getCurrentPosition() + (int) (1500));
        topMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        topMotor.setPower(1);
        middleMotor.setTargetPosition(topMotor.getCurrentPosition() + (int) (1500));
        middleMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        middleMotor.setPower(1);
        opMode.telemetry.addLine("topMotor on");
        opMode.telemetry.update();
        opMode.sleep(200);
        while (opMode.opModeIsActive()&& topMotor.isBusy()){
            opMode.telemetry.addData("topPosition",topMotor.getCurrentPosition());
            opMode.telemetry.addData("middlePosition",middleMotor.getCurrentPosition());
            opMode.telemetry.update();


        };
    }
}
