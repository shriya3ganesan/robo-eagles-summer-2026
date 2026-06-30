package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

/**
 * A straight-line movement waypoint. The robot drives in a straight line to this point
 * while holding a fixed heading.
 */
public class LinearWaypoint implements Waypoint {

    private final Coordinate coordinate;
    private final double speed;

    /** Linear waypoint with no fixed heading (defaults to 0). */
    public LinearWaypoint(double x, double y, double speed) {
        this.coordinate = new Coordinate(x,y,0);
        this.speed = speed;
    }
    public LinearWaypoint(Coordinate coordinate, double speed){
        this.coordinate = coordinate;
        this.speed = speed;
    }
    @Override public double getX()          { return coordinate.getX(); }
    @Override public double getY()          { return coordinate.getY(); }
    @Override public double getAngle()      { return coordinate.getAngle(); }
    @Override public double getSpeed()      { return speed; }
    @Override public double getDuration()   { return 0; }
    @Override public WaypointType getType() {
        return WaypointType.LINEAR;
    }
}

