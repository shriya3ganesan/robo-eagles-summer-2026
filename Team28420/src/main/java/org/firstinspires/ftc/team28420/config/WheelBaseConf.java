package org.firstinspires.ftc.team28420.config;

public class WheelBaseConf {
    public static String LEFT_TOP_MOTOR = "LTMotor";
    public static String RIGHT_TOP_MOTOR = "RTMotor";
    public static String LEFT_BOTTOM_MOTOR = "LBMotor";
    public static String RIGHT_BOTTOM_MOTOR = "RBMotor";
    public final static int VELOCITY_COEFFICIENT = 3600;
    public final static double WHEEL_DIAMETER = 10.0;
    public final static double WHEEL_DIAMETER_INCHES = 4.094;
    public final static double TICKS_PER_REV = 560.0;
    public final static double TICKS_PER_INCH = TICKS_PER_REV / (WHEEL_DIAMETER_INCHES * Math.PI);
    public final static int MAX_VELOCITY = 3600;
    public final static double ACCELERATION = 250.0;
}
