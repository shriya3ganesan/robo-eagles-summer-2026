package org.nknsd.teamcode.components.handlers.srs;

import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.utility.SensorGridPoint;

public class PeakFinder {

    private int[][] searchOrder = PeakFinder.GENERATE_SEARCH_ORDER();

    private static int[][] GENERATE_SEARCH_ORDER() {
        int[][] order = new int[64][2];
        for (int i = 0; i < 64; i++) {
            int x, y;
            if (i % 2 == 0) {
                x = 3 - (i % 8) / 2;
            } else {
                x = 3 + ((i % 8) + 1) / 2;
            }
            y = 7 - (i / 8);
            order[i][0] = x;
            order[i][1] = y;
//            RobotLog.v("SO[" + i + "] : " + x + " , " + y);
        }
        return order;
    }

    public SensorGridPoint findClosestPeak(short[][] normalizedDists) {
        for (int[] point : searchOrder) {
            if (normalizedDists[point[0]][point[1]] > 0 && normalizedDists[point[0]][point[1]] < 50) {
                return new SensorGridPoint(3.5 - point[0], 7 - point[1]);
            }
        }
        return null;
    }


}

