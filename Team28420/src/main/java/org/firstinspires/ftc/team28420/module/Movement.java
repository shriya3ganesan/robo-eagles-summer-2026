package org.firstinspires.ftc.team28420.module;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.team28420.types.MovementParams;
import org.firstinspires.ftc.team28420.types.WheelsRatio;

public class Movement {

    @Config
    public static class MovementConf {
        public static int MAX_VELOCITY = 3600;

    }

    private final DcMotorEx leftFront, rightFront, leftBack, rightBack;

    public Movement(HardwareMap hMap) {
        this.leftFront = hMap.get(DcMotorEx.class, "LTMotor");
        this.rightFront = hMap.get(DcMotorEx.class, "RFMotor");
        this.leftBack = hMap.get(DcMotorEx.class, "LBMotor");
        this.rightBack = hMap.get(DcMotorEx.class, "RBMotor");
    }

    public void setup() {
        rightFront.setDirection(DcMotor.Direction.REVERSE);
        rightBack.setDirection(DcMotor.Direction.REVERSE);

        setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorsMode(DcMotor.RunMode.RUN_USING_ENCODER);
        setMotorsZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void moveByParams(MovementParams params) {
        setMotorsVelocityRatios(paramsToRatios(params), MovementConf.MAX_VELOCITY);
    }

    public void brake() {
        setMotorsPowerRatios(WheelsRatio.ZERO);
    }

    public void log(Telemetry telemetry) {
        telemetry.addData("LF motor velocity", leftFront.getVelocity());
        telemetry.addData("RF motor velocity", rightFront.getVelocity());
        telemetry.addData("LB motor velocity", leftBack.getVelocity());
        telemetry.addData("RB motor velocity", rightBack.getVelocity());
    }

    private WheelsRatio<Double> paramsToRatios(MovementParams params) {
        double theta = params.getMoveVector().getTheta();
        double power = params.getMoveVector().getAbs();
        double turn = params.getTurnAbs();

        double sin = Math.sin(theta - Math.PI / 4);
        double cos = Math.cos(theta - Math.PI / 4);
        double max = Math.max(Math.abs(sin), Math.abs(cos));

        double lf = power * cos / max + turn;
        double rf = power * sin / max - turn;
        double lb = power * sin / max + turn;
        double rb = power * cos / max - turn;

        double scale = Math.max(1.0, Math.max(
                Math.max(Math.abs(lf), Math.abs(rf)),
                Math.max(Math.abs(lb), Math.abs(rb))
        ));

        return new WheelsRatio<>(lf / scale, rf / scale, lb / scale, rb / scale);
    }

    private void setMotorsVelocityRatios(WheelsRatio<Double> wheelsRatio, int maxVelocity) {
        leftFront.setVelocity(wheelsRatio.getLeftTop() * maxVelocity);
        rightFront.setVelocity(wheelsRatio.getRightTop() * maxVelocity);
        leftBack.setVelocity(wheelsRatio.getLeftBottom() * maxVelocity);
        rightBack.setVelocity(wheelsRatio.getRightBottom() * maxVelocity);
    }

    private void setMotorsMode(DcMotor.RunMode mode) {
        leftFront.setMode(mode);
        rightFront.setMode(mode);
        leftBack.setMode(mode);
        rightBack.setMode(mode);
    }

    private void setMotorsZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        leftFront.setZeroPowerBehavior(behavior);
        rightFront.setZeroPowerBehavior(behavior);
        leftBack.setZeroPowerBehavior(behavior);
        rightBack.setZeroPowerBehavior(behavior);
    }

    private void setMotorsPowerRatios(WheelsRatio<Double> wheelsRatio) {
        leftFront.setPower(wheelsRatio.getLeftTop());
        rightFront.setPower(wheelsRatio.getRightTop());
        leftBack.setPower(wheelsRatio.getLeftBottom());
        rightBack.setPower(wheelsRatio.getRightBottom());
    }
}
