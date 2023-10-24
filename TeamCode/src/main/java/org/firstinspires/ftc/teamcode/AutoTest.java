package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.processors.ImageProcessor;
import org.firstinspires.ftc.vision.VisionPortal;

@Autonomous(name = "Auto Test", group = "Test")
//@Disabled
public class AutoTest extends OpMode {
    private ImageProcessor imageProcessor;
    private VisionPortal.Builder visionPortalBuilder;
    private VisionPortal visionPortal;
    private ImageProcessor.Selected selectedSpike;

    @Override
    public void init() {
        imageProcessor = new ImageProcessor(telemetry);
        visionPortalBuilder = new VisionPortal.Builder();
        visionPortal = visionPortalBuilder.enableLiveView(true).
                setStreamFormat(VisionPortal.StreamFormat.MJPEG).
                setCameraResolution(new Size(640, 480)).
                setAutoStopLiveView(true).
                setCamera(hardwareMap.get(WebcamName.class, "webcam1")).
                addProcessor(imageProcessor).
                build();
        FtcDashboard.getInstance().startCameraStream(imageProcessor, 0);
    }

    @Override
    public void init_loop() {
        telemetry.addData("Init Identified", imageProcessor.getSelection());
        telemetry.update();
    }

    @Override
    public void start() {
        selectedSpike = imageProcessor.getSelection();
        telemetry.addData("Start Identified", selectedSpike);
        telemetry.update();
        // Save resources
//        visionPortal.setProcessorEnabled(imageProcessor,false);
//        visionPortal.stopStreaming();
    }

    @Override
    public void loop() {
        telemetry.addData("Identified", imageProcessor.getSelection());
        telemetry.update();
        // Do your paths here.
    }
}
