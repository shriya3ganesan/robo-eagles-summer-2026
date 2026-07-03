package org.firstinspires.ftc.teamcode.SWEEP.Splines;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Waypoint;

public class StartWaypoint implements Waypoint {
    /**
     * A hook-style movement waypoint. The robot approaches this point with a hooked
     * (curved overshoot-and-snap) trajectory while holding a fixed heading.
    */

        private final Coordinate coordinate;
        private final double speed;

        /** Hook waypoint with no fixed heading (defaults to 0). */
        public StartWaypoint(double x, double y, double speed) {
            this.coordinate = new Coordinate(x,y,0);
            this.speed = speed;
        }
        public StartWaypoint(Coordinate coordinate, double speed){
            if (coordinate == null) throw new IllegalArgumentException("coordinate cannot be null");
            this.coordinate = coordinate;
            this.speed = speed;
        }
        @Override public double getX()          { return coordinate.getX(); }
        @Override public double getY()          { return coordinate.getY(); }
        @Override public double getAngle()      { return coordinate.getAngle(); }
        @Override public double getSpeed()      { return speed; }
        @Override public WaypointType getType() {
            return WaypointType.START;
        }
}
