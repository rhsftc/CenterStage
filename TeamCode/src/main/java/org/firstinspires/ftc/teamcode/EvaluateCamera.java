package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.FocusControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.WhiteBalanceControl;
import org.firstinspires.ftc.vision.VisionPortal;

import java.util.concurrent.TimeUnit;

@TeleOp(name = "Camera Evaluation", group = "Test")
public class EvaluateCamera extends OpMode {
    VisionPortal portal;
    private long exposure;
    private int minExposure;
    private int maxExposure;
    private boolean isManualExposureSupported = false;
    private boolean isAutoExposureSupported = false;
    private boolean isAperturePriorityExposureSupported = false;
    private boolean iaContinuousAutoExposureSuppported = false;
    private boolean isGainSupported = false;
    private boolean isWhiteBalanceSupported = false;
    private boolean isAutoFocusSupported = false;
    private boolean isFixedFocusSupported = false;
    private boolean isContinuousFocusSupported = false;
    private boolean isMacroFocusSupported = false;
    private boolean isInfinityFocusSupported = false;
    private int minGain;
    private int maxGain;
    final int RESOLUTION_WIDTH = 640;
    final int RESOLUTION_HEIGHT = 480;

    @Override
    public void init() {
        portal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "webcam1"))
                .setCameraResolution(new Size(RESOLUTION_WIDTH, RESOLUTION_HEIGHT))
                .build();
        // Wait for the camera to be open
        if (portal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            telemetry.update();
            while (portal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            }

            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }
    }

    @Override
    public void loop() {
        ProcessCamera();
        ShowResults();
    }

    private void ProcessCamera() {
        // Exposure
        ExposureControl exposureControl = portal.getCameraControl(ExposureControl.class);
        if (exposureControl.isExposureSupported()) {
            minExposure = (int) exposureControl.getMinExposure(TimeUnit.MILLISECONDS);
            maxExposure = (int) exposureControl.getMaxExposure(TimeUnit.MILLISECONDS);
            exposure = exposureControl.getExposure(TimeUnit.MILLISECONDS);
        }

        isManualExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.Manual);
        isAutoExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.Auto);
        isAperturePriorityExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.AperturePriority);
        iaContinuousAutoExposureSuppported = exposureControl.isModeSupported(ExposureControl.Mode.ContinuousAuto);

        // Gain
        GainControl gainControl = portal.getCameraControl(GainControl.class);
        minGain = gainControl.getMinGain();
        maxGain = gainControl.getMaxGain();
        isGainSupported = gainControl.setGain(5);

        // White Balance
        WhiteBalanceControl whiteBalanceControl = portal.getCameraControl(WhiteBalanceControl.class);
        isWhiteBalanceSupported = whiteBalanceControl.setMode(WhiteBalanceControl.Mode.AUTO);

        // Focus
        FocusControl focusControl = portal.getCameraControl(FocusControl.class);
        isAutoFocusSupported = focusControl.isModeSupported(FocusControl.Mode.Auto);
        isFixedFocusSupported = focusControl.isModeSupported(FocusControl.Mode.Fixed);
        iaContinuousAutoExposureSuppported = focusControl.isModeSupported(FocusControl.Mode.ContinuousAuto);
        isMacroFocusSupported = focusControl.isModeSupported(FocusControl.Mode.Macro);
        isInfinityFocusSupported = focusControl.isModeSupported(FocusControl.Mode.Infinity);


    }

    private void ShowResults() {
        telemetry.addLine("Exposure");
        telemetry.addData("Manual Support", isManualExposureSupported);
        telemetry.addData("Auto Support", isAutoExposureSupported);
        telemetry.addData("Aperture Support", isAperturePriorityExposureSupported);
        telemetry.addData("Continuous Support", isContinuousFocusSupported);
        telemetry.update();
    }
}
