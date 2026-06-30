package org.firstinspires.ftc.teamcode.SWEEP.Builder;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
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
        speed = Math.min(speed, 1);
        speed = Math.max(speed,0);
        waypoints.add(new Waypoint(clipCoordinateToField(x),clipCoordinateToField(y),speed));
        return this;
    }
    public PathBuilder splineTo(Coordinate definedCoordinate, double speed){
        waypoints.add(new Waypoint(definedCoordinate.getX(),definedCoordinate.getY(),speed));
        return this;
    }

    public PathBuilder splineToAngle(double x, double y, double angle, double speed){
        waypoints.add(new Waypoint(clipCoordinateToField(x),clipCoordinateToField(y),angle,speed));
        return this;
    }
    public PathBuilder splineToAngle(Coordinate definedCoordinate, double speed){
        waypoints.add(new Waypoint(definedCoordinate.getX(),definedCoordinate.getY(),definedCoordinate.getAngle(),speed));
        return this;
    }
    public PathBuilder linearTo(double x, double y, double speed){
        waypoints.add(new Waypoint(clipCoordinateToField(x),clipCoordinateToField(y),speed,true));
        return this;
    }
    public PathBuilder linearTo(Coordinate definedCoordinate, double speed) {
        waypoints.add(new Waypoint(definedCoordinate.getX(), definedCoordinate.getY(), speed, true));
        return this;
    }
    public PathBuilder HookTo(double x, double y, double speed){
        waypoints.add(new Waypoint(clipCoordinateToField(x),clipCoordinateToField(y),speed,true,true));
        return this;
    }

    private double clipCoordinateToField(double coordinate){
        coordinate = Math.min(coordinate, fieldLength/2);
        coordinate = Math.max(coordinate, -fieldLength/2);
        return coordinate;
    }
    private double clipSpeedToRange(double speed){
        speed = Math.min(speed, 1);
        speed = Math.max(speed,0);
        return speed;
    }
}
//splineTo
//splineToAngle
//linearTo
//LinearToAngle

