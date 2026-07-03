package org.firstinspires.ftc.teamcode.SWEEP.Splines.Segments;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.Segment;

public class WaitSegment implements Segment {
    private double startTime, endTime;
    private final Coordinate position;
    public WaitSegment(Coordinate position, double startTime, double duration){
        this.position = position;
        this.startTime = startTime;
        this.endTime = startTime+duration;
    }
    @Override
    public double getStartTime(){
        return startTime;
    }
    @Override
    public double getEndTime(){
        return endTime;
    }
    @Override
    public boolean activeAt(double time){
        //inclusive of starting position, exclusive of end
        return startTime <= time && time < endTime;
    }
    @Override
    public Coordinate getPosition(double overallTime){
        return position;
    }
    @Override
    public double[] getVelocity(double overallTime){
        return new double[]{0,0};
    }
}
