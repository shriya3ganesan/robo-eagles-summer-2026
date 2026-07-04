package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;

public interface Segment {
    default public double getTotalTime(){
        return getEndTime() - getStartTime();
    }
    public double getStartTime();
    public double getEndTime();
    default public boolean activeAt(double time){
        return time >= getStartTime() && time < getEndTime();
    }
    public Coordinate getPosition(double overallTime);
    default public Coordinate getPositionIndependentTime(double independentTime){
        return getPosition(getStartTime()+independentTime);
    }
    public double[] getVelocity(double overallTime);
    default public double[] getVelocityIndependentTime(double independentTime){
        return getVelocity(getStartTime()+independentTime);
    }

    /**
     * Get the distance traveled along this segment between a
     * start and end time that is within the start and end time of the segment
     * @param tStart
     * @param tEnd
     * @return distance in inches along the curve
     */
    default double calculateDistance(double tStart, double tEnd){
        Coordinate start = getPosition(tStart);
        Coordinate end = getPosition(tEnd);
        return Coordinate.getDistanceBetweenCoordinates(start,end); // works for linear segments, override for complex methods
    }
}
