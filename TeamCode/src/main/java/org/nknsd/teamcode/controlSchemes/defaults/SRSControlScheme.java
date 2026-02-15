package org.nknsd.teamcode.controlSchemes.defaults;

import org.nknsd.teamcode.components.handlers.gamepad.GamePadHandler;
import org.nknsd.teamcode.frameworks.NKNControlScheme;

import java.util.concurrent.Callable;

public class SRSControlScheme extends NKNControlScheme {
    @Override
    public String getName() {
        return "Default";
    }


    public Callable<Boolean> lockTarget(){
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return GamePadHandler.GamepadButtons.B.detect(gamePadHandler.getGamePad1());
            }
        };
    }

    public Callable<Boolean> unlockTarget(){
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return !GamePadHandler.GamepadButtons.B.detect(gamePadHandler.getGamePad1());
            }
        };
    }
}