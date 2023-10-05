package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.processors.ImageProcessor;
import org.firstinspires.ftc.vision.VisionPortal;

@Autonomous
//@Disabled
public class AutoTest extends OpMode {
    private ImageProcessor imageProcessor;
    private VisionPortal visionPortal;

    @Override
    public void init() {
        imageProcessor = new ImageProcessor();
        visionPortal = VisionPortal.easyCreateWithDefaults(hardwareMap.get(WebcamName.class, "webcam1"));
    }

    @Override
    public void loop() {

    }
}
