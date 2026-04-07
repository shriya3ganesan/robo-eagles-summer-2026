package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class FarAuto extends BaseAuto {

    protected Pose intakeThree, intakeN;
    protected Pose controlPointToIntakeThree = new Pose(82.08791208791206, 40.95604395604395, Math.toRadians(0));

    public static double launchPower23 = 0.85;
    public static double shootAngle    = 65.4;

    private PathChain driveStartShootFar;
    private PathChain driveShootIntakeThree, driveIntakeThreeShoot;
    private PathChain driveShootIntakeN,     driveIntakeNShoot;

    private ElapsedTime launchTimer = new ElapsedTime();
    protected double launchTimeoutTime = 1.0; // TIMEOUT watcher
    protected double launchWaitTime = 0.4;    // WAITING phase
    protected double launchTransferTime = 0.5; // TRANSFER phase
    protected double launchShootTime = 0.2;    // SHOOT phase
    protected double launchResetTime = 0.15;   // RESET phase

    public enum LaunchState {
        RESET,
        WAITING,
        TRANSFER,
        SHOOT
    }
    protected LaunchState launchState = LaunchState.WAITING;

    public enum PathState {
        DRIVE_STARTPOS_SHOOTPOS,
        SHOOT_PRELOAD,
        DRIVE_SHOOTPOS_INTAKE_3, DRIVE_INTAKE_3_SHOOTPOS,
        SHOOT_SAMPLES,
        DRIVE_SHOOTPOS_INTAKE_N, DRIVE_INTAKE_N_SHOOTPOS
    }

    protected PathState pathState = PathState.DRIVE_STARTPOS_SHOOTPOS;

    /** Override and return true to skip the intake 3 sequence */
    protected boolean skipIntakeThree() { return false; }

    protected void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
        isShooting = false;
    }

    @Override
    protected void buildPaths() {
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
                .setLinearHeadingInterpolation(shootPose.getHeading(), intakeN.getHeading(), 0.6)
                .build();
        driveIntakeNShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeN, shootPose))
                .setLinearHeadingInterpolation(intakeN.getHeading(), shootPose.getHeading())
                .build();
        driveShootIntakeThree = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, controlPointToIntakeThree.mirror(), intakeThree))
                .setConstantHeadingInterpolation(intakeThree.getHeading())
                .build();
    }

    @Override
    protected void statePathUpdate() {
        switch (pathState) {
            case DRIVE_STARTPOS_SHOOTPOS:
                if (!isShooting) {
                    follower.followPath(driveStartShootFar, true);
                    setLaunchPower(launchPower);
                    robot.Trigger.setPosition(triggerStartPos);
                    isShooting = true;
                }
                if (!follower.isBusy()) { setPathState(PathState.SHOOT_PRELOAD); shotsFired = 0; }
                break;

            case SHOOT_PRELOAD:
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    setLaunchPower(launchPower);
                    launchTimer.reset();
                    break;
                }
                if (shotsFired < 3) { shootOneShot(); }
                else if (skipIntakeThree()) {
                    follower.followPath(driveShootIntakeN, true);
                    robot.intakeMotor.setPower(1); robot.transferMotor.setPower(-1);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKE_N);
                } else {
                    follower.followPath(driveShootIntakeThree, true);
                    robot.intakeMotor.setPower(1); robot.transferMotor.setPower(-1);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKE_3);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKE_3:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    robot.transferMotor.setPower(-1);
                    follower.followPath(driveIntakeThreeShoot, true);
                    setPathState(PathState.DRIVE_INTAKE_3_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKE_3_SHOOTPOS:
                if (!follower.isBusy()) {
                    setLaunchPower(launchPower); robot.transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES); shotsFired = 0;
                }
                break;

            case SHOOT_SAMPLES:
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    robot.intakeMotor.setPower(0);
                    setLaunchPower(launchPower);
                    launchTimer.reset();
                    break;
                }
                if (shotsFired < 3) { shootOneShot(); }
                else {
                    follower.followPath(driveShootIntakeN, true);
                    robot.intakeMotor.setPower(1); robot.transferMotor.setPower(-1);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKE_N);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKE_N:
                if (pathTimer.getElapsedTimeSeconds() > 3.0) {
                    robot.transferMotor.setPower(-1);
                    follower.followPath(driveIntakeNShoot, true);
                    setPathState(PathState.DRIVE_INTAKE_N_SHOOTPOS);
                }
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    robot.transferMotor.setPower(-1);
                    follower.followPath(driveIntakeNShoot, true);
                    setPathState(PathState.DRIVE_INTAKE_N_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKE_N_SHOOTPOS:
                if (!follower.isBusy()) {
                    setLaunchPower(launchPower); robot.intakeMotor.setPower(0); robot.transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES); shotsFired = 0;
                }
                break;

            default:
                telemetry.addLine("No State Set");
                break;
        }
    }

    private void shootOneShot() {
        if (shotsFired >= 3) return;

        if (launchTimer.seconds() > launchTimeoutTime) {
            launchState = LaunchState.WAITING;
            launchTimer.reset();
            telemetry.addLine("WARNING: Launch state timeout");
            return;
        }
        double elapsed = launchTimer.seconds();
        setLaunchPower(launchPower);
        switch (launchState) {
            case WAITING:
                telemetry.addLine("Waiting");
                if (elapsed > launchWaitTime) {
                    launchState = LaunchState.TRANSFER;
                    launchTimer.reset();
                }
                break;

            case TRANSFER:
                robot.transferMotor.setPower(1);
                telemetry.addLine("Shot " + (shotsFired + 1) + ": Firing");
                if (elapsed > launchTransferTime) {
                    launchState = LaunchState.SHOOT;
                    launchTimer.reset();
                }
                break;

            case SHOOT:
                robot.Trigger.setPosition(triggerShootPos);
                robot.transferMotor.setPower(0);
                if (elapsed > launchShootTime) {
                    launchState = LaunchState.RESET;
                    launchTimer.reset();
                }
                break;

            case RESET:
                robot.Trigger.setPosition(triggerStartPos);
                telemetry.addLine("Shot " + (shotsFired + 1) + ": Resetting");
                if (elapsed > launchResetTime) {
                    launchState = LaunchState.WAITING;
                    ++shotsFired;
                    launchTimer.reset();
                }
                break;

            default:
                // This should never happen, but recover gracefully
                telemetry.addLine("ERROR: Unknown launchState: " + launchState);
                launchState = LaunchState.WAITING;
                launchTimer.reset();
                break;
        }
    }
}