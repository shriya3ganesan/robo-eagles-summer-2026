package org.nknsd.teamcode.programs.parts;

import org.nknsd.teamcode.autoStates.AutoIntakeAllState;
import org.nknsd.teamcode.autoStates.AutoIntakeFromLoadingZoneState;
import org.nknsd.teamcode.autoStates.AutoLaunchAllState;
import org.nknsd.teamcode.autoStates.AutoMoveToPosState;
import org.nknsd.teamcode.autoStates.AutoReadPatternState;
import org.nknsd.teamcode.autoStates.AutoSlotCheck;
import org.nknsd.teamcode.autoStates.AutoTargetState;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.launch.FiringSystem;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.motormixers.AutoPositioner;
import org.nknsd.teamcode.components.sensors.AprilTagSensor;
import org.nknsd.teamcode.components.utility.PositionTransform;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.ProgramPart;
import org.nknsd.teamcode.states.TimerState;

import java.util.List;

public class FarAutoPart extends ProgramPart {


    final private PositionTransform transform;

    private final Setup setup;
    public FarAutoPart(PositionTransform positionTransform, Setup setup) {
        this.transform = positionTransform;
        this.setup = setup;
    }

    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        //        auto states
        final AutoPositioner autoPositioner = setup.getAutoPositioner();
        final AbsolutePosition absolutePosition = setup.getAbsolutePosition();
        final AprilTagSensor aprilTagSensor = setup.getAprilTagSensor();
        final FiringSystem firingSystem = setup.getFiringSystem();
        final ArtifactSystem artifactSystem = setup.getArtifactSystem();

        StateMachine.INSTANCE.addState("start", new AutoMoveToPosState(autoPositioner, absolutePosition,true, transform.adjustPos(0,7.6,0.09), 0,0,0,0, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("read pattern", new AutoReadPatternState(aprilTagSensor, firingSystem, new String[]{"start"}, new String[]{"rotate to fire pos"}));
        StateMachine.INSTANCE.addState("rotate to fire pos", new AutoMoveToPosState(autoPositioner, absolutePosition,true,transform.adjustPos(0,8, -0.35), 1,1,0.05,1,RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"target", "timeToTarget"}));
        StateMachine.INSTANCE.addState("target", new AutoTargetState(firingSystem,true, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("timeToTarget", new TimerState(2000, new String[]{"launch pattern", "target while firing"}, new String[]{"target"}));
        StateMachine.INSTANCE.addState("target while firing", new AutoTargetState(firingSystem,false, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("launch pattern", new AutoLaunchAllState(firingSystem, new String[]{"target while firing"}, new String[]{"move to loading zone"}));

        StateMachine.INSTANCE.addState("move to loading zone", new AutoMoveToPosState(autoPositioner, absolutePosition,true,transform.adjustPos(-35,8,Math.PI/2), 0.2,0.2,0.2,1,RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"intake", "loading zone procedure"}));
        StateMachine.INSTANCE.addState("intake", new AutoIntakeAllState(artifactSystem,new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState( "loading zone procedure",new AutoIntakeFromLoadingZoneState(autoPositioner,7,1, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, transform, new String[]{"intake"}, new String[]{"return to fire pos"}));

        StateMachine.INSTANCE.addState("return to fire pos", new AutoMoveToPosState(autoPositioner, absolutePosition,true,transform.adjustPos(0,7.2, Math.PI/2), 1,1,0.05,1,RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"turn to fire pos"}));
        StateMachine.INSTANCE.addState("turn to fire pos", new AutoMoveToPosState(autoPositioner, absolutePosition,true,transform.adjustPos(0,7.2, -0.35), 1,1,0.05,1,RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"target #2"}));
        StateMachine.INSTANCE.addState("target #2", new AutoTargetState(firingSystem,true, new String[]{}, new String[]{"timeToTarget #2"}));
        StateMachine.INSTANCE.addState("timeToTarget #2", new TimerState(2000, new String[]{"launch pattern #2", "target while firing #2"}, new String[]{"target #2"}));
        StateMachine.INSTANCE.addState("target while firing #2", new AutoTargetState(firingSystem,true, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("launch pattern #2", new AutoLaunchAllState(firingSystem, new String[]{"target while firing #2"}, new String[]{"move"}));
        StateMachine.INSTANCE.addState("move", new AutoMoveToPosState(autoPositioner, absolutePosition,true,transform.adjustPos(-5,20,0), 0,0,0,0,RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{}));


        StateMachine.INSTANCE.startState("start");
        StateMachine.INSTANCE.startState("read pattern");

    }
}
