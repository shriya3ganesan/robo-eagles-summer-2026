package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class Intake {
    private DcMotor intakeLeft;
    private DcMotor intakeRight;
    private Servo intakeServo;

    public void init(HardwareMap hwMap) {
        intakeLeft = hwMap.get(DcMotor.class, "intakeLeft");
        intakeRight = hwMap.get(DcMotor.class, "intakeRight");
        intakeServo = hwMap.get(Servo.class, "intakeServo");

        intakeLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeRight.setDirection(DcMotorSimple.Direction.REVERSE);

        intakeLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intakeRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intakeLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intakeRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intakeServo.setPosition(1.0);
    }

    public void intake() {
        intakeLeft.setPower(1.0);
        intakeRight.setPower(1.0);
    }

    public void outtake() {
        intakeLeft.setPower(-1.0);
        intakeRight.setPower(-1.0);
    }

    public void stop() {
        intakeLeft.setPower(0);
        intakeRight.setPower(0);
    }

    public void setPower(double power) {
        intakeLeft.setPower(power);
        intakeRight.setPower(power);
    }

    public void openServo() {
        intakeServo.setPosition(0.0);
    }

    public void closeServo() {
        intakeServo.setPosition(1.0);
    }


}