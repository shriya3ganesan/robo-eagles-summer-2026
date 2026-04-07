package org.firstinspires.ftc.teamcode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

@Configurable
@Autonomous(name="PedroAutoCloseBlue", group="Autonomous")
public class pedroAutoCloseBlue extends CloseAuto {
    @Override
    protected void definePoses() {
        startPose   = new Pose(20.77, 122.99, Math.toRadians(145));
        shootPose   = new Pose(58.78, 84.27,  Math.toRadians(135));
        intakeOne   = new Pose(21,    84.43,  Math.toRadians(185));
        intakeTwo   = new Pose(16.8,  59,     Math.toRadians(185));
        intakeThree = new Pose(16.3,  35,     Math.toRadians(185));
    }
}