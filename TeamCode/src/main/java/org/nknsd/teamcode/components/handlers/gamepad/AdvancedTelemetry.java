package org.nknsd.teamcode.components.handlers.gamepad;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.frameworks.NKNComponent;

import java.util.LinkedList;

// NOTE.
// AdvancedTelemetry does not need to be created as a component to work;
// creating AdvancedTelemetry as a component only serves to allow you to enable telemetry on it to automate logging
public class AdvancedTelemetry implements NKNComponent {
    private final Telemetry telemetry;
    private final LinkedList<StringPair> data = new LinkedList<>();

    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        return true;
    }

    @Override
    public void init_loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void start(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void stop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public String getName() {
        return "Advanced Telemetry";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
        printData();
    }

    private class StringPair {
        public final String a;
        public final String b;
        public StringPair(String a, String b) {
            this.a = a;
            this.b = b;
        }
    }

    public AdvancedTelemetry(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public void addData(String caption, Object value) {
        StringPair dataPair = new StringPair(caption, String.valueOf(value));
        data.add(dataPair);
    }

    @SuppressWarnings("unused")
    public void addSingleData(Object value) {
        StringPair dataPair = new StringPair(String.valueOf(value), "");
        data.add(dataPair);
    }

    public void modifyData(String caption, Object value) {
        for (StringPair s : data) {
            if (s.a.equals(caption)) {
                data.remove(s);
                addData(caption, value);
                return;
            }
        }

        addData(caption, value);
    }

    public void printData() {
        data.forEach((n) -> telemetry.addData(n.a, n.b));
        data.clear();
    }
}

