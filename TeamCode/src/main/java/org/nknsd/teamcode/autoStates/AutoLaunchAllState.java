package org.nknsd.teamcode.autoStates;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.launch.FiringSystem;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;

public class AutoLaunchAllState extends StateMachine.State {
    private final FiringSystem firingSystem;
    private final String[] toStopOnEnd;
    private final String[] toStartOnEnd;
    private boolean launched = false;

    public AutoLaunchAllState(FiringSystem firingSystem, String[] toStopOnEnd, String[] toStartOnEnd) {
        this.firingSystem = firingSystem;
        this.toStopOnEnd = toStopOnEnd;
        this.toStartOnEnd = toStartOnEnd;
    }

    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        if(launched && firingSystem.isReady()){
                StateMachine.INSTANCE.stopAnonymous(this);
                RobotLog.v("ending auto firing state");
        }

        if(firingSystem.isReady() && !launched){
            launched = true;
            firingSystem.fireAll();
            RobotLog.v("calling fireall");
        }
    }

    @Override
    protected void started() {
        RobotLog.v("starting auto firing state");
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

