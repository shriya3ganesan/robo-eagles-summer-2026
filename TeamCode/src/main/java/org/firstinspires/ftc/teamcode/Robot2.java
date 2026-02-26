
package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.sun.source.tree.CatchTree;
@Disabled



@TeleOp(name="Bob", group="Linear OpMode")

public class Robot2 extends LinearOpMode {

    BNO055IMU imuCH;
    double drvTrnSpd = .75;

    double ZeroPosition = Math.toRadians(180);
    double AbsoluteValue = 0;

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotorEx JudahBlack = null;
    private DcMotor topCollection = null;
    private DcMotor bottomCollection = null;


    @Override
    public void runOpMode() {


        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        JudahBlack = hardwareMap.get(DcMotorEx.class, "JudahBlack");
        topCollection = hardwareMap.get(DcMotor.class, "topCollection");
        bottomCollection = hardwareMap.get(DcMotor.class, "bottomCollection");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBackDrive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "leftBackDrive");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // Pushing the left stick forward MUST make robot go forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        topCollection.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bottomCollection.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Wait for the game to start (driver presses START)
        waitForStart();
        runtime.reset();


        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imuCH = hardwareMap.get(BNO055IMU.class, "imu");
        imuCH.initialize(parameters);

        telemetry.addData("Status", "Initialized");
        telemetry.update();


        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            drvTrnSpd = 1;
            leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            AbsoluteValue = -imuCH.getAngularOrientation().firstAngle + Math.PI;
            if (gamepad1.ps) {
                ZeroPosition = AbsoluteValue;

            }
            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x; // Counteract imperfect strafing
            double rx = -gamepad1.right_stick_x * .8;

            // Read inverse IMU heading, as the IMU heading is CW positive

            double botHeading = AbsoluteValue-ZeroPosition;


            double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
            double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio, but only when
            // at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;


            leftDrive.setPower(frontLeftPower * drvTrnSpd);
            leftBackDrive.setPower(backLeftPower * drvTrnSpd);
            rightDrive.setPower(frontRightPower * drvTrnSpd);
            rightBackDrive.setPower(backRightPower * drvTrnSpd);


            // Setup a variable for each drive wheel to save power level for telemetry
            double leftPower;
            double rightPower;


            // Send calculated power to wheels
            if (gamepad1.x) {
                bottomCollection.setPower(-1);
                topCollection.setPower(-.7);
            } else if (gamepad1.a) {
                bottomCollection.setPower(1);
                topCollection.setPower(.3);
            } else {
                bottomCollection.setPower(0);
                topCollection.setPower(0);
            }

            if (gamepad1.right_bumper) {
                JudahBlack.setPower(-.9);
            } else if (gamepad1.y) {
                JudahBlack.setPower(-.6);
            } else {
                JudahBlack.setPower(0);
            }

//                if (gamepad1.x) {
//                    JudahBlack.getVelocity();
//                }




            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]

            leftDrive.setPower(frontLeftPower);
            leftBackDrive.setPower(backLeftPower);
            rightDrive.setPower(frontRightPower);
            rightBackDrive.setPower(backRightPower);

            telemetry.addData("currentVelocity", JudahBlack.getVelocity());
            telemetry.update();

        }
    }
}
