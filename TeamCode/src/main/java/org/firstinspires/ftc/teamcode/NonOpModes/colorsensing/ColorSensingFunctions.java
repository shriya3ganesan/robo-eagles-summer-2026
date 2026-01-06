package org.firstinspires.ftc.teamcode.NonOpModes.colorsensing;

import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;

import android.graphics.Color;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

import org.firstinspires.ftc.teamcode.Util.Enum.Balls;
@Config
public class ColorSensingFunctions {
    public static double plowbounds = 161;
    public static double pupbounds = 255;
    public static double glowbounds = 140;
    public static double gupbounds = 160;

    public static Balls colorDetection (NormalizedColorSensor colorSensor1, NormalizedColorSensor colorSensor2) {
        float[] hsv1 = new float[3];
        float[] hsv2 = new float[3];
        // Sensor 1
        NormalizedRGBA colors1 = colorSensor1.getNormalizedColors();
        Color.colorToHSV(colors1.toColor(), hsv1);
        float hue1 = hsv1[0];

        // Sensor 2
        NormalizedRGBA colors2 = colorSensor2.getNormalizedColors();
        Color.colorToHSV(colors2.toColor(), hsv2);
        float hue2 = hsv2[0];

        double avghue = ((hue1 + hue2) / 2);

        if (avghue >= glowbounds && avghue <= gupbounds) {
            return green;

        } else if (avghue >= plowbounds && avghue <= pupbounds) {
            return purple;
        } else{
            return unknown;
        }

        // Average the hue values

    }
}
