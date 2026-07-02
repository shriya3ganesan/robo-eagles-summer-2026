package org.firstinspires.ftc.teamcode.SWEEP.Classes;

public interface Waypoint {
    enum WaypointType {
        SPLINE,
        SPLINE_ANGLE,
        LINEAR,
        LINEAR_ANGLE,
        HOOK,
        WAIT,
        START,
        END
    }
    double getX();
    double getY();
    double getAngle();
    double getSpeed();
    double getDuration();
    WaypointType getType();
}
