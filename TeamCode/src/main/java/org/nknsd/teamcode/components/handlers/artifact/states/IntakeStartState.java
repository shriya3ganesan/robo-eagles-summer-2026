package org.nknsd.teamcode.components.handlers.artifact.states;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.artifact.SlotTracker;
import org.nknsd.teamcode.components.utility.StateMachine;

public class IntakeStartState extends StateMachine.State{

    private final MicrowaveScoopHandler microwaveScoopHandler;
    private final SlotTracker slotTracker;
    private final ArtifactSystem artifactSystem;
    public IntakeStartState(MicrowaveScoopHandler microwaveScoopHandler, SlotTracker slotTracker, ArtifactSystem artifactSystem){
        this.microwaveScoopHandler = microwaveScoopHandler;
        this.slotTracker = slotTracker;
        this.artifactSystem = artifactSystem;
    }
    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        if(IntakeBallState.killIntake){
            IntakeBallState.killIntake = false;
            StateMachine.INSTANCE.stopAnonymous(this);
            return;
        }
        if(microwaveScoopHandler.isDone()){
            StateMachine.INSTANCE.startAnonymous(new IntakeBallState(microwaveScoopHandler, slotTracker, artifactSystem, 0, true, new String[]{}, new String[]{}));
            StateMachine.INSTANCE.stopAnonymous(this);
        }
    }

    @Override
    protected void started() {
        artifactSystem.setIntakeState(this);
    }

    @Override
    protected void stopped() {

    }
}