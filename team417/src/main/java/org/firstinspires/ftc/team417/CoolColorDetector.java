package org.firstinspires.ftc.team417;

import com.qualcomm.robotcore.hardware.HardwareMap;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import android.graphics.Color;

import org.firstinspires.ftc.robotcore.external.Telemetry;
//import com.qualcomm.robotcore.hardware.ColorSensor;
//import com.qualcomm.robotcore.hardware.NormalizedRGBA;
//import android.graphics.Color;
//import com.qualcomm.robotcore.hardware.SwitchableLight;

enum PixelColor {
    GREEN,
    PURPLE,
    NONE
}
 public class CoolColorDetector {
    private ColorSensor sensor1;
    private ColorSensor sensor2;
    private float gain = 4f; // adjust for brightness
    private float[] hsv = new float[3];
    public CoolColorDetector(HardwareMap map) {
        sensor1 = map.get(ColorSensor.class, "cs1");
        //sensor2 = map.get(ColorSensor.class, "sensor2");
    }
    // --- Convert a sensor to ONE PixelColor ---
    private PixelColor detectSingle(ColorSensor sensor1) {
        // Get raw values
        ((NormalizedColorSensor)sensor1).setGain(gain);
        //Just tried something new with the setGain
        float r = sensor1.red();
        float g = sensor1.green();
        float b = sensor1.blue();
        Color.RGBToHSV((int)r, (int)g, (int)b, hsv);
        float hue = hsv[0];
        // GREEN Range: 145 - 165
        if (hue >= 145 && hue <= 185) {
            return PixelColor.GREEN;
        }
        // PURPLE Range: 215 - 245
        else if (hue >= 215 && hue <= 245) {
            return PixelColor.PURPLE;
        }
        else {
            return PixelColor.NONE;
        }

    }

    // --- Use logic comparing both sensors ---
     /*PixelColor detectPixelPosition() {
        PixelColor s1 = detectSingle(sensor1);
        PixelColor s2 = detectSingle(sensor2);
        // Rule 1: If both detect something different → return sensor2
        if (s1 == s2) {
            return s1;
        }
        // Rule 2: If sensor1 detects color and sensor2 = NONE → sensor1 wins
        if ((s1 == PixelColor.GREEN || s1 == PixelColor.PURPLE) && s2 == PixelColor.NONE) {
            return s1;
        }
        // Rule 3: If sensor2 detects color and sensor1 = NONE → sensor2 wins
        if ((s2 == PixelColor.GREEN || s2 == PixelColor.PURPLE) && s1 == PixelColor.NONE) {
            return s2;
        }
        else {
            // Otherwise no decision → return none
            return PixelColor.NONE;
        }
    }*/
    public void showTelemetry(Telemetry telemetry) {
        telemetry.addData("Sensor 1", detectSingle(sensor1));
        //telemetry.addData("Sensor 1");
        //telemetry.addData("Sensor 2", detectSingle(sensor2));
        //telemetry.addData("Chosen Position", detectPixelPosition());
        telemetry.update();
    }
}

