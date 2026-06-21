package org.firstinspires.ftc.teamcode.mechanism;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class InitDrive {
    public DcMotor lfMotor;
    public DcMotor lbMotor;
    public DcMotor rfMotor;
    public DcMotor rbMotor;

    public void init(HardwareMap hwMap) {
        // DC motor
        lfMotor = hwMap.get(DcMotor.class, "leftFrontMotor");
        lbMotor = hwMap.get(DcMotor.class, "leftBackMotor");
        rfMotor = hwMap.get(DcMotor.class, "rightFrontMotor");
        rbMotor = hwMap.get(DcMotor.class, "rightBackMotor");
        lfMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lbMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rfMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rbMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lfMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lbMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rfMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rbMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lfMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        lbMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rfMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        rbMotor.setDirection(DcMotorSimple.Direction.FORWARD);
    }
}
