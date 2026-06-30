package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

/**
 * A curved spline waypoint where the robot holds a fixed heading throughout the segment.
 */
public class SplineAngleWaypoint implements Waypoint {

    /**
     * Target movement speed scalar for this waypoint.
     */
    private final double speed;

    /**
     * Target position and heading for this waypoint (inches, inches, radians).
     */
    private final Coordinate coordinate;

    /**
     * Creates a spline waypoint with a fixed heading.
     *
     * @param x target x position in inches
     * @param y target y position in inches
     * @param angle heading to hold in radians
     * @param speed speed scalar to use while traversing this segment
     */
    public SplineAngleWaypoint(double x, double y, double angle, double speed) {
        this.coordinate = new Coordinate(x, y, angle);
        this.speed = speed;
    }

    /**
     * @return target x position in inches
     */
    @Override
    public double getX() {
        return coordinate.getX();
    }

    /**
     * @return target y position in inches
     */
    @Override
    public double getY() {
        return coordinate.getY();
    }

    /**
     * @return fixed heading for this spline segment in radians
     */
    @Override
    public double getAngle() {
        return coordinate.getAngle();
    }

    /**
     * @return configured speed scalar for this waypoint
     */
    @Override
    public double getSpeed() {
        return speed;
    }

    /**
     * @return duration in seconds; spline waypoints are not timed waits so this is always zero
     */
    @Override
    public double getDuration() {
        return 0;
    }

    /**
     * @return waypoint classification used by path generation dispatch
     */
    @Override
    public WaypointType getType() {
        return WaypointType.SPLINE_ANGLE;
    }
}

