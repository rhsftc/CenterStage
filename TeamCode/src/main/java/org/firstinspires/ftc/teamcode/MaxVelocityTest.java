package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "Max Velocity Test", group = "Test")
//@Disabled
public class MaxVelocityTest extends LinearOpMode {
    DcMotorEx motor;
    double currentVelocity;
    double maxVelocity = 0.0;


    @Override
    public void runOpMode() {
        motor = hardwareMap.get(DcMotorEx.class, "motor");
        motor.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        waitForStart();
        motor.setPower(1);
        while (opModeIsActive()) {
            currentVelocity = motor.getVelocity();

            if (currentVelocity > maxVelocity) {
                maxVelocity = currentVelocity;
            }

            telemetry.addData("current velocity", currentVelocity);
            telemetry.addData("maximum velocity", maxVelocity);
            telemetry.update();
        }
    }
}
