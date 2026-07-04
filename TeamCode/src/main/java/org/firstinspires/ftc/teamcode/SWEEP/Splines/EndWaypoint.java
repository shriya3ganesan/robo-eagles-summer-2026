package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

public class EndWaypoint implements Waypoint {
    private Coordinate coordinate;
    private double speed;
    public EndWaypoint(double x, double y, double angle, double speed){
        this.coordinate = new Coordinate(x,y,angle);
        this.speed = speed;
    }
    public EndWaypoint(Coordinate coordinate, double speed){
        this.coordinate = coordinate;
        this.speed = speed;
    }
    @Override public double getX(){
        return coordinate.getX();
    }
    @Override public double getY(){
        return coordinate.getY();
    }
    @Override public Coordinate getCoordinate(){
        return coordinate;
    }
    @Override public double getAngle(){
        return coordinate.getAngle();
    }
    @Override public double getSpeed(){
        return speed;
    }
    @Override public WaypointType getType(){
        return WaypointType.END;
    }
}
