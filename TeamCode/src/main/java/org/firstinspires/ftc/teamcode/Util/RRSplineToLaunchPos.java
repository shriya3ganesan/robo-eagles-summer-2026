package org.firstinspires.ftc.teamcode.Util;

import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.FIELD_HALF;
import static org.firstinspires.ftc.teamcode.Util.constants.FIELD.mtoin;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;

import org.firstinspires.ftc.teamcode.MecanumDrive;

public class RRSplineToLaunchPos {

    public static void splineLaunchPos(MecanumDrive drive, Pose2d pose, double angledeg, int mirrory) {
        /*
        Action move = drive.actionBuilder(pose)
                .splineToConstantHeading(new Vector2d(-22.5, 22 * mirrory),0)
                .build();
        Actions.runBlocking(move);
        */
        Action movedirectlybackout = drive.actionBuilder(pose)
                .splineToConstantHeading(new Vector2d( -22.5,22* mirrory),pose.heading)
                .build();
        Actions.runBlocking(movedirectlybackout);

        pose = drive.localizer.getPose();
        Action rotate = drive.actionBuilder(pose)
                .turnTo(Math.toRadians(angledeg))
                .build();
        Actions.runBlocking(rotate);

         /*
        Action move = drive.actionBuilder(pose)
                .strafeTo(new Vector2d(-22.5,22))
                .build();
        Actions.runBlocking(move);*/
    }
    public static void returnToPreLoadY(MecanumDrive drive, Pose2d pose, double preloadingy,int mirrory){
        Action movedirectlybackout = drive.actionBuilder(pose)
            .splineToConstantHeading(new Vector2d( pose.position.x,preloadingy * mirrory),pose.heading)
            .build();
        Actions.runBlocking(movedirectlybackout);
    }
}