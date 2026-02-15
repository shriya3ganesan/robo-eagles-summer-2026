package org.nknsd.teamcode.programs.tests.allYears.sensor;


import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.R;
import org.nknsd.teamcode.components.handlers.srs.SRSHubHandler;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;
import org.nknsd.teamcode.frameworks.NKNProgram;
import org.nknsd.teamcode.programs.tests.thisYear.lift.ManualLiftTest;

import java.util.List;

@TeleOp(name = "SRSHub Test", group = "Tests") @Disabled
public class SRSHubTest extends NKNProgram {


    SRSHubHandler hub = new SRSHubHandler();

    public class ReadHubState extends StateMachine.State {

        private final double COS_45 = Math.cos(Math.PI / 4);
        private final double SIN_45 = Math.sin(Math.PI / 4);

        double lastRun;


        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
            if (runtime.milliseconds() > lastRun + 500) {
                hub.getNormalizedDists();
                hub.ballLocation();
//                double start = lastRun = runtime.milliseconds();
//                short[][] distances = hub.getDistances();
//                for (int i = 0; i < 8; i++) {
//                    double a = (double) distances[i][7];
//                    double b = (double) distances[i][0];
//
//                    double c = Math.sqrt(a * a + b * b - 2 * a * b * COS_45);
//                    double aAngle = Math.asin(a / (c / SIN_45));
////                    RobotLog.v("Iteration: "+i);
////                    RobotLog.v("angle " + aAngle);
//
//                    double cameraHeight = b * Math.sin(aAngle);
////                    RobotLog.v("cameraHeight " + cameraHeight);
//
//                    double cameraAngle = Math.PI / 2 - aAngle;
////                    RobotLog.v("camera angle " + cameraAngle);
//
//                }
//
//                RobotLog.v("ReadHubState interval (ms):" + (runtime.milliseconds() - start));
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

        components.add(StateMachine.INSTANCE);
        StateMachine.INSTANCE.startAnonymous(new SRSHubTest.ReadHubState());


        components.add(hub);
        telemetryEnabled.add(hub);
    }
}
