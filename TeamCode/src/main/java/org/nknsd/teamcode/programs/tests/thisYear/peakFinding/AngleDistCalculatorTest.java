package org.nknsd.teamcode.programs.tests.thisYear.peakFinding;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.gamepad.AdvancedTelemetry;
import org.nknsd.teamcode.components.handlers.srs.AngleDistCalculator;
import org.nknsd.teamcode.components.handlers.srs.PeakFinder;
import org.nknsd.teamcode.components.handlers.srs.SRSHubHandler;
import org.nknsd.teamcode.components.utility.SensorGridPoint;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;

import java.util.List;

@TeleOp(name="SRS Angle-Dist Calculator Test", group="Tests")
public class AngleDistCalculatorTest extends NKNProgram {
    private class AngleTestState extends StateMachine.State {
        private final SRSHubHandler srsHubHandler;
        private final PeakFinder peakFinder;
        private final AdvancedTelemetry advancedTelemetry;

        public AngleTestState(SRSHubHandler srsHubHandler, PeakFinder peakFinder, AdvancedTelemetry advancedTelemetry) {
            this.srsHubHandler = srsHubHandler;
            this.peakFinder = peakFinder;
            this.advancedTelemetry = advancedTelemetry;
            srsHubHandler.getMeans("meanFile.csv");
        }

        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
            short[][] data = srsHubHandler.getNormalizedDists();
            SensorGridPoint ballPoint = peakFinder.findClosestPeak(data);

            if (ballPoint == null ) {

                advancedTelemetry.modifyData("Ball X", "unknown");
                advancedTelemetry.modifyData("Ball Y", "unknown");
                advancedTelemetry.modifyData("Ball Angle", "unknown");
                advancedTelemetry.modifyData("Ball Distance", "unknown");
                return;
            }

            double angle = AngleDistCalculator.calculateHeadingAngle(ballPoint);
            double dist = AngleDistCalculator.calculateDistance(ballPoint);

            advancedTelemetry.modifyData("Ball X", ballPoint.getX());
            advancedTelemetry.modifyData("Ball Y", ballPoint.getY());
            advancedTelemetry.modifyData("Ball Angle", angle);
            advancedTelemetry.modifyData("Ball Distance", dist);
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
        StateMachine.INSTANCE.startAnonymous(new AngleTestState(srsHubHandler, peakFinder, advancedTelemetry));
    }
}
