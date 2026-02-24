package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_0;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_1;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_2;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.predictedmotifx;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.predictedmotify;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.shoottargetx;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.shoottargetyblue;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.shoottargetyred;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinfiringposition;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinnullposition;
import static org.firstinspires.ftc.teamcode.limelight.LimelightMotifSetting.limelightMotifSet;
import static java.lang.Math.atan2;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;
import org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots;


enum AutoStateCrosby{
    Initialization,
    MoveToLaunchZone,
    GetMotif,
    RotateToTarget,
    FireWithMotif,
    MoveToLoadZone,
    LoadBalls

}
@Config

public class Crosby3ballbackshots extends LinearOpMode {


    public static double movetolaunchzonetangent = 0;
    public static double targetangnle = 218;
    public static double movetolaunchzonexlimit = 22.5;
    public static double movetolaunchzoneylimit = 25;
    public static double motiftimelimitms = 5;
    public static double autoaimvariancelimiter = 300000;
    public static double preloadingy = 10;
    public static double ballpickupy = 60;

    public static double loadonex = -10;
    public static double loadtwox = 36;
    public static double loadthreex = 30;
    public static double intaketimelength = 200;
    public static double loadtangent = 45;
    public static double universalrotationoffset = -90;

    private AutoState currentstate = AutoState.Initialization;
    private int loadcount = 1;

    DrumSlots targetslotforautolaunch = null;

    int motifcyclingautofirearray = 0;

    protected boolean isred;



    @Override
    public void runOpMode(){
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        Servo DrumServo = hardwareMap.get(Servo.class, "DrumServo");
        Servo FiringPinServo = hardwareMap.get(Servo.class, "FiringPinServo");


        DcMotor rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftFront.setDirection(DcMotor.Direction.REVERSE); //so I don't have to think about
        leftBack.setDirection(DcMotor.Direction.REVERSE); //inverting later
        rightFront.setDirection(DcMotor.Direction.FORWARD); //should generally do whenever motors
        rightBack.setDirection(DcMotor.Direction.FORWARD);

        DcMotorEx LauncherFL = hardwareMap.get(DcMotorEx.class, "LauncherFL");
        DcMotorEx Scooper = hardwareMap.get(DcMotorEx.class, "Scooper");


        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        NormalizedColorSensor colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        NormalizedColorSensor colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");



        LauncherFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LauncherFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Scooper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Scooper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pinpoint.initialize();
        pinpoint.resetPosAndIMU();

        int mirrory = (isred) ? 1 : -1;

        telemetry.addData("mirroiry",mirrory);
        Pose2d currentpose = new Pose2d(63, 15 * mirrory, 0);
        MecanumDrive drive = new MecanumDrive(  hardwareMap, currentpose);

        DrumSlots[] drumslotarray = {SLOT_0,SLOT_1,SLOT_2};


        Balls[] motif = {unknown,unknown,unknown};

        waitForStart();
        while (opModeIsActive()){
            switch (currentstate) {
                case Initialization:
                    telemetry.addLine("initilzaing");
                    telemetry.update();

                    SLOT_0.setLoadedBall(green);
                    SLOT_1.setLoadedBall(purple);
                    SLOT_2.setLoadedBall(purple);

                    LauncherFL.setVelocity(-3.5, AngleUnit.RADIANS);
                    FiringPinServo.setPosition(firingpinnullposition);
                    DrumServo.setPosition(0.1);

                    currentstate = AutoState.MoveToLaunchZone;
                    break;

                case MoveToLaunchZone:
                    telemetry.addLine("moving to launch zone");
                    telemetry.update();

                    if (loadcount == 3) movetolaunchzonetangent = 0;
                    Action drivetolaunchzone = drive.actionBuilder(drive.localizer.getPose())
                            .splineToConstantHeading(new Vector2d(58, 10 * mirrory), movetolaunchzonetangent)
                            .build();
                    Actions.runBlocking(drivetolaunchzone);
                    currentpose = drive.localizer.getPose();


                    currentstate = AutoState.GetMotif;
                    telemetry.addLine("retrying");
                    telemetry.update();
                    break;

                case GetMotif:
                    telemetry.addLine("motifing");
                    telemetry.update();

                    ElapsedTime motiftime = new ElapsedTime();

                    currentpose = drive.localizer.getPose();
                    double motiftargetturn = atan2(predictedmotify - currentpose.position.y, predictedmotifx - currentpose.position.x);
                    Action turnTowardsMotif = drive.actionBuilder(drive.localizer.getPose())
                            .turnTo(motiftargetturn)
                            .build();
                    Actions.runBlocking(turnTowardsMotif);

                    motif = limelightMotifSet(limelight);
                    telemetry.addData("currentmotif0", motif[0]);
                    telemetry.addData("currentmotif1", motif[1]);
                    telemetry.addData("currentmotif2", motif[2]);
                    telemetry.update();

                    //TODO fix this garbage
                    if (motif[0] != unknown || motiftime.milliseconds() > motiftimelimitms) {
                        //if it breaks on time than it sets a fallback motif
                        if (motif[0] == unknown) motif = new Balls[]{purple, purple, green};
                        currentstate = AutoState.RotateToTarget;
                    }
                    telemetry.addLine("retrying");
                    telemetry.update();
                    break;

                case RotateToTarget:

                    telemetry.addLine("rotating to target");
                    telemetry.update();

                    double arctanintermediatex = shoottargetx - drive.localizer.getPose().position.x;
                    double arctanintermediatey;
                    double usedy;

                    if (isred) usedy = shoottargetyred;
                    else usedy = shoottargetyblue;

                    arctanintermediatey = usedy - drive.localizer.getPose().position.y;

                    double robotautoaimtargetangle = atan2(arctanintermediatey, arctanintermediatex);

                    Action rotatetotargetangle = drive.actionBuilder(drive.localizer.getPose())
                            .turnTo(robotautoaimtargetangle - Math.PI + Math.toRadians(universalrotationoffset))
                            .build();
                    Actions.runBlocking(rotatetotargetangle);
                    //if (Math.abs(drive.localizer.getPose().heading.toDouble() - robotautoaimtargetangle) < autoaimvariancelimiter) {
                        //currentstate = AutoState.FireWithMotif;
                    //}
                    telemetry.addLine("retrying");
                    telemetry.update();
                    break;

                case FireWithMotif:
                    telemetry.addLine("firing start");
                    telemetry.update();
                    ElapsedTime shootingtime = new ElapsedTime();

                    //TODO check if this works now

                    if (targetslotforautolaunch != null) {
                        DrumServo.setPosition(targetslotforautolaunch.shootPosition);
                        sleep(600);
                        FiringPinServo.setPosition(firingpinfiringposition);
                        sleep(200);
                        FiringPinServo.setPosition(firingpinnullposition);
                        sleep(200);
                    }
                    if (targetslotforautolaunch == null) {
                        Balls currentcolor = motif[motifcyclingautofirearray];
                        motifcyclingautofirearray++;
                        if (motifcyclingautofirearray > 2) motifcyclingautofirearray = 2;

                        for (DrumSlots slot : drumslotarray) {
                            if (slot.getLoadedBall() == currentcolor) {
                                targetslotforautolaunch = slot;
                                slot.setLoadedBall(unknown);
                                break;
                            }
                        }
                    }

                        /*
                    }
                    for(Balls currentcolor : motif){
                        for(DrumSlots slot : drumslotarray){
                            if (currentcolor == slot.getLoadedBall()){
                                telemetry.addData("target color", currentcolor);
                                telemetry.addData("selected slot color", slot.getLoadedBall());

                                slot.setLoadedBall(unknown);

                                DrumServo.setPosition(slot.shootPosition);
                                sleep(600);
                                FiringPinServo.setPosition(firingpinfiringposition);
                                sleep(200);
                                FiringPinServo.setPosition(firingpinnullposition);
                                sleep(200);

                            }
                        }
                    }*/
                    for (DrumSlots slot : drumslotarray) {

                        if (slot.getLoadedBall() != unknown) break;
                        else currentstate = AutoState.MoveToLoadZone;
                    }

                    telemetry.addData("s0", SLOT_0.getLoadedBall());
                    telemetry.addData("s1", SLOT_1.getLoadedBall());
                    telemetry.addData("s2", SLOT_2.getLoadedBall());
                    telemetry.update();
                    //sleep(5000);
                    DrumServo.setPosition(SLOT_0.loadPosition);
                    break;

                case MoveToLoadZone:
                    telemetry.addLine("moving to load zone");
                    telemetry.addData("loadcount", loadcount);
                    telemetry.update();

                    double zonetargetx = 0;
                    switch (loadcount) {
                        case 1:
                            zonetargetx = loadonex;
                            break;
                        case 2:
                            zonetargetx = loadtwox;
                            break;
                        case 3:
                            zonetargetx = loadthreex;
                            break;
                    }

                    Action DriveToBeforeLoad = drive.actionBuilder(drive.localizer.getPose())
                            .splineToConstantHeading(new Vector2d(63, 35 * mirrory), Math.toRadians(loadtangent * mirrory))
                            .build();
                    Actions.runBlocking(DriveToBeforeLoad);
                    Action TurnToBeforeLoad = drive.actionBuilder(drive.localizer.getPose())
                            .turnTo(Math.toRadians(90) * mirrory + Math.toRadians(universalrotationoffset))
                            .build();
                    Actions.runBlocking(TurnToBeforeLoad);
                    //if (Math.abs(drive.localizer.getPose().heading.toDouble() - Math.PI / 2) > 3)
                    Actions.runBlocking(TurnToBeforeLoad);
                    telemetry.update();
                    currentstate = AutoState.LoadBalls;
                    break;
/*
                case LoadBalls:
                    telemetry.addLine("loading balls");
                    telemetry.update();

                    ElapsedTime timer = new ElapsedTime();
                    ElapsedTime intakendingetimer = new ElapsedTime();

                    double[] drumlocations = {SLOT_0.loadPosition,SLOT_1.loadPosition, SLOT_2.loadPosition};

                    Action pickUpLoadOne = new ParallelAction(
                            drive.actionBuilder(drive.localizer.getPose())
                                    .splineToConstantHeading(new Vector2d(drive.localizer.getPose().position.x, ballpickupy * mirrory),Math.toRadians(90))
                                    .build(),
                            new Action() {
                                Boolean fullyloaded = false;
                                int targetdrumslotload = 0;
                                double targetdrumangleload = 0.27;
                                @Override
                                public boolean run(@NonNull TelemetryPacket telemetryPacket) {

                                    Balls loadedcolor = colorDetection(colorSensor1, colorSensor2);

                                    Scooper.setVelocity(-999, AngleUnit.RADIANS);
                                    telemetry.addData("loaded color", loadedcolor);
                                    telemetry.addData("targetdrumslot", targetdrumslotload);
                                    telemetry.addData("timer", timer.milliseconds());
                                    telemetry.addData("drjum imcrament", targetdrumslotload);
                                    telemetry.update();
                                    if (loadedcolor != unknown && targetdrumslotload < 3 && timer.milliseconds() > 600) {
                                        timer.reset();
                                        //drumBallColors[targetdrumslotload] = loadedcolor;
                                        telemetry.addLine("ball Detected");
                                        targetdrumslotload++;
                                    }
                                    fullyloaded = intakendingetimer.milliseconds() < intaketimelength;
                                    targetdrumslotload = Math.min(targetdrumslotload, 2);
                                    targetdrumangleload = drumlocations[targetdrumslotload];
                                    DrumServo.setPosition(targetdrumangleload);

                                    return fullyloaded;//true when below the timer
                                }
                            }
                    );
                    Actions.runBlocking(pickUpLoadOne);


                    Scooper.setVelocity(0);
                    Action PullOut = drive.actionBuilder(drive.localizer.getPose())
                            .lineToYConstantHeading(30 * mirrory)
                            .build();
                    Actions.runBlocking(PullOut);


                    switch (loadcount){
                        case 1:
                            SLOT_0.setLoadedBall(purple);
                            SLOT_1.setLoadedBall(purple);
                            SLOT_2.setLoadedBall(green);
                        case 2:
                            SLOT_0.setLoadedBall(purple);
                            SLOT_1.setLoadedBall(green);
                            SLOT_2.setLoadedBall(purple);
                        case 3:
                            SLOT_0.setLoadedBall(green);
                            SLOT_1.setLoadedBall(purple);
                            SLOT_2.setLoadedBall(purple);
                    }
                    loadcount++;

                    telemetry.update();
                    currentstate = AutoState.MoveToLaunchZone;
                    break;
            }


            if (loadcount == 4) break;
            */
            }



        }


    }



}

