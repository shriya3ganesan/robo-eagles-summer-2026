package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

public class Intake {
    private final double STOP_SPEED = 0.0; //We send this power to the servos when we want them to stop.
    private final double FULL_SPEED = 1.0;
    private final double REVERSE_SPEED = -1.0;
    private DcMotorEx Intake;
    public void init(HardwareMap hwMap) {
            Intake = hwMap.get(DcMotorEx.class, "intake");

            // Set launcher motor to RUN_USING_ENCODER and BRAKE to slow down faster than coasting.
            Intake.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            Intake.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            Intake.setDirection(DcMotor.Direction.FORWARD);

            stopIntake();
    }

    public void stopIntake() {
                    Intake.setPower(STOP_SPEED);
    }

    public void startIntake() {
                    Intake.setPower(FULL_SPEED);
    }

    public void reverseIntake() { Intake.setPower(REVERSE_SPEED); }
}