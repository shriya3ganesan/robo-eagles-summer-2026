package org.firstinspires.ftc.team28420.module;

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
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.team28420.processors.BallDetection;
import org.firstinspires.ftc.team28420.util.Config;
import org.opencv.core.Scalar;

import java.util.HashMap;

public class Shooter {
    private final DcMotorEx left, right, revolver;
    private final Servo pusher;
    private final ColorSensor cs;
    private final BallDetection.BallColor color = null;
    private final ElapsedTime shooterTime = new ElapsedTime();
    private final DcMotorEx dribbler;
    private HashMap<String, Integer> sortSeqMap = null;
    private int globalTarget = 0;
    private ShooterState state = ShooterState.IDLE;
    private boolean correctMotif = false;
    private boolean manualControl = false;
    private boolean ballPresent = false;
    private final ElapsedTime debounceTimer = new ElapsedTime();
    private boolean potentialBallDetected = false;
    // TODO: DELETE "PG" LATER
    public String curMotif = "PG";

    public Shooter(HardwareMap hMap) {
        sortSeqMap = new HashMap<String, Integer>();
        sortSeqMap.put("PPG", 0);
        sortSeqMap.put("GPP", 1);
        sortSeqMap.put("PGP", 2);

        this.left = hMap.get(DcMotorEx.class, "shLeft");
        this.right = hMap.get(DcMotorEx.class, "shRight");
        this.revolver = hMap.get(DcMotorEx.class, "sort");
        this.cs = hMap.get(ColorSensor.class, "colorSensor");
        this.pusher = hMap.get(Servo.class, "pusher");
        dribbler = hMap.get(DcMotorEx.class, "dribbler");
    }

    public void setup() {
        right.setDirection(DcMotorSimple.Direction.REVERSE);
        revolver.setPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION, new PIDFCoefficients(10, 0, 0, 0));

        setMotorsMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        setMotorsMode(DcMotor.RunMode.RUN_USING_ENCODER);

        setMotorsZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
    public void toggleManualControl(boolean active) {
        if(state == ShooterState.IDLE) {
            if (!active && manualControl) {
                snapToNearestSlot();
            }
            if(active != manualControl) {
                manualControl = active;
                correctMotif = false;
                curMotif = "";
            }
        }
    }

    public void toggleDribbler(boolean active) {
        dribbler.setVelocity(active?Config.ShooterConf.DRIBBLER_VELOCITY:0);
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

    public void syncTicks() {
        globalTarget = revolver.getCurrentPosition();
    }

    public void log(Telemetry telemetry) {
        telemetry.addData("ANGLE", currentAngle());
        telemetry.addData("MOTIF", curMotif);
        telemetry.addData("CORRECT MOTIF", correctMotif);
        telemetry.addData("SHOOTING ALLOWED", isShootable());
    }

    public void snapToNearestSlot() {
        double ticksPerTurn = Config.ShooterConf.SORT_MOTOR_TICKS_PER_TURN;
        double ticksPerSlot = ticksPerTurn / 3.0;
        double offsetTicks = 0; //(60.0 * ticksPerTurn) / 360.0; // ставим в положение для сканирования

        globalTarget = (int) (Math.round((globalTarget - offsetTicks) / ticksPerSlot) * ticksPerSlot + offsetTicks);

        revolver.setTargetPosition(globalTarget);
        revolver.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        revolver.setPower(Config.ShooterConf.SORT_MOTOR_POWER);
    }

    // 360 = ticks_turn
    // 360 / 120 = ticks_turn / x
    // x = target_deg * ticks_turn / 360
    public void rotateRevolver(double deg) {
        globalTarget += (int) (deg * Config.ShooterConf.SORT_MOTOR_TICKS_PER_TURN / 360.0);
        revolver.setTargetPosition(globalTarget);
        revolver.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        revolver.setPower(Config.ShooterConf.SORT_MOTOR_POWER);
    }

    public boolean isMotifFull() {
        return curMotif.length() == 3;
    }

    private BallDetection.BallColor getDetectedColor() {
        NormalizedRGBA colors = ((NormalizedColorSensor) cs).getNormalizedColors();
        float[] hsv = new float[3];
        Color.RGBToHSV((int)(colors.red * 255), (int)(colors.green * 255), (int)(colors.blue * 255), hsv);

        Config.Etc.telemetry.addData("hue", hsv[0]);
        Config.Etc.telemetry.addData("saturation", hsv[1]);
        Config.Etc.telemetry.addData("value", hsv[2]);

        if (checkColors(hsv, Config.BallDetectionConf.cslowPurple, Config.BallDetectionConf.cshighPurple)) {
            return BallDetection.BallColor.PURPLE;
        }
        if (checkColors(hsv, Config.BallDetectionConf.cslowGreen, Config.BallDetectionConf.cshighGreen)) {
            return BallDetection.BallColor.GREEN;
        }
        return null;
    }
    private boolean isBallInRange() {
        DistanceSensor sensorDistance = (DistanceSensor) cs;
        double distanceInCm = sensorDistance.getDistance(DistanceUnit.CM);

        return distanceInCm <= Config.ShooterConf.BALL_DETECTION_THRESHOLD;
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
            } else if (debounceTimer.milliseconds() >= Config.ShooterConf.SCANNED_BALL_MS && !ballPresent) {
                processNewBall(detectedColor);
                ballPresent = true;
            }
        } else {
            potentialBallDetected = false;
            ballPresent = false;
        }
    }
    private void appendBallToMotif(BallDetection.BallColor color) {
        curMotif += (color == BallDetection.BallColor.PURPLE) ? 'P' : 'G';
    }
    private void finalizeMotif() {
        if (isValidSequence(curMotif)) {
            correctMotif = alignRevolverToTarget();
        } else {
            correctMotif = false;
        }
    }
    private boolean alignRevolverToTarget() {
        rotateRevolver(60);
        if(Config.ShooterConf.TARGET_MOTIF == null || Config.ShooterConf.TARGET_MOTIF.isEmpty()) return false;

        int currentIndex = sortSeqMap.getOrDefault(curMotif, 0);
        int targetIndex = sortSeqMap.getOrDefault(Config.ShooterConf.TARGET_MOTIF, 0);

        int moveSlots = (targetIndex - currentIndex + 3) % 3;

        if (moveSlots == 1) rotateRevolver(120);
        if (moveSlots == 2) rotateRevolver(-120);

        curMotif = Config.ShooterConf.TARGET_MOTIF;
        return true;
    }
    private boolean isValidSequence(String motif) {
        long g = motif.chars().filter(ch -> ch == 'G').count();
        long p = motif.chars().filter(ch -> ch == 'P').count();
        return (g == 1 && p == 2);
    }
    private void processNewBall(BallDetection.BallColor color) {
        appendBallToMotif(color);

        if(isMotifFull()) {
            finalizeMotif();
        } else {
            rotateRevolver(-120);
        }
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
        double currentAngle = currentAngle() % 360;
        if (currentAngle < 0) currentAngle += 360;

        boolean nearSlot1 = Math.abs(currentAngle - 60) < 5;
        boolean nearSlot2 = Math.abs(currentAngle - 180) < 5;
        boolean nearSlot3 = Math.abs(currentAngle - 300) < 5;

        return (nearSlot1 || nearSlot2 || nearSlot3) && !revolver.isBusy();
    }

    public void sortedNextBall() {
        if (!revolver.isBusy()) {
            if (curMotif.length() > 0) {
                curMotif = curMotif.substring(0, curMotif.length() - 1);
                if (curMotif.length() > 0) rotateRevolver(-120);
                else {
                    // калі матываў няма
                    rotateRevolver(-60);
                    correctMotif = false;
                }
            }
        }
    }

    public void pushBall(boolean push) {
        if (push) {
            pusher.setPosition(0);
        } else {
            pusher.setPosition(0.5);
        }
    }

    public double currentAngle() {
        return revolver.getCurrentPosition() / Config.ShooterConf.SORT_MOTOR_TICKS_PER_TURN * 360.0;
    }

    public void setVelocityCoefficient(float k) {
        left.setVelocity(Config.ShooterConf.VELOCITY * k);
        right.setVelocity(Config.ShooterConf.VELOCITY * k);
    }

    public void shoot() {
        if(state == Shooter.ShooterState.IDLE && isShootable()
                && (manualControl || correctMotif))
        {
            pushBall(true);
            state = ShooterState.SHOOTING;
            shooterTime.reset();
        }
    }

    public void update() {
        switch(state) {
            case SHOOTING:
                if(shooterTime.milliseconds() >= 600) {
                    pushBall(false);
                    state = Shooter.ShooterState.STOP_SHOOTING;
                    shooterTime.reset();
                }
                break;
            case STOP_SHOOTING:
                if(shooterTime.milliseconds() >= 600) {
                    if (manualControl) {
                        // если ручной режим, то после выстрела не поворачиваем барабан
                        state = Shooter.ShooterState.IDLE;
                    } else {
                        sortedNextBall();
                        state = Shooter.ShooterState.REVOLVER_TURNING;
                        shooterTime.reset();
                    }
                }
                break;
            case REVOLVER_TURNING:
                if(!revolver.isBusy()) {
                    state = Shooter.ShooterState.IDLE;
                    shooterTime.reset();
                }
                break;

            case IDLE:
                if (!isShootable() && !manualControl) scanBall();
                break;
        }
    }

    public enum ShooterState {IDLE, SHOOTING, STOP_SHOOTING, REVOLVER_TURNING}
}
