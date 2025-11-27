package org.firstinspires.ftc.team417;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import android.graphics.Color;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class CoolColorDetector {
    Telemetry telemetry;
    private NormalizedColorSensor sensor1;
    private NormalizedColorSensor sensor2;
    private final float GAIN = 85f; // adjust for brightness

    public CoolColorDetector(HardwareMap map, Telemetry telemetry) {
        sensor1 = map.get(NormalizedColorSensor.class, "sensorColor1");
        sensor2 = map.get(NormalizedColorSensor.class, "sensorColor2");
        sensor1.setGain(GAIN);
        sensor2.setGain(GAIN);
        this.telemetry = telemetry;
    }

    // --- Convert a sensor to ONE PixelColor ---
    @SuppressLint("DefaultLocale")
    // --- Use logic comparing both sensors ---
    PixelColor detectArtifactColor() {
        final double MINIMUM_DISTANCE = 25;         //25mm
        double distance1 = ((DistanceSensor) sensor1).getDistance(DistanceUnit.MM);
        double distance2 = ((DistanceSensor) sensor2).getDistance(DistanceUnit.MM);
        NormalizedColorSensor sensor;

        if (distance1 < MINIMUM_DISTANCE) {
            sensor = sensor1;
        } else if (distance2 < MINIMUM_DISTANCE) {
            sensor = sensor2;
        } else {
            telemetry.addLine(String.format(" %.2f\", %.2f\"", distance1, distance2));
            return PixelColor.NONE;
        }

        NormalizedRGBA colors = sensor.getNormalizedColors();
        float[] hsv = new float[3];
        Color.colorToHSV(colors.toColor(), hsv);
        float hue = hsv[0];

        String colorCube = String.format("<big><big><big><font color='#%06x'>\u25a0</font></big></big></big>",
                colors.toColor() & 0xffffff);

        telemetry.addLine(String.format("Color Detect: %.2fmm, %.2fmm %s, Hue: %.1f",
                distance1, distance2, colorCube, hue));

        if (hue > 165 && hue < 180) {      //range determined from testing
            return PixelColor.GREEN;
        } else if (hue >= 200 && hue <= 225) {     //range determined from testing
            return PixelColor.PURPLE;
        } else {
            //error case use the most likely color
            return PixelColor.PURPLE;
        }
    }

    public void testTelemetry() {

        telemetry.addData("Chosen Position", detectArtifactColor());
        telemetry.update();
    }
}

