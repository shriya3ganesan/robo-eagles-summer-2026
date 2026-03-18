package org.firstinspires.ftc.teamcode.Subsystems;

import com.arcrobotics.ftclib.command.SubsystemBase;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.RobotContainer;

/** Gyro Subsystem */
public class Gyro extends SubsystemBase {

    // robot gyroscope
    private IMU gyro;

    private double YawAngle;
    private double RollAngle;

    // gyro offset
    private double YawAngleOffset;
    private double RollAngleOffset;

    /** Place code here to initialize subsystem */
    public Gyro() {
        // create gyro and initialize it
        YawAngleOffset = 0.0;
        gyro = RobotContainer.ActiveOpMode.hardwareMap.get(IMU.class, "imu");
        gyro.initialize(new IMU.Parameters(new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD)));
        gyro.resetYaw();
        RollAngleOffset = - gyro.getRobotYawPitchRollAngles().getRoll(AngleUnit.DEGREES);
    }

    /** Method called periodically by the scheduler
     * Place any code here you wish to have run periodically */
    @Override
    public void periodic() {
        YawAngle = gyro.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES)+YawAngleOffset;
        RollAngle = gyro.getRobotYawPitchRollAngles().getRoll(AngleUnit.DEGREES) + RollAngleOffset;
    }

    /**
     * Get Yaw angle from gyro
     *
     * @return angle in deg between -180 and 180
     */
    public double getYawAngle() {
        return YawAngle;
    }

    /**
     * Get Roll angle from gyro
     *
     * @return angle in deg between -180 and 180
     */
    public double getRollAngle() {
        return RollAngle;
    }

    /**
     * Resets gyro and offset value
     */
    public void resetYawAngle() {
        setYawAngle(0.0);
    }

    /**
     * sets gyro to provided angle (in deg)
     *
     * @param angle an angle in degrees
     */
    public void setYawAngle(double angle) {

        YawAngleOffset -= getYawAngle() - angle;
    }

}
