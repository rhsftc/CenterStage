package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .build();

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-36, -62, Math.toRadians(90)))
                // Left
                .splineTo(new Vector2d(-48, -32), Math.toRadians(90))
                .lineToY(-60)
                .turn(Math.toRadians(-90))
                .lineToX(12)
                .splineTo(new Vector2d(50, -30), Math.toRadians(0))
                .stopAndAdd(new SleepAction(1.5))
                .splineTo(new Vector2d(-36, -62), Math.toRadians(90))
                .stopAndAdd(new SleepAction(1.5))
                // Middle
                .lineToY(-32)
                .lineToY(-60)
                .turn(Math.toRadians(-90))
                .lineToX(12)
                .splineTo(new Vector2d(50, -36), Math.toRadians(0))
                .stopAndAdd(new SleepAction(1.5))
                .splineTo(new Vector2d(-36, -62), Math.toRadians(90))
                // right
                .stopAndAdd(new SleepAction(1.5))
                .lineToY(-48)
                .splineTo(new Vector2d(-24, -32), Math.toRadians(90))
                .strafeTo(new Vector2d(-36, -32))
                .setTangent(Math.toRadians(90))
                .lineToY(-60)
                .turn(Math.toRadians(-90))
                .lineToX(12)
                .splineTo(new Vector2d(50, -42), Math.toRadians(0))
                .stopAndAdd(new SleepAction(1.5))
                .splineTo(new Vector2d(-36, -62), Math.toRadians(90))
                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}