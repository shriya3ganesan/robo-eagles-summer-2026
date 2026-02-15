package org.nknsd.teamcode.components.utility;

import org.nknsd.teamcode.components.handlers.vision.ID;
import org.nknsd.teamcode.components.utility.feedbackcontroller.PidController;

public class RobotVersion {


//    comment out the unwanted version, top is new robot and bottom is old robot

    public final static RobotVersion INSTANCE= new RobotVersion(50,.038, false, 60,.5,1,
        new Interpolater(new double[][]{{16, 1600}, {24, 1700}, {36, 1800}, {48, 1900}, {60, 2000}, {72, 2150}, {84, 2250}, {96, 2350}, {108, 2400}, {130, 2400}}),
        new Interpolater(new double[][]{{16,1}, {32,.8}, {48,.7}, {64,0.6}, {80,0.5}, {96,0.3}, {112,0.25}, {132,0.22}}),
        new Interpolater(new double[][]{{0.265,12}, {0.2,24}, {0.16,36}, {0.132,48}, {0.11,60}, {0.098,72}, {0.085,84}, {0.077,96}, {0.068,108}, {0.064,120}, {0.058,132}, {0.054,144},{0.05,156}}),
        new PidController(0.1, .2, 0.15, .15, true, 0.01, 0.15), // for the SRS
        new PidController(0.15, .3, 0.1, .15, true, 0.02, 0.2), // x
        new PidController(0.15, .3, 0.1, .15, true, 0.02, 0.2), // y
        new PidController(0.6, .5, 0.1, .25, true, 0.2, 0.3)); // h

//    public final static RobotVersion INSTANCE= new RobotVersion(50,0,true,60,.5,1,
//            new Interpolater(new double[][]{{16,1370}, {32,1440}, {48,1500}, {64,1550}, {80,1620}, {96, 1720}, {112,1900}, {132, 1940}}),
//            new Interpolater(new double[][]{{16,1}, {32,.8}, {48,.7}, {64,0.6}, {80,0.5}, {96,0.3}, {112,0.25}, {132,0.22}}),
//            new Interpolater(new double[][]{{0.265,12}, {0.2,24}, {0.16,36}, {0.132,48}, {0.11,60}, {0.098,72}, {0.085,84}, {0.077,96}, {0.068,108}, {0.064,120}, {0.058,132}, {0.054,144},{0.05,156}}),
//            new PidController(1.2,0.6,1,0.4,true,0,0),
//            new PidController(0.15, .3, 0.1, .15, true, 0.02, 0.2),
//            new PidController(0.15, .3, 0.1, .15, true, 0.02, 0.2),
//            new PidController(0.6, .5, 0.1, .25, true, 0.2, 0.3));



    private static boolean autoMode = false;

    public static void setIsAutonomous(boolean isAutonomous) {
        RobotVersion.autoMode = isAutonomous;
    }


    public static boolean isAutonomous() {
        return autoMode;
    }

    private static ID robotAlliance = ID.NONE;

    public static void setRobotAlliance(ID alliance) {
        RobotVersion.robotAlliance = alliance;
    }

    public static ID getRobotAlliance() {
        return robotAlliance;
    }


    public final double microwaveOffset;
    public final boolean oldVoltagePositions;
    public final double distSensorThreshold;

    public final double scoopRestPos;
    public final double scoopLaunchPos;

    public final double visionLoopIntervalMS;

    public final Interpolater launchSpeedInterpolater;
    public final Interpolater launchAngleInterpolater;
    public final Interpolater aprilDistanceInterpolater;
  
    public final PidController ballEatingPidXY;

    public final PidController pidControllerX;
    public final PidController pidControllerY;
    public final PidController pidControllerH;



    private RobotVersion(double visionLoopIntervalMS, double microwaveOffset, boolean oldVoltagePositions, double distSensorThreshold, double scoopRestPos, double scoopLaunchPos, Interpolater launchSpeedInterpolater, Interpolater launchAngleInterpolater, Interpolater aprilHeightInterpolater, PidController ballEatingPidXY, PidController pidControllerX, PidController pidControllerY, PidController pidControllerH){

        this.visionLoopIntervalMS = visionLoopIntervalMS;
        this.microwaveOffset = microwaveOffset;
        this.oldVoltagePositions = oldVoltagePositions;
        this.distSensorThreshold = distSensorThreshold;
        this.scoopRestPos = scoopRestPos;
        this.scoopLaunchPos = scoopLaunchPos;
        this.launchSpeedInterpolater = launchSpeedInterpolater;
        this.launchAngleInterpolater = launchAngleInterpolater;
        this.aprilDistanceInterpolater = aprilHeightInterpolater;
        this.ballEatingPidXY = ballEatingPidXY;
        this.pidControllerX = pidControllerX;
        this.pidControllerY = pidControllerY;
        this.pidControllerH = pidControllerH;
    }


}
