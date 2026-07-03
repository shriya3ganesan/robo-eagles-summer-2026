package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;

public interface Segment {
    public double getTotalTime();
    public Coordinate getPositionOverallTime(double overallTime);
    public Coordinate getPositionIndependentTime(double independentTime);
    public double[] getVelocityOverallTime(double overallTime);
    public double[] getVelocityIndependentTime(double independentTime);
    double calculateDistance(double tStart, double tEnd);

}
