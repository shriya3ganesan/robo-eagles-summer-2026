package org.firstinspires.ftc.teamcode.SWEEP.Builder;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

import java.util.ArrayList;

public class PathBuilder {
    /**
     * Length of standard FTC field
     */
    private double fieldLength = 144; // inches
    /**
     * The array of all waypoints built into this path
     */
    private ArrayList<Waypoint> waypoints;
    public PathBuilder(){

    }
    public PathBuilder splineTo(double x, double y, double speed){
        speed = Math.min
        waypoints.add(new Waypoint(clipCoordinateToField(x),clipCoordinateToField(y),speedRatio));
        return this;
    }
    public PathBuilder splineTo(Waypoint definedWaypoint){
        waypoints.add(new Waypoint(definedWaypoint.getX(),definedWaypoint.getY(),definedWaypoint.getSpeed()));
        return this;
    }
    public PathBuilder splineTo(Waypoint definedWaypoint, double speed){
        waypoints.add(new Waypoint(definedWaypoint.getX(),definedWaypoint.getY(),speed));
        return this;
    }
    private double clipCoordinateToField(double coordinate){
        coordinate = Math.min(coordinate, fieldLength/2);
        coordinate = Math.max(coordinate, -fieldLength/2);
        return coordinate;
    }
}

