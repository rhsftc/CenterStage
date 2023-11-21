package org.firstinspires.ftc.teamcode;

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
    int targetPosition = 0;
    int currentPosition = 0;
    double maxVelocity = 0.0;
    double runVelocity = 0.0;
    double pidfP = 0;
    double pidfI = 0;
    double pidfD = 0;
    double pidfF = 0;
    ElapsedTime timer;
    Datalog dataLog;

    @Override
    public void runOpMode() {
        timer = new ElapsedTime();
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        dataLog = new Datalog("pidf");
        waitForStart();
        while (opModeIsActive()) {
            timer.reset();
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
//            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
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
            pidfF = 32767 / maxVelocity;
            pidfP = pidfF * .1;
            pidfI = pidfP * .1;

            sleep(2000);
            runVelocity = maxVelocity * .75;
            targetPosition = motor.getCurrentPosition() + 8000;
            motor.setTargetPosition(targetPosition);
            motor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motor.setVelocityPIDFCoefficients(pidfP, pidfI, pidfD, pidfF);
            motor.setPositionPIDFCoefficients(10);
            motor.setTargetPositionTolerance(5);
            motor.setVelocity(runVelocity);
            while (!isStopRequested() && motor.isBusy()) {
                currentPosition = motor.getCurrentPosition();
                currentVelocity = motor.getVelocity();
                telemetry.addData("PIDF", "P=%g I=%g D=%g F=%g", pidfP, pidfI, pidfD, pidfF);
                telemetry.addData("Run to position, velocity ",  runVelocity);
                telemetry.addData("Target", "%d", targetPosition);
                telemetry.addData("current position", "%d", currentPosition);
                telemetry.addData("current velocity",  currentVelocity);
                telemetry.addData("Power",  motor.getPower());
                telemetry.addData("Busy", motor.isBusy());
                telemetry.update();
                LogData();
            }

            while (opModeIsActive()) {
                telemetry.addData("Position Error", targetPosition - currentPosition);
                telemetry.addData("PIDF", "P=%g I=%g D=%g F=%g", pidfP, pidfI, pidfD, pidfF);
                telemetry.update();
                idle();
            }
        }
    }

    private void LogData() {
        dataLog.targetPosition.set(targetPosition);
        dataLog.currentPosition.set(currentPosition);
        dataLog.runVelocity.set(runVelocity);
        dataLog.currentVelocity.set(currentVelocity);
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
        public Datalogger.GenericField targetPosition = new Datalogger.GenericField("Target Position");
        public Datalogger.GenericField currentPosition = new Datalogger.GenericField("Current Position");
        public Datalogger.GenericField xRate = new Datalogger.GenericField("X Rate");
        public Datalogger.GenericField yRate = new Datalogger.GenericField("Y Rate");

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