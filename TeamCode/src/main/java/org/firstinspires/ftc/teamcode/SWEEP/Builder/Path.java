package org.firstinspires.ftc.teamcode.SWEEP.Builder;

import org.firstinspires.ftc.teamcode.SWEEP.Classes.Coordinate;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.SWEEPAction;
import org.firstinspires.ftc.teamcode.SWEEP.Splines.Segment;

public class Path {
    private Segment[] segments;
    private SWEEPAction[] actions;

    public Path(Segment[] segments){
        if (segments == null) throw new IllegalArgumentException("null path given");
        this.segments = segments;
    }

    public Segment[] getSegments() {
        return segments;
    }
    public double getTotalTime(){
        return 0;
    }
    public Coordinate getPosition(double time){

    }
    public double[] getVelocity(double time){
        return new double[]{0,0};
    }
    public void start(){

    }
    public void start(double startingTime){

    }

}
