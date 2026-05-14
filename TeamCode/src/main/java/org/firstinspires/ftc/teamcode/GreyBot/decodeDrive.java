package org.firstinspires.ftc.teamcode.GreyBot;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

@TeleOp(name="decodeDrive", group="Grey-bot")
public class decodeDrive extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx frontLeftDrive = null;
    private DcMotorEx backLeftDrive = null;
    private DcMotorEx frontRightDrive = null;
    private DcMotorEx backRightDrive = null;
    private static final boolean USE_WEBCAM = true; //not yet detecting april tags
    private AprilTagProcessor aprilTag;
    private VisionPortal visionPortal;
    private DcMotorEx lancher;

    private DcMotor intake = null;

    private DcMotor lift1 = null;

    private DcMotor lift2 = null;

    private DcMotor passThrough = null;

    private boolean motorOn = false;
    private boolean buttonPressed = false;

    public ElapsedTime lancherTimer;

    double f = 1;


    double[] stepSize = {10.0, 1.0, 0.1, 0.01, 0.001};

    int stepIndex = 0;

    public double curvelocity = 0;
    public double error = 0;



    @Override
    public void runOpMode() {

//        initAprilTag();


        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        frontLeftDrive = hardwareMap.get(DcMotorEx.class, "frontLeftDrive");
        backLeftDrive = hardwareMap.get(DcMotorEx.class, "backLeftDrive");
        frontRightDrive = hardwareMap.get(DcMotorEx.class, "frontRightDrive");
        backRightDrive = hardwareMap.get(DcMotorEx.class, "backRightDrive");
//frontRight; reverse
        lancher = hardwareMap.get(DcMotorEx.class, "lancher");
        intake = hardwareMap.get(DcMotor.class, "intake");
//        lift1 = hardwareMap.get(DcMotor.class, "lift1");
//        lift2 = hardwareMap.get(DcMotor.class, "lift2");
        passThrough = hardwareMap.get(DcMotor.class, "passThrough");
        lancher.setPower(0); // Ensure motor is off initially


        // ########################################################################################
        // !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
        // ########################################################################################
        // Most robots need the motors on one side to be reversed to drive forward.
        // The motor reversals shown here are for a "direct drive" robot (the wheels turn the same direction as the motor shaft)
        // If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
        // that your motors are turning in the correct direction.  So, start out with the reversals here, BUT
        // when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
        // Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward
        // Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
        backLeftDrive.setDirection(DcMotor.Direction.REVERSE);
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);
        frontRightDrive.setDirection(DcMotor.Direction.FORWARD);

        lancher.setDirection(DcMotorEx.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.REVERSE);
//        lift1.setDirection(DcMotorSimple.Direction.FORWARD);
//        lift2.setDirection(DcMotorSimple.Direction.FORWARD);
        passThrough.setDirection(DcMotorSimple.Direction.FORWARD);

        lancher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        if (opModeIsActive()) {

            // run until the end of the match (driver presses STOP)
            while (opModeIsActive()) {
                double max;

                // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
                double axial = -gamepad1.left_stick_y; //forward backward // Note: pushing stick forward gives negative value
                double lateral = gamepad1.left_stick_x; // lefty righty
                double yaw = gamepad1.right_stick_x; //you spin me right round baby right round like a record baby right round round round
//            double power = gamepad2.right_stick_y;   //part isn't complete yet
                double targetRPM = 5700; //shooter
                double ticksPerRev = 28;

//            if (gamepad1.left_trigger > 0.000) {
//                axial = axial * 0.55;
//                lateral = lateral * 0.55;
//                yaw = yaw * 0.55;
//            }
//            if (gamepad1.left_trigger > 0.000 && gamepad1.left_trigger < 0.001) {
//                axial = axial / 0.55;
//                lateral = lateral / 0.55;
//                yaw = yaw / 0.55;
//            }


                // Combine the joystick requests for each axis-motion to determine each wheel's power.
                // Set up a variable for each drive wheel to save the power level for telemetry.

                double frontLeftPower = axial + lateral + yaw;
                double frontRightPower = axial - lateral - yaw;
                double backLeftPower = axial - lateral + yaw;
                double backRightPower = axial + lateral - yaw;
//            double liftpower = power; //see line 102

                // Normalize the values so no wheel power exceeds 100%
                // This ensures that the robot maintains the desired motion.
                max = Math.max(Math.abs(frontLeftPower), Math.abs(frontRightPower)); //if the values are 0.5, 1, 1.5, and 2, 0.5 is now 0.25. Idk if this is intended
                max = Math.max(max, Math.abs(backLeftPower));
                max = Math.max(max, Math.abs(backRightPower));

                if (max > 1.0) {
                    frontLeftPower /= max;
                    frontRightPower /= max;
                    backLeftPower /= max;
                    backRightPower /= max;
//                liftpower /= max;
                }

                // This is test code:
                //
                // Uncomment the following code to test your motor directions.
                // Each button should make the corresponding motor run FORWARD.
                //   1) First get all the motors to take to correct positions on the robot
                //      by adjusting your Robot Configuration if necessary.
                //   2) Then make sure they run in the correct direction by modifying the
                //      the setDirection() calls above.
                // Once the correct motors move in the correct direction re-comment this code.

            /*
            frontLeftPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            backLeftPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            frontRightPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            backRightPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
            */

                // Send calculated power to wheels
                frontLeftDrive.setPower(Math.pow(frontLeftPower, 3));
                frontRightDrive.setPower(Math.pow(frontRightPower, 3));
                backLeftDrive.setPower(Math.pow(backLeftPower, 3));
                backRightDrive.setPower(Math.pow(backRightPower, 3));
//            lift1.setPower(liftpower);
//            lift2.setPower(liftpower);

                PIDFCoefficients ava = new PIDFCoefficients(96, 0, 0, 12.227);

                lancher.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, ava);



                PIDFCoefficients frontRight = new PIDFCoefficients(f, 0, 0, 0);
                frontRightDrive.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, frontRight);
                if (gamepad2.right_stick_y > 0.01) {
                    lancher.setVelocity((targetRPM * ticksPerRev) / 60);
                }
                if (gamepad2.right_stick_y <= 0.01){
                    lancher.setVelocity(0);
                }




                if (gamepad2.b){
                    if (!buttonPressed){
                        buttonPressed = true;
                    }
                }
                if (gamepad2.b){
                    if (buttonPressed){
                        buttonPressed = false;
                    }
                }
                else{
                    buttonPressed = false;
                }

                //  \/-Make the lancher lanch when right pad2 stick moved up
                //make passthough go forward/back when button pressed, when both are pressed, it goes negative
                if (gamepad2.a){
                    passThrough.setPower(-0.6);
                } else if (gamepad2.right_bumper) {
                    passThrough.setPower(0.4);
                    intake.setPower(.4);
                }
                else if (gamepad2.right_trigger > .1){
                    intake.setPower(0);
                    passThrough.setPower(0.4);
                }
                else if (gamepad2.left_bumper){
                    intake.setPower(.75);
                    passThrough.setPower(-0.3);
                }
                else if (gamepad2.dpad_down){
                    intake.setPower(-.75);
                }
                else {
                    intake.setPower(0);
                    passThrough.setPower(0);
                }
                if (gamepad1.bWasPressed()){
                    stepIndex = (stepIndex + 1) % stepSize.length;
                }

                if (gamepad1.dpadLeftWasPressed()){
                    f += stepSize[stepIndex];
                }
                if (gamepad1.dpadRightWasPressed()) {
                    f -= stepSize[stepIndex];
                }



                // Show the elapsed game time and wheel power.
                telemetry.addData("velocity",lancher.getVelocity());
                telemetry.addData("passthrough power",passThrough.getPower());
                telemetry.addData("intake power", intake.getPower());
                telemetry.addData("Left Front Velocity: ", frontLeftDrive.getVelocity());
                telemetry.addData("Right Front Velocity: ", frontRightDrive.getVelocity());
                telemetry.addData("Left Back Velocity: ", backLeftDrive.getVelocity());
                telemetry.addData("Right Back Velocity: ", backRightDrive.getVelocity());
                telemetry.addData("tunning f","%.4f (d-pad L/R)" ,f);
                telemetry.addData("step size","%.4f (b button)" ,stepSize[stepIndex]);
                telemetry.update();

//            telemetryAprilTag();

                // Push telemetry to the Driver Station.

                // Save CPU resources; can resume streaming when needed.
//            if (gamepad1.dpad_down) {
//                visionPortal.stopStreaming();
//            } else if (gamepad1.dpad_up) {
//                visionPortal.resumeStreaming();
//            }

                // Share the CPU.
//            sleep(20);
            }
//        visionPortal.close();
        }
    }

//    private void telemetryAprilTag() {
//
//        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//        telemetry.addData("# AprilTags Detected", currentDetections.size());
//
//        // Step through the list of detections and display info for each one.
//        for (AprilTagDetection detection : currentDetections) {
//            if (detection.metadata != null) {
//                telemetry.addLine(String.format("\n==== (ID %d) %s", detection.id, detection.metadata.name));
//                telemetry.addLine(String.format("XYZ %6.1f %6.1f %6.1f  (inch)", detection.ftcPose.x, detection.ftcPose.y, detection.ftcPose.z));
//                telemetry.addLine(String.format("PRY %6.1f %6.1f %6.1f  (deg)", detection.ftcPose.pitch, detection.ftcPose.roll, detection.ftcPose.yaw));
//                telemetry.addLine(String.format("RBE %6.1f %6.1f %6.1f  (inch, deg, deg)", detection.ftcPose.range, detection.ftcPose.bearing, detection.ftcPose.elevation));
//            } else {
//                telemetry.addLine(String.format("\n==== (ID %d) Unknown", detection.id));
//                telemetry.addLine(String.format("Center %6.0f %6.0f   (pixels)", detection.center.x, detection.center.y));
//            }
//        }   // end for() loop
//
//        // Add "key" information to telemetry
//        telemetry.addLine("\nkey:\nXYZ = X (Right), Y (Forward), Z (Up) dist.");
//        telemetry.addLine("PRY = Pitch, Roll & Yaw (XYZ Rotation)");
//        telemetry.addLine("RBE = Range, Bearing & Elevation");
//
//    }

//    private void initAprilTag() {
//
//        // Create the AprilTag processor the easy way.
//        aprilTag = AprilTagProcessor.easyCreateWithDefaults();
//
//        // Create the vision portal the easy way.
//        if (USE_WEBCAM) {
//            visionPortal = VisionPortal.easyCreateWithDefaults(
//                    hardwareMap.get(WebcamName.class, "Webcam 1"), aprilTag);
//        } else {
//            visionPortal = VisionPortal.easyCreateWithDefaults(
//                    BuiltinCameraDirection.BACK, aprilTag);
//        }
//    }
}