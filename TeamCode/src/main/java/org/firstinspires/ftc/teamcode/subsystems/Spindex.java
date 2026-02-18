package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.teamcode.myConstants.Spindex.KdSpindex;
import static org.firstinspires.ftc.teamcode.myConstants.Spindex.KiSpindex;
import static org.firstinspires.ftc.teamcode.myConstants.Spindex.KpSpindex;
import static org.firstinspires.ftc.teamcode.myConstants.Turret.KdTurret;
import static org.firstinspires.ftc.teamcode.myConstants.Turret.KiTurret;
import static org.firstinspires.ftc.teamcode.myConstants.Turret.KpTurret;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Spindex {
    AnalogInput encoder;
    DcMotor spindex;
    private double lastError = 0;
    private double integralSum = 0;
    private final double dt = 0.02;

    boolean atPosition = false;


    public Spindex(HardwareMap hardwareMap) {
        encoder = hardwareMap.get(AnalogInput.class, "encoder");
        spindex = hardwareMap.get(DcMotor.class, "spindexer");
        spindex.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        spindex.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

    }

    public void setSpindexPower(double power){
        spindex.setPower(power);
    }

    public double getPosition(){
        return encoder.getVoltage() / 3.2 * 360;
    }

    public void goToPosition(double target) {
        double position = encoder.getVoltage() / 3.2 * 360;
        double error = target-position;
        while (error > 180)  error -= 360;
        while (error < -180) error += 360;





        if (Math.abs(error) < 2) {
            spindex.setPower(0);
            lastError = 0;
            integralSum =0;

        } else {

            //  shortest-path error

            double derivative = (error - lastError) / dt;

            double output = (KpSpindex * error) + (KiSpindex * integralSum) + (KdSpindex * derivative);

            // Optional but recommended
            output = Math.max(-0.75, Math.min(0.75, output));

            spindex.setPower(output);

            lastError = error;


        }


    }
}
