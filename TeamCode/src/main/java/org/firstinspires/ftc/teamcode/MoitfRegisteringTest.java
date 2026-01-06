package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.TeamColorRED;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.getRobotCoordinates;
import static org.firstinspires.ftc.teamcode.launcher.AutoFirePower.autoLaunch;
import static org.firstinspires.ftc.teamcode.limelight.LimelightMotifSetting.limelightMotifSet;
import static org.firstinspires.ftc.teamcode.limelight.LimelightPosSetting.limelightposupdate;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;
import org.firstinspires.ftc.teamcode.positioning.odometry.FieldOrientedDriving;


@Autonomous(name = "motif test")
public class MoitfRegisteringTest extends LinearOpMode {

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime rapidtime = new ElapsedTime();
    private Servo DrumServo;
    private Servo FiringPinServo;
    private GoBildaPinpointDriver pinpoint;
    private DcMotorEx Scooper;
    private DcMotor rightBack;
    private DcMotor leftBack;
    private DcMotor leftFront;
    private DcMotor rightFront;
    private DcMotorEx LauncherFL;

    protected boolean isred;

    @Override
    public void runOpMode() {
        TeamColorRED = isred;

        double[] firingpositions = {.1,.42,.76};

        double[] drumlocations = {.27,.6,.92};
        Balls[] drumBallColors = {unknown, unknown, unknown};
        double targetdrumangle = .27;
        double targetfiringpinangle = 1;
        int targetdrumslot = 0;

        boolean fullunloadflag = false;
        double lasttime = timer.milliseconds();

        double[] drumBallColorsarray = {0, 0, 0};
        boolean firing = false;

        double motortargetspeedradians = 0;
        double currentleftmotorvelocity = 0;
        double currentrightmotorvelocity = 0;

        double firingpinnullposition = .98;

        double rapidloop = 0;

        int firingpositionstarget =0;

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        DrumServo = hardwareMap.get(Servo.class, "DrumServo");
        FiringPinServo = hardwareMap.get(Servo.class, "FiringPinServo");


        rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftFront.setDirection(DcMotor.Direction.REVERSE); //so I don't have to think about
        leftBack.setDirection(DcMotor.Direction.REVERSE); //inverting later
        rightFront.setDirection(DcMotor.Direction.FORWARD); //should generally do whenever motors
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        LauncherFL = hardwareMap.get(DcMotorEx.class, "LauncherFL");
        Scooper = hardwareMap.get(DcMotorEx.class, "Scooper");


        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        NormalizedColorSensor colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        NormalizedColorSensor colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");


        //zeros the encoders and sets the run using encoder mode
        //VariablePowerLauncherAbstract.initializeLauncher(LauncherFL,LauncherFR);
        LauncherFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LauncherFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Scooper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Scooper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pinpoint.initialize();
        pinpoint.resetPosAndIMU();   // resets encoders and IMU




        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            Balls[] motif = limelightMotifSet(limelight);
            telemetry.addData("1",motif[0]);
            telemetry.addData("2",motif[1]);
            telemetry.addData("3",motif[2]);
            telemetry.update();
        }
    }
}

