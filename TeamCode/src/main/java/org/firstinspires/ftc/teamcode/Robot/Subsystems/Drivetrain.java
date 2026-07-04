package org.firstinspires.ftc.teamcode.Robot.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.SWEEP.Classes.Subsystem;
//TODO: Finish commenting the class
public class Drivetrain implements Subsystem {
    private DcMotor FLMotor;
    private DcMotor FRMotor;
    private DcMotor BLMotor;
    private DcMotor BRMotor;
    private final DcMotor[] motors;
    private boolean fieldCentricDrivingEnabled = false;
    private double X = 0,Y = 0,Yaw = 0;
    private double robotYaw = 0; // From localization values, used to transform values to field centric
    private Telemetry telemetry;
    private boolean telemetryEnabled = false;
    private boolean disabled = false;

    public Drivetrain(HardwareMap hardwareMap, Telemetry telemetry, boolean telemetryEnabled){
        FLMotor = hardwareMap.get(DcMotor.class, "FLMotor");
        FRMotor = hardwareMap.get(DcMotor.class, "FRMotor");
        BLMotor = hardwareMap.get(DcMotor.class, "BLMotor");
        BRMotor = hardwareMap.get(DcMotor.class, "BRMotor");
        //TODO: check and standardize motor directions
        motors = new DcMotor[]{FLMotor,FRMotor,BLMotor,BRMotor};
        for (DcMotor motor: motors){
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        this.telemetry = telemetry;
        this.telemetryEnabled = telemetryEnabled;
    }
    @Override
    public void update() {
        // To the drive power every iteration
        if (fieldCentricDrivingEnabled){
            double[] fieldVector = calculateFieldCentric(robotYaw);
            X = fieldVector[0];
            Y = fieldVector[1];
        }
        setDrivePower(X,Y,Yaw);
        if (telemetryEnabled){
            postDrivetrainTelemetry();
        }
    }
    public void updateMovementVector(double X, double Y, double Yaw){
        this.X = X;
        this.Y = Y;
        this.Yaw = Yaw;
    }
    public void updateMovementVector(double X, double Y, double Yaw, double RobotYaw){
        updateMovementVector(X,Y,Yaw);
        this.robotYaw = robotYaw;
    }
    public void enableFieldCentricDriving(boolean enabled){
        this.fieldCentricDrivingEnabled = enabled;
    }
    @Override
    public void setDisabled(boolean disable){
        this.disabled = disable;
    }
    private double[] calculateFieldCentric(double angle){
        angle = AngleUnit.DEGREES.toRadians(angle); // convert to radians
        double fieldY = Y * Math.cos(angle) + X * Math.sin(angle);
        double fieldX = -Y * Math.sin(angle) + X * Math.cos(angle);
        return new double[]{fieldX,fieldY};
    }
    private void setDrivePower(double forward, double strafe, double rotate) {
        // Mecanum drive: distribute robot forces to individual motors
        double[] powers = {
                forward + strafe + rotate, //FL
                forward - strafe - rotate, //FR
                forward - strafe + rotate, //BL
                forward + strafe - rotate  //BR
        };
        // Calculate the max power value out of all possible solutions
        double maxPower = 0;
        for (double power : powers){
            maxPower = Math.max(maxPower, power);
        }
        // Apply motor power normalized to the greatest possible value
        for (int i = 0; i < 4; i++){
            motors[i].setPower(powers[i]/maxPower);
        }
    }
    private void postDrivetrainTelemetry(){
        telemetry.addLine("---Drivetrain---");
        telemetry.addData("X Power", X);
        telemetry.addData("Y Power", Y);
        telemetry.addData("Yaw Power", Yaw);
        telemetry.addData("FCD Enabled", fieldCentricDrivingEnabled);
        telemetry.addData("FCD Angle", robotYaw);
    }
}
