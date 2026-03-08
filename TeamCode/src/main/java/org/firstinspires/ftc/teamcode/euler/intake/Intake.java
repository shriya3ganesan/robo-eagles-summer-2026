package org.firstinspires.ftc.teamcode.euler.intake;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Intake {

    final DcMotor intakeMotor;
    public Intake(DcMotor intakeMotor) {
        this.intakeMotor = intakeMotor;
    }

    public void toggleCollect() {
        if(getState() == IntakeState.IDLE || getState() == IntakeState.EJECT) {
            intakeMotor.setPower(0.7);
        } else {
            stop();
        }
    }

    public void toggleEject(){
        if(getState() == IntakeState.IDLE || getState() == IntakeState.COLLECT ){
            intakeMotor.setPower(-1);
        } else {
            stop();
        }
    }

    private void stop() {
        intakeMotor.setPower(0);
    }

    public IntakeState getState(){
        if(intakeMotor.getPower() == 0){
            return IntakeState.IDLE;
        }
        if(intakeMotor.getPower() < 0) {
            return IntakeState.EJECT;
        } else {
            return IntakeState.COLLECT;
        }
    }


}
