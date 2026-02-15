package org.nknsd.teamcode.components.handlers.srs;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.motormixers.AutoPositioner;
import org.nknsd.teamcode.components.utility.SensorGridPoint;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.feedbackcontroller.PidController;
import org.nknsd.teamcode.frameworks.NKNComponent;

public class PeakPointer implements NKNComponent {
    final private double MAX_X_OFFSET = 0.1;
    final private double WAIT_TIME_MS = 100;

    private final PeakFinder peakFinder;
    private final AutoPositioner positioner;
    private final SRSHubHandler srsHubHandler;
    final private AbsolutePosition position;

    private PidController pidH;
    private PidController pidXY;

    private boolean targetingEnabled;
    private boolean eatEnabled;

    private double lastRunTime;

    private Double angle;
    private Double dist;
    private SensorGridPoint point;
    private short oldData;

    public PeakPointer(PeakFinder peakFinder, SRSHubHandler srsHubHandler, AutoPositioner positioner, AbsolutePosition position) {
        this.peakFinder = peakFinder;
        this.srsHubHandler = srsHubHandler;
        this.positioner = positioner;
        this.position = position;
    }

    public void setPids(PidController pidH, PidController pidXY) {
        this.pidH = pidH;
        this.pidXY = pidXY;
    }

    public void enableTargeting(boolean targetingEnabled, boolean eatEnabled) {
        this.targetingEnabled = targetingEnabled;
        this.eatEnabled = eatEnabled;
//        positioner.enableAutoPositioning(eatEnabled, eatEnabled, targetingEnabled); // for when we merge
        if(!RobotVersion.isAutonomous()){positioner.enableAutoPositioning(targetingEnabled);}
        positioner.setTargetH(position.getPosition().h, RobotVersion.INSTANCE.pidControllerH);
        positioner.setTargetX(position.getPosition().x, RobotVersion.INSTANCE.pidControllerX);
        positioner.setTargetY(position.getPosition().y, RobotVersion.INSTANCE.pidControllerY);
    }

    public boolean targetAcquired() {
        if (angle == null) {
            return false;
        }
        return Math.abs(angle) < MAX_X_OFFSET;
    }

    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        srsHubHandler.getMeans("meanFile.csv");
        return true;
    }

    @Override
    public void init_loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void start(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void stop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {
        if (runtime.milliseconds() - lastRunTime > WAIT_TIME_MS) {
            short[][] data = srsHubHandler.getNormalizedDists();
            if (data[0][0] == oldData){
//                RobotLog.v("same");
            } else {
//                RobotLog.v("not same");
            }
            oldData = data[0][0];
            point = peakFinder.findClosestPeak(data);

            if (targetingEnabled && point != null) {
//                RobotLog.v("peak " + point.toString());

                dist = AngleDistCalculator.calculateDistance(point);
                angle = -AngleDistCalculator.calculateHeadingAngle(point);

                positioner.setTargetH((position.getPosition().h + angle), pidH);

                if (eatEnabled) {
                    if (angle != null) {
//                        RobotLog.v("offset " + angle);
//                        RobotLog.v("dist " + dist);

                        double absAngle = position.getPosition().h + angle;

                        double x = Math.sin(absAngle) * dist;
                        double y = Math.cos(absAngle) * dist;

                        positioner.setTargetX(position.getPosition().x - x, pidXY);
                        positioner.setTargetY(position.getPosition().y - y, pidXY);
                    }
                    lastRunTime = runtime.milliseconds();
                }
            }
            lastRunTime = runtime.milliseconds();
        }
    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
        telemetry.addData("enable", targetingEnabled);
        if (targetingEnabled) {
            if (angle != null && point != null && dist != null) {
                telemetry.addData("x", point.getX());
                telemetry.addData("y", point.getY());
                telemetry.addData("angle", angle);
                telemetry.addData("dist", dist);
            } else {
                telemetry.addLine("no peak");
            }
            telemetry.addData("targeted?", targetAcquired());
        }
    }
}
