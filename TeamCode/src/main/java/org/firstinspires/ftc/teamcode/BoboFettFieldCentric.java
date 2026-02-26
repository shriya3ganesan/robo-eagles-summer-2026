package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class BoboFettFieldCentric {
    DcMotor leftDrive;
    DcMotor rightDrive;
    DcMotor leftBackDrive;
    DcMotor rightBackDrive;
    private OpMode theOpMode;

    BNO055IMU imuCH
            ;
    double drvTrnSpd = .75;

    double ZeroPosition = Math.toRadians(180);
    double AbsoluteValue = 0;


    public BoboFettFieldCentric(HardwareMap hardwareMap, OpMode opMode, double encoderTicksPerRev, double gearRatio, double wheelDiameter) {
        theOpMode = opMode;
        theOpMode.telemetry.addData("Running to", "here");
        theOpMode.telemetry.update();
        leftDrive = hardwareMap.dcMotor.get("left_drive");
        rightDrive = hardwareMap.dcMotor.get("right_drive");
        leftBackDrive = hardwareMap.dcMotor.get("leftBackDrive");
        rightBackDrive = hardwareMap.dcMotor.get("rightBackDrive");


        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        double countsPerInch = (encoderTicksPerRev * gearRatio) / (wheelDiameter * 3.14);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        imuCH = opMode.hardwareMap.get(BNO055IMU.class, "imu");
        imuCH.initialize(parameters);
    }
    public void UpdateDriveTrain() {
        drvTrnSpd = 1;
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        AbsoluteValue = -imuCH.getAngularOrientation().firstAngle;
        if (theOpMode.gamepad1.ps) {
            ZeroPosition = AbsoluteValue;

        }
        double y = theOpMode.gamepad1.left_stick_y; // Remember, this is reversed!
        double x = -theOpMode.gamepad1.left_stick_x; // Counteract imperfect strafing
        double rx = -theOpMode.gamepad1.right_stick_x * .8;

        // Read inverse IMU heading, as the IMU heading is CW positive

        //double botHeading = AbsoluteValue-ZeroPosition;
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


        leftDrive.setPower(frontLeftPower*drvTrnSpd);
        leftBackDrive.setPower(backLeftPower*drvTrnSpd);
        rightDrive.setPower(frontRightPower*drvTrnSpd);
        rightBackDrive.setPower(backRightPower*drvTrnSpd);

    }

}