package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.arcrobotics.ftclib.hardware.ServoEx;
import com.arcrobotics.ftclib.hardware.SimpleServo;
import com.arcrobotics.ftclib.hardware.motors.CRServo;
import com.arcrobotics.ftclib.hardware.motors.Motor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Test Servo", group = "Servo")
//@Disabled
public class TestServo extends OpMode {
    static final double INCREMENT = 0.1;     // amount to slew servo each button press.
    private double minScale = 0;
    private double maxScale = 1;
    private ServoEx servo;
    private double position = 0;
    private GamepadEx gamepad;

    @Override
    public void init() {
        // Make the name match your config file and robot.
        servo = new SimpleServo(hardwareMap, "servo1", 0, 90);
        gamepad = new GamepadEx(gamepad1);
        showTelemetry();
        telemetry.update();
    }

    @Override
    public void start() {
        servo.setPosition(position);
    }

    @Override
    public void loop() {
        gamepad.readButtons();
        if (gamepad.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
            position = -1;
        }

        if (gamepad.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
            position = 1;
        }

        if (gamepad.wasJustReleased(GamepadKeys.Button.Y)) {
            position = 0;
        }

        if (gamepad.wasJustPressed(GamepadKeys.Button.A)) {
            minScale = 0;
            maxScale = 1;
            servo.setRange(minScale, maxScale);
        }

        if (gamepad.wasJustReleased(GamepadKeys.Button.DPAD_UP) && position < .8) {
            position += INCREMENT;
        }

        if (gamepad.wasJustReleased(GamepadKeys.Button.DPAD_DOWN) && position > -.8) {
            position -= INCREMENT;
        }

        if (gamepad.wasJustReleased(GamepadKeys.Button.X)) {
            minScale = position;
            servo.setRange(minScale, maxScale);
        }

        if (gamepad.wasJustReleased(GamepadKeys.Button.B)) {
            maxScale = position;
            servo.setRange(minScale, maxScale);
        }

        servo.setPosition(position);
        showTelemetry();
    }

    private void showTelemetry() {
        telemetry.addLine("Left bumper = 0");
        telemetry.addLine("Right bumper = 1");
        telemetry.addLine("Y = .5 (middle)");
        telemetry.addLine("X = Set current position as Min. scale");
        telemetry.addLine("B = Set current position as Max. scale");
        telemetry.addLine("A = Reset scale to 0-1");
        telemetry.addLine("Dpad up: Increase position");
        telemetry.addLine("Dpad down: Decrease position");
        telemetry.addData("Scale", "%5.2f - %5.2f", minScale, maxScale);
        telemetry.addData("Position", position);
    }
}
