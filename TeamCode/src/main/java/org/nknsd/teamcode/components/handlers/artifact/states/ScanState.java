package org.nknsd.teamcode.components.handlers.artifact.states;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.artifact.MicrowavePositions;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.artifact.SlotTracker;
import org.nknsd.teamcode.components.handlers.color.BallColor;
import org.nknsd.teamcode.components.utility.StateMachine;

public class ScanState extends StateMachine.State {

    private final ArtifactSystem artifactSystem;
    private final MicrowaveScoopHandler microwaveScoopHandler;
    private final SlotTracker slotTracker;
    private final int timesRan;
    private final boolean override;

    public ScanState(ArtifactSystem artifactSystem, MicrowaveScoopHandler microwaveScoopHandler, SlotTracker slotTracker, int timesRan, boolean override) {
        this.artifactSystem = artifactSystem;
        this.microwaveScoopHandler = microwaveScoopHandler;
        this.slotTracker = slotTracker;
        this.timesRan = timesRan;
        this.override = override;
    }

    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        RobotLog.v("slot color " + slotTracker.getSlotColor(timesRan));
        if (override) {
            if (microwaveScoopHandler.isDone()) {
                if (timesRan < 2) {
                    StateMachine.INSTANCE.startAnonymous(new ScanState(artifactSystem, microwaveScoopHandler, slotTracker, timesRan + 1, override));
                }
                StateMachine.INSTANCE.stopAnonymous(this);
            }
        }
        else if (microwaveScoopHandler.isDone() && slotTracker.getSlotColor(timesRan) != BallColor.UNSURE) {
            if (timesRan < 2) {
                StateMachine.INSTANCE.startAnonymous(new ScanState(artifactSystem, microwaveScoopHandler, slotTracker, timesRan + 1, override));
            }
            StateMachine.INSTANCE.stopAnonymous(this);
        }
    }

    @Override
    protected void started() {
        if (timesRan >= 0 && timesRan <= 2) {
            microwaveScoopHandler.setMicrowavePosition(MicrowavePositions.values()[timesRan]);
        }
        artifactSystem.setScanState(this);
        RobotLog.v("starting scanstate #" + timesRan);
    }

    @Override
    protected void stopped() {
        RobotLog.v("stopping scanstate #" + timesRan);
        if(timesRan == 2){
            artifactSystem.setIsScanning(false);
            RobotLog.v("ending scan sequence");
        }
    }
}
