package org.nknsd.teamcode.programs.teleops;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.nknsd.teamcode.components.drivers.IntakeDriverDEPRECATED;
import org.nknsd.teamcode.components.drivers.MicrowaveDriver;
//import org.nknsd.teamcode.components.drivers.WheelDriver;
import org.nknsd.teamcode.components.handlers.WheelHandlerTODODELETEMEUSEPOWERINPUTMIXER;
import org.nknsd.teamcode.components.handlers.artifact.MicrowaveScoopHandler;
import org.nknsd.teamcode.components.handlers.gamepad.GamePadHandler;
import org.nknsd.teamcode.controlSchemes.defaults.MicrowaveControlScheme;
import org.nknsd.teamcode.controlSchemes.defaults.WheelControlScheme;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;

import java.util.List;
@TeleOp (name = "MicrowaveTeleOp")
public class MicrowaveTeleOp extends NKNProgram {
    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        // SPECIAL
        GamePadHandler gamePadHandler = new GamePadHandler();
        components.add(gamePadHandler);
        telemetryEnabled.add(gamePadHandler);


        // WHEELS
        WheelHandlerTODODELETEMEUSEPOWERINPUTMIXER wheelHandlerTODODELETEMEUSEPOWERINPUTMIXER = new WheelHandlerTODODELETEMEUSEPOWERINPUTMIXER();
        components.add(wheelHandlerTODODELETEMEUSEPOWERINPUTMIXER);

//        WheelDriver wheelDriver = new WheelDriver(0, 1, 5, GamePadHandler.GamepadSticks.LEFT_JOYSTICK_Y, GamePadHandler.GamepadSticks.LEFT_JOYSTICK_X, GamePadHandler.GamepadSticks.RIGHT_JOYSTICK_X);
//        components.add(wheelDriver);


        // SENSORS
//        ColorReader colourSensor = new ColorReader("ColorSensor");
//        components.add(colourSensor);


        // MICROWAVE
        MicrowaveScoopHandler microwaveScoopHandler = new MicrowaveScoopHandler();
        components.add(microwaveScoopHandler);

        MicrowaveDriver microwaveDriver = new MicrowaveDriver();
        components.add(microwaveDriver);

        IntakeDriverDEPRECATED intakeDriverDEPRECATED = new IntakeDriverDEPRECATED();
        components.add(intakeDriverDEPRECATED);


        // CONTROL SCHEME
        WheelControlScheme wheelControlScheme = new WheelControlScheme();
        wheelControlScheme.link(gamePadHandler);

        MicrowaveControlScheme microwaveControlScheme = new MicrowaveControlScheme();
        microwaveControlScheme.link(gamePadHandler);

//        wheelDriver.link(gamePadHandler, wheelHandlerTODODELETEMEUSEPOWERINPUTMIXER,wheelControlScheme);
        microwaveDriver.link(gamePadHandler, microwaveScoopHandler, microwaveControlScheme);
        intakeDriverDEPRECATED.link(gamePadHandler, microwaveScoopHandler, microwaveControlScheme);
    }
}