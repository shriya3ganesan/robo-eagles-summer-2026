package org.nknsd.teamcode.components.handlers.srs;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.artifact.SlotTracker;
import org.nknsd.teamcode.components.handlers.artifact.states.IntakeBallState;
import org.nknsd.teamcode.components.handlers.color.BallColor;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.components.utility.feedbackcontroller.PidController;

public class SRSIntakeState extends StateMachine.State {

    final private PeakPointer peakPointer;
    final private boolean eat;
    final private boolean killSelf;

    private final PidController pidH;
    private final PidController pidXY;

    private final String[] toStopOnEnd;
    private final String[] toStartOnEnd;

    private MicrowaveScoopHandler microwaveScoopHandler;
    private SlotTracker slotTracker;
    private ArtifactSystem artifactSystem;


    public SRSIntakeState(PeakPointer peakPointer, boolean killSelf, PidController pidH, PidController pidXY, String[] toStopOnEnd, String[] toStartOnEnd) {
        this.peakPointer = peakPointer;
        this.killSelf = killSelf;
        this.pidH = pidH;
        this.pidXY = pidXY;
        this.toStopOnEnd = toStopOnEnd;
        this.toStartOnEnd = toStartOnEnd;
        eat = false;
    }

    public SRSIntakeState(PeakPointer peakPointer, boolean killSelf, PidController pidH, PidController pidXY, MicrowaveScoopHandler microwaveScoopHandler, SlotTracker slotTracker, ArtifactSystem artifactSystem, String[] toStopOnEnd, String[] toStartOnEnd) {
        this.peakPointer = peakPointer;
        this.killSelf = killSelf;
        this.pidH = pidH;
        this.pidXY = pidXY;
        this.toStopOnEnd = toStopOnEnd;
        this.toStartOnEnd = toStartOnEnd;
        this.microwaveScoopHandler = microwaveScoopHandler;
        this.slotTracker = slotTracker;
        this.artifactSystem = artifactSystem;
        eat = true;
    }

    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        if (killSelf && peakPointer.targetAcquired() && !eat) {
            StateMachine.INSTANCE.stopAnonymous(this);
        }
    }

    @Override
    protected void started() {
        peakPointer.setPids(pidH, pidXY);
        peakPointer.enableTargeting(true, eat);

        if (eat) {
            int slot = 0;
            for (int i = 0; i < 3; i++) {
                BallColor color = slotTracker.getSlotColor(i);
                if (color == BallColor.NOTHING || color == BallColor.UNSURE) {
                    slot = i;
                }
            }

            RobotLog.v("starting srs mode for slot " + slot);

            if (killSelf) {
                StateMachine.INSTANCE.startAnonymous(new IntakeBallState(microwaveScoopHandler, slotTracker, artifactSystem, slot, false, new String[]{name}, new String[]{}));
            } else {
                StateMachine.INSTANCE.startAnonymous(new IntakeBallState(microwaveScoopHandler, slotTracker, artifactSystem, slot, false, new String[]{}, new String[]{}));
            }
        }
    }

    @Override
    protected void stopped() {
        peakPointer.enableTargeting(false, false);
        for (String stateName : this.toStopOnEnd) {
            StateMachine.INSTANCE.stopState(stateName);
        }
        for (String stateName : this.toStartOnEnd) {
            StateMachine.INSTANCE.startState(stateName);
        }
        RobotLog.v("ending srs mode");
    }
}

