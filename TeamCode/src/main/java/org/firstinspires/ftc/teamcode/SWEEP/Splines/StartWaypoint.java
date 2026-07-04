package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

public class StartWaypoint implements Waypoint {
    /**
     * The starting position and heading of the robot at the beginning of the path.
     */

        private final Coordinate coordinate;

        /** Starting waypoint with a defined x, y position and heading angle. */
        public StartWaypoint(double x, double y, double angle) {
            this.coordinate = new Coordinate(x,y,angle);
        }
        public StartWaypoint(Coordinate coordinate){
            if (coordinate == null) throw new IllegalArgumentException("coordinate cannot be null");
            this.coordinate = coordinate;
        }
        @Override public Coordinate getCoordinate() { return coordinate; }
        @Override public double getX()          { return coordinate.getX(); }
        @Override public double getY()          { return coordinate.getY(); }
        @Override public double getAngle()      { return coordinate.getAngle(); }
        @Override public WaypointType getType() {
            return WaypointType.START;
        }
}
