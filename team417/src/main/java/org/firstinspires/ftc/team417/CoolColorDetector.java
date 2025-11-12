package org.firstinspires.ftc.team417;

import android.annotation.SuppressLint;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

 public class CoolColorDetector {
     Telemetry telemetry;
    private ColorSensor sensor1;
    private ColorSensor sensor2;
    private float gain = 50f; // adjust for brightness
    private float[] hsv = new float[3];
    public CoolColorDetector(HardwareMap map, Telemetry telemetry) {
        sensor1 = map.get(ColorSensor.class, "cs1");
        sensor2 = map.get(ColorSensor.class, "cs2");
        this.telemetry = telemetry;
    }

    // --- Convert a sensor to ONE PixelColor ---
    @SuppressLint("DefaultLocale")
    private PixelColor detectSingle(ColorSensor sensor) {
        // Get raw values
        ((NormalizedColorSensor)sensor).setGain(gain);
        //Just tried something new with the setGain
        float r = sensor.red();
        float g = sensor.green();
        float b = sensor.blue();
        hsv = rgbToHsv((int)r, (int)g, (int)b);

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

     public static float[] rgbToHsv(int r, int g, int b) {
         float[] hsv = new float[3];

         // Normalize R, G, B values to the range 0-1
         float red = r / 255.0f;
         float green = g / 255.0f;
         float blue = b / 255.0f;

         float cmax = Math.max(red, Math.max(green, blue)); // Maximum of R, G, B
         float cmin = Math.min(red, Math.min(green, blue)); // Minimum of R, G, B
         float delta = cmax - cmin; // Delta of max and min

         // Calculate Hue (H)
         if (delta == 0) {
             hsv[0] = 0; // Hue is undefined for achromatic colors (grays)
         } else if (cmax == red) {
             hsv[0] = (60 * ((green - blue) / delta) + 360) % 360;
         } else if (cmax == green) {
             hsv[0] = (60 * ((blue - red) / delta) + 120);
         } else { // cmax == blue
             hsv[0] = (60 * ((red - green) / delta) + 240);
         }

         // Calculate Saturation (S)
         if (cmax == 0) {
             hsv[1] = 0; // Saturation is 0 for black
         } else {
             hsv[1] = delta / cmax;
         }

         // Calculate Value (V)
         hsv[2] = cmax;

         return hsv;
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

