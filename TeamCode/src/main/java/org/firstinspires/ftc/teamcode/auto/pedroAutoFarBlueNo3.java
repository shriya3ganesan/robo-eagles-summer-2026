package org.firstinspires.ftc.teamcode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Configurable
@Autonomous(name="PedroAutoFarBlueNo3", group="Autonomous")
public class pedroAutoFarBlueNo3 extends FarAuto {
    @Override
    protected void definePoses() {
        startPose   = new Pose(87.47252747252746, 8,                   Math.toRadians(90)).mirror();
        shootPose   = new Pose(86.7,              20.3,                Math.toRadians(shootAngle)).mirror();
        intakeThree = new Pose(140.08206455817657, 34.28571428571429,  Math.toRadians(0)).mirror();
        intakeN     = new Pose(142.57660626029653, 9.043956043956046,  Math.toRadians(0)).mirror();

        controlPointToIntakeThree = new Pose(82.08791208791206, 40.95604395604395, Math.toRadians(0)).mirror();
    }

    @Override
    protected boolean skipIntakeThree() { return true; }
}