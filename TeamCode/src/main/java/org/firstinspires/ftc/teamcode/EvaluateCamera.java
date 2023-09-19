package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.concurrent.TimeUnit;

@TeleOp(name = "Camera Evaluation", group = "Concept")
public class EvaluateCamera extends OpMode {
    VisionPortal portal;
    private long exposure;
    private int minExposure;
    private int maxExposure;
    private boolean manualExposureSupported = false;
    private boolean autoExposureSupported = false;
    private boolean aperturePriorityExposureSupported = false;
    private boolean continuousAutoExposureSuppported = false;
    private int minGain;
    private int maxGain;
    final int RESOLUTION_WIDTH = 640;
    final int RESOLUTION_HEIGHT = 480;

    @Override
    public void init() {
        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                .build();
    }

    @Override
    public void loop() {
        ExposureControl exposureControl = portal.getCameraControl(ExposureControl.class);
        if (exposureControl.isExposureSupported()) {
            minExposure = (int) exposureControl.getMinExposure(TimeUnit.MILLISECONDS);
            maxExposure = (int) exposureControl.getMaxExposure(TimeUnit.MILLISECONDS);
            exposure = exposureControl.getExposure(TimeUnit.MILLISECONDS);
        }

        manualExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.Manual);
        autoExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.Auto);
        aperturePriorityExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.AperturePriority);
        continuousAutoExposureSuppported = exposureControl.isModeSupported(ExposureControl.Mode.ContinuousAuto);
    }
}
