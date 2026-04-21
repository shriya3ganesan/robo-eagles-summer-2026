package org.firstinspires.ftc.team28420.types;

public class PolarVector {

    private final static double TWO_PI = 2 * Math.PI;
    private double theta;
    private final double abs;

    public PolarVector(double theta, double abs) {
        this.theta = theta;
        this.abs = abs;
    }

    public PolarVector(float x, float y) {
        this.theta = Math.atan2(y, x);
        this.abs = Math.hypot(x, y);
    }

    public PolarVector(double x, double y, double absK) {
        this.theta = Math.atan2(y, x);
        this.abs = Math.min(Math.hypot(x, y) / absK, 1);
    }

    public double getTheta() {
        return theta;
    }

    public double getAbs() {
        return abs;
    }

    public PolarVector rotate(double angle) {
        theta += angle + TWO_PI;
        theta %= TWO_PI;
        return this;
    }
}
