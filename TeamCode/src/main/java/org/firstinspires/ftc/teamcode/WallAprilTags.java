/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.processors.PixelStackAprilTags;
import org.firstinspires.ftc.vision.apriltag.AprilTagPoseFtc;

/*
 * Test op mode for wall april tags.
 */
@TeleOp(name = "Wall April Tags", group = "Camera")
//@Disabled
public class WallAprilTags extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private PixelStackAprilTags pixelStackAprilTags;
    private Servo angleServo;    //    Angle servo
    private final double LAUNCHING_SERVO_POSITION = 0.4;
    private final double WAITING_SERVO_POSITION = 0.64;
    private final double TAG_RANGE = 72;
    private double launchedAngle = 0;


    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        angleServo = hardwareMap.get(Servo.class, "servo1");
        angleServo.setPosition(0);
        angleServo.setPosition(WAITING_SERVO_POSITION);
        pixelStackAprilTags = new PixelStackAprilTags(hardwareMap);
        pixelStackAprilTags.init();
        telemetry.addData("Status", "Initialized");
    }

    /**
     * This method will be called repeatedly during the period between when
     * the init button is pressed and when the play button is pressed (or the
     * OpMode is stopped).
     */
    @Override
    public void init_loop() {
    }

    /**
     * This method will be called once, when the play button is pressed.
     */
    @Override
    public void start() {
        runtime.reset();
    }

    /**
     * This method will be called repeatedly during the period between when
     * the play button is pressed and when the OpMode is stopped.
     */
    @Override
    public void loop() {
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        launchedAngle = launchDrone();
        if (pixelStackAprilTags.detectTags() != null) {
            AprilTagPoseFtc aprilTagPoseFtc = pixelStackAprilTags.getAprilTagPose();
            telemetry.addData("Tag Id", pixelStackAprilTags.getDetection().id);
            telemetry.addData("Tag Name", pixelStackAprilTags.getDetection().metadata.name);
            telemetry.addData("Range", aprilTagPoseFtc.range);
            telemetry.addData("Bearing", aprilTagPoseFtc.bearing);
            telemetry.addData("x", aprilTagPoseFtc.x);
            telemetry.addData("y", aprilTagPoseFtc.y);
            telemetry.addData("z", aprilTagPoseFtc.z);
            telemetry.addData("Yaw", aprilTagPoseFtc.yaw);
        } else {
            telemetry.addLine("No tags found");
        }

        telemetry.addData("Launch angle", launchedAngle);
    }

    /**
     * This method will be called once, when this OpMode is stopped.
     * <p>
     * Your ability to control hardware from this method will be limited.
     */
    @Override
    public void stop() {

    }

    // Map the range to the tag to the angle range of the launcher.
    // Return the servo position.
    private double launchDrone() {
        double launchRange = pixelStackAprilTags.getRangeToWall();
        // 0 means use the default angle, we did not see the tag.
        double launchAngle = launchRange == 0 ? LAUNCHING_SERVO_POSITION : getLaunchPosition(launchRange);
        angleServo.setPosition(launchAngle);
        return launchAngle;
    }

    private double getLaunchPosition(double range) {
        return ((1 - ((range - 72) / (TAG_RANGE)) *
                (LAUNCHING_SERVO_POSITION - WAITING_SERVO_POSITION)) + WAITING_SERVO_POSITION);
    }
}
