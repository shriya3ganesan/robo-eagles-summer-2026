package org.firstinspires.ftc.teamcode.SWEEP.Classes;

public class Coordinate {
    private double x, y, angle;
    public Coordinate(double x, double y, double angle){
        this.x = x;
        this.y = y;
        this.angle = angle;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getAngle() {
        return angle;
    }
}
