package org.firstinspires.ftc.teamcode.drive;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class M2 extends OpMode {

    private DcMotor backLeft;
    private DcMotor backRight;


    @Override
    public void init() {
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        

        backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        double y = gamepad2.left_stick_y;
        double x = gamepad2.left_stick_x;

        double backLeftPower = y + x;
        double backRightPower = y - x;


        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        
        telemetry.addData("Status", "Running");
        telemetry.addData("Left Stick X", x);
        telemetry.addData("Left Stick Y", y);
    }
}
