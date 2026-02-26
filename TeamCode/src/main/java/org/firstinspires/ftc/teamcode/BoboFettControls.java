package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.ColorSensor;


public class BoboFettControls {

    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotorEx JudahBlack = null;
    private DcMotor topCollection = null;
    private DcMotor bottomCollection = null;
    public ColorSensor colorSensorTop = null;
    public ColorSensor colorSensorMiddle = null;
    public ColorSensor colorSensorBottom = null;
    public Servo topLight = null;
    public Servo middleLight = null;
    public Servo bottomLight = null;
    public Servo Finger = null;

    BNO055IMU imuCH;

    double drvTrnSpd = .75;
    private OpMode theOpMode;
    double ZeroPosition = Math.toRadians(180);
    double AbsoluteValue = 0;
    public BoboFettControls(HardwareMap hardwareMap, OpMode opMode) {
        theOpMode = opMode;


        colorSensorTop = theOpMode.hardwareMap.get(ColorSensor.class, "colorSensorTop");
        colorSensorMiddle = theOpMode.hardwareMap.get(ColorSensor.class, "colorSensorMiddle");
        colorSensorBottom = theOpMode.hardwareMap.get(ColorSensor.class, "colorSensorBottom");
        topLight = theOpMode.hardwareMap.get(Servo.class, "topLight");
        middleLight = theOpMode.hardwareMap.get(Servo.class, "middleLight");
        bottomLight = theOpMode.hardwareMap.get(Servo.class, "bottomLight");
        Finger = theOpMode.hardwareMap.get(Servo.class, "Finger");
        leftDrive = theOpMode.hardwareMap.get(DcMotor.class, "left_drive");
        rightDrive = theOpMode.hardwareMap.get(DcMotor.class, "right_drive");
        JudahBlack = theOpMode.hardwareMap.get(DcMotorEx.class, "JudahBlack");
        topCollection = theOpMode.hardwareMap.get(DcMotor.class, "topCollection");
        bottomCollection = theOpMode.hardwareMap.get(DcMotor.class, "bottomCollection");
        rightBackDrive = theOpMode.hardwareMap.get(DcMotor.class, "rightBackDrive");
        leftBackDrive = theOpMode.hardwareMap.get(DcMotor.class, "leftBackDrive");
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.REVERSE);


    }
    public void teleOpControls(){

        if (theOpMode.gamepad2.x || theOpMode.gamepad1.x ) {
            bottomCollection.setPower(-1);
            topCollection.setPower(-1);
        } else if (theOpMode.gamepad2.left_bumper) {
            bottomCollection.setPower(1)
            ;
            topCollection.setPower(.8);
        } else {
            bottomCollection.setPower(0);
            topCollection.setPower(0);
        }

        if (theOpMode.gamepad2.right_bumper) {
            JudahBlack.setVelocity(-2300);
        }

        else if(theOpMode.gamepad2.y || theOpMode.gamepad1.y) {
            JudahBlack.setVelocity(-1800);
        } else {
            JudahBlack.setPower(0);
        }

        if (theOpMode.gamepad1.right_bumper ){
            Finger.setPosition(.3);
        }
        else if(theOpMode.gamepad1.left_bumper){
            Finger.setPosition(.5);
        }


        topCollection.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bottomCollection.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);



        if (colorSensorTop.alpha() < 90){
            theOpMode.telemetry.addData("far", "distance");
            if (colorSensorTop.red() < 22 && colorSensorTop.red() > 18){
                theOpMode.telemetry.addData("purple", "artifact");
                topLight.setPosition(.7);

            }
            else if (colorSensorTop.red() < 18){
                theOpMode.telemetry.addData("green", "artifact");
                topLight.setPosition(.5);
            }
            else
                topLight.setPosition(0);

        }
        else if (colorSensorTop.alpha() > 1500){
            theOpMode.telemetry.addData("close","lessDistance");
            if (colorSensorTop.red() > 920){
                theOpMode.telemetry.addData("purple", "artifact");
                topLight.setPosition(.7);
            }
            else if (colorSensorTop.red() < 800){
                theOpMode.telemetry.addData("green", "artifact");
                topLight.setPosition(.5);
            }
            else
                topLight.setPosition(0);
        }
        else {
            theOpMode.telemetry.addData("mid", "midDistance");
            if (colorSensorTop.red() > 30){
                theOpMode.telemetry.addData("purple", "artifact");
                topLight.setPosition(.7);
            }
            else if (colorSensorTop.red() < 28 && colorSensorTop.red() > 22){
                theOpMode.telemetry.addData("green", "artifact");
                topLight.setPosition(.5);
            }
            else
                topLight.setPosition(0);
        }


        if (colorSensorMiddle.alpha() < 90){
            theOpMode.telemetry.addData("far", "distance");
            if (colorSensorMiddle.red() < 22 && colorSensorMiddle.red() > 18){
                theOpMode.telemetry.addData("purple", "artifact");
                middleLight.setPosition(.7);

            }
            else if (colorSensorMiddle.red() < 18){
                theOpMode.telemetry.addData("green", "artifact");
                middleLight.setPosition(.5);
            }
            else
                middleLight.setPosition(0);

        }
        else if (colorSensorMiddle.alpha() > 1500){
            theOpMode.telemetry.addData("close","lessDistance");
            if (colorSensorMiddle.red() > 920){
                theOpMode.telemetry.addData("purple", "artifact");
                middleLight.setPosition(.7);
            }
            else if (colorSensorMiddle.red() < 800){
                theOpMode.telemetry.addData("green", "artifact");
                middleLight.setPosition(.5);
            }
            else
                middleLight.setPosition(0);
        }
        else {
            theOpMode.telemetry.addData("mid", "midDistance");
            if (colorSensorMiddle.red() > 30){
                theOpMode.telemetry.addData("purple", "artifact");
                middleLight.setPosition(.7);
            }
            else if (colorSensorMiddle.red() < 28 && colorSensorMiddle.red() > 22){
                theOpMode.telemetry.addData("green", "artifact");
                middleLight.setPosition(.5);
            }
            else
                middleLight.setPosition(0);
        }






        if (colorSensorBottom.alpha() < 90){
            theOpMode.telemetry.addData("far", "distance");
            if (colorSensorBottom.red() < 22 && colorSensorBottom.red() > 18){
                theOpMode.telemetry.addData("purple", "artifact");
                bottomLight.setPosition(.7);

            }
            else if (colorSensorBottom.red() < 18){
                theOpMode.telemetry.addData("green", "artifact");
                bottomLight.setPosition(.5);
            }
            else
                bottomLight.setPosition(0);

        }
        else if (colorSensorBottom.alpha() > 1500){
            theOpMode.telemetry.addData("close","lessDistance");
            if (colorSensorBottom.red() > 920){
                theOpMode.telemetry.addData("purple", "artifact");
                bottomLight.setPosition(.7);
            }
            else if (colorSensorBottom.red() < 800){
                theOpMode.telemetry.addData("green", "artifact");
                bottomLight.setPosition(.5);
            }
            else
                bottomLight.setPosition(0);
        }
        else {
            theOpMode.telemetry.addData("mid", "midDistance");
            if (colorSensorBottom.red() > 30){
                theOpMode.telemetry.addData("purple", "artifact");
                bottomLight.setPosition(.7);
            }
            else if (colorSensorBottom.red() < 28 && colorSensorBottom.red() > 22){
                theOpMode.telemetry.addData("green", "artifact");
                bottomLight.setPosition(.5);
            }
            else
                bottomLight.setPosition(0);
        }

        theOpMode.telemetry.addData("colorSensorAlphaTop", colorSensorTop.alpha());
        theOpMode.telemetry.addData("colorSensorRedTop", colorSensorTop.red());
        theOpMode.telemetry.addData("colorSensorGreenTop", colorSensorTop.green());
        theOpMode.telemetry.addData("colorSensor", colorSensorMiddle.alpha());
        theOpMode.telemetry.addData("colorSensorRed", colorSensorMiddle.red());
        theOpMode.telemetry.addData("colorSensorGreen", colorSensorMiddle.green());
        theOpMode.telemetry.addData("colorSensor", colorSensorBottom.alpha());
        theOpMode.telemetry.addData("colorSensorRed", colorSensorBottom.red());
        theOpMode.telemetry.addData("colorSensorGreen", colorSensorBottom.green());
        theOpMode.telemetry.addData("currentVelocity", JudahBlack.getVelocity());
        theOpMode.telemetry.update();

    }
}