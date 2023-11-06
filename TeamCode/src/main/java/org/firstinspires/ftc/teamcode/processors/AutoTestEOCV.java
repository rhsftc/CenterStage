package org.firstinspires.ftc.teamcode.processors;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.WhiteBalanceControl;
import org.firstinspires.ftc.vision.VisionPortal;

@Autonomous(name = "Auto EOCV-SIM", group = "Camera")
//@Disabled
public class AutoTestEOCV extends OpMode {
    private ImageProcessor imageProcessor;
    private VisionPortal.Builder visionPortalBuilder;
    private VisionPortal visionPortal;
    private ImageProcessor.Selected selectedSpike;
    private WhiteBalanceControl whiteBalanceControl;

    @Override
    public void init() {
        imageProcessor = new ImageProcessor(telemetry);
        visionPortalBuilder = new VisionPortal.Builder();
        visionPortal = visionPortalBuilder.enableLiveView(true).
                addProcessor(imageProcessor).
                setCamera(hardwareMap.get(WebcamName.class, "webcam1")).
                setCameraResolution(new Size(640, 480)).
                setStreamFormat(VisionPortal.StreamFormat.MJPEG).
                build();
    }

    @Override
    public void init_loop() {
        // Wait for the camera to be open
        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            return;
        } else {
            whiteBalanceControl = visionPortal.getCameraControl(WhiteBalanceControl.class);
        }

        if (whiteBalanceControl.getMode() != WhiteBalanceControl.Mode.AUTO) {
            whiteBalanceControl.setMode(WhiteBalanceControl.Mode.AUTO);
        }

        telemetry.addData("Init Identified", imageProcessor.getSelection());
    }

    @Override
    public void start() {
        selectedSpike = imageProcessor.getSelection();
        // Save resources
//        visionPortal.setProcessorEnabled(imageProcessor, false);
//        visionPortal.stopStreaming();
    }

    @Override
    public void loop() {
        telemetry.addData("Identified", imageProcessor.getSelection());
        // Do your paths here.
    }
}
