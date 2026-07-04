package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

/**
 * A hold/wait waypoint. The robot stops at this position and holds its heading
 * for the specified duration before continuing the path.
 */
public class WaitWaypoint implements Waypoint {

    private final Coordinate coordinate;
    private final double duration;

    /**
     * @param x        hold position x (inches)
     * @param y        hold position y (inches)
     * @param angle    heading to hold (degrees)
     * @param duration time to hold at this point (seconds)
     */
    public WaitWaypoint(double x, double y, double angle, double duration) {
        this(new Coordinate(x, y, angle), duration);
    }

    /**
     * @param coordinate hold position and heading
     * @param duration   time to hold at this point (seconds)
     */
    public WaitWaypoint(Coordinate coordinate, double duration) {
        if (coordinate == null) throw new IllegalArgumentException("coordinate cannot be null");

        this.coordinate = coordinate;
        this.duration = duration;
    }

    @Override public Coordinate getCoordinate() { return coordinate; }
    @Override public double getX()          { return coordinate.getX(); }
    @Override public double getY()          { return coordinate.getY(); }
    @Override public double getAngle()      { return coordinate.getAngle(); }
    @Override public double getDuration()   { return duration; }
    @Override public WaypointType getType() { return WaypointType.WAIT; }
}

