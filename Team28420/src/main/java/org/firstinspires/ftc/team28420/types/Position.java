package org.firstinspires.ftc.team28420.types;

import androidx.annotation.NonNull;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;

public class Position {
    private final double x, y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Position(Pose2D pose) {
        this.x = pose.getX(DistanceUnit.CM);
        this.y = pose.getY(DistanceUnit.CM);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getAngleTo(Position target) {
        return Math.atan2(target.y - y, target.x - x);
    }

    public PolarVector getVectorTo(Position target) {
        return new PolarVector(target.x - x, target.y - y, 1000);
    }

    public boolean isValidForShoot() {
        return (y + 6.096) / Math.abs(x) >= 1 || (-y - 115.824) / Math.abs(x) >= 1;
    }

    public PolarVector getVectorToNearestValidShootPoint() {
        Position nearest = getNearestValidShootPoint();
        return getVectorTo(nearest);
    }

    private Position getNearestValidShootPoint() {
        if (isValidForShoot()) return this;

        Position[] candidates = new Position[] {
                projectToLine(x, y, -1, 1,   6.096),
                projectToLine(x, y,  1, 1,   6.096),
                projectToLine(x, y,  1, 1, 115.824),
                projectToLine(x, y, -1, 1, 115.824)
        };

        Position best = candidates[0];
        double bestDist2 = dist2(best);

        for (int i = 1; i < candidates.length; i++) {
            double d2 = dist2(candidates[i]);
            if (d2 < bestDist2) {
                bestDist2 = d2;
                best = candidates[i];
            }
        }
        return best;
    }

    private Position projectToLine(double px, double py, double a, double b, double c) {
        double t = (a * px + b * py + c) / (a * a + b * b);
        return new Position(px - a * t, py - b * t);
    }

    private double dist2(Position p) {
        double dx = p.x - x;
        double dy = p.y - y;
        return dx * dx + dy * dy;
    }

    @Override
    @NonNull
    public String toString() {
        return "(Position) x = " + x + ", y = " + y;
    }
}
