package org.nknsd.teamcode.autoStates;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.color.BallColor;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.handlers.vision.ID;
import org.nknsd.teamcode.components.motormixers.AutoPositioner;
import org.nknsd.teamcode.components.utility.PositionTransform;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.components.utility.feedbackcontroller.PidController;
import org.opencv.core.Mat;

public class AutoIntakeFromLoadingZoneState extends StateMachine.State {
    private final AutoPositioner autoPositioner;
    private final int maxTries;
    private final double stepDist;
    private final PidController pidX;
    private final PidController pidY;
    private final PidController pidH;
    private final PositionTransform transform;
    private final String[] toStopOnEnd;
    private final String[] toStartOnEnd;
    private int ballTries = 0;
    private double lastRunTime;


    public AutoIntakeFromLoadingZoneState(AutoPositioner autoPositioner, int maxTries, double stepDist, PidController pidX, PidController pidY, PidController pidH, PositionTransform transform, String[] toStopOnEnd, String[] toStartOnEnd) {
        this.pidX = pidX;
        this.pidY = pidY;
        this.pidH = pidH;
        this.autoPositioner = autoPositioner;
        this.maxTries = maxTries;
        this.stepDist = stepDist;
        this.transform = transform;
        this.toStopOnEnd = toStopOnEnd;
        this.toStartOnEnd = toStartOnEnd;
    }

    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        if (runtime.milliseconds() - lastRunTime > 500) {
            lastRunTime = runtime.milliseconds();

            double[] target = transform.adjustPos(-46, 10 - ballTries * stepDist, 1);
            autoPositioner.setTargetX(target[0], pidX);
            autoPositioner.setTargetY(target[1], pidY);
            autoPositioner.setTargetH(target[2], pidH);

            if (ballTries > maxTries) {
                StateMachine.INSTANCE.stopAnonymous(this);
            }
            ballTries++;
        }
    }


    @Override
    protected void started() {
        lastRunTime = startTimeMS;
        autoPositioner.enableAutoPositioning(true, true, true);
        double[] target = transform.adjustPos(-46, 10, 1);
        autoPositioner.setTargetX(target[0], pidX);
        autoPositioner.setTargetY(target[1], pidY);
        autoPositioner.setTargetH(target[2], pidH);
    }

    @Override
    protected void stopped() {
        for (String stateName : this.toStopOnEnd) {
            StateMachine.INSTANCE.stopState(stateName);
        }

        for (String stateName : this.toStartOnEnd) {
            StateMachine.INSTANCE.startState(stateName);
        }
    }
}
