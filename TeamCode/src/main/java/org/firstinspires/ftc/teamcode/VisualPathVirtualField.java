package org.firstinspires.ftc.teamcode;

import android.content.res.AssetManager;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Dictionary;
import java.util.Hashtable;

/**
 * VisualPathVirtualField
 *
 * Virtualizes the robot's position on a 2D field. Supports tracking robot 
 * coordinates, field boundaries, and stationary field elements imported 
 * from configuration files.
 *
 * Usage:
 * Initialize with field dimensions and starting coordinates. Use 
 * shiftRobotPosition() to update location and getRobotPosition() to 
 * retrieve current coordinates.
 */
public class VisualPathVirtualField {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private double[] fieldSize;
    private double[] robotPosition;
    private Dictionary<double[], double[]> elements;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * @param fieldSize The size of the field as [length, width].
     * @param robotPosition The initial position of the robot as [x, y].
     */
    public VisualPathVirtualField(double[] fieldSize, double[] robotPosition) {
        this.fieldSize = fieldSize;
        this.robotPosition = robotPosition;
    }

    /**
     * @param fieldSize The size of the field as [length, width].
     * @param robotPosition The initial position of the robot as [x, y].
     * @param elements Dictionary of elements where keys are positions [x, y] 
     *                 and values are dimensions [length, width].
     */
    public VisualPathVirtualField(double[] fieldSize, double[] robotPosition, Dictionary<double[], double[]> elements) {
        this.fieldSize = fieldSize;
        this.robotPosition = robotPosition;
        this.elements = elements;
    }

    // -------------------------------------------------------------------------
    // Manipulators
    // -------------------------------------------------------------------------

    /**
     * Imports field element data from 'elements.json' in the assets folder.
     *
     * @throws IOException If the file cannot be read or is malformed.
     */
    public void readElementJSON() throws IOException {
        AssetManager assetManager = AppUtil.getInstance().getApplication().getAssets();

        StringBuilder raw = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(assetManager.open("elements.json"))
        )) {
            String line;
            while ((line = reader.readLine()) != null) {
                raw.append(line.trim());
            }
        }

        Dictionary<double[], double[]> elements = new Hashtable<>();

        String content = raw.toString().trim();

        content = content.substring(1, content.length() - 1);

        int depth = 0;
        int start = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '{') {
                if (depth++ == 0) start = i;
            } else if (c == '}') {
                if (--depth == 0) {
                    String obj = content.substring(start, i + 1);

                    double[] position = extractArray(obj, "position");
                    double[] size = extractArray(obj, "size");

                    if (position == null || size == null) {
                        throw new IOException("Malformed element: " + obj);
                    }

                    elements.put(position, size);
                }
            }
        }

        this.elements = elements;
    }

    /**
     * Sets the robot's current position on the field.
     *
     * @param robotPosition The new position as [x, y].
     */
    public void setRobotPosition(double[] robotPosition) {
        this.robotPosition = robotPosition;
    }

    /**
     * Shifts the robot's current position by the specified X and Y deltas.
     *
     * @param xShift Displacement along the X axis.
     * @param yShift Displacement along the Y axis.
     */
    public void shiftRobotPosition(double xShift, double yShift) {
        this.robotPosition[0] += xShift;
        this.robotPosition[1] += yShift;
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    /**
     * @return The current robot position as [x, y].
     */
    public double[] getRobotPosition() {
        return this.robotPosition;
    }

    /**
     * @return The field size as [length, width].
     */
    public double[] getFieldSize() {
        return fieldSize;
    }

    /**
     * @return All field elements as a Dictionary mapping positions to dimensions.
     */
    public Dictionary<double[], double[]> getElements() {
        return elements;
    }

    // -------------------------------------------------------------------------
    // Internal Logic
    // -------------------------------------------------------------------------

    /**
     * Helper to extract a double array from a JSON-like string object.
     */
    private static double[] extractArray(String obj, String key) {
        String marker = "\"" + key + "\"";
        int keyIdx = obj.indexOf(marker);
        if (keyIdx == -1) return null;

        int arrStart = obj.indexOf('[', keyIdx);
        int arrEnd = obj.indexOf(']', arrStart);
        if (arrStart == -1 || arrEnd == -1) return null;

        String[] parts = obj.substring(arrStart + 1, arrEnd).split(",");
        double[] result = new double[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                result[i] = Double.parseDouble(parts[i].trim());
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return result;
    }
}