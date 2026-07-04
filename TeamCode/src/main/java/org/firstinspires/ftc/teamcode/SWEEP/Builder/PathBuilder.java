package org.firstinspires.ftc.teamcode.SWEEP.Builder;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.SWEEPAction;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.EndWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.BreakWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.LinearAngleWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.LinearWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.Segment;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.Segments.WaitSegment;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.SplineAngleWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.SplineWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.StartWaypoint;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.WaitWaypoint;

import java.util.ArrayList;

public class PathBuilder {
    /**
     * Length of standard FTC field
     */
    private final double fieldLength = 144; // inches
    /**
     * The array of all waypoints built into this path
     */
    private ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
    private ArrayList<SWEEPAction> actions = new ArrayList<SWEEPAction>();
    private Coordinate previousCoordinate = new Coordinate(0,0,0);
    public PathBuilder(){

    }
    public PathBuilder addAction(SWEEPAction actionClass){
        if (actionClass == null) throw new NullPointerException("Action cannot be null");
        if (!actionClass.isPositionSet()) actionClass.setPosition(previousCoordinate);
        actions.add(actionClass);
        return this;
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

    public PathBuilder addBreak(){
        waypoints.add(new BreakWaypoint(previousCoordinate));
        return this;
    }
    public PathBuilder linearToAngle(double x, double y, double angle, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new LinearAngleWaypoint(clipCoordinateToField(x),clipCoordinateToField(y),angle,speed));
        previousCoordinate = new Coordinate(x,y,angle);
        return this;
    }
    public PathBuilder linearToAngle(Coordinate definedCoordinate, double speed){
        speed = clipSpeedToRange(speed);
        waypoints.add(new LinearAngleWaypoint(definedCoordinate, speed));
        previousCoordinate = definedCoordinate;
        return this;
    }
    public PathBuilder waitAt(double duration){
        waypoints.add(new WaitWaypoint(previousCoordinate, duration));
        return this;
    }
    public PathBuilder start(double x, double y, double angle){
        waypoints.add(new StartWaypoint(x,y,angle));
        previousCoordinate = new Coordinate(x,y,angle);
        return this;
    }
    public PathBuilder start(Coordinate coordinate){
        waypoints.add(new StartWaypoint(coordinate));
        previousCoordinate = coordinate;
        return this;
    }
    public PathBuilder end(double x, double y, double angle, double speed){
        waypoints.add(new EndWaypoint(x,y,angle,speed));
        previousCoordinate = new Coordinate(x,y,angle);
        return this;
    }
    public PathBuilder end(Coordinate coordinate, double speed){
        waypoints.add(new EndWaypoint(coordinate, speed));
        previousCoordinate = coordinate;
        return this;
    }
    public Path build(){
        return build(0);
    }
    public Path build(double time) {
        if (waypoints == null)
            throw new NullPointerException("Empty waypoints");
        if (waypoints.size() < 2)
            throw new RuntimeException("Cannot create path, must have at least two waypoints");
        if (waypoints.get(0).getType() != Waypoint.WaypointType.START)
            throw new RuntimeException("First Waypoint must be type START");
        if (waypoints.get(waypoints.size() - 1).getType() != Waypoint.WaypointType.END)
            throw new RuntimeException("Last Waypoint must be type END");

        ArrayList<Segment> segments = new ArrayList<Segment>();

        for (int i = 1; i < waypoints.size(); i++){
            Segment segment = createNewSegment(i, time);
            time += segment.getTotalTime();
            segments.add(segment);
        }
        return new Path(segments.toArray(new Segment[0]));
    }
    private Segment createNewSegment(int waypointIndex,double time){
        if (waypointIndex < 1 || waypoints.get(waypointIndex).getType() == Waypoint.WaypointType.START) throw new IllegalArgumentException("Cannot create segment on type START");
        Waypoint waypoint = getWaypointInRange(waypointIndex);
        switch (waypoint.getType()){
            case END:
                //TODO
                break;
            case WAIT:
                return new WaitSegment(waypoint.getCoordinate(),time, waypoint.getDuration());
            case BREAK:
                //TODO
                break;
            case SPLINE:
                //TODO
                break;
            case SPLINE_ANGLE:
                //TODO
                break;
            case LINEAR:
                //TODO
                break;
            default: // Also case for LinearAngle
                break;
        }
        throw new RuntimeException("Unable to create a new segment at waypoint IDX: " + waypointIndex);
    }
    private Waypoint getWaypointInRange(int waypointIndex){
        waypointIndex = Math.max(waypointIndex,0);
        waypointIndex = Math.min(waypointIndex,waypoints.size()-1);
        return waypoints.get(waypointIndex);
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

