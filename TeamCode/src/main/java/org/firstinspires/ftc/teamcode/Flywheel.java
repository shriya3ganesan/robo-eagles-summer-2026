package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class Flywheel {
    private OpMode theOpMode;
    private ElapsedTime runtime = new ElapsedTime();
    DcMotorEx JudahBlack;
    public DcMotorEx BottomCollection;
    public DcMotorEx TopCollection;
    public Servo Finger;
    public double tpr = 28;
    public double threshold = 1400;
public void init(HardwareMap hardwareMap){
    JudahBlack = hardwareMap.get(DcMotorEx.class, "JudahBlack");
    JudahBlack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    TopCollection = hardwareMap.get(  DcMotorEx.class, "topCollection");
    TopCollection.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    BottomCollection = hardwareMap.get(DcMotorEx.class, "bottomCollection");
    BottomCollection.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    Finger = hardwareMap.get(Servo.class, "Finger");
}
    public Flywheel(OpMode opMode) {
        theOpMode = opMode;

    }
    public void flyWheelAuto(double speed, double timeoutS){
        runtime.reset();
        JudahBlack.setVelocity(-speed);
        while(((LinearOpMode)theOpMode).opModeIsActive()&& runtime.seconds()< timeoutS) {
            theOpMode.telemetry.addData("speed", JudahBlack.getVelocity());
            theOpMode.telemetry.update();
            if ((Math.abs(JudahBlack.getVelocity())) >= (Math.abs(speed)- 20)){
                TopCollection.setPower(.22);
                BottomCollection.setPower(.22);
            } else if (Math.abs(JudahBlack.getVelocity()) <= (Math.abs(speed) - 20)) {
                TopCollection.setPower(0);
            }
        }
        JudahBlack.setVelocity(0);
        TopCollection.setPower(0);

        BottomCollection.setPower(0);


    }


}