package org.firstinspires.ftc.teamcode.SWEEP.Builder;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.HookWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.LinearAngleWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.LinearWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.Segment;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.SplineAngleWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.SplineWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.WaitWaypoint;

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
    private Coordinate previousCoordinate = new Coordinate(0,0,0);
    public PathBuilder(){

    }


    public PathBuilder splineTo(double x, double y, double speed){
        speed = Math.min(speed, 1);
        speed = Math.max(speed,0);
        waypoints.add(new SplineWaypoint(clipCoordinateToField(x),clipCoordinateToField(y),speed));
        previousCoordinate = new Coordinate(x,y,0);
        return this;
    }
    public PathBuilder splineTo(Coordinate definedCoordinate, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new SplineWaypoint(definedCoordinate.getX(),definedCoordinate.getY(),speed));
        previousCoordinate = definedCoordinate;
        return this;
    }

    public PathBuilder splineToAngle(double x, double y, double angle, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new SplineAngleWaypoint(clipCoordinateToField(x),clipCoordinateToField(y),angle,speed));
        previousCoordinate = new Coordinate(x,y,0);
        return this;
    }
    public PathBuilder splineToAngle(Coordinate definedCoordinate, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new SplineAngleWaypoint(definedCoordinate.getX(),definedCoordinate.getY(),definedCoordinate.getAngle(),speed));
        previousCoordinate = definedCoordinate;
        return this;
    }
    public PathBuilder linearTo(double x, double y, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new LinearWaypoint(clipCoordinateToField(x),clipCoordinateToField(y),speed));
        previousCoordinate = new Coordinate(x,y,0);
        return this;
    }
    public PathBuilder linearTo(Coordinate definedCoordinate, double speed) {
        speed = clipSpeedToRange(speed);
        waypoints.add(new LinearWaypoint(definedCoordinate.getX(), definedCoordinate.getY(), speed));
        previousCoordinate = definedCoordinate;
        return this;
    }
    public PathBuilder HookTo(double x, double y, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new HookWaypoint(clipCoordinateToField(x),clipCoordinateToField(y),speed));
        previousCoordinate = new Coordinate(x,y,0);
        return this;
    }

    public PathBuilder HookTo(Coordinate coordinate, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new HookWaypoint(coordinate, speed));
        previousCoordinate = coordinate;
        return this;
    }
    public PathBuilder LinearAngleTo(double x, double y, double angle, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new LinearAngleWaypoint(clipCoordinateToField(x),clipCoordinateToField(y),angle,speed));
        previousCoordinate = new Coordinate(x,y,angle);
        return this;
    }
    public PathBuilder LinearAngleTo(Coordinate definedCoordinate, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new LinearAngleWaypoint(definedCoordinate, speed));
        previousCoordinate = definedCoordinate;
        return this;
    }
    public PathBuilder Wait(double duration){
        waypoints.add(new WaitWaypoint(previousCoordinate, duration));
        return this;
    }

    public Path build(){
        if (waypoints.size() < 2) return new S
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

