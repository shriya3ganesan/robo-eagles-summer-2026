package org.nknsd.teamcode.programs.parts;

import org.nknsd.teamcode.components.drivers.FiringDriver;
import org.nknsd.teamcode.components.drivers.IntakeDriver;
import org.nknsd.teamcode.components.drivers.LiftDriver;
import org.nknsd.teamcode.components.drivers.MixedInputWheelDriver;
import org.nknsd.teamcode.components.drivers.SRSDriver;
import org.nknsd.teamcode.components.handlers.gamepad.GamePadHandler;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.controlSchemes.defaults.FiringControlScheme;
import org.nknsd.teamcode.controlSchemes.defaults.IntakeControlScheme;
import org.nknsd.teamcode.controlSchemes.defaults.LiftControlScheme;
import org.nknsd.teamcode.controlSchemes.defaults.SRSControlScheme;
import org.nknsd.teamcode.controlSchemes.defaults.WheelControlScheme;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.ProgramPart;

import java.util.List;

public class GamepadPart extends ProgramPart {

    final Setup setup;

    public GamepadPart(Setup setup) {
        this.setup = setup;
    }


    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        GamePadHandler gamePadHandler = new GamePadHandler();
        components.add(gamePadHandler);


        FiringDriver firingDriver = new FiringDriver();
        components.add(firingDriver);

        FiringControlScheme firingControlScheme = new FiringControlScheme();

        MixedInputWheelDriver mixedInputWheelDriver = new MixedInputWheelDriver(0, 1, 5, GamePadHandler.GamepadSticks.LEFT_JOYSTICK_Y, GamePadHandler.GamepadSticks.LEFT_JOYSTICK_X, GamePadHandler.GamepadSticks.RIGHT_JOYSTICK_X);
        components.add(mixedInputWheelDriver);

        WheelControlScheme wheelControlScheme = new WheelControlScheme();

        IntakeDriver intakeDriver = new IntakeDriver();
        components.add(intakeDriver);

        IntakeControlScheme intakeControlScheme = new IntakeControlScheme();

        LiftDriver liftDriver = new LiftDriver();
        components.add(liftDriver);

        LiftControlScheme liftControlScheme = new LiftControlScheme();

        SRSControlScheme srsControlScheme = new SRSControlScheme();

        SRSDriver srsDriver = new SRSDriver();


        firingDriver.link(gamePadHandler, setup.getFiringSystem(), firingControlScheme);
        firingControlScheme.link(gamePadHandler);
        mixedInputWheelDriver.link(gamePadHandler, setup.getPowerInputMixer(), wheelControlScheme);
        wheelControlScheme.link(gamePadHandler);
        intakeDriver.link(gamePadHandler, setup.getArtifactSystem(), intakeControlScheme);
        intakeControlScheme.link(gamePadHandler);
        liftDriver.link(gamePadHandler, setup.getBalancedLiftHandler(), liftControlScheme);
        liftControlScheme.link(gamePadHandler);
        srsDriver.link(gamePadHandler, srsControlScheme, StateMachine.INSTANCE, setup.getMicrowaveScoopHandler(), setup.getSlotTracker(), setup.getArtifactSystem());
    }
}
