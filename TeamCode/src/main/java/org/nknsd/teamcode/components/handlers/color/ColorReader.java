package org.nknsd.teamcode.components.handlers.color;

import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.nknsd.teamcode.frameworks.NKNComponent;

public class ColorReader implements NKNComponent {

    private final String sensorName;
    private RevColorSensorV3 sensor;

    public ColorReader(String sensorName) {
        this.sensorName = sensorName;
    }

    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        sensor = hardwareMap.get(RevColorSensorV3.class, sensorName);
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
        return "ColorReader:" + sensorName;
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
        double[] reading = getReading();

        telemetry.addData(sensorName + " distance", reading[3]);
//        telemetry.addData(sensorName + " redness", reading[0]);
//        telemetry.addData(sensorName + " greenness", reading[1]);
//        telemetry.addData(sensorName + " blueness", reading[2]);
    }

    public void enableLED() {
        sensor.enableLed(true);
    }

    public void disableLED() {
        sensor.enableLed(false);
    }

    public double[] getReading() {
        double[] rgb = new double[4];
        rgb[0] = sensor.red();
        rgb[1] = sensor.green();
        rgb[2] = sensor.blue();
        rgb[3] = sensor.getDistance(DistanceUnit.MM);
//        RobotLog.v("ColorReader: R=" + rgb[0] + " G=" + rgb[1] + " B=" + rgb[2]);
        return rgb;
    }

//    public int[] getHueLight(){
//        int[] hueLight = new int[2];
//        hueLight[0] = sensor.argb();
//        hueLight[1] = sensor.alpha();
//        RobotLog.v("ColorReader: H="+hueLight[0]+" L="+hueLight[1]);
//        return hueLight;
//    }
//
//    public double getDistance() {
//        double dist = sensor.getDistance(DistanceUnit.MM);
//        RobotLog.v("ColorReader: D=" + dist);
//        return dist;
//    }

}
