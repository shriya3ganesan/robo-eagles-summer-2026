package org.firstinspires.ftc.teamcode.SWEEP.Classes;

public interface Waypoint {
    public enum WaypointType {
        SPLINE,
        SPLINE_ANGLE,
        LINEAR,
        LINEAR_ANGLE,
        HOOK,
        WAIT,
        START,
        END
    }
    public double getX();
    public double getY();
    public double getAngle();
    default public double getSpeed(){
        return 0; // If waypoint does not need speed, don't bother overriding it!
    }
    default public double getDuration(){
        return 0; // Segment with no waiting at a single point
    }
    public WaypointType getType();
}
