package org.nknsd.teamcode.components.utility;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.frameworks.NKNComponent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class StateMachine implements NKNComponent {

    static public abstract class State {
        protected double startTimeMS = -1;
        protected boolean stopping = true;
        protected  boolean starting = false;

        protected String name;

        protected abstract void run(ElapsedTime runtime, Telemetry telemetry);

        protected abstract void started();

        protected abstract void stopped();

        public boolean isRunning(){
            return !stopping;
        }
    }

    public static StateMachine INSTANCE = new StateMachine();
    public static void RESET(){
        INSTANCE = new StateMachine();
    }

    final private HashMap<String, State> stateMap = new HashMap<>();
    final private List<State> runList = new LinkedList<>();

    private StateMachine(){}

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
        return "stateMachine";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {
        List<State> copyList = new LinkedList<>(runList);
        for (State state : copyList) {
            if (state.stopping){
                continue;
            }
            if (state.starting) {
                state.startTimeMS = runtime.milliseconds();
//                RobotLog.v("Actively starting %s",state.name);
                state.started();
                state.starting = false;
            }
            state.run(runtime, telemetry);
        }
    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
        String nameString = "";
        for (State state : runList) {
            nameString = nameString + "|" + state.name;
            nameString = nameString + ", ";
        }
        telemetry.addData("States",nameString);
    }

    public void addState(String name, State state) {
        stateMap.put(name, state);
        state.name = name;
    }

    public void startState(String name) {
        State state = stateMap.get(name);
        if (state == null) {
            throw new NullPointerException("State: " + name + " not found!");
        }
//        RobotLog.v("Adding starting state %s which is %s and has time %f",name,state.name,state.startTimeMS);
        state.stopping = false;
        state.starting = true;
        runList.add(state);
    }

    public void stopState(String name) {
        State state = stateMap.get(name);
        if (state == null) {
            throw new NullPointerException("State: " + name + " not found!");
        }
        state.stopped();
        state.stopping = true;
        runList.remove(state);
    }

    public void startAnonymous(State state){
        state.name="anon_"+state.getClass().getSimpleName();
        state.stopping = false;
        state.starting = true;
        runList.add(state);
    }
    public void stopAnonymous(State state){
        state.stopped();
        state.stopping = true;
        runList.remove(state);
    }

    public boolean isStateRunning(String stateName) {
        for (State state : runList) {
            if (stateName.equals(state.name)) {
                return true;
            }
        }
        return false;
    }
}
