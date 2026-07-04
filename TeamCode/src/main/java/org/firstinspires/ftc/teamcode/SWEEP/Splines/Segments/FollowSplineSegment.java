package org.firstinspires.ftc.teamcode.SWEEP.Splines.Segments;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.Segment;

public class FollowSplineSegment implements Segment {
    double startTime;
    double endTime;
    public FollowSplineSegment(double startTime) {
    }
    @Override
    public double getStartTime() {
        return startTime;
    }
    @Override
    public double getEndTime() {
        return endTime;
    }
    @Override
    public Coordinate getPosition(double overallTime) {
        return null;
    }
    @Override
    public double[] getVelocity(double overallTime) {
        return new double[0];
    }
}
