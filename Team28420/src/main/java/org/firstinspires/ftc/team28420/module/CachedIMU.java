package org.firstinspires.ftc.team28420.module;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

public class CachedIMU {

    public static class CachedIMUConfig {
        public final static RevHubOrientationOnRobot.LogoFacingDirection
                logoFacingDirection = RevHubOrientationOnRobot.LogoFacingDirection.RIGHT;
        public final static RevHubOrientationOnRobot.UsbFacingDirection
                usbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.UP;
    }

    private final IMU imu;
    private YawPitchRollAngles lastAngles;

    public CachedIMU(HardwareMap hMap) {
        this.imu = hMap.get(IMU.class, "imu");
    }

    public void setup() {
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(CachedIMUConfig.logoFacingDirection, CachedIMUConfig.usbFacingDirection)));
        this.lastAngles = imu.getRobotYawPitchRollAngles();
    }

    public YawPitchRollAngles getRobotAngles() {
        return lastAngles;
    }

    public void update() {
        this.lastAngles = imu.getRobotYawPitchRollAngles();
    }

    public void log(Telemetry telemetry) {
        telemetry.addData("control hub pos", lastAngles.toString());
    }
}
