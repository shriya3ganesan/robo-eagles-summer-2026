package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.util.ElapsedTime;

// Single use per object
public class TurnPidController {
    private double kP, kI, kD;
    private ElapsedTime timer = new ElapsedTime();
    private double targetAngle;
    private double lastError = 0;
    private double accumulatedError = 0;
    private double lastTime = -1;
    private double lastSlope = 0;
    private double integral = 0;
    private double derivative = 0;
    private double dT = 0;
    private double masxPower = 0;
    public TurnPidController(double target, double p, double i, double d) {
        kP = p;
        kI = i;
        kD = d;
        targetAngle = target;
    }
    public double update(double currentAngle) {

        // P
        double error = targetAngle - currentAngle;
        error %= 360;
        error += 360;
        error %= 360;
        if (error > 180) {
            error -= 360;
        }
        dT = (timer.milliseconds() - lastTime);
        integral = integral + error * dT;
        derivative = ((error - lastError) / dT);
        double motorPower = (kP * error + kI * integral + kD * derivative);
        motorPower = Math.max(-.35, Math.min(motorPower, .35));
        //I
        accumulatedError = Math.signum(error);
        accumulatedError += error;
        if (Math.abs(error) < 2) {
            accumulatedError = 0;
        }



        // D
        double slope = 0;
        if (lastTime > 0) {
            slope = (error - lastError) / (timer.milliseconds() - lastTime);
        }

        lastSlope = slope;
        lastError = error;
        lastTime = timer.milliseconds();

        //     double motorPower = 0.1 Math.signum(error)
        //     + 0.9 Math.tanh(kP * error + kI * accumulatedError - kD * slope);
        return motorPower;
    }

    public double getLastSlope() {
        return lastSlope;
    }

}