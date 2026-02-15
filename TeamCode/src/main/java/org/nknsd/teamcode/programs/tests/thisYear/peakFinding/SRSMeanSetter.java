package org.nknsd.teamcode.programs.tests.thisYear.peakFinding;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.gamepad.AdvancedTelemetry;
import org.nknsd.teamcode.components.handlers.srs.PeakFinder;
import org.nknsd.teamcode.components.handlers.srs.SRSHubHandler;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;

import java.util.List;

@TeleOp(name="SRS mean setter", group="Tests")
public class SRSMeanSetter extends NKNProgram {
    private class MeanSetterState extends StateMachine.State {
        private final SRSHubHandler srsHubHandler;
        private final PeakFinder peakFinder;
        private final AdvancedTelemetry advancedTelemetry;
        private double lastRunTime;

        public MeanSetterState(SRSHubHandler srsHubHandler, PeakFinder peakFinder, AdvancedTelemetry advancedTelemetry) {
            this.srsHubHandler = srsHubHandler;
            this.peakFinder = peakFinder;
            this.advancedTelemetry = advancedTelemetry;
        }

        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
            if(runtime.milliseconds() - lastRunTime > 500){
                srsHubHandler.updateMean(10);
                srsHubHandler.saveMean("meanFile.csv");
                short mean[][] = srsHubHandler.getCurrentMean();
                lastRunTime = runtime.milliseconds();
            }


        }

        @Override
        protected void started() {

        }

        @Override
        protected void stopped() {

        }
    }

    @Override
    public void createComponents(List<NKNComponent> components, List<NKNComponent> telemetryEnabled) {
        SRSHubHandler srsHubHandler = new SRSHubHandler();
        components.add(srsHubHandler);

        AdvancedTelemetry advancedTelemetry = new AdvancedTelemetry(telemetry);
        components.add(advancedTelemetry);
        telemetryEnabled.add(advancedTelemetry);

        PeakFinder peakFinder = new PeakFinder();

        telemetryEnabled.add(srsHubHandler);

        components.add(StateMachine.INSTANCE);
        StateMachine.INSTANCE.startAnonymous(new MeanSetterState(srsHubHandler, peakFinder, advancedTelemetry));
    }
}


