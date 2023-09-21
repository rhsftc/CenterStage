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
    private boolean isManualExposureSupported = false;
    private boolean isAutoExposureSupported = false;
    private boolean isAperturePriorityExposureSupported = false;
    private boolean isContinuousAutoExposureSupported = false;
    private boolean isGainSupported = false;
    private boolean isWhiteBalanceAutoSupported = false;
    private boolean isWhiteBalanceManualSupported = false;
    private boolean isAutoFocusSupported = false;
    private boolean isFixedFocusSupported = false;
    private boolean isContinuousFocusSupported = false;
    private boolean isMacroFocusSupported = false;
    private boolean isInfinityFocusSupported = false;
    private long exposure;
    private int minExposure;
    private int maxExposure;
    private boolean isTemperatureSet = false;
    private int minTemperature;
    private int maxTemperature;
    private int currentTemperature;
    private int gain;
    private int minGain;
    private int maxGain;
    private double focusLength = 0;
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
        isManualExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.Manual);
        isAutoExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.Auto);
        isAperturePriorityExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.AperturePriority);
        isContinuousAutoExposureSupported = exposureControl.isModeSupported(ExposureControl.Mode.ContinuousAuto);
        // Set to manual so gain will work.
        exposureControl.setMode(ExposureControl.Mode.Manual);

        minExposure = (int) exposureControl.getMinExposure(TimeUnit.MILLISECONDS);
        maxExposure = (int) exposureControl.getMaxExposure(TimeUnit.MILLISECONDS);
        exposure = exposureControl.getExposure(TimeUnit.MILLISECONDS);
        // Gain
        GainControl gainControl = portal.getCameraControl(GainControl.class);
        minGain = gainControl.getMinGain();
        maxGain = gainControl.getMaxGain();
        isGainSupported = gainControl.setGain(25);
        gain = gainControl.getGain();

        // White Balance
        WhiteBalanceControl whiteBalanceControl = portal.getCameraControl(WhiteBalanceControl.class);
        isWhiteBalanceAutoSupported = whiteBalanceControl.setMode(WhiteBalanceControl.Mode.AUTO);
        isWhiteBalanceManualSupported = whiteBalanceControl.setMode(WhiteBalanceControl.Mode.MANUAL);
        isTemperatureSet = whiteBalanceControl.setWhiteBalanceTemperature(6500);
        minTemperature = whiteBalanceControl.getMinWhiteBalanceTemperature();
        maxTemperature = whiteBalanceControl.getMaxWhiteBalanceTemperature();
        currentTemperature = whiteBalanceControl.getMinWhiteBalanceTemperature();

        // Focus
        FocusControl focusControl = portal.getCameraControl(FocusControl.class);
        focusLength = focusControl.getFocusLength();
        isAutoFocusSupported = focusControl.isModeSupported(FocusControl.Mode.Auto);
        isFixedFocusSupported = focusControl.isModeSupported(FocusControl.Mode.Fixed);
        isContinuousFocusSupported = focusControl.isModeSupported(FocusControl.Mode.ContinuousAuto);
        isMacroFocusSupported = focusControl.isModeSupported(FocusControl.Mode.Macro);
        isInfinityFocusSupported = focusControl.isModeSupported(FocusControl.Mode.Infinity);


    }

    private void ShowResults() {
        telemetry.addLine("Exposure");
        telemetry.addData("Manual Support", isManualExposureSupported);
        telemetry.addData("Auto Support", isAutoExposureSupported);
        telemetry.addData("Aperture Support", isAperturePriorityExposureSupported);
        telemetry.addData("Continuous Support", isContinuousAutoExposureSupported);
        telemetry.addData("Min", minExposure);
        telemetry.addData("Max", maxExposure);
        telemetry.addData("Current", exposure);
        telemetry.addLine("Gain");
        telemetry.addData("Gain Support", isGainSupported);
        telemetry.addData("Min", minGain);
        telemetry.addData("Max", maxGain);
        telemetry.addData("Current", gain);
        telemetry.addLine("White Balance");
        telemetry.addData("Auto Support", isWhiteBalanceAutoSupported);
        telemetry.addData("Manual Support", isWhiteBalanceManualSupported);
        telemetry.addData("Min", minTemperature);
        telemetry.addData("Max", maxTemperature);
        telemetry.addData("Current", currentTemperature);
        telemetry.addLine("Focus");
        telemetry.addData("Auto Support", isAutoFocusSupported);
        telemetry.addData("Fixed Support", isFixedFocusSupported);
        telemetry.addData("Macro Support", isMacroFocusSupported);
        telemetry.addData("Continuous Support", isContinuousFocusSupported);
        telemetry.addData("Infinity Support", isInfinityFocusSupported);
        telemetry.addData("Focus Length", focusLength);
        telemetry.update();
    }
}
