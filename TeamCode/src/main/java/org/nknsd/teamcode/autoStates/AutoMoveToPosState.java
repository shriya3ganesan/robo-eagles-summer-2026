package org.nknsd.teamcode.autoStates;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.motormixers.AutoPositioner;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.components.utility.feedbackcontroller.PidController;

public class AutoMoveToPosState extends StateMachine.State {
    final private AutoPositioner autoPositioner;
    final private AbsolutePosition absolutePosition;
    boolean killSelf;
    final private double xTarget;
    final private double yTarget;
    final private double hTarget;
    final private double errorXMargin;
    final private double errorYMargin;
    final private double errorHMargin;
    final private double speedError;

    private final PidController pidControllerX;
    private final PidController pidControllerY;
    private final PidController pidControllerH;
    private final String[] toStopOnEnd;
    private final String[] toStartOnEnd;


    public AutoMoveToPosState(AutoPositioner autoPositioner, AbsolutePosition absolutePosition, boolean killSelf, double[] target, double errorXMargin, double errorYMargin, double errorHMargin, double speedError, String[] toStopOnEnd, String[] toStartOnEnd) {
        this(autoPositioner, absolutePosition, killSelf, target, errorXMargin, errorYMargin, errorHMargin, speedError, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, toStopOnEnd, toStartOnEnd);
    }

    /**
     * @param target sets the absolute X target, Y target, H target
     * @param errorXMargin sets the x error margin based on (SOMETHING IDK)
     * @param errorYMargin sets the y error margin based on (SOMETHING IDK)
     * @param errorHMargin sets the heading error margin based on (SOMETHING IDK)
     * @param speedError sets the speed error margin
     */
    public AutoMoveToPosState(AutoPositioner autoPositioner, AbsolutePosition absolutePosition, boolean killSelf, double[] target, double errorXMargin, double errorYMargin, double errorHMargin, double speedError, PidController pidControllerX, PidController pidControllerY, PidController pidControllerH, String[] toStopOnEnd, String[] toStartOnEnd){
        this.autoPositioner = autoPositioner;
        this.absolutePosition = absolutePosition;
        this.killSelf = killSelf;
        this.xTarget = target[0];
        this.yTarget = target[1];
        this.hTarget = target[2];
        this.errorXMargin = errorXMargin;
        this.errorYMargin = errorYMargin;
        this.errorHMargin = errorHMargin;
        this.speedError = speedError;
        this.pidControllerX = pidControllerX;
        this.pidControllerY = pidControllerY;
        this.pidControllerH = pidControllerH;
        this.toStopOnEnd = toStopOnEnd;
        this.toStartOnEnd = toStartOnEnd;
    }
    private boolean isWithin(double currentError, double allowedError) {
        return (Math.abs(currentError) < allowedError);
    }
    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {

        boolean angleCheck = false;
        boolean speedCheck = false;
        boolean xyCheck = false;

        if (isWithin(xTarget - absolutePosition.getPosition().x, errorXMargin) && isWithin(yTarget - absolutePosition.getPosition().y, errorYMargin)) {
            xyCheck = true;
        }
        if(isWithin(hTarget - absolutePosition.getPosition().h, errorHMargin)){
            angleCheck = true;
        }

        if (isWithin(absolutePosition.getVelocity().x, speedError) && isWithin(absolutePosition.getVelocity().y, speedError)) {
            speedCheck = true;
        }


        if (angleCheck && speedCheck && xyCheck && killSelf) {
            StateMachine.INSTANCE.stopAnonymous(this);
        }
    }

    @Override
    protected void started() {
        autoPositioner.enableAutoPositioning(true, true, true);
//        RobotLog.v("setting targets x: " + xTarget + ", y: " + yTarget + ", h: " + hTarget);
        autoPositioner.setTargetX(xTarget, pidControllerX);
        autoPositioner.setTargetY(yTarget, pidControllerY);
        autoPositioner.setTargetH(hTarget, pidControllerH);
//        RobotLog.v("targets set!");
    }

    @Override
    protected void stopped() {
        autoPositioner.enableAutoPositioning(false, false, false);
        for (String stateName : this.toStopOnEnd) {
            StateMachine.INSTANCE.stopState(stateName);
        }
        for (String stateName : this.toStartOnEnd) {
            StateMachine.INSTANCE.startState(stateName);
        }
    }
}
