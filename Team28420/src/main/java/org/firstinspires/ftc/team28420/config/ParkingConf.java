package org.firstinspires.ftc.team28420.config;

public enum ParkingConf {
    PARKING_SERVO_1("parking_servo_1", 0.0, 1.0),
    PARKING_SERVO_2("parking_servo_2", 0.0, 1.0);

    private final String name;
    private final double startPos;
    private final double stopPos;

    ParkingConf(String name, double startPos, double stopPos) {
        this.name = name;
        this.startPos = startPos;
        this.stopPos = stopPos;
    }

    public String getName() {
        return name;
    }

    public double getStartPos() {
        return startPos;
    }

    public double getStopPos() {
        return stopPos;
    }
}
