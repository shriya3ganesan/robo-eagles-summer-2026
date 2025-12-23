package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class LEDIndicator {
    private Servo turretServo;

    private final double LEDStartValue = 0.0; // nominally 0 degrees, may need to be tuned based on mounting angle of servo
    private double currentLEDValue;

    public void init (HardwareMap hwMap) {
        turretServo = hwMap.get(Servo.class,"LEDIndicator");
        currentLEDValue = LEDStartValue;
    }

    public void incrementLEDValue() {
        currentLEDValue = currentLEDValue + .1;
        turretServo.setPosition(currentLEDValue);
    }

    public void decrementLEDValue() {
        currentLEDValue = currentLEDValue - .1;
        turretServo.setPosition(currentLEDValue);
    }

    public double getLEDValue(){
        return currentLEDValue;
    }

}
