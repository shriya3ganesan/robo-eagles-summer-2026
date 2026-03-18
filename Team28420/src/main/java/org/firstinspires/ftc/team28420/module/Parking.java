package org.firstinspires.ftc.team28420.module;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.team28420.config.ParkingConf;

public class Parking {
    private final Servo parkingServo1;
    private final Servo parkingServo2;

    public Parking(HardwareMap hMap) {
        this.parkingServo1 = hMap.get(Servo.class, ParkingConf.PARKING_SERVO_1.getName());
        this.parkingServo2 = hMap.get(Servo.class, ParkingConf.PARKING_SERVO_2.getName());
    }

    public void setup() {
        parkingServo1.setPosition(ParkingConf.PARKING_SERVO_1.getStartPos());
        parkingServo2.setPosition(ParkingConf.PARKING_SERVO_2.getStartPos());
    }

    public void park() {
        parkingServo1.setPosition(ParkingConf.PARKING_SERVO_1.getStopPos());
        parkingServo2.setPosition(ParkingConf.PARKING_SERVO_2.getStopPos());
    }

}
