package org.firstinspires.ftc.team28420.module;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.team28420.module.shooter.Shooter;
import org.firstinspires.ftc.team28420.types.AprilTag;
import org.firstinspires.ftc.team28420.types.MovementParams;
import org.firstinspires.ftc.team28420.types.PolarVector;
import org.firstinspires.ftc.team28420.types.Position;
import org.firstinspires.ftc.team28420.types.WheelsRatio;
import org.firstinspires.ftc.team28420.util.Config;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

import java.util.ArrayList;
import java.util.List;

public class Actions {

    private final Movement mv;
    private final IMU imu;
    private final Camera cam;
    private final Shooter shooter;
    private final List<Servo> parkingServo = new ArrayList<>();

    public boolean scanAllowed = true;

    private double cachedHeading = 0.0;


    public Actions(Movement mv, IMU imu, Camera cam, Shooter shooter, Servo parkingServo1, Servo parkingServo2) {
        this.mv = mv;
        this.imu = imu;
        this.cam = cam;
        this.shooter = shooter;
        this.parkingServo.add(parkingServo1);
        this.parkingServo.add(parkingServo2);
    }

    public void init() {
        mv.setup();
        imu.initialize(
                new IMU.Parameters(
                        new RevHubOrientationOnRobot(
                                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                                RevHubOrientationOnRobot.UsbFacingDirection.UP
                        )
                )
        );
        shooter.setup();
        parkingServo.get(0).setPosition(Config.ServoConf.PARKING_SERVO_START_POS_1);
        parkingServo.get(1).setPosition(Config.ServoConf.PARKING_SERVO_START_POS_2);
    }

    public void updateHeading() {
        cachedHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
    }
    public void updateShooter() {
        shooter.update(scanAllowed);
    }

    public void toggleShooterManualControl(boolean active) {
        shooter.toggleManualControl(active);
    }
    public void setDribblerVelocityCoefficient(float k) {
        shooter.setDribblerVelocityCoefficient(k);
    }

    public void setShooterVelocityCoefficient(float k) {
        shooter.setVelocityCoefficient(k);
    }

    public void afterStart() {
        shooter.afterStart();
    }

    public boolean shoot() {
        return shooter.shoot();
    }

    public void revolverRotate(double deg){
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
        for(char color : motif.toCharArray()) {
            shooter.appendBallToMotif(color);
        }
    }
    public boolean isShootable() {
        return shooter.isShootable();
    }
    public void move(WheelsRatio<Double> ratio) {
        mv.setMotorsVelocityRatiosWithAcceleration(ratio, Config.WheelBaseConf.MAX_VELOCITY);
    }

    public WheelsRatio<Double> getRatios(double axisX, double axisY, double axisR) {
        return Movement.vectorToRatios(
                PolarVector.fromPos(new Position(axisX, axisY)), axisR);
    }

    public WheelsRatio<Double> getRatiosForApriltag(AprilTag tag, double offsetX, double offsetY) {
        cam.updateApriltags();
        AprilTagDetection detection = cam.getAprilTagDetection(tag);
        MovementParams params = cam.getMovementParamsToPoint(detection, offsetX, offsetY);
        return Movement.vectorToRatios(params.getMoveVector(), params.getTurnAbs());
    }

    public WheelsRatio<Double> getRatiosLookApriltag(AprilTag tag, double offsetX, double offsetY) {
        cam.updateApriltags();
        AprilTagDetection detection = cam.getAprilTagDetection(tag);
        MovementParams params = cam.getMovementParamsToOffset(detection, offsetX, offsetY);
        return Movement.vectorToRatios(params.getMoveVector(), params.getTurnAbs());
    }

    public void park() {
        parkingServo.get(0).setPosition(Config.ServoConf.PARKING_SERVO_STOP_POS_1);
        parkingServo.get(1).setPosition(Config.ServoConf.PARKING_SERVO_STOP_POS_2);
    }

    public void setMotif() {
        cam.updateApriltags();

        AprilTagDetection detection = cam.getAprilTagDetection(AprilTag.GREEN);
        if (detection != null) {
            Config.ShooterConf.TARGET_MOTIF = AprilTag.getMotif(detection.id);
        }
    }

    public double getCubic(double axis) {
        return Math.pow(axis, 3);
    }

    public double withDeathzone(double axis, double threshold) {
        return Math.abs(axis) < threshold ? 0 : axis;
    }

    public void log() {
        cam.log(Config.Etc.telemetry);
        shooter.log(Config.Etc.telemetry);
    }
}
