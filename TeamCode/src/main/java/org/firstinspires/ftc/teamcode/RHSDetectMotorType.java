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

import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.arcrobotics.ftclib.hardware.motors.MotorEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.configuration.typecontainers.MotorConfigurationType;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * This OpMode ramps a single motor speed up and down repeatedly until Stop is pressed.
 * The code is structured as a LinearOpMode
 * <p>
 * This code assumes a DC motor configured with the name "motor" as is found on a Robot.
 * <p>
 * INCREMENT sets how much to increase/decrease the power each cycle
 * CYCLE_MS sets the update period.
 * <p>
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */
@TeleOp(name = "Detect Motor Type", group = "Test")
//@Disabled
public class RHSDetectMotorType extends LinearOpMode {

    private static final double INCREMENT = 0.01;     // amount to ramp motor each CYCLE_MS cycle
    private static final int CYCLE_MS = 50;     // period of each cycle
    private static final double MAX_FWD = 1.0;     // Maximum FWD power applied to motor
    private static final double MAX_REV = -1.0;     // Maximum REV power applied to motor
    private ElapsedTime timer;

    // Define class members
    private MotorEx motor;
    // Motor information
    private HardwareDevice.Manufacturer manufacturer;
    private double maxRPM;
    double maxVelocity = 0;
    private double velocity;
    private double achievableCountsPerRev;
    private double countsPerRev;
    private double ticsPerRev;
    private double encoderRate;
    private int currentPosition;
    private double revolutions;
    private double current;
    private MotorConfigurationType type;
    private double power = 0;
    private boolean rampUp = true;

    @Override
    public void runOpMode() {
        timer = new ElapsedTime();
        // Change the text in quotes to match any motor name on your robot.
        motor = new MotorEx(hardwareMap, "motor");
        motor.setRunMode(Motor.RunMode.VelocityControl);
        motor.stopAndResetEncoder();
        type = motor.motorEx.getMotorType();
        manufacturer = motor.motor.getManufacturer();
        maxRPM = motor.getMaxRPM();
        achievableCountsPerRev = motor.ACHIEVABLE_MAX_TICKS_PER_SECOND;
        countsPerRev = motor.getCPR();
        encoderRate = motor.getRate();
        ticsPerRev = motor.motorEx.getMotorType().getTicksPerRev();
        getMaxVelocity();

        // Wait for the start button
        telemetry.addData("Device Type", type.getName());
        telemetry.addData("Manufacturer", manufacturer);
        telemetry.addData("RPM", "%5.2f", maxRPM);
        telemetry.addData("Max Velocity", maxVelocity);
        telemetry.addData("Achievable Tics", "%5.2f", achievableCountsPerRev);
        telemetry.addData("Tics Per Rev", "%5.2f", ticsPerRev);
        telemetry.addData("Tic Rate", "%5.2f", countsPerRev);
        telemetry.addData("Encoder Rate", "%5.2f", encoderRate);
        telemetry.addData(">", "Press Start to run Motors.");
        telemetry.update();
        waitForStart();

        // Ramp motor speeds till stop pressed.
        while (opModeIsActive()) {

            // Ramp the motors, according to the rampUp variable.
            if (rampUp) {
                // Keep stepping up until we hit the max value.
                power += INCREMENT;
                if (power >= MAX_FWD) {
                    power = MAX_FWD;
                    rampUp = !rampUp;   // Switch ramp direction
                }
            } else {
                // Keep stepping down until we hit the min value.
                power -= INCREMENT;
                if (power <= MAX_REV) {
                    power = MAX_REV;
                    rampUp = !rampUp;  // Switch ramp direction
                }
            }

            // Set the motor to the new power and pause;
            motor.set(power);
            sleep(CYCLE_MS);

            countsPerRev = motor.getCPR();
            encoderRate = motor.getRate();
            velocity = motor.getVelocity();
            currentPosition = motor.getCurrentPosition();
            revolutions = motor.encoder.getRevolutions();
            current = motor.motorEx.getCurrent(CurrentUnit.MILLIAMPS);

            // Display the current value
            telemetry.addData("Motor Power", "%5.2f", power);
            telemetry.addData("Tick Rate", "%5.2f", countsPerRev);
            telemetry.addData("Encoder Rate", "%5.2f", encoderRate);
            telemetry.addData("Velocity", "%5.2f", velocity);
            telemetry.addData("Acceleration", "%5.2f", motor.getAcceleration());
            telemetry.addData("Current Position", currentPosition);
            telemetry.addData("Revolutions", "%5.2f", revolutions);
            telemetry.addData("Current (milli amps)", "%5.2f", current);
            telemetry.addData(">", "Press Stop to end test.");
            telemetry.update();
            idle();
        }

        // Turn off motor and signal done;
        motor.set(0);
        telemetry.addData(">", "Done");
        telemetry.update();
    }

    private void getMaxVelocity() {
        timer.reset();
        motor.set(1);
        while (timer.seconds() < 4 && !isStopRequested()) {
            velocity = motor.getVelocity();
            if (velocity > maxVelocity) {
                maxVelocity = velocity;
            }
            telemetry.addData("current velocity", velocity);
            telemetry.addData("maximum velocity", maxVelocity);
            telemetry.update();
        }

        motor.stopMotor();
    }
}
