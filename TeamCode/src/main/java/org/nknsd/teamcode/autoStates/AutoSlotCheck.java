package org.nknsd.teamcode.autoStates;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.utility.StateMachine;

public class AutoSlotCheck extends StateMachine.State {
    private final ArtifactSystem artifactSystem;
    private final String[] toStopOnEnd;
    private final String[] toStartOnEnd;

    public AutoSlotCheck(ArtifactSystem artifactSystem, String[] toStopOnEnd, String[] toStartOnEnd) {
        this.artifactSystem = artifactSystem;
        this.toStopOnEnd = toStopOnEnd;
        this.toStartOnEnd = toStartOnEnd;
    }

    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        if (artifactSystem.isReady() && runtime.milliseconds() > 100 + startTimeMS) {
            StateMachine.INSTANCE.stopAnonymous(this);
        }
    }

    @Override
    protected void started() {
        RobotLog.v("started scanning");
        artifactSystem.scanAll();

    }

    @Override
    protected void stopped() {
        for (String stateName : this.toStopOnEnd) {
            StateMachine.INSTANCE.stopState(stateName);
        }
        for (String stateName : this.toStartOnEnd) {
            StateMachine.INSTANCE.startState(stateName);
        }
        RobotLog.v("finished scanning");
    }
}

