package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

/**
 * A straight-line movement waypoint. The robot drives in a straight line to this point
 * while holding a fixed heading.
 */
public class LinearAngleWaypoint implements Waypoint {

    private final Coordinate coordinate;
    private final double speed;

    /** Linear waypoint with no fixed heading (defaults to 0). */
    public LinearAngleWaypoint(double x, double y, double angle, double speed) {
        this.coordinate = new Coordinate(x,y,angle);
        this.speed = speed;
    }
    public LinearAngleWaypoint(Coordinate coordinate, double speed){
        if (coordinate == null) throw new IllegalArgumentException("coordinate cannot be null");
        this.coordinate = coordinate;
        this.speed = speed;
    }
    @Override public Coordinate getCoordinate() { return coordinate; }
    @Override public double getX(){
        return coordinate.getX(); }
    @Override public double getY(){
        return coordinate.getY(); }
    @Override public double getAngle(){
        return coordinate.getAngle(); }
    @Override public double getSpeed(){
        return speed; }
    @Override public WaypointType getType() {
        return WaypointType.LINEAR_ANGLE;
    }
}

