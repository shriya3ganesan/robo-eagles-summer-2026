package org.firstinspires.ftc.teamcode.pedroPathing;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;

import org.firstinspires.ftc.teamcode.Teleop.OuttakeSetup;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous
@Configurable // Panels
public class farredAuto extends OpMode {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths; // Paths defined in the Paths class
    private ElapsedTime shootTimer; // Timer for shooting pause

    private Timer servoTimer;
    private OuttakeSetup outtake = new OuttakeSetup();
    private boolean timerReset;

    private double velocityToleranceHigh, velocityToleranceLow, currentVelocity, targetTPS;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(90.127, 7.227, Math.toRadians(90)));
        paths = new Paths(follower); // Build paths
        shootTimer = new ElapsedTime();
        pathState = 0;
        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void start() {
        pathState = 1; // Start the autonomous when play is pressed
        follower.followPath(paths.Path1);
        outtake.init(hardwareMap);
    }

    @Override
    public void loop() {
        follower.update(); // Update Pedro Pathing
        autonomousPathUpdate(); // Update autonomous state machine
        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }

    public static class Paths {
        public PathChain Path1;
        public PathChain Path2;

        public Paths(Follower follower) {
            Path1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(90.127, 7.227),
                                    new Pose(89.546, 15.705)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(75))
                    .build();

            Path2 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(89.546, 15.705),
                                    new Pose(91.999, 33.600)
                            )
                    ).setTangentHeadingInterpolation()
                    .build();
        }
    }

    public void autonomousPathUpdate() {
        switch (pathState) {
            case 1: // Follow Path1 (red path)
                follower.followPath(paths.Path2, true);
                pathState = 2;
                break;

            case 2: // Pause to shoot at end of red path
                // TODO: Add your shooting mechanism code here
                // For example: shooter.shoot();
                if (!follower.isBusy()) {
                    launch_balls();
                    targetTPS = 1550.0;
                    setFlywheelTPS(targetTPS);
                    if (shootTimer.seconds() >= 2.0 && timerReset) { // Adjust pause duration as needed (2 seconds)
                        resetServo();
                        pathState = 3;
                        follower.followPath(paths.Path2, true);
                    }
                }
                break;

            case 3: // Follow Path2 (tan/beige path)
                if (!follower.isBusy()) {
                    pathState = 4;
                }
                break;

            case 4: // Autonomous complete
                // Do nothing or add end-of-auto actions
                break;
        }
    }
    public void launch_balls() {
        if (!timerReset) {
            // releases ball
            outtake.Servo_release();
            servoTimer.resetTimer();
            timerReset = true;
            outtake.setIntakePow(0.0);
        }
        if (timerReset && servoTimer.getElapsedTimeSeconds() > 0.2){
            outtake.setIntakePow(1.0);
        }
    }
    public void resetServo() {
        timerReset = false;
        // blocks balls
        outtake.Servo_reset();
    }
    public void setFlywheelTPS(double targetTPS) {
        velocityToleranceHigh = targetTPS * 1.03;
        velocityToleranceLow = targetTPS * 0.97;
        outtake.setOuttakeVelocity(targetTPS);
    }
    public boolean flywheelIsCorrectSpeed() {
        currentVelocity = (outtake.getOuttakeVelocityLeft()+ outtake.getOuttakeVelocityRight())/2;
        return (currentVelocity <= velocityToleranceHigh && currentVelocity >= velocityToleranceLow);
    }
}
