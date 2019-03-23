package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@TeleOp(name="TensorTest")
public class TensorTest extends LinearOpMode {

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final String VUFORIA_KEY = "AW43gwP/////AAAAmYhyz/zuEEVHnvzoxHlLyZItf4ilP0/dinBMnTUxXLYeVNLMHQmuS0m+8deBPobAQUB6JXl9rH3l3VC6eJQdYCL7ucXcYRzIaySgu5Edw18foo+xbQpFci4D7t/gEPkx5bkW8OsMCN8oaHnjJfDsm2yuE7YGWzmDs4NRIi929mQxrBk7BFxhDpDV97bGssofJZ16mCAaBgeIj+IUtW2RfZZ9QNOQRs0l0Nlf6vaFtI8/alOhJPjwpQc9ZXmyjF8Yc83mSOKLW8ei3UsYTzrAlZtYeHPiG4FHuGx6t/OCuN5z3V4sw06bvt7Hi9eYa2MivKl8GXlKppNt6kUPHRNFTVz11vboZTYAAAzafNiXyfNj";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private int cnt = 0;

    @Override
    public void runOpMode() {

        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();
        waitForStart();

        while(opModeIsActive()) {
            if (tfod != null) {
                tfod.activate();
            }

            getMinerals();
            cnt++;
            telemetry.addData("Count", cnt);

        }
    }

    private String getMinerals() {
        String side = "None";
        List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
        if (updatedRecognitions != null) {
            telemetry.addData("I SPY", updatedRecognitions.size());
            if (updatedRecognitions.size() == 2) {
                int goldMineralX = -1;
                int silverMineralX = -1;
                for (Recognition recognition : updatedRecognitions) {
                    if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                        goldMineralX = (int) recognition.getLeft();
                    } else if (silverMineralX == -1) {
                        silverMineralX = (int) recognition.getLeft();
                    }
                }
                if (goldMineralX == -1) {
                    telemetry.addData("Gold Mineral Position", "Right");
                    side = "Right";
                } else if (goldMineralX < silverMineralX) {
                    telemetry.addData("Gold Mineral Position", "Left");
                    side = "Left";
                } else {
                    telemetry.addData("Gold Mineral Position", "Center");
                    side = "Center";
                }
            }
            telemetry.update();
        }
        return side;
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

}
