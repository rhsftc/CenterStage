package org.firstinspires.ftc.teamcode;

import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.arcrobotics.ftclib.gamepad.GamepadKeys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "Set Velocity PIDF", group = "Motor")
//@Disabled
public class SetVelocityPIDF extends LinearOpMode {
    DcMotorEx motor;
    double currentVelocity;
    double currentPower;
    int targetPosition = 0;
    int currentPosition = 0;
    double maxVelocity = 0.0;
    double runVelocity = 0.0;
    double pidfP = 0;
    double pidfI = 0;
    double pidfD = 0;
    double pidfF = 0;
    ElapsedTime timer;
    private final int VELOCITY_RUN_TIME = 3;
    Datalog dataLog;
    GamepadEx gamepad;
    MotorTest motorTest = MotorTest.VELOCITY;
    EncoderMode encoderMode = EncoderMode.WITH_ENCODER;
    ZeroBehavior zeroBehavior = ZeroBehavior.BRAKE;
    PIDversion piDversion = PIDversion.LEGACY;

    private enum ZeroBehavior {
        BRAKE,
        FLOAT;

        private ZeroBehavior getNext() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private enum EncoderMode {
        WITH_ENCODER,
        WITHOUT_ENCODER;

        private EncoderMode getNext() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private enum PIDversion {
        LEGACY,
        PIDF;

        private PIDversion getNext() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private enum MotorTest {
        VELOCITY,
        POSITION
    }

    @Override
    public void runOpMode() {
        timer = new ElapsedTime();
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        gamepad = new GamepadEx(gamepad1);
        dataLog = new Datalog("pidf");
        while (!opModeIsActive()) {
            gamepad.readButtons();
            if (gamepad.wasJustReleased(GamepadKeys.Button.LEFT_BUMPER)) {
                motorTest = MotorTest.POSITION;
            }

            if (gamepad.wasJustReleased(GamepadKeys.Button.RIGHT_BUMPER)) {
                motorTest = MotorTest.VELOCITY;
            }

            if (gamepad.wasJustReleased(GamepadKeys.Button.X)) {
                encoderMode = encoderMode.getNext();
            }

            if (gamepad.wasJustReleased(GamepadKeys.Button.Y)) {
                zeroBehavior = zeroBehavior.getNext();
            }

            if (gamepad.wasJustReleased(GamepadKeys.Button.A)) {
                piDversion = piDversion.getNext();
            }

            telemetry.addLine("Left bumper: Position test");
            telemetry.addLine("Right bumper: Velocity test");
            telemetry.addLine("X: Encoder");
            telemetry.addLine("Y: Zero Power");
            telemetry.addLine("A: Pid");
            telemetry.addData("Motor Test", motorTest);
            telemetry.addData("", "%s, %s, %s",
                    encoderMode, zeroBehavior, piDversion);
            telemetry.update();
        }

        waitForStart();
        while (opModeIsActive()) {
            timer.reset();
            // Get the max velocity
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setPower(1);
            while (timer.seconds() < 4 && !isStopRequested()) {
                currentVelocity = motor.getVelocity();
                if (currentVelocity > maxVelocity) {
                    maxVelocity = currentVelocity;
                }
                telemetry.addData("current velocity", "%5.2f", currentVelocity);
                telemetry.addData("maximum velocity", "%5.2f", maxVelocity);
                telemetry.update();
            }

            motor.setPower(0);
            // Use max to calculate default PIDF values.
            pidfF = 32767 / maxVelocity;
            pidfP = pidfF * .1;
            pidfI = pidfP * .1;

            sleep(2000);
            if (motorTest == MotorTest.POSITION) {
                runVelocity = maxVelocity * .5f;
                targetPosition = motor.getCurrentPosition() + 8000;
                // Set options selected
                motor.setZeroPowerBehavior(zeroBehavior == ZeroBehavior.FLOAT ? DcMotor.ZeroPowerBehavior.FLOAT : DcMotor.ZeroPowerBehavior.BRAKE);
                if (piDversion == PIDversion.PIDF) {
                    motor.setVelocityPIDFCoefficients(pidfP, pidfI, pidfD, pidfF);
                    motor.setPositionPIDFCoefficients(10);
                    motor.setTargetPositionTolerance(5);
                }

                motor.setTargetPosition(targetPosition);
                motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                motor.setVelocity(runVelocity);
                while (!isStopRequested() && (motor.isBusy() || motor.getVelocity() != 0)) {
                    runPositionTest();
                }
            } else {
                runVelocity = maxVelocity * .5f;
                // Set options selected
                motor.setZeroPowerBehavior(zeroBehavior == ZeroBehavior.FLOAT ? DcMotor.ZeroPowerBehavior.FLOAT : DcMotor.ZeroPowerBehavior.BRAKE);
                motor.setMode(encoderMode == EncoderMode.WITHOUT_ENCODER ? DcMotor.RunMode.RUN_WITHOUT_ENCODER : DcMotor.RunMode.RUN_USING_ENCODER);
                if (piDversion == PIDversion.PIDF) {
                    motor.setVelocityPIDFCoefficients(pidfP, pidfI, pidfD, pidfF);
                }

                motor.setVelocity(runVelocity);
                timer.reset();
                while (!isStopRequested() && timer.seconds() <= VELOCITY_RUN_TIME) {
                    runVelocityTest();
                }

                motor.setPower(0);
                // Wait until motor comes to rest.
                timer.reset();
                while (motor.isBusy() && motor.getVelocity() != 0 && timer.seconds() < 1) {
                    runVelocityTest();
                }
            }

            while (opModeIsActive()) {
                telemetry.addData("Position Error", targetPosition - currentPosition);
                telemetry.addData("PIDF", "P=%g I=%g D=%g F=%g", pidfP, pidfI, pidfD, pidfF);
                telemetry.update();
                idle();
            }
        }
    }

    private void runPositionTest() {
        currentPosition = motor.getCurrentPosition();
        currentVelocity = motor.getVelocity();
        currentPower = motor.getPower();
        telemetry.addData("PIDF", "P=%g I=%g D=%g F=%g", pidfP, pidfI, pidfD, pidfF);
        telemetry.addData("Run to position, velocity ", runVelocity);
        telemetry.addData("Target", "%d", targetPosition);
        telemetry.addData("current position", "%d", currentPosition);
        telemetry.addData("current velocity", currentVelocity);
        telemetry.addData("Power", currentPower);
        telemetry.addData("Busy", motor.isBusy());
        telemetry.update();
        LogPositionData();
    }

    private void runVelocityTest() {
        currentVelocity = motor.getVelocity();
        currentPower = motor.getPower();
        telemetry.addData("current velocity", currentVelocity);
        telemetry.addData("Power", currentPower);
        telemetry.update();
        logVelocityData();
    }

    private void LogPositionData() {
        dataLog.targetPosition.set(targetPosition);
        dataLog.currentPosition.set(currentPosition);
        dataLog.runVelocity.set(runVelocity);
        dataLog.currentPower.set(currentPower);
        dataLog.currentVelocity.set(currentVelocity);
        dataLog.writeLine();
    }

    private void logVelocityData() {
        dataLog.runVelocity.set(runVelocity);
        dataLog.currentVelocity.set(currentVelocity);
        dataLog.currentPower.set(currentPower);
        dataLog.writeLine();
    }

    /*
     * This class encapsulates all the fields that will go into the datalog.
     */
    public static class Datalog {
        // The underlying datalogger object - it cares only about an array of loggable fields
        private final Datalogger datalogger;

        // These are all of the fields that we want in the datalog.
        // Note that order here is NOT important. The order is important in the setFields() call below
        public Datalogger.GenericField runVelocity = new Datalogger.GenericField("Run Velocity");
        public Datalogger.GenericField currentVelocity = new Datalogger.GenericField("Current Velocity");
        public Datalogger.GenericField currentPower = new Datalogger.GenericField("Current Power");
        public Datalogger.GenericField targetPosition = new Datalogger.GenericField("Target Position");
        public Datalogger.GenericField currentPosition = new Datalogger.GenericField("Current Position");
        public Datalog(String name) {
            // Build the underlying datalog object
            datalogger = new Datalogger.Builder()

                    // Pass through the filename
                    .setFilename(name)

                    // Request an automatic timestamp field
                    .setAutoTimestamp(Datalogger.AutoTimestamp.DECIMAL_SECONDS)

                    // Tell it about the fields we care to log.
                    // Note that order *IS* important here! The order in which we list
                    // the fields is the order in which they will appear in the log.
                    .setFields(
                            runVelocity,
                            currentVelocity,
                            currentPower,
                            targetPosition,
                            currentPosition
                    )
                    .build();
        }

        // Tell the datalogger to gather the values of the fields
        // and write a new line in the log.
        public void writeLine() {
            datalogger.writeLine();
        }
    }
}