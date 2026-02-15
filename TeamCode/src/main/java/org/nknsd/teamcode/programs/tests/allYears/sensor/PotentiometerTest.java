package org.nknsd.teamcode.programs.tests.allYears.sensor;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.nknsd.teamcode.components.sensors.PotentiometerSensor;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;

import java.util.List;

@TeleOp(name = "potentiometer Tester", group="Tests")
public class PotentiometerTest extends NKNProgram {
    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        PotentiometerSensor potentiometerSensor = new PotentiometerSensor();
        components.add(potentiometerSensor);
        telemetryEnabled.add(potentiometerSensor);
    }
}
