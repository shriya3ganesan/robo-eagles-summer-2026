package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.NonOpModes.colorsensing.ColorSensingFunctions.colorDetection;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.green;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.purple;
import static org.firstinspires.ftc.teamcode.Util.Enum.Balls.unknown;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.AllSlots;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_0;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_1;
import static org.firstinspires.ftc.teamcode.Util.Enum.DrumSlots.SLOT_2;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.FiringPinIn;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.FiringPinOut;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.None;
import static org.firstinspires.ftc.teamcode.Util.Enum.States.TurnToBall;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.TeamColorRED;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.getRobotCoordinates;
import static org.firstinspires.ftc.teamcode.Util.constants.PART_NAMES.drumslotarray;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinfiringposition;
import static org.firstinspires.ftc.teamcode.Util.constants.RobotStats.firingpinnullposition;
import static org.firstinspires.ftc.teamcode.launcher.AutoFirePower.autoLaunch;
import static org.firstinspires.ftc.teamcode.limelight.LimelightMotifSetting.limelightMotifSet;
import static org.firstinspires.ftc.teamcode.limelight.LimelightPosSetting.limelightposupdate;

import com.acmerobotics.roadrunner.Pose2d;
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
import org.firstinspires.ftc.teamcode.Util.Enum.States;
import org.firstinspires.ftc.teamcode.positioning.odometry.FieldOrientedDriving;


public class BaseOpModeAutoAimCrosby extends LinearOpMode {

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime rapidtime = new ElapsedTime();

    protected boolean isred;

    States currentstate = None;

    @Override
    public void runOpMode() {

        SLOT_0.setLoadedBall(unknown);
        SLOT_1.setLoadedBall(unknown);
        SLOT_2.setLoadedBall(unknown);

        TeamColorRED = isred;

        int motifcyclingautofirearray = 0;

        Balls[] motif = {purple, green, purple};
        double[] firingpositions = {.76,.1,.42};

        DrumSlots targetslotforautolaunch = null;

        boolean autoAimLast = false;

        double[] drumlocations = {.27,.6,.92};
        double targetdrumangle = .27;
        double targetfiringpinangle = firingpinnullposition;
        int targetdrumslot = 0;

        boolean fullunloadflag = false;

        double motortargetspeedradians;
        double currentleftmotorvelocity;

        int firingpositionstarget = 0;

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        Servo drumServo = hardwareMap.get(Servo.class, "DrumServo");
        Servo firingPinServo = hardwareMap.get(Servo.class, "FiringPinServo");

        DcMotor rightBack = hardwareMap.get(DcMotor.class, "rightBack");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "leftBack");
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        leftFront.setDirection(DcMotor.Direction.REVERSE); //so I don't have to think about
        leftBack.setDirection(DcMotor.Direction.REVERSE); //inverting later
        rightFront.setDirection(DcMotor.Direction.FORWARD); //should generally do whenever motors
        rightBack.setDirection(DcMotor.Direction.FORWARD);
        rightBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBack.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFront.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        DcMotorEx launcherFL = hardwareMap.get(DcMotorEx.class, "LauncherFL");
        DcMotorEx scooper = hardwareMap.get(DcMotorEx.class, "Scooper");




        NormalizedColorSensor colorSensor1 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor1");
        NormalizedColorSensor colorSensor2 = hardwareMap.get(NormalizedColorSensor.class, "colorSensor2");


        //zeros the encoders and sets the run using encoder mode
        //VariablePowerLauncherAbstract.initializeLauncher(LauncherFL,LauncherFR);
        launcherFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        launcherFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        scooper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        scooper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        pinpoint.initialize();
        pinpoint.resetPosAndIMU();   // resets encoders and IMU




        telemetry.addData("Status", "Initialized");
        telemetry.update();

        Pose2d startPose = new Pose2d(0,0,0);
        MecanumDrive drive = new MecanumDrive(hardwareMap, startPose);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        //TODO find a better way to distijnguish between the while and not wihle
        while (opModeIsActive()) {



            double leftstickinputy = gamepad1.left_stick_y; // Forward/backward negative because it's naturally inverted
            double leftstickinputx = gamepad1.left_stick_x; // side to side
            double targetturn = gamepad1.right_stick_x; // Turning

            //slowermovement for the guner
            double leftstickinputy2 = gamepad2.left_stick_y / 4;
            double leftstickinputx2 = gamepad2.left_stick_x / 4;
            double targetturn2 = gamepad2.right_stick_x / 4;

            Balls loadedcolor = colorDetection(colorSensor1, colorSensor2);

            Balls[] newmotif = limelightMotifSet(limelight);

            if(newmotif[0] != unknown){
                motif = newmotif;
            }

            double[] currentrobotlocation = getRobotCoordinates();

            Pose2d roadrunerlocation = drive.localizer.getPose();
            double currentrelativeheading = roadrunerlocation.heading.toDouble();



            //Calls FieldOrientedDriving function and sets motor power
            double[] motorpowerarray = FieldOrientedDriving.fieldOrientedMath(leftstickinputy, -leftstickinputx, targetturn, currentrelativeheading);
            double[] smallmotorpowerarray = FieldOrientedDriving.fieldOrientedMath(leftstickinputy2, -leftstickinputx2, targetturn2, currentrelativeheading);

            double BRmotorpower = motorpowerarray[0] + smallmotorpowerarray[0];
            double BLmotorpower = motorpowerarray[1] + smallmotorpowerarray[1];
            double FRmotorpower = motorpowerarray[2] + smallmotorpowerarray[2];
            double FLmotorpower = motorpowerarray[3] + smallmotorpowerarray[3];


            //assigns power to each motor based on gamepad inputs
            rightBack.setPower(BRmotorpower);
            leftBack.setPower(BLmotorpower);
            rightFront.setPower(FRmotorpower);
            leftFront.setPower(FLmotorpower);


            //limelight
            limelightposupdate(limelight);

            //sets motor speeds
            motortargetspeedradians = autoLaunch();
            if (gamepad1.left_trigger >= 0.3) {
                motortargetspeedradians = 0;
            }
            launcherFL.setVelocity(-motortargetspeedradians, AngleUnit.RADIANS);
            currentleftmotorvelocity = launcherFL.getVelocity(AngleUnit.RADIANS);




            // sets the velocity of the motors

            if( Math.abs(currentleftmotorvelocity - motortargetspeedradians) < .02){
                gamepad2.rumble( .75,.75,50);
            }
            if(gamepad1.a){
                targetdrumangle = SLOT_0.loadPosition;
                targetdrumslot = 0;
                SLOT_0.setLoadedBall(unknown);
                SLOT_1.setLoadedBall(unknown);
                SLOT_2.setLoadedBall(unknown);

            }


            if (gamepad2.a) {//firing bin controls
                targetfiringpinangle = firingpinnullposition - .32  ;

            } else {
                if(!fullunloadflag)targetfiringpinangle = firingpinnullposition;

                firingpositionstarget = gamepad2.x ? 0:
                                 gamepad2.y ? 1:
                                 gamepad2.b ? 2:
                                 firingpositionstarget;

            }


            if (gamepad1.left_bumper){
                scooper.setVelocity(999, AngleUnit.RADIANS);
            }
            else if (gamepad1.right_bumper){
                scooper.setVelocity(-999, AngleUnit.RADIANS);
                if (loadedcolor != unknown && targetdrumslot < 3 && timer.milliseconds() > 600){
                    timer.reset();
                    AllSlots.setFromSlot(loadedcolor,targetdrumslot);
                    telemetry.addLine("ball Detected");
                    targetdrumslot++;
                }else if (loadedcolor != unknown && targetdrumslot < 4 && timer.milliseconds() > 600){
                    scooper.setVelocity(999,AngleUnit.RADIANS);
                }
                
                for(DrumSlots lookslot : drumslotarray){
                    telemetry.addData("loadedball", lookslot.getLoadedBall());
                    if(lookslot.getLoadedBall() == unknown){
                        break;
                    }
                    gamepad1.rumble(.75,.75,100);
                }
                
            }
            else scooper.setVelocity(0, AngleUnit.RADIANS);


            if(gamepad1.right_bumper){
                targetdrumslot = Math.min(targetdrumslot,2);
                targetdrumangle = drumlocations[targetdrumslot];
            }else if (gamepad2.x || gamepad2.y || gamepad2.b){
                targetdrumangle = firingpositions[firingpositionstarget];
            }




            telemetry.addData("selected slot",targetdrumslot);

            //MAG Dump code
            //test time offsets
            if (gamepad1.dpad_up && rapidtime.milliseconds() >= 500) {//use timesrs use cancle when not held
                rapidtime.reset();
                fullunloadflag = true;

                currentstate = TurnToBall;
            }

            telemetry.addData("attempting to fire all balls",fullunloadflag);
            if (fullunloadflag) {
                double currenttime = rapidtime.milliseconds();
                //TODO FIX THESE IF TIMES THEY ARE WRONG

                if(targetslotforautolaunch != null) {
                    switch (currentstate) {
                        case TurnToBall:
                            targetdrumangle = targetslotforautolaunch.shootPosition;
                            if (currenttime > 600) currentstate = FiringPinIn;
                            break;

                        case FiringPinIn:
                            targetfiringpinangle = firingpinfiringposition;
                            if (currenttime > 800) currentstate = FiringPinOut;
                            break;

                        case FiringPinOut:
                            targetfiringpinangle = firingpinnullposition;
                            if (currenttime > 1000) {
                                currentstate = TurnToBall;
                                targetslotforautolaunch.setLoadedBall(unknown);
                                targetslotforautolaunch = null;
                                rapidtime.reset();
                            }
                            break;
                    }
                }
                if (targetslotforautolaunch == null) {
                    Balls currentcolor = motif[motifcyclingautofirearray];
                    motifcyclingautofirearray++;
                    if(motifcyclingautofirearray > 2) motifcyclingautofirearray = 2;

                    for (DrumSlots slot : drumslotarray) {
                        if (slot.getLoadedBall() == currentcolor) {
                            targetslotforautolaunch = slot;
                            break;
                        }
                    }
                }
                for(DrumSlots slot : drumslotarray) {

                    if (slot.getLoadedBall() != unknown) break;
                    if (slot == SLOT_2) {
                        fullunloadflag = false;
                        targetdrumslot = 0;
                        motifcyclingautofirearray = 0;
                        break;
                    }
                }
            }

            if(gamepad2.left_bumper) targetfiringpinangle = firingpinnullposition;
            drumServo.setPosition(targetdrumangle);
            telemetry.addData("drumangle", targetdrumangle);
            firingPinServo.setPosition(targetfiringpinangle);

            Pose2d currentpose = drive.localizer.getPose();
            if (gamepad1.dpad_down) drive.localizer.setPose( new Pose2d(currentpose.position,0));   // resets encoders and IMU

            //while holding down the bumpers moves the intake
            if (gamepad1.left_bumper) scooper.setVelocity(999, AngleUnit.RADIANS);
            else if (gamepad1.right_bumper) scooper.setVelocity(-999, AngleUnit.RADIANS);
            else scooper.setVelocity(0, AngleUnit.RADIANS);

            /*boolean autoAimPressed = gamepad2.right_bumper && !autoAimLast;
            autoAimLast = gamepad2.right_bumper;

            if (autoAimPressed){

                double[] robotcoordinates = RobotPosition.getRobotCoordinates();



                double arctanintermediatex = shoottargetx-robotcoordinates[0];
                double arctanintermediatey;
                double usedy;

                if (TeamColorRED) usedy = shoottargetyred;
                else usedy = shoottargetyblue;

                arctanintermediatey = usedy - robotcoordinates[1];

                double robotautoaimtargetangle = atan2(arctanintermediatey, arctanintermediatex);


                Action rotatetotargetangle = drive.actionBuilder(drive.localizer.getPose())
                        .turnTo(robotautoaimtargetangle)
                        .build();
                Actions.runBlocking(rotatetotargetangle);


                //double robotnewrotation = atan2(startPose.heading.imag, startPose.heading.real);
                //pinpoint.setHeading(robotnewrotation,AngleUnit.RADIANS);

                robotcoordinates = RobotPosition.getRobotCoordinates();
                telemetry.addData("robotx", robotcoordinates[0]);
                telemetry.addData("roboty", robotcoordinates[1]);

                telemetry.addData("actual end rotation",currentrelativeheading);
                telemetry.addData("robat aurot aim target angle", robotautoaimtargetangle);


                fullunloadflag = true;
                rapidtime.reset();
            }//*/


            drive.localizer.update();


            double[] robotcoordinates = getRobotCoordinates();
            telemetry.addData("robotx", robotcoordinates[0]);
            telemetry.addData("roboty", robotcoordinates[1]);
            telemetry.addData("robot timer",rapidtime.milliseconds());

            telemetry.addData("loaded ball1",SLOT_0.getLoadedBall().name());
            telemetry.addData("loaded ball2",SLOT_1.getLoadedBall().name());
            telemetry.addData("loaded ball3",SLOT_2.getLoadedBall().name());
            telemetry.addData("moitf ball1",motif[0]);
            telemetry.addData("motif ball2",motif[1]);
            telemetry.addData("motif ball3",motif[2]);

            telemetry.addLine("All Speeds are in Jacks Per Second");
            telemetry.addData("Motors' Target Rate of Rotation ", motortargetspeedradians);
            telemetry.addData("Left Motor Actual Rate of Rotation", currentleftmotorvelocity);
            /*//telemetry.addData("Right Motor Actual Rate of Rotation", currentrightmotorvelocity);
            //telemetry.addData("rightmotorraw", rawrightmotorvelocity);
            telemetry.addData("Left Motor difference in Rate of Rotation", motortargetspeedradians - currentleftmotorvelocity);
            //telemetry.addData("Right Motor difference in Rate of Rotation", motortargetspeedradians+currentrightmotorvelocity);
            //telemetry.addData("Left Motor Speed at Wheel Surface meters per second",currentleftmotorvelocity*launcherwheelradiusm);
            //telemetry.addData("Right Motor Speed at Wheel Surface meters per second",currentrightmotorvelocity*launcherwheelradiusm);
            telemetry.addData("drim target servoangle", targetdrumangle);
            telemetry.addData("firingpin target servoangle", targetfiringpinangle);*/
            telemetry.addData("rotation perceived", currentrelativeheading);
            telemetry.addData("rotation from ll", currentrobotlocation[5]);
            telemetry.addData("robotx", currentrobotlocation[0]);
            telemetry.addData("roboty", currentrobotlocation[1]);

            telemetry.update();

            }
        }
    }

