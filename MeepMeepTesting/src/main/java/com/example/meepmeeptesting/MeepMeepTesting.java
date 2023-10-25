package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {

    public static void main(String[] args) {
        Pose2d homePose = new Pose2d(-35, -60,Math.toRadians(90));
        MeepMeep meepMeep = new MeepMeep(640);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(homePose)
                                .forward(30)
                                .back(5)
                                .turn(Math.toRadians(-90))
                                .forward(80)
                                .splineToLinearHeading(homePose, Math.toRadians(90))
                                .splineTo(new Vector2d(-46, -30), Math.toRadians(90))
                                .back(5)
                                .turn(Math.toRadians(-90))
                                .forward(90)
                                .splineToLinearHeading(homePose, Math.toRadians(90))
                                .splineTo(new Vector2d(-24, -30), Math.toRadians(90))
                                .back(5)
                                .turn(Math.toRadians(-90))
                                .forward(70)
                                .splineToLinearHeading(homePose, Math.toRadians(90))
                                .build()
                );

        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}