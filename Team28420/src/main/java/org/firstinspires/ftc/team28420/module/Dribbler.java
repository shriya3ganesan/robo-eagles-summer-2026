package org.firstinspires.ftc.team28420.module;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.team28420.config.ShooterConf;

public class Dribbler {
    private final DcMotorEx dribblerMotor;

    public Dribbler(HardwareMap hMap) {
        dribblerMotor = hMap.get(DcMotorEx.class, "dribbler");
    }

    public void setVelocityCoefficient(float k) {
        dribblerMotor.setVelocity(ShooterConf.DRIBBLER_VELOCITY * k);
    }
}
