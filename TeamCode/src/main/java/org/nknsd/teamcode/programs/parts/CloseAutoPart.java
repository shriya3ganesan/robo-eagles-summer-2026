package org.nknsd.teamcode.programs.parts;

import org.nknsd.teamcode.autoStates.AutoIntakeAllState;
import org.nknsd.teamcode.autoStates.AutoLaunchAllState;
import org.nknsd.teamcode.autoStates.AutoMoveToPosState;
import org.nknsd.teamcode.autoStates.AutoReadPatternState;
import org.nknsd.teamcode.autoStates.AutoSlotCheck;
import org.nknsd.teamcode.autoStates.AutoTargetState;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.launch.FiringSystem;
import org.nknsd.teamcode.components.handlers.launch.LaunchSystem;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.handlers.vision.ID;
import org.nknsd.teamcode.components.motormixers.AutoPositioner;
import org.nknsd.teamcode.components.sensors.AprilTagSensor;
import org.nknsd.teamcode.components.utility.PositionTransform;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.components.utility.feedbackcontroller.PidController;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.ProgramPart;
import org.nknsd.teamcode.states.TimerState;

import java.util.List;

public class CloseAutoPart extends ProgramPart {
    final private PositionTransform transform;

    private final Setup setup;

    public CloseAutoPart(PositionTransform positionTransform, Setup setup) {
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

        PidController[] pidControllers = new PidController[]{
                RobotVersion.INSTANCE.pidControllerX,
                RobotVersion.INSTANCE.pidControllerY,
                RobotVersion.INSTANCE.pidControllerH,
                new PidController(0.15, 0.15, 0.1, 0.1, true, 0.1, 0.17),
                new PidController(0.15, 0.15, 0.1, 0.1, true, 0.1, 0.17),
                new PidController(0.5, 0.5, 0.5, 0.5, true, 0.2, 0.2)};


        //        auto states
        StateMachine.INSTANCE.addState("start", new AutoMoveToPosState(autoPositioner, absolutePosition, true, transform.adjustPos(0, -20, Math.PI / 2), 1, 1, 0.1, 2, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("read pattern", new AutoReadPatternState(aprilTagSensor, firingSystem, new String[]{"start"}, new String[]{"move to fire pos"}));

        StateMachine.INSTANCE.addState("move to fire pos", new AutoMoveToPosState(autoPositioner, absolutePosition, true, transform.adjustPos(0, -30, 0), 1, 1, 0.05, 2, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"target", "timeToTarget"}));
        StateMachine.INSTANCE.addState("target", new AutoTargetState(firingSystem, true, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("timeToTarget", new TimerState(2000, new String[]{"launch pattern", "target while firing"}, new String[]{"target"}));
        StateMachine.INSTANCE.addState("target while firing", new AutoTargetState(firingSystem, false, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("launch pattern", new AutoLaunchAllState(firingSystem, new String[]{"target while firing"}, new String[]{"move to spike"}));
        StateMachine.INSTANCE.addState("move to spike", new AutoMoveToPosState(autoPositioner, absolutePosition, true, transform.adjustPos(-20, -38, 2.37), 1, 1, 0.1, 2, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"intake", "intake 1st ball", "timer 1"}));
        StateMachine.INSTANCE.addState("intake", new AutoIntakeAllState(artifactSystem, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("intake 1st ball", new AutoMoveToPosState(autoPositioner, absolutePosition, false, transform.adjustPos(-23.5, -32.5, 2.38), 1, 1, 0.1, 2, pidControllers[3], pidControllers[4], pidControllers[5], new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("timer 1", new TimerState(1000, new String[]{"intake 2nd ball", "timer 2"}, new String[]{"intake 1st ball"}));
        StateMachine.INSTANCE.addState("intake 2nd ball", new AutoMoveToPosState(autoPositioner, absolutePosition, false, transform.adjustPos(-26, -30, 2.38), 1, 1, 0.1, 2, pidControllers[3], pidControllers[4], pidControllers[5], new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("timer 2", new TimerState(1000, new String[]{"intake 3rd ball", "timer 3"}, new String[]{"intake 2nd ball"}));
        StateMachine.INSTANCE.addState("intake 3rd ball", new AutoMoveToPosState(autoPositioner, absolutePosition, false, transform.adjustPos(-30.75, -26.13, 2.38), 1, 1, 0.1, 2, pidControllers[3], pidControllers[4], pidControllers[5], new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("timer 3", new TimerState(2000, new String[]{"move to fire pos #2"}, new String[]{"intake", "intake 3rd ball"}));
        StateMachine.INSTANCE.addState("move to fire pos #2", new AutoMoveToPosState(autoPositioner, absolutePosition, true, transform.adjustPos(0, -30, 0), 1, 1, 0.1, 2, RobotVersion.INSTANCE.pidControllerX, RobotVersion.INSTANCE.pidControllerY, RobotVersion.INSTANCE.pidControllerH, new String[]{}, new String[]{"target #2"}));
        StateMachine.INSTANCE.addState("target #2", new AutoTargetState(firingSystem, true, new String[]{}, new String[]{"timeToTarget #2"}));
        StateMachine.INSTANCE.addState("timeToTarget #2", new TimerState(2000, new String[]{"launch pattern #2", "target while firing #2"}, new String[]{"target #2"}));
        StateMachine.INSTANCE.addState("target while firing #2", new AutoTargetState(firingSystem, true, new String[]{}, new String[]{}));
        StateMachine.INSTANCE.addState("launch pattern #2", new AutoLaunchAllState(firingSystem, new String[]{"target while firing #2"}, new String[]{}));


        StateMachine.INSTANCE.startState("start");
        StateMachine.INSTANCE.startState("read pattern");
    }
    }
