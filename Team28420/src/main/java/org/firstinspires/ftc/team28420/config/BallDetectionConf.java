package org.firstinspires.ftc.team28420.config;

import org.opencv.core.Scalar;

public class BallDetectionConf {
    public static Scalar lowGreen = new Scalar(100, 150, 50);
    public static Scalar highGreen = new Scalar(130, 255, 255);
    public static Scalar lowPurple = new Scalar(130, 40, 40);
    public static Scalar highPurple = new Scalar(160, 255, 255);

    public static double MIN_CIRCULARITY = 0.5; // насколько объект должен быть круглым (0.0 - 1.0)
    public static double MIN_AREA = 60000; // минимальный размер объекта
    public static double MAX_AREA = 120000;
    public static double kP = 0.01;
}
