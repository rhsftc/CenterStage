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
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.robotcore.external.navigation.TempUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

import java.util.List;

/*
 * Demonstrates an empty iterative OpMode
 */
@TeleOp(name = "Lynx Module", group = "Motor")
//@Disabled
public class LynxTest extends OpMode {
    private GamepadEx gamePad;
    private DcMotorEx motor;
    private List<LynxModule> lynxModules;
    private ElapsedTime runtime = new ElapsedTime();

    /**
     * This method will be called once, when the INIT button is pressed.
     */
    @Override
    public void init() {
        telemetry.addData("Status", "Initialized");
        gamePad = new GamepadEx(gamepad1);
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        lynxModules = hardwareMap.getAll(LynxModule.class);
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
        if (gamePad.wasJustPressed(GamepadKeys.Button.DPAD_UP)) {
            motor.setTargetPosition((int) (motor.getCurrentPosition() + 1500));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(2800);
        }

        if (gamePad.wasJustPressed(GamepadKeys.Button.DPAD_DOWN)) {
            motor.setTargetPosition((int) (motor.getCurrentPosition() - 1500));
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocity(2800);
        }

        if (gamePad.wasJustPressed(GamepadKeys.Button.Y)) {
            stopAndResetEncoder(motor);
        }

        telemetry.addData("Motor Current", "%6.2f", motor.getCurrent(CurrentUnit.MILLIAMPS));
        telemetry.addData("Modules", lynxModules.size());
        for (LynxModule m : lynxModules) {
            telemetry.addData("Module", m.getDeviceName());
            telemetry.addData("Connection", m.getConnectionInfo());
            telemetry.addData("Input Voltage", "%6.2f", m.getInputVoltage(VoltageUnit.VOLTS));
            telemetry.addData("Aux. Voltage", "%6.2f", m.getAuxiliaryVoltage(VoltageUnit.VOLTS));
            telemetry.addData("Current", "%6.2f", m.getCurrent(CurrentUnit.AMPS));
            telemetry.addData("Temp", "%6.2f", m.getTemperature(TempUnit.FARENHEIT));
        }
    }

    /**
     * This method will be called once, when this OpMode is stopped.
     * <p>
     * Your ability to control hardware from this method will be limited.
     */
    @Override
    public void stop() {

    }

    /**
     * This seems to be the only way to reliably stop a motor and reset the encoder.
     * This wos only tested on a goBilda motor.
     *
     * @param motor
     */
    private void stopAndResetEncoder(DcMotorEx motor) {
        motor.setPower(0);
        motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        while (motor.getCurrentPosition() != 0) {
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        }
    }

}
