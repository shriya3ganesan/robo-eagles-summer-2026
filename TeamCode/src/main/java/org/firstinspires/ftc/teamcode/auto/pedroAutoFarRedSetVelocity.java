package org.firstinspires.ftc.teamcode.auto;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;


import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
@Disabled
@Autonomous(name="PedroAutoFarRedSetVelocity", group="Autonomous")
public class pedroAutoFarRedSetVelocity extends OpMode{
    private DcMotorEx launchMotor = null;
    private DcMotor transferMotor = null;
    private Servo trigger = null;
    private DcMotor intakeMotor = null;
    private Follower follower;
    private Timer pathTimer, opModeTimer, shootTimer;

    public static int TARGET_RPM = 2187;
    public static double TICKS_PER_REV = 383.6;
    public static double IDLE_RPM = 1000;
    public static double oneShot_Shoot_time = 2.3;
    //public static double wait_time = 0.85; We use smart waiting instead

    private double getLaunchTargetTicks() {
        return (TARGET_RPM * TICKS_PER_REV) / 60.0;
    }

    // Servo positions (servos use 0.0 to 1.0)
    private double triggerStartPos = 0.11;
    private double triggerShootPos = 0.4;  // Adjust this value based on your mechanism

    private int shotsFired = 0;
    private boolean isShooting = false;

    public enum PathState{
        DRIVE_STARTPOS_SHOOTPOS,
        SHOOT_PRELOAD,
        DRIVE_SHOOTPOS_INTAKE_3,
        DRIVE_INTAKE_3_SHOOTPOS,
        SHOOT_SAMPLES,
        DRIVE_SHOOTPOS_INTAKE_N,
        DRIVE_INTAKE_N_SHOOTPOS
    }

    PathState pathState;
    private final Pose startPose = new Pose(87.47252747252746, 8, Math.toRadians(90));
    private final Pose shootPose = new Pose(87, 8, Math.toRadians(70));
    private final Pose intakeThree = new Pose(133.43956043956044, 34.28571428571429, Math.toRadians(0));
    private final Pose intakeN = new Pose(133.36263736263737, 9.043956043956046, Math.toRadians(0));

    private PathChain driveStartShootFar;
    private PathChain driveShootIntakeThree, driveIntakeThreeShoot;
    private PathChain driveShootIntakeN, driveIntakeNShoot;

    public void buildPaths(){
        // Initial paths
        driveStartShootFar = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        driveIntakeThreeShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeThree, shootPose))
                .setLinearHeadingInterpolation(intakeThree.getHeading(), shootPose.getHeading())
                .build();
        driveShootIntakeN = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, intakeN))
                .setLinearHeadingInterpolation(shootPose.getHeading(), intakeN.getHeading())
                .build();
        driveIntakeNShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeN, shootPose))
                .setLinearHeadingInterpolation(intakeN.getHeading(), shootPose.getHeading())
                .build();

        // Intake 2 paths with control point
        Pose controlPointShootToIntake3 = new Pose(82.08791208791206, 40.95604395604395, Math.toRadians(0));
        driveShootIntakeThree = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, controlPointShootToIntake3, intakeThree))
                .setConstantHeadingInterpolation(intakeThree.getHeading())
                .build();
    }

    public void statePathUpdate(){
        double currentVel = launchMotor.getVelocity();
        double targetVel = getLaunchTargetTicks();
        switch(pathState){
            case DRIVE_STARTPOS_SHOOTPOS:
                if (!isShooting) {
                    follower.followPath(driveStartShootFar, true);
                    launchMotor.setVelocity(getLaunchTargetTicks());  // Start flywheel early
                    isShooting = true;
                    trigger.setPosition(triggerStartPos);
                }
                if (!follower.isBusy()) {
                    setPathState(PathState.SHOOT_PRELOAD);
                    shotsFired = 0;
                }
                break;

            case SHOOT_PRELOAD:
                // Wait a moment for flywheel to spin up
                // Check if we are within 5% of target speed
                currentVel = launchMotor.getVelocity();
                targetVel = getLaunchTargetTicks();

                // Only wait if we are spinning too slow
                if (currentVel < targetVel * 0.95) {
                    launchMotor.setVelocity(targetVel);
                    // Add telemetry so you know it's waiting on speed
                    telemetry.addData("Status", "Spinning Up..."); 
                    telemetry.addData("Current RPM", (currentVel * 60) / TICKS_PER_REV);
                    break; 
                }

                // Fire 3 shots
                if (shotsFired < 3) {
                    shootOneShot();
                } else {
                    // Done shooting, move to intake 3
                    follower.followPath(driveShootIntakeThree, true);
                    //follower.followPath(driveShootIntakeN, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    launchMotor.setVelocity(IDLE_RPM);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKE_3);
                    //setPathState(PathState.DRIVE_SHOOTPOS_INTAKE_N);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKE_3:
                // Keep intake running while driving
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    transferMotor.setPower(-1);
                    follower.followPath(driveIntakeThreeShoot, true);
                    setPathState(PathState.DRIVE_INTAKE_3_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKE_3_SHOOTPOS:
                if (!follower.isBusy()) {
                    launchMotor.setVelocity(getLaunchTargetTicks());  // Spin up flywheel

                    transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES);
                    shotsFired = 0;
                }
                break;

            case SHOOT_SAMPLES:
                // Failsafe: If we've been in this state for > 4 seconds, just move on
                if (pathTimer.getElapsedTimeSeconds() > 4 * oneShot_Shoot_time) {
                    // Force transition logic here
                    follower.followPath(driveShootIntakeN, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    launchMotor.setVelocity(IDLE_RPM);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKE_N);
                }

                // Check if we are within 5% of target speed
                currentVel = launchMotor.getVelocity();
                targetVel = getLaunchTargetTicks();

                // Only wait if we are spinning too slow
                if (currentVel < targetVel * 0.95) {
                    intakeMotor.setPower(0);
                    launchMotor.setVelocity(targetVel);
                    // Add telemetry so you know it's waiting on speed
                    telemetry.addData("Status", "Spinning Up..."); 
                    telemetry.addData("Current RPM", (currentVel * 60) / TICKS_PER_REV);
                    break; 
                }

                // Fire 3 shots
                if (shotsFired < 3) {
                    shootOneShot();
                } else {
                    // Done shooting, move to intake N
                    follower.followPath(driveShootIntakeN, true);
                    intakeMotor.setPower(1);
                    transferMotor.setPower(-1);
                    launchMotor.setVelocity(IDLE_RPM);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKE_N);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKE_N:
                // Keep intake running while driving
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    transferMotor.setPower(-1);
                    follower.followPath(driveIntakeNShoot, true);
                    setPathState(PathState.DRIVE_INTAKE_N_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKE_N_SHOOTPOS:
                if (!follower.isBusy()) {
                    launchMotor.setVelocity(getLaunchTargetTicks());  // Spin up flywheel
                    intakeMotor.setPower(0);
                    transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES);
                    shotsFired = 0;
                }
                break;

            default:
                telemetry.addLine("No State Set");
                break;
        }
    }

    private void shootOneShot() {
        double elapsed = pathTimer.getElapsedTimeSeconds();
        launchMotor.setVelocity(getLaunchTargetTicks());
        // Each shot cycle takes ~oneShot_Shoot_time seconds
        double cycleTime = elapsed % oneShot_Shoot_time;
        if (cycleTime <= 0.1*oneShot_Shoot_time) {
            telemetry.addLine("Waiting");
        }
        //else if (1.7 < cycleTime && cycleTime < 1.8 && shotsFired < 1) {
        //    transferMotor.setPower(1);
        //    telemetry.addLine("Shot " + (shotsFired + 1) + ": Firing");
        //}
        else if (cycleTime < 0.6 * oneShot_Shoot_time) {
            // Move trigger to shoot position and run transfer

            transferMotor.setPower(1);
            telemetry.addLine("Shot " + (shotsFired + 1) + ": Firing");
        }
        else if (cycleTime < 0.84 * oneShot_Shoot_time) {
            trigger.setPosition(triggerShootPos);
            transferMotor.setPower(0);
        }
        else if (cycleTime < 0.96 * oneShot_Shoot_time) {
            trigger.setPosition(triggerStartPos);
            telemetry.addLine("Shot " + (shotsFired + 1) + ": Resetting");
        }
        else {
            // Wait before next shot
            if (elapsed > (shotsFired + 1) * oneShot_Shoot_time) {
                shotsFired++;
            }
        }
    }

    public void setPathState(PathState newState){
        pathState = newState;
        pathTimer.resetTimer();
        isShooting = false;
    }

    @Override
    public void init(){
        pathState = PathState.DRIVE_STARTPOS_SHOOTPOS;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        shootTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);

        launchMotor = hardwareMap.get(DcMotorEx.class, "launch_motor");
        transferMotor = hardwareMap.get(DcMotor.class, "transfer");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        trigger = hardwareMap.get(Servo.class, "Trigger");

        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        trigger.setDirection(Servo.Direction.FORWARD);
        transferMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        launchMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        launchMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        /*
        launchMotor.setVelocityPIDFCoefficients(
                0.0,   // kP (start at 0) -> 0 is a terrible idea, regular PIDF is okay, tune if absolutely necesaary
                0.0,   // kI
                0.0,   // kD
                13.5   // kF (starting point for goBILDA 6000 RPM)
        );
        */
        trigger.setPosition(triggerStartPos);

        buildPaths();
        follower.setPose(startPose);
    }

    public void start(){
        opModeTimer.resetTimer();
        setPathState(pathState);
    }

    @Override
    public void loop(){
        follower.update();
        statePathUpdate();

        double velocity = launchMotor.getVelocity();
        double rpm = (velocity * 60) / TICKS_PER_REV;
        
        telemetry.addData("Shooter RPM", rpm);
        telemetry.addData("Target RPM", TARGET_RPM);
        telemetry.addData("Error", TARGET_RPM - rpm); // helpful for tuning

        telemetry.addData("path state", pathState.toString());
        telemetry.addData("shots fired", shotsFired);
        telemetry.addData("x", follower.getPose().getX());
        telemetry.addData("y", follower.getPose().getY());
        telemetry.addData("heading", follower.getPose().getHeading());
        telemetry.addData("path time", pathTimer.getElapsedTimeSeconds());
        telemetry.update();
    }
}
