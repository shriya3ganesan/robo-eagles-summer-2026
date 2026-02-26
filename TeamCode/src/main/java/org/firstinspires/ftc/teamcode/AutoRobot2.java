/*package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import android.graphics.drawable.GradientDrawable;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class AutoRobot2 {
    BNO055IMU imu;
    public DcMotor leftDrive, rightDrive, leftBackDrive, rightBackDrive;
    private LinearOpMode opMode;
    private ElapsedTime runtime = new ElapsedTime();
    Orientation lastAngles = new Orientation();
    double currAngle = 0.0;
    static final double COUNTS_PER_MOTOR_REV = 537.7;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    public AutoRobot2(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void init(HardwareMap hardwareMap) {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBackDrive");


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

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

    }
    public void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        currAngle = 0;
    }
    public double getAngle() {
        Orientation orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double deltaAngle = orientation.firstAngle - lastAngles.firstAngle;
        if (deltaAngle > 180) {
            deltaAngle -= 360;
        } else if (deltaAngle <= -180) {
            deltaAngle += 360;
        }
        currAngle += deltaAngle;
        lastAngles = orientation;
        opMode.telemetry.addData("gyro", orientation.firstAngle);
        return currAngle;
    }
    public void setAllPower(double p){setMotorPower(p,p,p,p);}
    public void setMotorPower(double leftDrive, double rightDrive, double leftBackDrive, double RightBackDrive){

    }

    public void turn(double degrees) {

        resetAngle();

        double error = degrees;

        while (linearOpMode.opModeIsActive() && Math.abs(error) > 2) {
            double motorPower = (error < 0 ? -0.3 : 0.3);
            setMotorPower(-motorPower, motorPower, -motorPower, motorPower);
            error = degrees - getAngle();
            opMode.telemetry.addData("error", error);
            opMode.telemetry.update();
        }
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);



    }
    public double getAbsoluteAngle(){
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }
    public void turnTo(double degrees){
        Orientation orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);


        double error = degrees - orientation.firstAngle;
        if (error > 180){
            error -= 360;

        } else if(error < -180){
            error += 360;
        }}
    public void turnToPID(double targetAngle, double timeoutS) {
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        TurnPidController pid = new TurnPidController(targetAngle, 0.03, 0.00000000, 0.00000000000001);
        //theOpMode.telemetry.setMsTransmissionInterval(50);
        double degreeCount = 0;
        runtime.reset();
        // Checking lastSlope to make sure that it's not oscillating when it quits
        while ((Math.abs(targetAngle - getAbsoluteAngle()) > 1 ||  pid.getLastSlope() > 1.25 || degreeCount < 4) && runtime.seconds() < timeoutS) {
            if (Math.abs(targetAngle - getAbsoluteAngle()) < 1){
                degreeCount +=1;
            }
            double motorSpeed = pid.update(getAbsoluteAngle());
            leftDrive.setPower(motorSpeed);
            rightDrive.setPower(-motorSpeed);
            leftBackDrive.setPower(motorSpeed);
            rightBackDrive.setPower(-motorSpeed);
            opMode.telemetry.addData("degreeCount", degreeCount);
            opMode.telemetry.addData("degreeCount", degreeCount);
            opMode.telemetry.addData("Current Angle", getAbsoluteAngle());
            opMode.telemetry.addData("Target Angle", targetAngle);
            opMode.telemetry.addData("Slope", pid.getLastSlope());
            opMode.telemetry.addData("Power", motorSpeed);
            opMode.telemetry.update();
        }
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }


    public void encoderDrive(double speed, double leftInches, double rightInches, double timeoutS) {


        int leftFrontTarget = leftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        int rightFrontTarget = rightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        int leftBackTarget = leftBackDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        int rightBackTarget = rightBackDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);

        leftDrive.setTargetPosition(leftFrontTarget);
        rightDrive.setTargetPosition(rightFrontTarget);
        leftBackDrive.setTargetPosition(leftBackTarget);
        rightBackDrive.setTargetPosition(rightBackTarget);

        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();

        while (opMode.opModeIsActive() &&
                runtime.seconds() < timeoutS &&
                (leftDrive.isBusy() && rightDrive.isBusy() && leftBackDrive.isBusy() && rightBackDrive.isBusy())) {
            opMode.telemetry.addData("current position",leftDrive.getCurrentPosition());
            opMode.telemetry.addData("current position",rightDrive.getCurrentPosition());
            opMode.telemetry.addData("current position",leftBackDrive.getCurrentPosition());
            opMode.telemetry.addData("current position",rightBackDrive.getCurrentPosition());
            opMode.telemetry.update();

            leftDrive.setPower(Math.abs(-speed));
            rightDrive.setPower(Math.abs(-speed));
            leftBackDrive.setPower(Math.abs(-speed));
            rightBackDrive.setPower(Math.abs(-speed));
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

}

 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.linearOpMode;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import android.graphics.drawable.GradientDrawable;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

public class AutoRobot2 {
    BNO055IMU imu;
    public DcMotor leftDrive, rightDrive, leftBackDrive, rightBackDrive;
    private LinearOpMode opMode;
    private ElapsedTime runtime = new ElapsedTime();
    Orientation lastAngles = new Orientation();
    double currAngle = 0.0;
    static final double COUNTS_PER_MOTOR_REV = 537.7;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV / (WHEEL_DIAMETER_INCHES * Math.PI);

    public AutoRobot2(LinearOpMode opMode) {
        this.opMode = opMode;
    }

    public void init(HardwareMap hardwareMap) {
        leftDrive = hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = hardwareMap.get(DcMotor.class, "right_drive");
        leftBackDrive = hardwareMap.get(DcMotor.class, "leftBackDrive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightBackDrive");


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

        imu = hardwareMap.get(BNO055IMU.class, "imu");

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json";
        parameters.loggingEnabled = true;
        parameters.loggingTag = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();


        imu.initialize(parameters);

    }
    public void resetAngle() {
        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        currAngle = 0;
    }
    public double getAngle() {
        Orientation orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double deltaAngle = orientation.firstAngle - lastAngles.firstAngle;
        if (deltaAngle > 180) {
            deltaAngle -= 360;
        } else if (deltaAngle <= -180) {
            deltaAngle += 360;
        }
        currAngle += deltaAngle;
        lastAngles = orientation;
        opMode.telemetry.addData("gyro", orientation.firstAngle);
        return currAngle;
    }
    public void setAllPower(double p){setMotorPower(p,p,p,p);}
    public void setMotorPower(double leftDrive, double rightDrive, double leftBackDrive, double RightBackDrive){

    }

    public void turn(double degrees) {

        resetAngle();

        double error = degrees;

        while (linearOpMode.opModeIsActive() && Math.abs(error) > 2) {
            double motorPower = (error < 0 ? -0.3 : 0.3);
            setMotorPower(-motorPower, motorPower, -motorPower, motorPower);
            error = degrees - getAngle();
            opMode.telemetry.addData("error", error);
            opMode.telemetry.update();
        }
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);



    }
    public double getAbsoluteAngle(){
        return imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES).firstAngle;
    }
    public void turnTo(double degrees){
        Orientation orientation = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);


        double error = degrees - orientation.firstAngle;
        if (error > 180){
            error -= 360;

        } else if(error < -180){
            error += 360;
        }}
    public void turnToPID(double targetAngle, double timeoutS) {
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        TurnPidController pid = new TurnPidController(targetAngle, 0.03, 0.00000000, 0.00000000000001);
        //theOpMode.telemetry.setMsTransmissionInterval(50);
        double degreeCount = 0;
        runtime.reset();
        // Checking lastSlope to make sure that it's not oscillating when it quits
        while ((Math.abs(targetAngle - getAbsoluteAngle()) > 1 ||  pid.getLastSlope() > 1.25 || degreeCount < 4) && runtime.seconds() < timeoutS) {
            if (Math.abs(targetAngle - getAbsoluteAngle()) < 1){
                degreeCount +=1;
            }
            double motorSpeed = pid.update(getAbsoluteAngle());
            leftDrive.setPower(motorSpeed);
            rightDrive.setPower(-motorSpeed);
            leftBackDrive.setPower(motorSpeed);
            rightBackDrive.setPower(-motorSpeed);
            opMode.telemetry.addData("degreeCount", degreeCount);
            opMode.telemetry.addData("degreeCount", degreeCount);
            opMode.telemetry.addData("Current Angle", getAbsoluteAngle());
            opMode.telemetry.addData("Target Angle", targetAngle);
            opMode.telemetry.addData("Slope", pid.getLastSlope());
            opMode.telemetry.addData("Power", motorSpeed);
            opMode.telemetry.update();
        }
        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);
    }


    public void encoderDrive(double speed, double leftInches, double rightInches, double timeoutS) {


        int leftFrontTarget = leftDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        int rightFrontTarget = rightDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        int leftBackTarget = leftBackDrive.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        int rightBackTarget = rightBackDrive.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);

        leftDrive.setTargetPosition(leftFrontTarget);
        rightDrive.setTargetPosition(rightFrontTarget);
        leftBackDrive.setTargetPosition(leftBackTarget);
        rightBackDrive.setTargetPosition(rightBackTarget);

        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();

        while (opMode.opModeIsActive() &&
                runtime.seconds() < timeoutS &&
                (leftDrive.isBusy() && rightDrive.isBusy() && leftBackDrive.isBusy() && rightBackDrive.isBusy())) {
            opMode.telemetry.addData("current position",leftDrive.getCurrentPosition());
            opMode.telemetry.addData("current position",rightDrive.getCurrentPosition());
            opMode.telemetry.addData("current position",leftBackDrive.getCurrentPosition());
            opMode.telemetry.addData("current position",rightBackDrive.getCurrentPosition());
            opMode.telemetry.update();

            leftDrive.setPower(Math.abs(-speed));
            rightDrive.setPower(Math.abs(-speed));
            leftBackDrive.setPower(Math.abs(-speed));
            rightBackDrive.setPower(Math.abs(-speed));
        }

        leftDrive.setPower(0);
        rightDrive.setPower(0);
        leftBackDrive.setPower(0);
        rightBackDrive.setPower(0);

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

    }

}