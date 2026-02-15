package org.nknsd.teamcode.components.handlers.artifact.states;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.artifact.SlotTracker;
import org.nknsd.teamcode.components.utility.StateMachine;

public class ScanStartState extends StateMachine.State {
    private final ArtifactSystem artifactSystem;
    private final MicrowaveScoopHandler microwaveScoopHandler;
    private final SlotTracker slotTracker;
    private final boolean override;

    public ScanStartState(ArtifactSystem artifactSystem, MicrowaveScoopHandler microwaveScoopHandler, SlotTracker slotTracker, boolean override){
        this.artifactSystem = artifactSystem;
        this.microwaveScoopHandler = microwaveScoopHandler;
        this.slotTracker = slotTracker;
        this.override = override;
    }
    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        if(microwaveScoopHandler.isDone()){
//            RobotLog.v("scanning done! moving on (hopefully)");
            StateMachine.INSTANCE.startAnonymous(new ScanState(artifactSystem, microwaveScoopHandler, slotTracker, 0, override));
            StateMachine.INSTANCE.stopAnonymous(this);
        } else {
//            RobotLog.v("is Scanning done" + microwaveScoopHandler.isDone());
        }
    }

    @Override
    protected void started() {
        artifactSystem.setScanState(this);
        artifactSystem.setIsScanning(true);
//        RobotLog.v("started startscanstate");
    }

    @Override
    protected void stopped() {
//        RobotLog.v("stopped startscanstate");
    }
}
