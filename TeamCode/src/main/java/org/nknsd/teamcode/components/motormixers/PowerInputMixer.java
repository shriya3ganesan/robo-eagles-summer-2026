package org.nknsd.teamcode.components.motormixers;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.frameworks.NKNComponent;

public class PowerInputMixer implements NKNComponent {

    AbsolutePowerMixer absolutePowerMixer;
    MecanumMotorMixer mecanumMotorMixer;

    double[] powers = new double[]{0,0,0};
    boolean[] autoEnabled = new boolean[]{false, false, false};

    boolean directEnabled = false;

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
        return "";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void doTelemetry(Telemetry telemetry) {

    }

    public void setAutoPowers(double[] autoPowers) {
//        RobotLog.v("given auto powers x: " + autoPowers[0] + ", y: " + autoPowers[1] + ", h: " + autoPowers[2]);
        if (autoEnabled[0]) {
//            RobotLog.v("enable 0");
            powers[0] = autoPowers[0];
        }
        if (autoEnabled[1]) {
//            RobotLog.v("enable 1");
            powers[1] = autoPowers[1];
        }
        if (autoEnabled[2]) {
//            RobotLog.v("enable 2");
            powers[2] = autoPowers[2];
        }
//        RobotLog.v("set auto powers x: " + powers[0] + ", y: " + powers[1] + ", h: " + powers[2]);
        if(directEnabled){
            absolutePowerMixer.setDirectPowers(powers);
        }
        absolutePowerMixer.setPowers(powers);
    }

    public void setManualPowers(double[] manualPowers) {
        if (!autoEnabled[0]) {
            powers[0] = manualPowers[0];
        }
        if (!autoEnabled[1]) {
            powers[1] = manualPowers[1];
        }
        if (!autoEnabled[2]) {
            powers[2] = manualPowers[2];
        }
        if(directEnabled){
            absolutePowerMixer.setDirectPowers(powers);
        }
        mecanumMotorMixer.setPowers(powers);
//        RobotLog.v("manual powers x: " + powers[0] + ", y: " + powers[1] + ", h: " + powers[2]);
    }

    public void setAutoEnabled(boolean[] autoEnable) {
        this.autoEnabled = autoEnable;
//        Thread.dumpStack();
//        RobotLog.v("enabling auto " + autoEnable[0] + ", " + autoEnable[1] + ", " + autoEnable[2]);
    }

    public void setDirectPower(boolean enable){
        directEnabled = enable;
    }

    public void link(AbsolutePowerMixer absolutePowerMixer, MecanumMotorMixer mecanumMotorMixer) {
        this.absolutePowerMixer = absolutePowerMixer;
        this.mecanumMotorMixer = mecanumMotorMixer;
    }
}
