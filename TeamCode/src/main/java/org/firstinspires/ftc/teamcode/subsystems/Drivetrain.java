package org.firstinspires.ftc.teamcode.subsystems;


import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

@Config

public class Drivetrain {
    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor backLeft;
    DcMotor backRight;

    public Drivetrain(HardwareMap hardwareMap){
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");

        backLeft.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
    }
    public void driveCA(double right_stick_x, double right_stick_y, double left_stick_x, double left_trigger, double right_trigger){
        double rtx = right_stick_x;
        double rty = right_stick_y;
        double ltx = left_stick_x * 1.1;

        double powerfactor = 1;
        double rotate = -ltx;
        double x = -rtx;
        double y = -rty;
        double fleft = (x + y - rotate) * powerfactor;
        double fright = (-x + y + rotate) * powerfactor;
        double bleft = (-x + y - rotate) * powerfactor;
        double bright = (x + y + rotate) * powerfactor;
        frontLeft.setPower(fleft);
        frontRight.setPower(fright);
        backLeft.setPower(bleft);
        backRight.setPower(bright);
    }

    public void driveIG(double right_stick_x, double right_stick_y, double left_stick_x, double left_trigger, double right_trigger){
        double rtx = right_stick_x;
        double rty = right_stick_y;
        double ltx = left_stick_x * 1.1;

        double powerfactor = 1;
        double rotate = -ltx;
        double x = -rtx;
        double y = -rty;
        double fleft = (x + y - rotate) * powerfactor;
        double fright = (-x + y + rotate) * powerfactor;
        double bleft = (-x + y - rotate) * powerfactor;
        double bright = (x + y + rotate) * powerfactor;

        if (left_trigger > 0.25) {
            frontLeft.setPower(-0.75);
            frontRight.setPower(0.75);
            backLeft.setPower(-0.75);
            backRight.setPower(0.75);
        } else if (right_trigger > 0.25) {
            frontLeft.setPower(0.75);
            frontRight.setPower(-0.75);
            backLeft.setPower(0.75);
            backRight.setPower(-0.75);
        } else {
            frontLeft.setPower(fleft);
            frontRight.setPower(fright);
            backLeft.setPower(bleft);
            backRight.setPower(bright);
        }
    }
}
