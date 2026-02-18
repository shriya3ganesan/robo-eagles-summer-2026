package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class otos {
    SparkFunOTOS myOtos;
    SparkFunOTOS.Pose2D position;


    public otos(HardwareMap hardwareMap) {
        myOtos = hardwareMap.get(SparkFunOTOS.class, "otos");
        configureOtos();
    }

    public otos(HardwareMap hardwareMap, double x, double y, double h) {
        myOtos = hardwareMap.get(SparkFunOTOS.class, "otos");
        configureOtos();
        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(x, y, h);
        myOtos.setPosition(currentPosition);
        position = currentPosition;
    }

    public SparkFunOTOS.Pose2D getVelocity(){
        return myOtos.getVelocity();
    }


    public void updateOtos(){
        position = myOtos.getPosition();
    }


    private void configureOtos () {
        // myOtos.setLinearUnit(DistanceUnit.METER);
        myOtos.setLinearUnit(DistanceUnit.INCH);
        // myOtos.setAngularUnit(AngleUnit.RADIANS);
        myOtos.setAngularUnit(AngleUnit.DEGREES);


        SparkFunOTOS.Pose2D offset = new SparkFunOTOS.Pose2D(0, -0.75, 0);
        myOtos.setOffset(offset);


        myOtos.setLinearScalar(0.9634);
        myOtos.setAngularScalar(0.995);

        myOtos.calibrateImu();


        myOtos.resetTracking();


        // Get the hardware and firmware version
        SparkFunOTOS.Version hwVersion = new SparkFunOTOS.Version();
        SparkFunOTOS.Version fwVersion = new SparkFunOTOS.Version();
        myOtos.getVersionInfo(hwVersion, fwVersion);


    }

    public void setOtosPosition(double x, double y, double h) {
        SparkFunOTOS.Pose2D currentPosition = new SparkFunOTOS.Pose2D(x, y, h);
        myOtos.setPosition(currentPosition);
    }
    public SparkFunOTOS.Pose2D getPositionotos(){
        return position;
    }

    public double getX(){
        return position.x;
    }
    public double getY(){
        return position.y;
    }
    public double getH(){
        return position.h;
    }


}
