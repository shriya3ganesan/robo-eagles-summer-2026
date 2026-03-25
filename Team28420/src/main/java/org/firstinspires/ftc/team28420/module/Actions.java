package org.firstinspires.ftc.team28420.module;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.team28420.config.GyroConf;
import org.firstinspires.ftc.team28420.config.ShooterConf;
import org.firstinspires.ftc.team28420.config.WheelBaseConf;
import org.firstinspires.ftc.team28420.module.shooter.Shooter;
import org.firstinspires.ftc.team28420.types.AprilTag;
import org.firstinspires.ftc.team28420.types.MovementParams;
import org.firstinspires.ftc.team28420.types.PolarVector;
import org.firstinspires.ftc.team28420.types.WheelsRatio;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;
import java.util.List;

public class Actions {

    private final Movement mv;
    private final IMU imu;
    private final Camera cam;
    private final Shooter shooter;
    private final Parking parking;
    private final Turret turret;
    private final Telemetry telemetry;

    private YawPitchRollAngles lastAngles = new YawPitchRollAngles(AngleUnit.RADIANS, 0, 0, 0, 0);

    public Actions(HardwareMap hMap, Telemetry telemetry) throws InterruptedException {
        this.mv = new Movement(hMap);
        this.imu = hMap.get(IMU.class, GyroConf.IMU);
        this.cam = new Camera(hMap);
        this.shooter = new Shooter(hMap, telemetry);
        this.parking = new Parking(hMap);
        this.turret = new Turret(hMap);
        this.telemetry = telemetry;
    }

    public void init() {
        mv.setup();
        imu.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT, RevHubOrientationOnRobot.UsbFacingDirection.UP)));
        shooter.setup();
        parking.setup();
        imu.resetYaw();
    }

    public void updateShooter() {
        shooter.update();
    }

    public void toggleShooterManualControl(boolean active) {
        shooter.toggleManualControl(active);
    }

    public void setDribblerVelocityCoefficient(float k) {
        shooter.setDribblerVelocityCoefficient(k);
    }

    public void prepareForShoot(float k) {
        shooter.setVelocityCoefficient(k);
        shooter.pushBall(k != 0);
    }

    public void afterStart() {
        shooter.afterStart();
    }

    public boolean shoot() {
        return shooter.shoot();
    }

    public void revolverRotate(double deg) {
        shooter.rotateRevolver(deg);
        shooter.toggleManualControl(true);
    }

    public void resetRevolverTicks() {
        shooter.resetRevolverTicks();
    }

    public void alignRevolverToTarget() {
        shooter.alignRevolverToTarget();
    }

    public void setDefaultAutoMotif(String motif) {
        for (char color : motif.toCharArray()) {
            shooter.appendBallToMotif(color);
        }
    }

    public boolean isShootable() {
        return shooter.isShootable();
    }

    public YawPitchRollAngles getRobotAngles() {
        return lastAngles;
    }

    public void updateLastAngles() {
        lastAngles = imu.getRobotYawPitchRollAngles();
    }

    public void move(WheelsRatio<Double> ratio) {
        mv.setMotorsVelocityRatiosWithAcceleration(ratio, WheelBaseConf.MAX_VELOCITY);
    }

    public WheelsRatio<Double> getRatios(MovementParams params) {
        return Movement.vectorToRatios(params);
    }

    public WheelsRatio<Double> getRatios(double x, double y, double rx) {
        return Movement.vectorToRatios(new MovementParams(new PolarVector(x, y), rx));
    }

    public void updateApriltags() {
        cam.updateApriltags();
    }

    public WheelsRatio<Double> getRatiosForApriltag(AprilTag tag, double offsetX, double offsetY) {
        AprilTagDetection detection = cam.getAprilTagDetection(tag);
        MovementParams params = cam.getMovementParamsToPoint(detection, offsetX, offsetY);
        return Movement.vectorToRatios(params);
    }

    public WheelsRatio<Double> getRatiosLookApriltag(AprilTag tag, double offsetX, double offsetY) {
        AprilTagDetection detection = cam.getAprilTagDetection(tag);
        MovementParams params = cam.getMovementParamsToOffset(detection, offsetX, offsetY);
        return Movement.vectorToRatios(params);
    }

    public void park() {
        parking.park();
    }

    public void setMotif() {
        AprilTagDetection detection = cam.getAprilTagDetection(AprilTag.GREEN);
        if (detection != null) {
            ShooterConf.TARGET_MOTIF = AprilTag.getMotif(detection.id);
        }
    }

    public void goTurretToAprilTag(AprilTag tag, double offset) {
        AprilTagDetection detection = cam.getAprilTagDetection(tag);
        if (detection == null) {
            return;
        }
        turret.goAngle(- detection.ftcPose.yaw + offset);
    }

    public void goTurretToGyroAngle(double offset) {
        turret.goAngle(- getRobotAngles().getYaw(AngleUnit.RADIANS) + offset);
    }

    public double getCubic(double axis) {
        return Math.pow(axis, 3);
    }

    public double withDeathzone(double axis, double threshold) {
        return Math.abs(axis) < threshold ? 0 : axis;
    }

    public void log() {
        cam.log(telemetry);
        shooter.log(telemetry);
        telemetry.addData("yaw", getRobotAngles().getYaw(AngleUnit.RADIANS));
    }
}
