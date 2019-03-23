package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name="CraterAuto")
public class CraterAuto extends LinearOpMode{

    private double time = 0;
    
    private DcMotor Motor0;
    private DcMotor Motor1;
    private DcMotor Motor2;
    private DcMotor Motor3;
    private DcMotor LiftMotor;
    private DcMotor ArmMotor;
    private Servo MarkerServo;

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    private static final String VUFORIA_KEY = "AW43gwP/////AAAAmYhyz/zuEEVHnvzoxHlLyZItf4ilP0/dinBMnTUxXLYeVNLMHQmuS0m+8deBPobAQUB6JXl9rH3l3VC6eJQdYCL7ucXcYRzIaySgu5Edw18foo+xbQpFci4D7t/gEPkx5bkW8OsMCN8oaHnjJfDsm2yuE7YGWzmDs4NRIi929mQxrBk7BFxhDpDV97bGssofJZ16mCAaBgeIj+IUtW2RfZZ9QNOQRs0l0Nlf6vaFtI8/alOhJPjwpQc9ZXmyjF8Yc83mSOKLW8ei3UsYTzrAlZtYeHPiG4FHuGx6t/OCuN5z3V4sw06bvt7Hi9eYa2MivKl8GXlKppNt6kUPHRNFTVz11vboZTYAAAzafNiXyfNj";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private boolean detected = false;

    @Override
    public void runOpMode() throws InterruptedException{

        Motor0 = hardwareMap.dcMotor.get("Motor0");
        Motor1 = hardwareMap.dcMotor.get("Motor1");
        Motor2 = hardwareMap.dcMotor.get("Motor2");
        Motor3 = hardwareMap.dcMotor.get("Motor3");
        Motor3.setDirection(DcMotor.Direction.REVERSE);
        LiftMotor = hardwareMap.dcMotor.get("LiftMotor");
        ArmMotor = hardwareMap.dcMotor.get("ArmMotor");
        MarkerServo = hardwareMap.servo.get("MarkerServo");
        MarkerServo.setPosition(0);

        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        //start command
        waitForStart();

        start();
        
        if (tfod != null) {
            tfod.activate();
        }

        time = getRuntime();

        LiftMotor.setPower(1);
        sleep(1450);
        LiftMotor.setPower(0);

        //Move to sampling position
        moveFor(.5,-1,0,0,600);
        moveFor(.5,0,0,1,800);

        String side = "";
        while (!detected && !isStopRequested() && opModeIsActive()) { //add timed exception
            side = getMinerals();
            if (getRuntime() - time > 6) {
                side = "Center";
                telemetry.addData("Gold Mineral Position", side);
                telemetry.addData("ERROR", "Could not find mineral");
                telemetry.update();
                detected = true;
            }
        }

        tfod.deactivate();
        
        //do stuff
        moveFor(.5,0,0,-1,1200);
        moveFor(.5,0,1,0,1100);
        moveFor(.5,0,0,1,100);
        
        //get position and hit that mineral
        if (side.equals("Right")) {
            moveFor(.5,-1,0,0,400);
        }
        else if (side.equals("Center")) {
            moveFor(.5,1,0,0,1000);
        }
        else {
            moveFor(.5,1,0,0,2400);
        }
        
        //pivot and move to the wall
        moveFor(.5,0,1,0,600);
        moveFor(.5,0,-1,0,600);
        
        if (side == "Right") {
            moveFor(.5,0,-1,0,100);
        }
        
        moveFor(.8,0,0,1,600);
        
        if (side.equals("Right")) {
            moveFor(.8,0,1,0,2200);
        }
        else if (side.equals("Center")) {
            moveFor(.8,0,1,0,1400);
        }
        else {
            moveFor(.8,0,1,0,700);
        }
        
        //move to depot
        moveFor(.8,0,0,1,300);
        
        if (side == "Left") {
            moveFor(.8,0,1,0,800);
        }
        
        moveFor(.8,0,1,0,1700);
        moveFor(.8,0,0,1,600);
        
        //drop team marker
        LiftMotor.setPower(-1);
        ArmMotor.setPower(1);
        sleep(200);
        MarkerServo.setPosition(1.0);
        sleep(400);
        LiftMotor.setPower(0);
        ArmMotor.setPower(0);
        sleep(1000);
        MarkerServo.setPosition(0);
        
        moveFor(.8,0,0,1,600);
        moveFor(.8,0,1,0,2000);
        moveFor(.5,0,1,0,3000);
        
    }

    /**
     * The move function makes the robot move or pivot
     *
     * @param speed how fast the robot moves
     * @param dx change in x
     * @param dy change in y
     * @param pivot 0 means it moves normally, 1 = left turn, -1 = right turn
     */
    private void move(double speed, double dx, double dy, double pivot) {
        if (pivot == 0) {
            dy = -dy;
            dx = -dx;
            float M1 = (float)(dy - dx);
            float M2 = (float)(dy + dx);
            Motor0.setPower((M1) * speed);
            Motor1.setPower((M2) * speed);
            Motor2.setPower((-M1) * speed);
            Motor3.setPower((-M2) * speed);
        }
        else {
            pivot *= speed;
            Motor0.setPower(pivot);
            Motor1.setPower(pivot);
            Motor2.setPower(pivot);
            Motor3.setPower(pivot);
        }
    }
    
    /**
     * The moveFOR function makes the robot move or pivot for a period of time
     *
     * @param speed how fast the robot moves
     * @param dx change in x
     * @param dy change in y
     * @param pivot 0 means it moves normally, 1 = left turn, -1 = right turn
     * @param time is the time in ms it should run
     */
    private void moveFor(double speed, double dx, double dy, double pivot, long time) {
        move(speed, dx, dy, pivot);
        sleep(time);
        stopMove();
    }

    /**
     * stops movement
     */
    private void stopMove() {
        move(0,0,0,0);
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
                telemetry.addData("GoldX", goldMineralX);
                telemetry.addData("SilverX", silverMineralX);
                if (goldMineralX == -1) {
                    side = "Left";
                } else if (goldMineralX < silverMineralX) {
                    side = "Center";
                } else {
                    side = "Right";
                }
                detected = true;
                telemetry.addData("Gold Mineral Position", side);
            }
            telemetry.update();
        }
        return side;
    }
}
