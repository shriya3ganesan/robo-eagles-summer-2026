package org.nknsd.teamcode.components.handlers.artifact;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robot.Robot;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.components.utility.StateMachine;
import org.nknsd.teamcode.frameworks.NKNComponent;

public class MicrowaveScoopHandler implements NKNComponent {


    // this state makes the scoop go up and down
    class ScoopActionState extends StateMachine.State {
        final double SCOOPACTIONTIMEMS = 300;

        private boolean scoopResting = false;

        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {
//            RobotLog.v("scoopstate running");
            if (runtime.milliseconds() > (startTimeMS + SCOOPACTIONTIMEMS)) {
                StateMachine.INSTANCE.stopAnonymous(this);
            }
            // halfway through the scoop will start coming back down
            if ((runtime.milliseconds() > (startTimeMS + (SCOOPACTIONTIMEMS / 2))) && !scoopResting) {
                scoopServo.setPosition(SERVO_REST_POS);
                // a flag just in case telling the scoop to rest over and over would create problems
                scoopResting = true;
            }
        }

        @Override
        protected void started() {
            scoopServo.setPosition(SERVO_LAUNCH_POS);
            scoopResting = false;
        }

        @Override
        protected void stopped() {
//            RobotLog.v("scoopstate stopping");
        }
    }

    // this state now uses the axon servo's feedback to get the position and check if has reached its target instead of using a timer
    class MicrowaveActionState extends StateMachine.State {

        //        Threshold for microwave position feedback
        private final double FEEDBACK_POSITION_THRESHOLD = 0.05;

        @Override
        protected void run(ElapsedTime runtime, Telemetry telemetry) {

//            RobotLog.v("micromovestate running");
//            RobotLog.v("position voltage  " + servoPosInput.getVoltage() + " for slot " + microwavePos);
            if (Math.abs(microwavePos.powerPosition - servoPosInput.getVoltage()) < FEEDBACK_POSITION_THRESHOLD) {
                StateMachine.INSTANCE.stopAnonymous(this);
            }

        }

        private boolean lastIntake;

        @Override
        protected void started() {
            lastIntake = intaking;
            toggleIntake(true);
        }

        @Override
        protected void stopped() {
//            RobotLog.v("micromovestate stopping");
            toggleIntake(lastIntake);
        }
    }

    final private String microwaveServoName = "Spin";
    final private String scoopServoName = "Scoop";
    private ScoopActionState scoopActionState = new ScoopActionState();
    private MicrowaveActionState microwaveActionState = new MicrowaveActionState();

    private MicrowavePositions microwavePos;
    private static final double SERVO_REST_POS = RobotVersion.INSTANCE.scoopRestPos;
    private static final double SERVO_LAUNCH_POS = RobotVersion.INSTANCE.scoopLaunchPos;

    public boolean setMicrowavePosition(MicrowavePositions position) {
        if (!isDone()) {
            return false;
        }
        microwaveServo.setPosition(position.microPosition);
        microwavePos = position;
        StateMachine.INSTANCE.startAnonymous(microwaveActionState);
        return true;
    }

    public boolean doScoopLaunch() {
        if (!isDone()) {
            return false;
        }
        StateMachine.INSTANCE.startAnonymous(scoopActionState);
        return true;
    }

    boolean intaking = false;

    public void toggleIntake(boolean startSpinning) {
        intaking = startSpinning;
        spinner.setPower(startSpinning ? 1 : 0);
    }

    public boolean isDone() {
        // to ensure that things aren't happening before starting them
        boolean done = scoopActionState.isRunning() || microwaveActionState.isRunning();
//        RobotLog.v("is done = " + !done);
        return !(done);
    }

    public MicrowavePositions getMicrowavePosition() {
        return microwavePos;
    }


    Servo microwaveServo;
    Servo scoopServo;
    CRServo spinner;

    AnalogInput servoPosInput;

    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        scoopServo = hardwareMap.servo.get(scoopServoName);
        scoopServo.setPosition(SERVO_REST_POS);

        microwaveServo = hardwareMap.servo.get(microwaveServoName);
        microwaveServo.setPosition(MicrowavePositions.FIRE0.microPosition);
        microwavePos = MicrowavePositions.FIRE0;

        spinner = hardwareMap.get(CRServo.class, "Intake");
        spinner.setPower(0);

        servoPosInput = hardwareMap.get(AnalogInput.class,"ServoPos");

        return true;
    }

    @Override
    public void init_loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void start(ElapsedTime runtime, Telemetry telemetry) {
        setMicrowavePosition(MicrowavePositions.LOAD0);
    }

    @Override
    public void stop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public String getName() {
        return "MicrowaveScoopHandler";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {
    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
        telemetry.addData("Microwave", microwavePos.name());
        telemetry.addData("IsDone", isDone());
        telemetry.addData("Microwave Servo Pos", servoPosInput.getVoltage());
    }

}