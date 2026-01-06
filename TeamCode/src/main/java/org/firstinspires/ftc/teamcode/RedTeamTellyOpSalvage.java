package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Util.RobotPosition.TeamColorRED;
import static org.firstinspires.ftc.teamcode.Util.RobotPosition.getRobotCoordinates;
import static org.firstinspires.ftc.teamcode.launcher.AutoFirePower.autoLaunch;
import static org.firstinspires.ftc.teamcode.limelight.LimelightPosSetting.limelightposupdate;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.positioning.odometry.FieldOrientedDriving;

@TeleOp(name="RedTellyOPSalvage")

public class RedTeamTellyOpSalvage extends LinearOpMode {

    ElapsedTime timer = new ElapsedTime();
    ElapsedTime rapidtime = new ElapsedTime();
    private Servo DrumServo;
    private Servo FiringPinServo;
    private GoBildaPinpointDriver odomhub;
    private DcMotorEx Scooper;
    private DcMotor BR;
    private DcMotor BL;
    private DcMotor FL;
    private DcMotor FR;
    private DcMotorEx LauncherFL;

    protected boolean isred = true;

    @Override
    public void runOpMode() {
        TeamColorRED = isred;

        boolean fullunloadflag = false;
        double lasttime = timer.milliseconds();

        double[] drumBallColors = {0, 0, 0};
        double targetdrumangle = 0;
        double targetfiringpinangle = 1;
        boolean firing = false;

        double motortargetspeedradians = 0;
        double currentleftmotorvelocity = 0;
        double currentrightmotorvelocity = 0;

        double firingpinnullposition = .98;

        double rapidloop = 0;

        Limelight3A limelight = hardwareMap.get(Limelight3A.class, "limelight");// INitilizes the limelights
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        odomhub = hardwareMap.get(GoBildaPinpointDriver.class, "odomhub");

        DrumServo = hardwareMap.get(Servo.class, "DrumServo");
        FiringPinServo = hardwareMap.get(Servo.class, "FiringPinServo");


        BR = hardwareMap.get(DcMotor.class, "BR");
        BL = hardwareMap.get(DcMotor.class, "BL");
        FL = hardwareMap.get(DcMotor.class, "FL");
        FR = hardwareMap.get(DcMotor.class, "FR");
        FL.setDirection(DcMotor.Direction.REVERSE); //so I don't have to think about
        BL.setDirection(DcMotor.Direction.REVERSE); //inverting later
        FR.setDirection(DcMotor.Direction.FORWARD); //should generally do whenever motors
        BR.setDirection(DcMotor.Direction.FORWARD);

        LauncherFL = hardwareMap.get(DcMotorEx.class, "LauncherFL");
        Scooper = hardwareMap.get(DcMotorEx.class, "Scooper");


        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(0);
        limelight.start();

        //zeros the encoders and sets the run using encoder mode
        //VariablePowerLauncherAbstract.initializeLauncher(LauncherFL,LauncherFR);
        LauncherFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LauncherFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Scooper.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Scooper.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        odomhub.initialize();
        odomhub.resetPosAndIMU();   // resets encoders and IMU




        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            double leftstickinputy = gamepad1.left_stick_y; // Forward/backward negative because it's naturally inverted
            double leftstickinputx = gamepad1.left_stick_x; // side to side
            double targetturn = gamepad1.right_stick_x; // Turning

            //slowermovement for the guner
            double leftstickinputy2 = gamepad2.left_stick_y / 6;
            double leftstickinputx2 = gamepad2.left_stick_x / 6;
            double targetturn2 = gamepad2.right_stick_x / 4;

            double[] currentrobotlocation = getRobotCoordinates();

            double currentrelativeheading = odomhub.getHeading(AngleUnit.RADIANS);


            //Calls FieldOrientedDriving function and sets motor power
            double[] motorpowerarray = FieldOrientedDriving.fieldOrientedMath(leftstickinputy, -leftstickinputx, targetturn, currentrelativeheading);
            double[] smallmotorpowerarray = FieldOrientedDriving.fieldOrientedMath(leftstickinputy2, -leftstickinputx2, targetturn2, currentrelativeheading);

            double BRmotorpower = motorpowerarray[0] + smallmotorpowerarray[0];
            double BLmotorpower = motorpowerarray[1] + smallmotorpowerarray[1];
            double FRmotorpower = motorpowerarray[2] + smallmotorpowerarray[2];
            double FLmotorpower = motorpowerarray[3] + smallmotorpowerarray[3];


            //assigns power to each motor based on gamepad inputs
            BR.setPower(BRmotorpower);
            BL.setPower(BLmotorpower);
            FR.setPower(FRmotorpower);
            FL.setPower(FLmotorpower);


            //limelight
            limelightposupdate(limelight);

            //auto rangeing commands
            motortargetspeedradians = autoLaunch();
            if (gamepad2.left_trigger >= 0.3) {
                motortargetspeedradians = 0;
            }
            // sets the velocity of the motors
            LauncherFL.setVelocity(motortargetspeedradians, AngleUnit.RADIANS);

            if (gamepad2.a) {//firing bin controls
                targetfiringpinangle = firingpinnullposition - .32  ;
            } else {
                targetfiringpinangle = firingpinnullposition;
                targetdrumangle = gamepad2.x ? .1 ://Firing angles
                                gamepad2.y ? .42 :
                                gamepad2.b ? .76 :
                                gamepad1.x ? .27 ://loading angles
                                gamepad1.y ? .6 :
                                gamepad1.b ? .92 :
                                    targetdrumangle;
            }
            double[] firingpositions = {.1,.42,.76};

            //MAG Dump code
            //test time offsets
            if (gamepad2.dpad_up) {//use timesrs use cancle when not held
                rapidtime.reset();
                fullunloadflag = true;
            }
            rapidloop =  rapidtime.milliseconds();
            if (fullunloadflag) {
                // state machine this for next comp
                if (rapidloop < 500) DrumServo.setPosition(firingpositions[0]);// 500 ms for drumb
                if (rapidloop > 500 && rapidloop < 700) FiringPinServo.setPosition(.98 - .32);// 200ms for firing in
                if (rapidloop > 700 && rapidloop < 900) FiringPinServo.setPosition(.98);

                if (rapidloop > 900 && rapidloop < 1400) DrumServo.setPosition(firingpositions[0]);
                if (rapidloop > 1400 && rapidloop < 1600) FiringPinServo.setPosition(.98 - .32);
                if (rapidloop > 1600 && rapidloop < 1800) FiringPinServo.setPosition(.98);

                if (rapidloop > 1800 && rapidloop < 2300) DrumServo.setPosition(firingpositions[0]);
                if (rapidloop > 2500 && rapidloop < 2700) FiringPinServo.setPosition(.98 - .32);
                if (rapidloop > 2700 && rapidloop < 2900) FiringPinServo.setPosition(.98);

            }

            DrumServo.setPosition(targetdrumangle);
            FiringPinServo.setPosition(targetfiringpinangle);

            if (gamepad1.dpad_down) odomhub.resetPosAndIMU();   // resets encoders and IMU

            //wihle holding down the bumpers moves the intake
            if (gamepad1.left_bumper) Scooper.setVelocity(999, AngleUnit.RADIANS);
            else if (gamepad1.right_bumper) Scooper.setVelocity(-999, AngleUnit.RADIANS);
            else Scooper.setVelocity(0, AngleUnit.RADIANS);

            odomhub.update();

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
            telemetry.addData("firingpin target servoangle", targetfiringpinangle);
            telemetry.addData("rotation perceived", currentrelativeheading);
            telemetry.addData("robotx", currentrobotlocation[0]);
            telemetry.addData("roboty", currentrobotlocation[1]);*/
            telemetry.update();
            }
        }
    }

