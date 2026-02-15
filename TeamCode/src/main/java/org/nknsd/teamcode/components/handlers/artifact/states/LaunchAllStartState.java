package org.nknsd.teamcode.components.handlers.artifact.states;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.artifact.SlotTracker;
import org.nknsd.teamcode.components.handlers.launch.LaunchSystem;
import org.nknsd.teamcode.components.utility.StateMachine;

public class LaunchAllStartState extends StateMachine.State {
    private final MicrowaveScoopHandler microwaveScoopHandler;
    private final SlotTracker slotTracker;
    private final ArtifactSystem artifactSystem;
    private final LaunchSystem launchSystem;

    private final int[] slotOrder;

    public LaunchAllStartState(int[] slotOrder, MicrowaveScoopHandler microwaveScoopHandler, SlotTracker slotTracker, ArtifactSystem artifactSystem, LaunchSystem launchSystem) {
        this.slotOrder = slotOrder;
        this.microwaveScoopHandler = microwaveScoopHandler;
        this.slotTracker = slotTracker;
        this.artifactSystem = artifactSystem;
        this.launchSystem = launchSystem;
    }

    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
//        if (microwaveScoopHandler.isDone()) {
            StateMachine.INSTANCE.startAnonymous(new LaunchAllState(slotOrder,artifactSystem, launchSystem, slotTracker, microwaveScoopHandler, 0));
            StateMachine.INSTANCE.stopAnonymous(this);
//        }
    }

    @Override
    protected void started() {
        artifactSystem.setIsLaunching(true);
    }

    @Override
    protected void stopped() {

    }
}
