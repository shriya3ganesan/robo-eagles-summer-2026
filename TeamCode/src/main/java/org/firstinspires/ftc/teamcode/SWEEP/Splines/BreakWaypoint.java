package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

/**
 * A break waypoint. Signals the end of the current spline and the start of a new one,
 * discarding accumulated spline tangent history at this position.
 */
public class BreakWaypoint implements Waypoint {
    private final Coordinate coordinate;
    public BreakWaypoint(Coordinate coordinate){
        if (coordinate == null) throw new IllegalArgumentException("coordinate cannot be null");
        this.coordinate = coordinate;
    }
    @Override public Coordinate getCoordinate() { return coordinate; }
    @Override public double getX()          { return coordinate.getX(); }
    @Override public double getY()          { return coordinate.getY(); }
    @Override public double getAngle()      { return coordinate.getAngle(); }
    @Override public double getSpeed()      { return 0; }
    @Override public WaypointType getType() {
        return WaypointType.BREAK;
    }
}

