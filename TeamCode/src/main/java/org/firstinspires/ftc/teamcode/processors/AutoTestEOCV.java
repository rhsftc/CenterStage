package org.firstinspires.ftc.teamcode.processors;

import android.util.Size;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.CameraControl;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.vision.VisionPortal;

@Config
@Autonomous(name = "Auto EOCV-SIM", group = "Sim")
//@Disabled
public class AutoTestEOCV extends OpMode {
    private ImageProcessor imageProcessor;
    private VisionPortal.Builder visionPortalBuilder;
    private VisionPortal visionPortal;
    FtcDashboard dashboard;
    private ImageProcessor.Selected selectedSpike;

    @Override
    public void init() {
        imageProcessor = new ImageProcessor(telemetry);
        visionPortalBuilder = new VisionPortal.Builder();
        visionPortal = visionPortalBuilder.enableLiveView(true).
                addProcessor(imageProcessor).
                setCamera(hardwareMap.get(WebcamName.class, "webcam1")).
                setCameraResolution(new Size(640, 480)).
                build();
        FtcDashboard.getInstance().startCameraStream(imageProcessor, 0);
//        dashboard = FtcDashboard.getInstance();
//        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
//        telemetry.update();
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
//        visionPortal.setProcessorEnabled(imageProcessor, false);
//        visionPortal.stopStreaming();
    }

    @Override
    public void loop() {
        // Do your paths here.
        telemetry.addData("Identified", imageProcessor.getSelection());
        telemetry.update();
    }
}
