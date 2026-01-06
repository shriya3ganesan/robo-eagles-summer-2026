package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.RRSplineToLaunchPos.returnToPreLoadY;
import static org.firstinspires.ftc.teamcode.Util.RRSplineToLaunchPos.splineLaunchPos;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinfiringposition;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinnullposition;
import static org.firstinspires.ftc.teamcode.limelight.LimelightMotifSetting.limelightMotifSet;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.Util.Enum.Balls;


@Config

public class NineBallAuto extends LinearOpMode {
    ElapsedTime timer = new ElapsedTime();
    ElapsedTime intaketimer = new ElapsedTime();
    private DcMotorEx Scooper;
    private Servo DrumServo;
    private Servo FiringPinServo;
    private DcMotorEx LauncherFL;
    public static double intaketimelength = 2.25;
    public static double firingangle = 130;//142 is what it should be based off of logic

    public static double preloadingy = 18;
    public static double ballpickupy = 60;

    public static double loadonex = 0;
    public static double loadtwox = 16;
    public static double loadthreex = 30;

    protected boolean isred;



    @Override
    public void runOpMode() {
        int mirrory;
        if (isred){
            mirrory = 1;
        } else{
            mirrory = -1;
            firingangle = 144;
        }
        ballpickupy = mirrory * ballpickupy;
        preloadingy = mirrory * preloadingy;
        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        Scooper = hardwareMap.get(DcMotorEx.class, "Scooper");

        DrumServo = hardwareMap.get(Servo.class, "DrumServo");
        FiringPinServo = hardwareMap.get(Servo.class, "FiringPinServo");

        LauncherFL = hardwareMap.get(DcMotorEx.class, "LauncherFL");

        NormalizedColorSensor colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        NormalizedColorSensor colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");


        double[] firingpositions = {.1,.42,.76};
        double motortargetspeedradians = 0;
        Balls[] drumBallColors = {purple,green,purple};
        Balls[] targetballcolors = {green,purple,purple};

        double[] drumlocations = {.27,.6,.92};

        double targetdrumangle = .27;
        double targetfiringpinangle = 1;
        int targetdrumslot = 0;


        double firingpositionstarget;


        // y = x
        // x = -y
        Pose2d startPose = new Pose2d(-62, 37.5 * mirrory, 0);
        MecanumDrive drive = new MecanumDrive(  hardwareMap,  startPose);

        waitForStart();////////////////////////////////////////////////////

        FiringPinServo.setPosition(firingpinnullposition);
        motortargetspeedradians = 3.5;//autoLaunch();
        LauncherFL.setVelocity(-motortargetspeedradians, AngleUnit.RADIANS);
        DrumServo.setPosition(0.1);



        splineLaunchPos(drive,startPose,-160*mirrory,mirrory);//-170
        startPose = drive.localizer.getPose();


        //limelight get pattern

        targetballcolors = limelightMotifSet(limelight);


        // TODO add error handling default is to not attempt to fire and replase the testing only hardset
        if (targetballcolors[0] == unknown){
            targetballcolors = new Balls[]{purple,purple,green};
        }
        telemetry.addData("motif",targetballcolors[0]);
        telemetry.addData("motif",targetballcolors[1]);
        telemetry.addData("motif",targetballcolors[2]);

        splineLaunchPos(drive,startPose,firingangle*mirrory,mirrory);

        startPose = drive.localizer.getPose();

        telemetry.addLine("moving to first load");
        telemetry.addData("x" , startPose.position.x);
        telemetry.addData("y", startPose.position.y);
        telemetry.update();





        fireWithPattern(drumBallColors,targetballcolors,firingpositions);

        //launch with pattern

        Action movetoloadingone = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(loadonex, preloadingy),Math.toRadians(85*mirrory))
                .build();
        Actions.runBlocking(movetoloadingone);
        startPose = drive.localizer.getPose();



        //intake
        loadingMove(drive,startPose,drumlocations, colorSensor1, colorSensor2);
        startPose = drive.localizer.getPose();
        drumBallColors = new Balls[]{Balls.purple, Balls.purple, Balls.green};

        //returnToPreLoadY(drive,startPose,preloadingy, mirrory);
        startPose = drive.localizer.getPose();

        splineLaunchPos(drive,startPose,firingangle * mirrory,mirrory);
        startPose = drive.localizer.getPose();

        fireWithPattern(drumBallColors,targetballcolors,firingpositions);

        Action moveToLoadingTwo = drive.actionBuilder(startPose)
                .splineTo(new Vector2d(loadtwox,preloadingy ),Math.toRadians(85*mirrory))
                .build();
        Actions.runBlocking(moveToLoadingTwo);
        startPose = drive.localizer.getPose();

        loadingMove(drive,startPose,drumlocations, colorSensor1, colorSensor2);
        drumBallColors = new Balls[]{Balls.purple, Balls.green, purple};
        startPose = drive.localizer.getPose();

        returnToPreLoadY(drive,startPose,preloadingy, mirrory);

        splineLaunchPos(drive,startPose,firingangle * mirrory,mirrory);
        startPose = drive.localizer.getPose();

        splineLaunchPos(drive,startPose,firingangle * mirrory,mirrory);
        startPose = drive.localizer.getPose();

        fireWithPattern(drumBallColors,targetballcolors,firingpositions);

        Action EndSpot = drive.actionBuilder(startPose)
                .splineToConstantHeading(new Vector2d(loadtwox,preloadingy * mirrory),Math.toRadians(85))
                .build();
        Actions.runBlocking(EndSpot);
    }
    protected void fireWithPattern(Balls[] drumBallColors, Balls[] targetballcolors, double[] firingpositions){
        for(int i = 0; i <= 2; i++){
            for(int j = 0; j<= 2; j++) {
                if (drumBallColors[j] == targetballcolors[i]){
                    drumBallColors[j] = unknown;
                    double targetdrumangle = firingpositions[j];
                    DrumServo.setPosition(targetdrumangle);
                    sleep(600);
                    FiringPinServo.setPosition(firingpinfiringposition);
                    sleep(200);
                    FiringPinServo.setPosition(firingpinnullposition);
                    sleep(400);

                }
                telemetry.addData("in slot color", drumBallColors[j]);
                telemetry.addData("lookingfor color", targetballcolors[i]);
                telemetry.update();

            }

        }
        DrumServo.setPosition(0.27);
    }
    protected void loadingMove(MecanumDrive drive, Pose2d startPose,double[] drumlocations,NormalizedColorSensor colorSensor1, NormalizedColorSensor colorSensor2){
        intaketimer.reset();
        Action pickUpLoadOne = new ParallelAction(
                drive.actionBuilder(startPose)
                        .splineToConstantHeading(new Vector2d(startPose.position.x, ballpickupy),Math.toRadians(90))
                        .build(),
                new Action() {
                    int targetdrumslotload = 0;
                    double targetdrumangleload = 0.27;
                    @Override
                    public boolean run(@NonNull TelemetryPacket telemetryPacket) {


                        // TODO make sure switch this back to color sensing for comp
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
                        targetdrumslotload = Math.min(targetdrumslotload, 2);
                        targetdrumangleload = drumlocations[targetdrumslotload];
                        DrumServo.setPosition(targetdrumangleload);

                        return (intaketimer.seconds() < intaketimelength);//true when below the timer
                    }
                }
        );
        Actions.runBlocking(pickUpLoadOne);
        Scooper.setVelocity(0);
    }
}
