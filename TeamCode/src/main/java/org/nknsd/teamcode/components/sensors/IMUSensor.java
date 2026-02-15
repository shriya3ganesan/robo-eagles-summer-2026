package org.nknsd.teamcode.components.sensors;

import com.qualcomm.hardware.adafruit.AdafruitBNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.frameworks.NKNComponent;

public class IMUSensor implements NKNComponent {
    private AdafruitBNO055IMU imu;
    private HardwareMap hardwareMap;
    private double xOffset;
    private double yOffset;

//

//    private final RevHubOrientationOnRobot orientationOnRobot;

    public IMUSensor(/*RevHubOrientationOnRobot orientationOnRobot*/) {
//        this.orientationOnRobot = orientationOnRobot;
    }

    private void initIMU(){
        imu = hardwareMap.get(AdafruitBNO055IMU.class, "imu");
        imu.initialize(new AdafruitBNO055IMU.Parameters());
    }

    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        this.hardwareMap = hardwareMap;
        initIMU();
        if (imu == null)
            return false;

        return true;
    }

    @Override
    public void init_loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void start(ElapsedTime runtime, Telemetry telemetry) {
        xOffset = imu.getGravity().xAccel;
        yOffset = imu.getGravity().yAccel;
    }

    public void relocatilizeIMUinGame(){
        xOffset = imu.getGravity().xAccel;
        yOffset = imu.getGravity().yAccel;
    }

    @Override
    public void stop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public String getName() {
        return "IMUComponent";
    }

    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {

    }

    public double getPitch() {
//        return -imu.getRobotYawPitchRollAngles().getYaw();
        return imu.getGravity().xAccel - xOffset;
    }
    public double getRoll() {
//        return imu.getRobotYawPitchRollAngles().getPitch();
        return imu.getGravity().yAccel - yOffset;
    }
//    public double getRoll() {
////        return imu.getRobotYawPitchRollAngles().getRoll();
//        return imu.getGravity().zAccel - 9.49;
//    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
//        telemetry.addData("Yaw", -imu.getRobotYawPitchRollAngles().getYaw());
//        telemetry.addData("Pitch", imu.getRobotYawPitchRollAngles().getPitch());
//        telemetry.addData("Roll", imu.getRobotYawPitchRollAngles().getRoll());
        telemetry.addData("pitch", getPitch());
        telemetry.addData("roll", getRoll());

    }

    public void resetIMU() {
    }
}