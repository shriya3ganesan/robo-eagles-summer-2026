package org.firstinspires.ftc.team28420.util;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.opencv.core.Scalar;

public final class Config {
    public final static class GamepadConf {
        public final static double LEFT_DEAD_ZONE = 0.15;
        public final static double RIGHT_DEAD_ZONE = 0.15;
    }

    public final static class WheelBaseConf {
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
        public final static double ACCELERATION = 100.0;
    }

    public final static class GyroConf {
        public final static String IMU = "imu";
        public final static RevHubOrientationOnRobot.LogoFacingDirection logoFacingDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        public final static RevHubOrientationOnRobot.UsbFacingDirection usbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
    }

    public static final class CameraConf {
        public static final String WEBCAM = "Webcam 1";
        public static final double RANGE_TO_TAG = 60; // inches
        public static final double ANGLE_MAX_VELOCITY = Math.PI;

    }

    public static final class ServoConf {
        public static final String PARKING_SERVO = "parkingServo";
        public static final double PARKING_SERVO_START_POS = 1;
        public static final double PARKING_SERVO_STOP_POS = 0.8;
    }

    public static final class Etc {
        public static Telemetry telemetry;
        public static ElapsedTime teleopTime;
    }

    public static class BallDetectionConf {
        // in hsv
        public static Scalar lowGreen = new Scalar(100, 150, 50);
        public static Scalar highGreen = new Scalar(130, 255, 255);
        public static Scalar lowPurple = new Scalar(130, 40, 40);
        public static Scalar highPurple = new Scalar(160, 255, 255);

        public static Scalar cslowGreen = new Scalar(100, 150, 50);
        public static Scalar cshighGreen = new Scalar(130, 255, 255);
        public static Scalar cslowPurple = new Scalar(130, 0.5, 0.006);
        public static Scalar cshighPurple = new Scalar(240, 0.6, 0.01);
        public static double MIN_CIRCULARITY = 0.5; // насколько объект должен быть круглым (0.0 - 1.0)
        public static double MIN_AREA = 60000; // минимальный размер объекта
        public static double MAX_AREA = 120000;
        public static double kP = 0.01;
    }

    public static class ShooterConf {
        public static String TARGET_MOTIF = "PGP";
        public static double SORT_MOTOR_POWER = 1;
        public static double BALL_DETECTION_THRESHOLD = 4;
        public static int VELOCITY = 7000;
        public static int DRIBBLER_VELOCITY = 2000;
        public static double SORT_MOTOR_TICKS_PER_TURN = 2380.0;
    }

}
