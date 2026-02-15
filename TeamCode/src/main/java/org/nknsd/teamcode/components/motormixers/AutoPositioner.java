package org.nknsd.teamcode.components.motormixers;

import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.RobotLog;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.nknsd.teamcode.components.handlers.odometry.AbsolutePosition;
import org.nknsd.teamcode.components.utility.feedbackcontroller.PidController;
import org.nknsd.teamcode.frameworks.NKNComponent;

public class AutoPositioner implements NKNComponent {
    private PidController pidControllerX;
    private PidController pidControllerY;
    private PidController pidControllerH;
    private PowerInputMixer powerInputMixer;
    private AbsolutePosition absolutePosition;

    SparkFunOTOS.Pose2D errorDelta = new SparkFunOTOS.Pose2D(0, 0, 0);
    private SparkFunOTOS.Pose2D velocity = new SparkFunOTOS.Pose2D(0, 0, 0);
    private double lastTime = 0;
    private SparkFunOTOS.Pose2D target = new SparkFunOTOS.Pose2D(0, 0, 0);
    private SparkFunOTOS.Pose2D lastPos = new SparkFunOTOS.Pose2D(0, 0, 0);
    SparkFunOTOS.Pose2D error = new SparkFunOTOS.Pose2D(0, 0, 0);
    boolean enableX, enableY, enableH;
    private double hSpeed;
    private double ySpeed;
    private double xSpeed;

    public void enableAutoPositioning(boolean enableX, boolean enableY, boolean enableH) {
        if(enableX == this.enableX && enableY == this.enableY && enableH == this.enableH){
            return;
        }
        this.enableX = enableX;
        this.enableY = enableY;
        this.enableH = enableH;
        if (!enableX && !enableY && !enableH) {
            powerInputMixer.setAutoPowers(new double[]{0, 0, 0});
        }
        powerInputMixer.setAutoEnabled(new boolean[]{enableX, enableY, enableH});
    }

    public void setTargetX(double targetX, PidController pid) {
        target.x = targetX;
        pidControllerX = pid;
    }

    public void setTargetY(double targetY, PidController pid) {
        target.y = targetY;
        pidControllerY = pid;
    }

    public void setTargetH(double targetH, PidController pid) {
        target.h = targetH;
        pidControllerH = pid;
    }


    public SparkFunOTOS.Pose2D getErrorDelta() {
        return errorDelta;
    }

    public SparkFunOTOS.Pose2D getError() {
        return error;
    }

    public SparkFunOTOS.Pose2D getVelocity() {
        return velocity;
    }

    private SparkFunOTOS.Pose2D calcDelta(SparkFunOTOS.Pose2D target, SparkFunOTOS.Pose2D current) {
        return new SparkFunOTOS.Pose2D(
                target.x - current.x,
                target.y - current.y,
                (target.h - current.h + 3 * Math.PI) % (2 * Math.PI) - Math.PI
        );

    }


    @Override
    public boolean init(Telemetry telemetry, HardwareMap hardwareMap, Gamepad gamepad1, Gamepad gamepad2) {
        return true;
    }

    @Override
    public void init_loop(ElapsedTime runtime, Telemetry telemetry) {
        lastTime = runtime.milliseconds();
    }

    @Override
    public void start(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public void stop(ElapsedTime runtime, Telemetry telemetry) {

    }

    @Override
    public String getName() {
        return "MotorDriver";
    }


    @Override
    public void loop(ElapsedTime runtime, Telemetry telemetry) {

        if (enableX || enableY || enableH) {
            SparkFunOTOS.Pose2D current = absolutePosition.getPosition();
//        RobotLog.v("current: x " + current.x + ", y " + current.y + ", h " + current.h);
//        RobotLog.v("target: x " + target.x + ", y " + target.y + ", h " + target.h);
            current.h = current.h % (2 * Math.PI);
            SparkFunOTOS.Pose2D oldError = error;
            error = calcDelta(target, current);
            errorDelta = calcDelta(error, oldError);


            double interval = runtime.milliseconds() - lastTime;
//        RobotLog.v("interval: " + interval);
            velocity = new SparkFunOTOS.Pose2D(
                    (current.x - lastPos.x) * 1000 / interval,
                    (current.y - lastPos.y) * 1000 / interval,
                    ((current.h - lastPos.h + 3 * Math.PI) % (2 * Math.PI) - Math.PI) * 1000 / interval
            );
//        RobotLog.v("velocity: x " + velocity.x + ", y " + velocity.y + ", h " + velocity.h);

             xSpeed = 0;
            if (enableX) {
                xSpeed = pidControllerX.findOutput(error.x, errorDelta.x, velocity.x, interval);
            }

             ySpeed = 0;
            if (enableY) {
                ySpeed = pidControllerY.findOutput(error.y, errorDelta.y, velocity.y, interval);
            }

             hSpeed = 0;
            if (enableH) {
                hSpeed = pidControllerH.findOutput(error.h, errorDelta.h, velocity.h, interval);
            }

            lastTime = runtime.milliseconds();
            lastPos = current;

//            RobotLog.v("auto postitioner x: " + output.x + ", y: " + output.y + ", h: " + output.h);
            powerInputMixer.setAutoPowers(new double[]{xSpeed, ySpeed, hSpeed});
        }
    }

    @Override
    public void doTelemetry(Telemetry telemetry) {
//        telemetry.addData("target x", target.x);
//        telemetry.addData("target y", target.y);
        telemetry.addData("target h", target.h);
        telemetry.addData("speeds x, y, h", xSpeed + ", " + ySpeed + ", " + hSpeed);
        telemetry.addData("enabled x, y, h", enableX + ", " + enableY + ", " + enableH);
    }

    public void link(PowerInputMixer powerInputMixer, AbsolutePosition absolutePosition) {
        this.powerInputMixer = powerInputMixer;
        this.absolutePosition = absolutePosition;
    }
}
