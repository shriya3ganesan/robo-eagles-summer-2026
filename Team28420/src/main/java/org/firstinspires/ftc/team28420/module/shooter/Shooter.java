package org.firstinspires.ftc.team28420.module.shooter;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.team28420.config.ShooterConf;
import org.firstinspires.ftc.team28420.module.Dribbler;
import org.firstinspires.ftc.team28420.processors.BallDetection;
import org.opencv.core.Scalar;

import java.util.HashMap;

public class Shooter {
    private final DcMotorEx left, right, revolver;
    private final ColorSensor cs;
    private final BallDetection.BallColor color = null;
    private final ElapsedTime shooterTime = new ElapsedTime();
    private final ElapsedTime debounceTimer = new ElapsedTime();
    private final Dribbler dribbler;
    private final Pusher pusher;
    public String curMotif = "";
    private HashMap<String, Integer> sortSeqMap = null;
    private int globalTarget = 0;
    private ShooterState state = ShooterState.IDLE;
    private boolean correctMotif = false;
    private boolean manualControl = false;
    private boolean ballPresent = false;
    private boolean potentialBallDetected = false;
    private Telemetry telemetry;

    public Shooter(HardwareMap hMap, Telemetry telemetry) {
        initSortSeq();

        this.left = hMap.get(DcMotorEx.class, "shLeft");
        this.right = hMap.get(DcMotorEx.class, "shRight");
        this.revolver = hMap.get(DcMotorEx.class, "sort");
        this.cs = hMap.get(ColorSensor.class, "colorSensor");

        pusher = new Pusher(hMap);
        dribbler = new Dribbler(hMap);

        this.telemetry = telemetry;
    }

    public void initSortSeq() {
        sortSeqMap = new HashMap<String, Integer>();
        sortSeqMap.put("PPG", 0);
        sortSeqMap.put("GPP", 1);
        sortSeqMap.put("PGP", 2);
    }

    public void setup() {
        left.setDirection(DcMotorSimple.Direction.REVERSE);

        setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorsMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pusher.setState(Pusher.PusherState.NEUTRAL);
        revolver.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, new PIDFCoefficients(10, 0, 0, 0));
        left.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(ShooterConf.SHOOTER_P, ShooterConf.SHOOTER_I, ShooterConf.SHOOTER_D, ShooterConf.SHOOTER_F));
        right.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(ShooterConf.SHOOTER_P, ShooterConf.SHOOTER_I, ShooterConf.SHOOTER_D, ShooterConf.SHOOTER_F));

        setMotorsZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void afterStart() {
        syncTicks();
        pusher.setState(Pusher.PusherState.NEUTRAL);
    }

    public void toggleManualControl(boolean active) {
        if (state == ShooterState.IDLE) {
            if (active != manualControl) {
                manualControl = active;
                correctMotif = false;
                curMotif = "";
            }
        }
    }

    public void setDribblerVelocityCoefficient(float k) {
        dribbler.setVelocityCoefficient(k);
    }

    public void setMotorsMode(DcMotor.RunMode mode) {
        left.setMode(mode);
        right.setMode(mode);
        revolver.setMode(mode);
    }

    public void setMotorsZeroPowerBehavior(DcMotor.ZeroPowerBehavior behavior) {
        left.setZeroPowerBehavior(behavior);
        right.setZeroPowerBehavior(behavior);
    }

    private void syncTicks() {
        globalTarget = revolver.getCurrentPosition();
    }

    private double toRPM(double tps) {
        return tps * 60.0 / 28.0;
    }

    public void log(Telemetry telemetry) {
        telemetry.addData("ANGLE", currentAngle());
        telemetry.addData("MOTIF", curMotif);
        telemetry.addData("CORRECT MOTIF", correctMotif);
        telemetry.addData("SHOOTING ALLOWED", isShootable());
        telemetry.addData("CURRENT VELOCITY LEFT", left.getVelocity());
        telemetry.addData("CURRENT VELOCITY RIGHT", right.getVelocity());
        telemetry.addData("CURRENT RPM LEFT", toRPM(left.getVelocity()));
        telemetry.addData("CURRENT RPM RIGHT", toRPM(right.getVelocity()));
        telemetry.addData("CURRENT REVOLVER TICKS", revolver.getCurrentPosition());
    }

    public void snapToNearestSlot() {
        double ticksPerTurn = ShooterConf.SORT_MOTOR_TICKS_PER_TURN;
        double ticksPerSlot = ticksPerTurn / 3.0;
        double offsetTicks = 0; //(60.0 * ticksPerTurn) / 360.0; // ставим в положение для сканирования

        globalTarget = (int) (Math.round((globalTarget - offsetTicks) / ticksPerSlot) * ticksPerSlot + offsetTicks);

        revolver.setTargetPosition(globalTarget);
        revolver.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        revolver.setPower(ShooterConf.SORT_MOTOR_POWER);
    }

    // 360 = ticks_turn
    // 360 / 120 = ticks_turn / x
    // x = target_deg * ticks_turn / 360
    public void rotateRevolver(double deg) {
        globalTarget += (int) (deg * ShooterConf.SORT_MOTOR_TICKS_PER_TURN / 360.0);
        revolver.setTargetPosition(globalTarget);
        revolver.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        revolver.setPower(ShooterConf.SORT_MOTOR_POWER);
    }

    public boolean isMotifFull() {
        return curMotif.length() == 3;
    }

    private BallDetection.BallColor getDetectedColor() {
        NormalizedRGBA colors = ((NormalizedColorSensor) cs).getNormalizedColors();
        float[] hsv = new float[3];
        Color.RGBToHSV((int) (colors.red * 255), (int) (colors.green * 255), (int) (colors.blue * 255), hsv);

        telemetry.addData("hue", hsv[0]);
        telemetry.addData("saturation", hsv[1]);
        telemetry.addData("value", hsv[2]);

        if (checkColors(hsv, ShooterConf.cslowPurple, ShooterConf.cshighPurple)) {
            return BallDetection.BallColor.PURPLE;
        }
        if (checkColors(hsv, ShooterConf.cslowGreen, ShooterConf.cshighGreen)) {
            return BallDetection.BallColor.GREEN;
        }
        return null;
    }

    private boolean isBallInRange() {
        DistanceSensor sensorDistance = (DistanceSensor) cs;
        double distanceInCm = sensorDistance.getDistance(DistanceUnit.CM);

        return distanceInCm <= ShooterConf.BALL_DETECTION_THRESHOLD;
    }

    public void scanBall() {
        if (isMotifFull()) return;

        BallDetection.BallColor detectedColor = getDetectedColor();
        boolean ballInRange = isBallInRange();

        boolean currentlySeeingBall = ballInRange && (detectedColor != null);

        if (currentlySeeingBall) {
            if (!potentialBallDetected) {
                potentialBallDetected = true;
                debounceTimer.reset();
            } else if (debounceTimer.milliseconds() >= ShooterConf.SCANNED_BALL_MS && !ballPresent) {
                processNewBall(detectedColor);
                ballPresent = true;
            }
        } else {
            potentialBallDetected = false;
            ballPresent = false;
        }
    }

    public void appendBallToMotif(BallDetection.BallColor color) {
        curMotif += (color == BallDetection.BallColor.PURPLE) ? 'P' : 'G';
    }

    public void appendBallToMotif(char color) {
        curMotif += color;
    }

    private void finalizeMotif() {
        if (isValidSequence(curMotif)) {
            correctMotif = alignRevolverToTarget();
        } else {
            correctMotif = false;
        }
    }

    public boolean alignRevolverToTarget() {
        if (ShooterConf.TARGET_MOTIF == null || ShooterConf.TARGET_MOTIF.isEmpty()) return false;

        int currentIndex = sortSeqMap.getOrDefault(curMotif, 0);
        int targetIndex = sortSeqMap.getOrDefault(ShooterConf.TARGET_MOTIF, 0);

        int moveSlots = (targetIndex - currentIndex + 3) % 3;

        if (moveSlots == 1) rotateRevolver(120);
        if (moveSlots == 2) rotateRevolver(-120);

        curMotif = ShooterConf.TARGET_MOTIF;
        return true;
    }

    private boolean isValidSequence(String motif) {
        long g = motif.chars().filter(ch -> ch == 'G').count();
        long p = motif.chars().filter(ch -> ch == 'P').count();
        return (g == 1 && p == 2);
    }

    private void processNewBall(BallDetection.BallColor color) {
        appendBallToMotif(color);

        if (isMotifFull()) {
            finalizeMotif();
        } else {
            rotateRevolver(-120);
        }
    }

    public void resetRevolverTicks() {
        revolver.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        revolver.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        syncTicks();
    }

    public boolean checkColors(float[] hsv, Scalar low, Scalar high) {
        boolean hueMatch;

        if (low.val[0] > high.val[0]) {
            // This handles the Red wrap-around (e.g., low is 350, high is 10)
            hueMatch = (hsv[0] >= low.val[0] || hsv[0] <= high.val[0]);
        } else {
            // Standard range check
            hueMatch = (hsv[0] >= low.val[0] && hsv[0] <= high.val[0]);
        }

        return hueMatch && (hsv[1] >= low.val[1] && hsv[1] <= high.val[1]) && (hsv[2] >= low.val[2] && hsv[2] <= high.val[2]);
    }

    public boolean isShootable() {
        return !revolver.isBusy();
    }

    public void sortedNextBall() {
        if (!revolver.isBusy()) {
            if (curMotif.length() > 0) {
                curMotif = curMotif.substring(0, curMotif.length() - 1);
                if (curMotif.length() > 0) rotateRevolver(-120);
                else {
                    correctMotif = false;
                }
            }
        }
    }

    public void pushBall(boolean push) {
        if (push) {
            pusher.setState(Pusher.PusherState.PUSH);
        } else {
            pusher.setState(Pusher.PusherState.NEUTRAL);
        }
    }

    public double currentAngle() {
        return revolver.getCurrentPosition() / ShooterConf.SORT_MOTOR_TICKS_PER_TURN * 360.0;
    }

    public void setPids() {
        left.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(ShooterConf.SHOOTER_P, ShooterConf.SHOOTER_I, ShooterConf.SHOOTER_D, ShooterConf.SHOOTER_F));
        right.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(ShooterConf.SHOOTER_P, ShooterConf.SHOOTER_I, ShooterConf.SHOOTER_D, ShooterConf.SHOOTER_F));
    }


    public void setVelocityCoefficient(float k) {
        double desired = ShooterConf.VELOCITY * k;

        left.setVelocity(desired);
        right.setVelocity(desired);
    }

    public boolean shoot() {
        if (state == Shooter.ShooterState.IDLE && isShootable()) {
            state = ShooterState.REVOLVER_TURNING;
            if(manualControl) {
                rotateRevolver(120);
            } else rotateRevolver(360);

            return true;
        }
        return false;
    }

    public void update() {
        switch (state) {
            case REVOLVER_TURNING:
                if (!revolver.isBusy()) {
                    state = Shooter.ShooterState.IDLE;
                    shooterTime.reset();
                }
                break;

            case IDLE:
                if (!isShootable() && !manualControl && !isMotifFull()) scanBall();
                break;
        }
    }

    public enum ShooterState {IDLE, REVOLVER_TURNING}
}
