package org.firstinspires.ftc.teamcode.Robot.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Robot.Subsystem;

public class Drivetrain implements Subsystem {
    private DcMotor FLMotor;
    private DcMotor FRMotor;
    private DcMotor BLMotor;
    private DcMotor BRMotor;
    
    //
    private double X = 0,Y = 0,Yaw = 0;

    public Drivetrain(HardwareMap hardwareMap, Telemetry telemetry){
        FLMotor = hardwareMap.get(DcMotor.class, "FLMotor");
        FRMotor = hardwareMap.get(DcMotor.class, "FRMotor");
        BLMotor = hardwareMap.get(DcMotor.class, "BLMotor");
        BRMotor = hardwareMap.get(DcMotor.class, "BRMotor");
        DcMotor[] motors = {FLMotor,FRMotor,BLMotor,BRMotor};

        for (DcMotor motor: motors){
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    public void updateMovementVector(double Y, double X, double Yaw){

    }
    private void setDrivePower(double forward, double strafe, double rotate) {
        // Mecanum drive: distribute robot forces to individual motors
        double FLPower = forward + strafe + rotate;
        double FRPower = forward - strafe - rotate;
        double BLPower = forward - strafe + rotate;
        double BRPower = forward + strafe - rotate;
        
        // Normalize if any value exceeds 1.0
        double maxPower = Math.max(1.0, Math.max(Math.abs(FLPower), 
                          Math.max(Math.abs(FRPower), 
                          Math.max(Math.abs(BLPower), Math.abs(BRPower)))));
        
        FLMotor.setPower(FLPower / maxPower);
        FRMotor.setPower(FRPower / maxPower);
        BLMotor.setPower(BLPower / maxPower);
        BRMotor.setPower(BRPower / maxPower);
    }
    @Override
    public void update() {
        // No general update logic for the drivetrain
    }
}
