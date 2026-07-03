package org.firstinspires.ftc.teamcode.SWEEP.Builder;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.SWEEPAction;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.Segment;

public class Path {
    private Segment[] segments;
    private SWEEPAction[] actions;
    private int currentSegmentIndex = 0;


    public Path(Segment[] segments){
        if (segments == null) throw new IllegalArgumentException("null path given");
        this.segments = segments;
    }
    public double getTotalTime(){
        return getEndTime() - getStartTime();
    }
    public double getEndTime(){
        return segments[segments.length-1].getEndTime();
    }
    public double getStartTime(){
        return segments[0].getStartTime();
    }
    public Coordinate getPosition(double time){
        return getCurrentSegment(time).getPosition(time);
    }
    public double[] getVelocity(double time){
        return new double[]{0,0};
    }
    public void start(){
        currentSegmentIndex = 0;
    }
    public void start(double startingTime){
        if (startingTime < 0) throw new IllegalArgumentException("Provided Time cannot be negative");
        currentSegmentIndex = getSegmentIndexAt(startingTime);
    }
    private Segment getCurrentSegment(double time){
        return segments[getSegmentIndexAt(time)];
    }
    private int getSegmentIndexAt(double time){
        if (segments.length == 0) throw new IllegalArgumentException("EmptyPath");
        if (time < 0) throw new IllegalArgumentException("Provided Time cannot be negative");

        if (time < getStartTime() || time > getEndTime()) return -1; // Time outside of path definition

        //Common case that the segment we were last on is the same one
        if (segments[currentSegmentIndex].activeAt(time)) return currentSegmentIndex;
        // Other normal case that the path just moved forward onto the next segment
        if (segments.length > currentSegmentIndex+1){
            if (segments[currentSegmentIndex+1].activeAt(time)) return currentSegmentIndex+1;
        }
        // If neither of these are true, we will then run a linear search for the target segment
        for (int i = 0; i < segments.length; i++){
            if(segments[i].activeAt(time)) return i;
        }

        // if that fails then we have a big problem
        throw new RuntimeException("No segment could be solved for time: Library Bug");
    }
}
