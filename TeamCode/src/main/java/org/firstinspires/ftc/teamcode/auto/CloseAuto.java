package org.firstinspires.ftc.teamcode.auto;

import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.util.ElapsedTime;

public abstract class CloseAuto extends BaseAuto {

    protected Pose intakeOne, intakeTwo, intakeThree;

    // Control points - override in definePoses() if red/blue differ
    protected Pose controlPointToIntakeTwo   = new Pose(55.51, 54.81, Math.toRadians(185));
    protected Pose controlPointToIntakeThree = new Pose(90, 31.2,     Math.toRadians(185));

    private ElapsedTime launchTimer = new ElapsedTime();

    private PathChain driveStartShoot;
    private PathChain driveShootIntakeOne,   driveIntakeOneShoot;
    private PathChain driveShootIntakeTwo,   driveIntakeTwoShoot;
    private PathChain driveShootIntakeThree, driveIntakeThreeShoot;


    protected double launchTimeoutTime = 1.0; // TIMEOUT watcher
    protected double launchWaitTime = 0.4;    // WAITING phase
    protected double launchTransferTime = 0.5; // TRANSFER phase
    protected double launchShootTime = 0.2;    // SHOOT phase
    protected double launchResetTime = 0.15;   // RESET phase

    public enum PathState {
        DRIVE_STARTPOS_SHOOTPOS,
        SHOOT_PRELOAD,
        DRIVE_SHOOTPOS_INTAKEONE,   DRIVE_INTAKEONE_SHOOTPOS,   SHOOT_SAMPLES_1,
        DRIVE_SHOOTPOS_INTAKETWO,   DRIVE_INTAKETWO_SHOOTPOS,   SHOOT_SAMPLES_2,
        DRIVE_SHOOTPOS_INTAKETHREE, DRIVE_INTAKETHREE_SHOOTPOS, SHOOT_SAMPLES_3,
        DONE
    }

    public enum LaunchState {
        RESET,
        WAITING,
        TRANSFER,
        SHOOT
    }
    protected LaunchState launchState = LaunchState.WAITING;
    protected PathState pathState = PathState.DRIVE_STARTPOS_SHOOTPOS;

    protected void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
        isShooting = false;
    }

    @Override
    protected void buildPaths() {
        driveStartShoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        driveShootIntakeOne = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, intakeOne))
                .setConstantHeadingInterpolation(intakeOne.getHeading())
                .build();
        driveIntakeOneShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeOne, shootPose))
                .setLinearHeadingInterpolation(intakeOne.getHeading(), shootPose.getHeading(), 0.6)
                .build();
        driveShootIntakeTwo = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, controlPointToIntakeTwo, intakeTwo))
                .setConstantHeadingInterpolation(intakeTwo.getHeading())
                .build();
        driveIntakeTwoShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeTwo, shootPose))
                .setLinearHeadingInterpolation(intakeTwo.getHeading(), shootPose.getHeading(), 0.6)
                .build();
        driveShootIntakeThree = follower.pathBuilder()
                .addPath(new BezierCurve(shootPose, controlPointToIntakeThree, intakeThree))
                .setConstantHeadingInterpolation(intakeThree.getHeading())
                .build();
        driveIntakeThreeShoot = follower.pathBuilder()
                .addPath(new BezierLine(intakeThree, shootPose))
                .setLinearHeadingInterpolation(intakeThree.getHeading(), shootPose.getHeading(), 0.6)
                .build();
    }

    @Override
    protected void statePathUpdate() {
        switch (pathState) {
            case DRIVE_STARTPOS_SHOOTPOS:
                if (!isShooting) {
                    follower.followPath(driveStartShoot, true);
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
                if (shotsFired < 3) {
                    shootOneShot();
                }
                else {
                    follower.followPath(driveShootIntakeOne, true);
                    robot.intakeMotor.setPower(1); robot.transferMotor.setPower(-1);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKEONE);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKEONE:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    robot.transferMotor.setPower(-1);
                    follower.followPath(driveIntakeOneShoot, true);
                    setPathState(PathState.DRIVE_INTAKEONE_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKEONE_SHOOTPOS:
                if (!follower.isBusy()) {
                    setLaunchPower(launchPower); robot.transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_1); shotsFired = 0;
                }
                break;

            case SHOOT_SAMPLES_1:
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    robot.intakeMotor.setPower(0);
                    setLaunchPower(launchPower);
                    launchTimer.reset();
                    break;
                }
                if (shotsFired < 3) { shootOneShot(); }
                else {
                    follower.followPath(driveShootIntakeTwo, true);
                    robot.intakeMotor.setPower(1); robot.transferMotor.setPower(-1);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKETWO);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKETWO:
                if (!follower.isBusy() && pathTimer.getElapsedTimeSeconds() > 1.0) {
                    robot.transferMotor.setPower(-1);
                    follower.followPath(driveIntakeTwoShoot, true);
                    setPathState(PathState.DRIVE_INTAKETWO_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKETWO_SHOOTPOS:
                if (!follower.isBusy()) {
                    setLaunchPower(launchPower); robot.intakeMotor.setPower(0); robot.transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_2); shotsFired = 0;
                }
                break;

            case SHOOT_SAMPLES_2:
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    robot.intakeMotor.setPower(0);
                    setLaunchPower(launchPower);
                    launchTimer.reset();
                    break;
                }
                if (shotsFired < 3) { shootOneShot(); }
                else {
                    follower.followPath(driveShootIntakeThree, true);
                    robot.intakeMotor.setPower(1); robot.transferMotor.setPower(-1);
                    setPathState(PathState.DRIVE_SHOOTPOS_INTAKETHREE);
                }
                break;

            case DRIVE_SHOOTPOS_INTAKETHREE:
                if (!follower.isBusy()) {
                    robot.transferMotor.setPower(-1);
                    follower.followPath(driveIntakeThreeShoot, true);
                    setPathState(PathState.DRIVE_INTAKETHREE_SHOOTPOS);
                }
                break;

            case DRIVE_INTAKETHREE_SHOOTPOS:
                if (!follower.isBusy()) {
                    setLaunchPower(launchPower); robot.transferMotor.setPower(0);
                    setPathState(PathState.SHOOT_SAMPLES_3); shotsFired = 0;
                }
                break;

            case SHOOT_SAMPLES_3:
                if (pathTimer.getElapsedTimeSeconds() < spinUpTime) {
                    robot.intakeMotor.setPower(0);
                    setLaunchPower(launchPower);
                    launchTimer.reset();
                    break;
                }
                if (shotsFired < 3) { shootOneShot(); }
                else {
                    robot.launchMotor.setPower(0); robot.intakeMotor.setPower(0); robot.transferMotor.setPower(0);
                    setPathState(PathState.DONE);
                }
                break;

            case DONE:
                telemetry.addLine("Autonomous Complete!");
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