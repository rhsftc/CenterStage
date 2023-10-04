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

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/*
 * Demonstrates an empty iterative OpMode
 */
@TeleOp(name = "Encoder Test", group = "Test")
//@Disabled
public class EncoderTest extends OpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private GamepadEx gamePad;
    private DcMotorEx motor;
    private final float ENCODER_INCREMENT = 1120f * 5; // Ten revolutions
    private final double MAX_VELOCITY = 2900;
    private PIDFCoefficients pidfVelocityCoefficients;

    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        telemetry.addLine("Dpad Up and Dpad Down");
        telemetry.update();
        gamePad = new GamepadEx(gamepad1);
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setDirection(DcMotorSimple.Direction.FORWARD);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        pidfVelocityCoefficients = motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        pidfVelocityCoefficients.p = 1.063f;
        pidfVelocityCoefficients.i = 1.063f;
        pidfVelocityCoefficients.f = 10.63f;
        motor.setVelocityPIDFCoefficients(pidfVelocityCoefficients.p, pidfVelocityCoefficients.i, pidfVelocityCoefficients.d, pidfVelocityCoefficients.f);
        motor.setPositionPIDFCoefficients(8f);
        motor.setTargetPositionTolerance(10);
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
        gamePad.readButtons();
        telemetry.addData("Status", "Run Time: " + runtime.toString());
        if (gamePad.wasJustReleased(GamepadKeys.Button.DPAD_UP)) {
            motor.setTargetPosition((int) (motor.getCurrentPosition() + ENCODER_INCREMENT));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(.8 * MAX_VELOCITY);
        }

        if (gamePad.wasJustReleased(GamepadKeys.Button.DPAD_DOWN)) {
            motor.setTargetPosition((int) (motor.getCurrentPosition() - ENCODER_INCREMENT));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(.8 * MAX_VELOCITY);
        }

        telemetry.addData("Target", "%d", motor.getTargetPosition());
        telemetry.addData("Position", "%d", motor.getCurrentPosition());
        telemetry.addData("Velocity", "%6.2f", motor.getVelocity());
        telemetry.addData("Power", "%6.2f", motor.getPower());
        telemetry.addData("Busy", motor.isBusy());
        telemetry.addData("Current (milli amps)", "%6.2f", motor.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("", motor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER));
        telemetry.update();
    }

    /**
     * This method will be called once, when this OpMode is stopped.
     * <p>
     * Your ability to control hardware from this method will be limited.
     */
    @Override
    public void stop() {
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }
}
