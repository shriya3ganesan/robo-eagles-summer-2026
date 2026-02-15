package org.nknsd.teamcode.components.handlers.launch;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.artifact.ArtifactSystem;
import org.nknsd.teamcode.components.handlers.color.BallColor;
import org.nknsd.teamcode.components.handlers.vision.ID;
import org.nknsd.teamcode.components.handlers.vision.TargetingSystem;
import org.nknsd.teamcode.components.utility.RobotVersion;
import org.nknsd.teamcode.frameworks.NKNComponent;

public class FiringSystem implements NKNComponent {

    private final int WHEELSPEED_CONFIDENCE = 4;

    private LaunchSystem launchSystem;
    private TargetingSystem targetingSystem;
    private ArtifactSystem artifactSystem;

    private double lastTime;
    private boolean autoLocked = false;
    private boolean isFiring = false;
    private ID color;
    private ID pattern = ID.NONE;


    public void setPattern(ID pattern) {
        this.pattern = pattern;
    }

    public ID getPattern() {
        return pattern;
    }

    public void lockTarget(boolean enable) {
        autoLocked = enable;
        targetingSystem.enableAutoTargeting(enable);
    }

    public void setManualDistance(double dist) {
        launchSystem.setDistance(dist);
    }

    public boolean isReady() {
        boolean ready;
        if (autoLocked) {
            ready = launchSystem.isReady() && artifactSystem.isReady() && targetingSystem.targetAcquired();
        } else {
            ready = launchSystem.isReady() && artifactSystem.isReady();
        }
//        RobotLog.v("firing system is ready" + ready);
        return ready;
//        if (autoLocked) {
//            return launchSystem.isReady() && artifactSystem.isReady() && targetingSystem.targetAcquired();
//        } else {
//            return launchSystem.isReady() && artifactSystem.isReady();
//        }
    }

    public boolean fireGreen() {
        if (isReady()) {
            return artifactSystem.launchColor(BallColor.GREEN);
        }
        return false;
    }

    public boolean firePurple() {
        if (isReady()) {
            return artifactSystem.launchColor(BallColor.PURPLE);
        }
        return false;
    }

    public void fireAll() {

        if (isReady()) {
            if (pattern == ID.NONE) {
                artifactSystem.launchAll();
            } else {
                BallColor[] patternColors;
                switch (pattern.ordinal()) {
                    case 0:
                        patternColors = new BallColor[]{BallColor.PURPLE, BallColor.GREEN, BallColor.PURPLE};
                        break;
                    case 1:
                        patternColors = new BallColor[]{BallColor.PURPLE, BallColor.PURPLE, BallColor.GREEN};
                        break;
                    case 2:
                        patternColors = new BallColor[]{BallColor.GREEN, BallColor.PURPLE, BallColor.PURPLE};
                        break;
                    default:
                        artifactSystem.launchAll();
                        isFiring = true;
                        return;
                }
                artifactSystem.launchAll(patternColors);
            }
        }
    }

    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        return true;
    }

    @Override
    public void init_loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void start(ElapsedTime runtime, Telemetry telemetry) {
        color = RobotVersion.getRobotAlliance();
        targetingSystem.setTargetingColor(RobotVersion.getRobotAlliance());

    }

    @Override
    public void stop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {
        if (runtime.milliseconds() - lastTime > RobotVersion.INSTANCE.visionLoopIntervalMS) {
            lastTime = runtime.milliseconds();

            if (isFiring && artifactSystem.isReady()) {
                isFiring = false;
            }

            if (targetingSystem.targetVisible() && !isFiring) {
                launchSystem.setDistance(targetingSystem.getDistance());
//                telemetry.addData("setting distance", targetingSystem.getDistance());
            }
        }
    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
        telemetry.addData("alliance color", color.name());
    }

    public void link(LaunchSystem launchSystem, TargetingSystem targetingSystem, ArtifactSystem artifactSystem) {
        this.launchSystem = launchSystem;
        this.targetingSystem = targetingSystem;
        this.artifactSystem = artifactSystem;
    }

}
