package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

/**
 * A curved spline waypoint where the robot's heading follows the direction of travel.
 */
public class SplineWaypoint implements Waypoint{
    private final double speed;
    private final Coordinate coordinate;

    /** Spline waypoint — heading is derived from path direction, no fixed angle. */
    public SplineWaypoint(double x, double y, double speed){
        this.coordinate = new Coordinate(x,y,0);
        this.speed = speed;
    }
    public SplineWaypoint(Coordinate coordinate, double speed){
        if (coordinate == null) throw new IllegalArgumentException("coordinate cannot be null");
        this.coordinate = coordinate;
        this.speed = speed;
    }
    @Override public double getX()          {
        return this.coordinate.getX();
    }
    @Override public double getY()          {
        return this.coordinate.getY();
    }
    @Override public double getAngle()      {
        return this.coordinate.getAngle();
    }
    @Override public double getSpeed(){
        return this.speed;
    }
    @Override public WaypointType getType(){
        return WaypointType.SPLINE;
    }
}

