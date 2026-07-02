package org.firstinspires.ftc.teamcode.SWEEP.Splines;

public interface Segment {
    public double getTotalTime();
    public double getPositionOverallTime(double overallTime);
    public double getPositionIndependentTime(double independentTime);
    public double getVelocityOverallTime(double overallTime);
    public double getVelocityIndependentTime(double independentTime);
    double calculateDistance(double tStart, double tEnd);

}
