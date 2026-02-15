package org.nknsd.teamcode.components.handlers.srs;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.ReadWriteFile;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.nknsd.teamcode.components.utility.DoublePoint;
import org.nknsd.teamcode.frameworks.NKNComponent;

import java.io.File;

public class SRSHubHandler implements NKNComponent {
    private SRSHub hub;
    private static final short[][] NULLARRAY = new short[8][8];
    private short[][] distArray;
    private static short[][] distMeans = new short[8][8];
    private static boolean meansFound = false;
    private double previousSampleTime = 0;
    private final double SAMPLE_DELAY = 100;
    private double lastRestartTime = 0;

    public short[][] getDistances() {
        hub.update();
        SRSHub.VL53L5CX distData = hub.getI2CDevice(1, SRSHub.VL53L5CX.class);

        distArray = new short[8][8];

        boolean dataFound = false;

        for (int i = 0; i < 64; i++) {
            distArray[7 - i / 8][i % 8] = distData.distances[i];
            dataFound = dataFound || distData.distances[i] != 0;
        }

        if (!dataFound) {
            return NULLARRAY;
        }

        return distArray;
    }

    private void saveArrayToFile(short[][] array, String fileName) {
        StringBuilder arrayString = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                arrayString.append(array[x][y]);
                arrayString.append(" ");
            }
            arrayString.append("\n");
        }

        RobotLog.v("Save:"+arrayString.toString());

        File srsFile = AppUtil.getInstance().getSettingsFile(fileName);

        ReadWriteFile.writeFile(srsFile, arrayString.toString());
    }

    private short[][] readArrayFromFile(String fileName) {
        File srsFile = AppUtil.getInstance().getSettingsFile(fileName);
        String arrayString = ReadWriteFile.readFile(srsFile);

        RobotLog.v("Read:"+arrayString);

        String[] rows = arrayString.split("\n");

        short[][] array = new short[8][8];
        for (int y = 0; y < 8; y++) {
            String[] rowElements = rows[y].split(" ");

            for (int x = 0; x < 8; x++) {
                array[x][y] = Short.parseShort(rowElements[x]);
            }
        }

        return array;
    }

    public void saveMean(String fileName) {
        saveArrayToFile(distMeans, fileName);
    }

    public void getMeans(String fileName) {
        distMeans = readArrayFromFile(fileName);
        meansFound = true;
    }

    private void updateMeanWithSample(short[][] currentDists, int weight) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (distMeans[x][y] == 0) {
                    distMeans[x][y] = currentDists[x][y];
                } else {
                    distMeans[x][y] = (short) (distMeans[x][y]*((float)(weight-1)/(float)weight) + currentDists[x][y]*(1.0/(float)weight));
                }
            }
        }
    }

    public void updateMean(int weight){
        updateMeanWithSample(getDistances(), weight);
    }

    public short[][] getNormalizedDists() {
        getDistances();
        short[][] normalDists = new short[8][8];
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                normalDists[x][y] = (short) ((distArray[x][y] - distMeans[x][y]) * -1);
            }
        }
        return normalDists;
    }

    public DoublePoint ballLocation() {
        DoublePoint thePlaceOfBallResting = new DoublePoint(10, 10);
        short[][] normalDists;
        normalDists = getNormalizedDists();
        double highestPoint = -30;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                //greater than because the sensor reads negative values as high
                if (highestPoint > normalDists[x][y]) {
                    highestPoint = normalDists[x][y];

                    thePlaceOfBallResting.setX(x - 3.5);
                    thePlaceOfBallResting.setY(y - 3.5);
                }
            }
        }
        return thePlaceOfBallResting;
    }

    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        // All ports default to NONE, buses default to empty
        SRSHub.Config config = new SRSHub.Config();
        config.addI2CDevice(1, new SRSHub.VL53L5CX(SRSHub.VL53L5CX.Resolution.GRID_8x8));

        hub = hardwareMap.get(
                SRSHub.class,
                "srsHub"
        );
        hub.init(config);
        return true;
    }

    private boolean nullDists = true;

    @Override
    public void init_loop(ElapsedTime runtime, Telemetry telemetry) {
        telemetry.addLine("Waiting for SRSHub");
        if (!meansFound) {
            telemetry.addLine("No mean data loaded... uh oh");
        }
        if (hub.ready()) {
            if (runtime.milliseconds() >= previousSampleTime + SAMPLE_DELAY) {
                previousSampleTime = runtime.milliseconds();
                nullDists = getDistances() == NULLARRAY;
                if (nullDists) {
                    RobotLog.v("No distance array found");
                    return;
                }
            }
        }
        if (!nullDists && hub.ready()) {
            telemetry.addLine("SRSHub Ready!");
        }
        if (runtime.milliseconds() - lastRestartTime > 10000 && nullDists) {
            SRSHub.Config config = new SRSHub.Config();
            config.addI2CDevice(1, new SRSHub.VL53L5CX(SRSHub.VL53L5CX.Resolution.GRID_8x8));
            hub.init(config);
            lastRestartTime = runtime.milliseconds();
            RobotLog.v("RESTARTING");
        }
    }

    @Override
    public void start(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void stop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public String getName() {
        return "SRSHub";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void doTelemetry(Telemetry telemetry) {

        if (getNormalizedDists() != null) {

            short[][] printVals;
            printVals = getNormalizedDists();
            for (int y = 0; y < 8; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < 8; x++) {
                    sb.append(normalizeIntCharacterLength(printVals[x][y], 3));
                    sb.append(", ");
                }
                telemetry.addData("row: " + y, sb.toString());
            }
        }

    }

    private String normalizeIntCharacterLength(int number, int amountOfCharacters) {
        StringBuilder out = new StringBuilder(String.valueOf(number));

        while (out.length() < amountOfCharacters) {
            out.insert(0, " ");
        }

        return out.toString();
    }

    public short[][] getCurrentMean() {
        return distMeans;
    }
}

