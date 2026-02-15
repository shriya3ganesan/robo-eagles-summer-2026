package org.nknsd.teamcode.components.utility;

public class SensorGridPoint {
    final double x;
    final double y;
    public SensorGridPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof SensorGridPoint)) {
            return false;
        }

        return ((SensorGridPoint) o).getX() == x && ((SensorGridPoint) o).getY() == y;
    }
}
