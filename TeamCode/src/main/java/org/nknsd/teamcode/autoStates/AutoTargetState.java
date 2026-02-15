package org.nknsd.teamcode.autoStates;

import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.launch.FiringSystem;
import org.nknsd.teamcode.components.utility.StateMachine;

public class AutoTargetState extends StateMachine.State {
    private final FiringSystem firingSystem;
    private final boolean killWhenTargeted;
    private final String[] toStopOnEnd;
    private final String[] toStartOnEnd;

    public AutoTargetState(FiringSystem firingSystem, boolean killWhenTargeted, String[] toStopOnEnd, String[] toStartOnEnd) {
        this.firingSystem = firingSystem;
        this.killWhenTargeted = killWhenTargeted;
        this.toStopOnEnd = toStopOnEnd;
        this.toStartOnEnd = toStartOnEnd;
    }

    @Override
    protected void run(ElapsedTime runtime, Telemetry telemetry) {
        if (firingSystem.isReady() && killWhenTargeted) {
            StateMachine.INSTANCE.stopAnonymous(this);
            RobotLog.v("ending target state");
        }
        if (firingSystem.isReady()) {
            RobotLog.v("AHA! target targeted");
        }
    }

    @Override
    protected void started() {
        firingSystem.lockTarget(true);
    }

    @Override
    protected void stopped() {
        firingSystem.lockTarget(false);
        for (String stateName : this.toStopOnEnd) {
            StateMachine.INSTANCE.stopState(stateName);
        }
        for (String stateName : this.toStartOnEnd) {
            StateMachine.INSTANCE.startState(stateName);
        }
    }
}
