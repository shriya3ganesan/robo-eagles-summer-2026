package org.nknsd.teamcode.components.handlers.artifact.states;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.artifact.MicrowavePositions;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.artifact.SlotTracker;
import org.nknsd.teamcode.components.handlers.launch.LaunchSystem;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.programs.tests.thisYear.firing.ChuteAdjustTest;

public class LaunchAllState extends StateMachine.State {
    private final ArtifactSystem artifactSystem;
    private final LaunchSystem launchSystem;
    private final SlotTracker slotTracker;
    private final MicrowaveScoopHandler microwaveScoopHandler;
    private final int timesRan;
    private final int[] slotOrder;


    public LaunchAllState(int[] slotOrder, ArtifactSystem artifactSystem, LaunchSystem launchSystem, SlotTracker slotTracker, MicrowaveScoopHandler microwaveScoopHandler, int timesRan) {
        this.slotOrder = slotOrder;
        this.artifactSystem = artifactSystem;
        this.launchSystem = launchSystem;
        this.slotTracker = slotTracker;
        this.microwaveScoopHandler = microwaveScoopHandler;
        this.timesRan = timesRan;
    }

    boolean endNow = false;

    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        if (microwaveScoopHandler.isDone() && launchSystem.isReady()){
            if (!endNow) {
                microwaveScoopHandler.doScoopLaunch();
                slotTracker.clearSlot(slotOrder[timesRan]);
                launchSystem.resetConfidence();
                endNow = true;
            } else {
                if (timesRan < 2) {
                    StateMachine.INSTANCE.startAnonymous(new LaunchAllState(slotOrder, artifactSystem, launchSystem, slotTracker, microwaveScoopHandler, timesRan + 1));
                } else {artifactSystem.setIsLaunching(false);}
                RobotLog.v("LaunchAllState ended at: " + runtime.milliseconds());
                StateMachine.INSTANCE.stopAnonymous(this);
            }
        }
    }

    @Override
    protected void started() {
        if (timesRan >= 0 && timesRan <= 2) {
            RobotLog.v("LaunchAll moving to launchPos: " + slotOrder[timesRan]);
            microwaveScoopHandler.setMicrowavePosition(MicrowavePositions.values()[slotOrder[timesRan] + 3]);
        }

    }

    @Override
    protected void stopped() {
//        RobotLog.v("stoppint launch all #" + timesRan);
    }
}