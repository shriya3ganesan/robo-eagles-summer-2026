package org.firstinspires.ftc.team417;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import android.app.Activity;
import android.graphics.Color;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.SwitchableLight;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.Telemetry;

 public class CoolColorDetector {
     Telemetry telemetry;
    private NormalizedColorSensor sensor1;
    private NormalizedColorSensor sensor2;
    private float gain = 50f; // adjust for brightness
    private float[] hsv = new float[3];
    public CoolColorDetector(HardwareMap map, Telemetry telemetry) {
        sensor1 = map.get(NormalizedColorSensor.class, "cs1");
        sensor2 = map.get(NormalizedColorSensor.class, "cs2");
        this.telemetry = telemetry;
    }

    // --- Convert a sensor to ONE PixelColor ---
    @SuppressLint("DefaultLocale")
    private PixelColor detectSingle(NormalizedColorSensor sensor) {
        // Get raw values
        sensor.setGain(gain);
        NormalizedRGBA colors = sensor.getNormalizedColors();
        Color.colorToHSV(colors.toColor(), hsv);

        telemetry.addData("HSV", String.format("{%f, %f, %f}", hsv[0], hsv[1], hsv[2]));
        float hue = hsv[0];
        float value = hsv[2];
        //
        // GREEN Range: 145 - 165
        if(value < 0.45) {
            return PixelColor.NONE;
        }
        else if (hue >= 10 && hue <= 190) {
            return PixelColor.GREEN;
        }
        // PURPLE Range: 215 - 245
        else{
            return PixelColor.PURPLE;
        }
    }



    // --- Use logic comparing both sensors ---
     PixelColor detectPixelPosition() {
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
    }
    public void showTelemetry() {
        telemetry.addData("Sensor 1", detectSingle(sensor1));
        telemetry.addData("Sensor 2", detectSingle(sensor2));
        telemetry.addData("Chosen Position", detectPixelPosition());
        telemetry.update();
    }
}

