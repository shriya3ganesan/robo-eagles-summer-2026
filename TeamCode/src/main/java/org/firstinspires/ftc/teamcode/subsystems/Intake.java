package org.firstinspires.ftc.teamcode.subsystems;
import com.qualcomm.robotcore.hardware.HardwareMap;


import static org.firstinspires.ftc.teamcode.myConstants.intake.intakeLIntakePos;
import static org.firstinspires.ftc.teamcode.myConstants.intake.intakeLoutakePos;
import static org.firstinspires.ftc.teamcode.myConstants.intake.intakeRIntakePos;
import static org.firstinspires.ftc.teamcode.myConstants.intake.intakeRoutakePos;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;

import com.qualcomm.robotcore.hardware.Servo;
@Config

public class Intake {

    DcMotor intakeMotor;
    Servo intakeR;
    Servo intakeL;
    CRServo suctionL;
    CRServo suctionR;

    public Intake (HardwareMap hardwareMap){
        intakeMotor = hardwareMap.get(DcMotor.class, "intake");

        intakeR = hardwareMap.get(Servo.class, "intakeR");
        intakeL = hardwareMap.get(Servo.class, "intakeL");

        suctionL = hardwareMap.get(CRServo.class, "suctionL");
        suctionR = hardwareMap.get(CRServo.class, "suctionR");

        intakeMotor.setDirection(DcMotor.Direction.REVERSE);

        intakeL.setPosition(intakeLIntakePos);
        intakeR.setPosition(intakeRIntakePos);
    }

    //Useful Funcitons

    public void intakeBalls(){
        intakeL.setPosition(intakeLIntakePos);
        intakeR.setPosition(intakeRIntakePos);


    }
    public void shootBalls(){
        intakeL.setPosition(intakeLoutakePos);
        intakeR.setPosition(intakeRoutakePos);

    }

    public void stop(){


    }
public void forwardIntakeDirection(){
    intakeMotor.setPower(1);
    suctionL.setPower(-1);
    suctionR.setPower(1);

}
    public void reverseIntakeDirection(){
        intakeMotor.setPower(-1);
        suctionL.setPower(1);
        suctionR.setPower(-1);
    }

    //setters
    public void setIntakePower(double x){
        intakeMotor.setPower(x);
    }

    public void setIntakeLPosition (double x){
        intakeL.setPosition(x);
    }
    public void setIntakeRPosition (double x){
        intakeR.setPosition(x);
    }
    public void setSuctionLPower(double x){
        suctionL.setPower(x);
    }
    public void setSuctionRPower(double x){
        suctionR.setPower(x);
    }

}
