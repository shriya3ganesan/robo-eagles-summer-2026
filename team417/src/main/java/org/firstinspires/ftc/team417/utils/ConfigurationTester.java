package org.firstinspires.ftc.team417.utils;

import static java.lang.System.nanoTime;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoController;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import java.util.ArrayList;
import java.util.List;

@TeleOp(name="Configuration Tester", group="Utility")
public class ConfigurationTester extends LinearOpMode {
    ArrayList<String> deviceNames = new ArrayList<>();
    /**
     *
     */
    int menu(String header, List<String> options, int current, boolean topmost) {
        while (isActive()) {
            if (header != null) {
                telemetry.addLine(header);
            }
            for (int i = 0; i < options.size(); i++) {
                String cursor = (i == current) ? ">" : " ";
                telemetry.addLine(cursor + options.get(i));
            }
            telemetry.update();
            if (gamepad1.dpadUpWasPressed()) {
                current--;
                if (current < 0)
                    current = options.size() - 1;
            }
            if (gamepad1.dpadDownWasPressed()) {
                current++;
                if (current == options.size())
                    current = 0;
            }
            if (gamepad1.bWasPressed() && !topmost) // Cancel
                return -1;
            if (gamepad1.aWasPressed()) // Select
                return current;
        }
        return topmost ? 0 : -1;
    }
    boolean isActive() {
        return opModeIsActive();
    }
    boolean notCancelled() {
        telemetry.update();
        return isActive() && !gamepad1.bWasPressed();
    }
    void commonHeader(String deviceName, HardwareDevice device) {
        telemetry.addLine(String.format("Name: %s", deviceName));
        telemetry.addLine(device.getDeviceName());
        telemetry.addLine(device.getConnectionInfo());
        telemetry.addLine(String.format("Version: %d", device.getVersion()));
        telemetry.addLine(String.format("Manufacturer: %s", device.getManufacturer().name()));
    }
    void testMotor(String deviceName) {
        int previousTicks = 0;
        DcMotor motor = (DcMotor) hardwareMap.get(deviceName);
        String encoderStatus = "";
        double previousTime = nanoTime()*1e-9;
        do {
            commonHeader(deviceName, motor);
            telemetry.addLine("Right trigger to spin forward, left to spin backward.");
            double power = gamepad1.right_trigger - gamepad1.left_trigger;
            telemetry.addLine(String.format("Power: %.2f", power));
            motor.setPower(power);
            int currentTicks = motor.getCurrentPosition();
            int deltaTicks = currentTicks - previousTicks;
            double currentTime = nanoTime()*1e-9;
            double deltaTime = currentTime - previousTime;
            if (power != 0) {
                if ((currentTicks == 0) && (deltaTicks == 0)) {
                    encoderStatus = "No encoder detected.";
                } else if (((deltaTicks < 0) && (power > 0)) || ((deltaTicks > 0) && (power < 0))) {
                    encoderStatus = "ERROR: Encoder turns opposite of motor; is motor wiring wrong?";
                } else {
                    encoderStatus = "Encoder detected.";
                }
            }
            if (!encoderStatus.isEmpty()) {
                telemetry.addLine(encoderStatus);
                if ((currentTicks != 0) || (deltaTicks != 0)) {
                    telemetry.addLine(String.format("Position: %d", currentTicks));
                    telemetry.addLine(String.format("Velocity: %.0f", deltaTicks / deltaTime));
                }
            }
            previousTicks = currentTicks;
            previousTime = currentTime;
        } while (notCancelled());
    }
    void testCRServo(String deviceName) {
        CRServo crServo = (CRServo) hardwareMap.get(deviceName);
        do {
            commonHeader(deviceName, crServo);
            telemetry.addLine("Right trigger to spin forward, left to spin backward.");
            double power = gamepad1.right_trigger - gamepad1.left_trigger;
            telemetry.addLine(String.format("Power: %.2f", power));
            crServo.setPower(power);
        } while (notCancelled());
    }
    void testServo(String deviceName) {
        Servo servo = (Servo) hardwareMap.get(deviceName);
        ServoController controller = servo.getController();
        boolean enabled = false;
        do {
            commonHeader(deviceName, servo);
            telemetry.addLine("Right trigger to control amount of rotation.");
            double position = gamepad1.right_trigger - gamepad1.left_trigger;
            telemetry.addLine(String.format("Position: %.2f, Status: %s", position, controller.getPwmStatus().toString()));
            if (position != 0)
                enabled = true; // Don't enable until there's a non-zero input
            if (enabled)
                servo.setPosition(position);
        } while (notCancelled());
    }
    void testDistance(String deviceName) {
        DistanceSensor distance = (DistanceSensor) hardwareMap.get(deviceName);
        do {
            commonHeader(deviceName, distance);
            telemetry.addLine(String.format("Distance CM: %.2f", distance.getDistance(DistanceUnit.CM)));
        } while (notCancelled());
    }
    void testGeneric(String deviceName) {
        HardwareDevice device = hardwareMap.get(deviceName);
        do {
            commonHeader(deviceName, device);
        } while (notCancelled());
    }
    void testIMU(String deviceName) {
        IMU imu = (IMU) hardwareMap.get(deviceName);
        do {
            commonHeader(deviceName, imu);
            YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
            telemetry.addLine(String.format("Yaw: %.2f, Pitch: %.2f, Roll: %.2f (degrees)",
                    angles.getYaw(AngleUnit.DEGREES),
                    angles.getPitch(AngleUnit.DEGREES),
                    angles.getRoll(AngleUnit.DEGREES)));
        } while (notCancelled());
    }
    // @@@ Deprecated:
    void addDeviceNames(Class<?> classType) {
        for (String name: hardwareMap.getAllNames(DcMotor.class)) {
            if (!deviceNames.contains(name))
                deviceNames.add(name);
        }
    }
    @Override
    public void runOpMode() {
        telemetry.addLine("Press START to test the current configuration.");
        telemetry.addLine("");
        telemetry.addLine("All devices set via 'Configure Robot' will be listed. Use touch " +
                "to scroll if they extend below the bottom of the screen.");
        telemetry.addLine("");
        telemetry.addLine("Use A to select, B to cancel, dpad to navigate.");
        telemetry.update();
        for (String name: hardwareMap.getAllNames(DcMotor.class)) {
            if (!deviceNames.contains(name))
                deviceNames.add(name);
        }
        for (String name: hardwareMap.getAllNames(CRServo.class)) {
            if (!deviceNames.contains(name))
                deviceNames.add(name);
        }
        for (String name: hardwareMap.getAllNames(Servo.class)) {
            if (!deviceNames.contains(name))
                deviceNames.add(name);
        }
        for (String name: hardwareMap.getAllNames(IMU.class)) {
            if (!deviceNames.contains(name))
                deviceNames.add(name);
        }
        for (String name: hardwareMap.getAllNames(DistanceSensor.class)) {
            if (!deviceNames.contains(name))
                deviceNames.add(name);
        }
        for (HardwareDevice device: hardwareMap) {
            for (String name: hardwareMap.getAllNames(device.getClass())) {
                if (!deviceNames.contains(name))
                    deviceNames.add(name);
            }
        }
        waitForStart();
        if (gamepad1.getUser() == null) {
            while (isActive() && !gamepad1.aWasPressed()) {
                telemetry.addLine("Please configure Gamepad #1 and press A to continue");
                telemetry.update();
            }
        }

        ArrayList<String> options = new ArrayList<>();
        for (String name: deviceNames) {
            HardwareDevice device = hardwareMap.get(name);
            options.add(String.format("%s: %s", device.getClass().getSimpleName(), name));
        }
        int selection = 0;
        while (isActive()) {
            selection = menu("", options, selection, true);
            String deviceName = deviceNames.get(selection);
            HardwareDevice device = hardwareMap.get(deviceName);
            if (device instanceof DcMotor) {
                testMotor(deviceName);
            } else if (device instanceof CRServo) {
                testCRServo(deviceName);
            } else if (device instanceof Servo) {
                testServo(deviceName);
            } else if (device instanceof DistanceSensor) {
                testDistance(deviceName);
            } else if (device instanceof IMU) {
                testIMU(deviceName);
            } else {
                testGeneric(deviceName);
            }
        }
    }
}
