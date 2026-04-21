package org.firstinspires.ftc.team28420.module;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.team28420.types.Position;

public class Odometry {

    public static class OdometryConfig {
        public static double OFFSET_X = 24.871;
        public static double OFFSET_Y = -43.235;
    }

    private final GoBildaPinpointDriver pinpoint;

    public Odometry(HardwareMap hMap) {
        pinpoint = hMap.get(GoBildaPinpointDriver.class, "pinpoint");
    }

    public void setup() {
        pinpoint.setOffsets(OdometryConfig.OFFSET_X, OdometryConfig.OFFSET_Y, DistanceUnit.MM);
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,
                GoBildaPinpointDriver.EncoderDirection.FORWARD
        );
        pinpoint.resetPosAndIMU();
    }

    public void update() {
        pinpoint.update();
    }

    public double getRobotHeading() {
        return pinpoint.getHeading(AngleUnit.RADIANS);
    }

    public Position getRobotPosition() {
        return new Position(pinpoint.getPosition());
    }

    public void log(Telemetry telemetry) {
        telemetry.addData("odometry x", pinpoint.getPosition().getX(DistanceUnit.CM));
        telemetry.addData("odometry y", pinpoint.getPosition().getY(DistanceUnit.CM));
        telemetry.addData("odometry heading", pinpoint.getHeading(AngleUnit.DEGREES));
    }
}
