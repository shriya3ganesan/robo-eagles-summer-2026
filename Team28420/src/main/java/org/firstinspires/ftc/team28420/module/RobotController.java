package org.firstinspires.ftc.team28420.module;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

public class RobotController {

    private final List<LynxModule> hubs;

    public RobotController(HardwareMap hMap) {
        hubs = hMap.getAll(LynxModule.class);
    }

    public void setManualCachingMode() {
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
    }

    public void clearCache() {
        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }
    }
}
