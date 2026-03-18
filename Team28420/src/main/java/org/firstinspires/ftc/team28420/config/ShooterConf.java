package org.firstinspires.ftc.team28420.config;

import org.opencv.core.Scalar;

public class ShooterConf {
    public static Scalar cslowGreen = new Scalar(155, 0.71, 0.019);
    public static Scalar cshighGreen = new Scalar(180, 1, 0.031);
    public static Scalar cslowPurple = new Scalar(130, 0.5, 0.006);
    public static Scalar cshighPurple = new Scalar(240, 0.6, 0.04);
    public static double SCANNED_BALL_MS = 70;
    public static String TARGET_MOTIF = null;
    public static double SORT_MOTOR_POWER = 1;
    public static double BALL_DETECTION_THRESHOLD = 4;
    public static int VELOCITY = 6767;
    public static int DRIBBLER_VELOCITY = 4000;
    public static double SORT_MOTOR_TICKS_PER_TURN = 2380.0;
}
