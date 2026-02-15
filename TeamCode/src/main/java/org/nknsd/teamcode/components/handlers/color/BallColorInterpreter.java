package org.nknsd.teamcode.components.handlers.color;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.frameworks.NKNComponent;


import java.util.Arrays;

public class BallColorInterpreter implements NKNComponent {

    //    private ColorReader colourSensor = new ColorReader("ColorSensor");
    private final int maxSamples;
    private final BallColor[] ballColorSamples;
    private int sampleCounter = 0;
    private final double confidenceThreshold = 0.7;
    private ColorClassifier colorClassifier;

    //timeNext and timeDelay to make sure the ball color happens evenly.
    private final double timeDelaySeconds;
    private double timeNext;

    public BallColorInterpreter(int maxSamples, double timeDelaySeconds) {
        this.maxSamples = maxSamples;
        this.timeDelaySeconds = timeDelaySeconds;
        ballColorSamples = new BallColor[maxSamples];
    }

    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        Arrays.fill(ballColorSamples, BallColor.UNSURE);
        return true;
    }

    @Override
    public void init_loop(ElapsedTime runtime, Telemetry telemetry) {
    }

    @Override
    public void start(ElapsedTime runtime, Telemetry telemetry) {
        timeNext = 0;
    }

    @Override
    public void stop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public String getName() {
        return "BallColorInterpreter";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {
        if (runtime.seconds() >= timeNext) {
            sample();
            timeNext += timeDelaySeconds;
        }
    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
        telemetry.addData("DetectedColor ", getColorGuess());
    }

    public void resetGuess(){
         Arrays.fill(ballColorSamples, BallColor.UNSURE);
    }

    public BallColor getColorGuess() {
        int[] colorCount = new int[4];
        for (int i = 0; i < maxSamples; i += 1) {
            BallColor color = ballColorSamples[i];
            switch (color) {
                case GREEN:
                    colorCount[BallColor.GREEN.ordinal()] += 1;
                    break;
                case PURPLE:
                    colorCount[BallColor.PURPLE.ordinal()] += 1;
                    break;
                case NOTHING:
                    colorCount[BallColor.NOTHING.ordinal()] += 1;
                    break;
                case UNSURE:
                    colorCount[BallColor.UNSURE.ordinal()] += 1;
                    break;
            }
        }
        if (colorCount[BallColor.GREEN.ordinal()] >= maxSamples * confidenceThreshold) {
            return BallColor.GREEN;
        }
        if (colorCount[BallColor.PURPLE.ordinal()] >= maxSamples * confidenceThreshold) {
            return BallColor.PURPLE;
        }
        if (colorCount[BallColor.NOTHING.ordinal()] >= maxSamples * confidenceThreshold) {
            return BallColor.NOTHING;
        }
        return BallColor.UNSURE;
    }

    private void sample() {
        ballColorSamples[sampleCounter] = colorClassifier.classifyColor();
        RobotLog.v("sample counter " + sampleCounter + " color " + ballColorSamples[sampleCounter]);
        sampleCounter += 1;
        if (sampleCounter >= maxSamples) {
            sampleCounter = 0;
        }

    }


    public void link(ColorReader colorReader) {
        this.colorClassifier = new ColorClassifier(colorReader);
    }


}
