package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.Arrays;
import java.util.List;

/**
 * Wheel diagram:
 *      ___
 *    0\ F /3
 *     |   |
 *    1/_B_\2
 *
 */
@Autonomous(name="NewAutoTestDEPOTOP")
public class NewAutoTestDEPOTOP extends LinearOpMode {

    private DcMotor Motor0;
    private DcMotor Motor1;
    private DcMotor Motor2;
    private DcMotor Motor3;
    private DcMotor LiftMotor;
    private DcMotor ArmMotor;
    private Servo MarkerServo;
    private ElapsedTime runtime = new ElapsedTime();
    private String side;

    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private static final String VUFORIA_KEY = "AW43gwP/////AAAAmYhyz/zuEEVHnvzoxHlLyZItf4ilP0/dinBMnTUxXLYeVNLMHQmuS0m+8deBPobAQUB6JXl9rH3l3VC6eJQdYCL7ucXcYRzIaySgu5Edw18foo+xbQpFci4D7t/gEPkx5bkW8OsMCN8oaHnjJfDsm2yuE7YGWzmDs4NRIi929mQxrBk7BFxhDpDV97bGssofJZ16mCAaBgeIj+IUtW2RfZZ9QNOQRs0l0Nlf6vaFtI8/alOhJPjwpQc9ZXmyjF8Yc83mSOKLW8ei3UsYTzrAlZtYeHPiG4FHuGx6t/OCuN5z3V4sw06bvt7Hi9eYa2MivKl8GXlKppNt6kUPHRNFTVz11vboZTYAAAzafNiXyfNj";
    private VuforiaLocalizer vuforia;
    private TFObjectDetector tfod;
    private boolean detected = false;

    private static final double COUNTS_PER_REV = 1120.0;
    private static final double WHEEL_DIAMETER = 4.0;
    private static final double FORWARD_COUNTS_PER_INCH = (COUNTS_PER_REV) / (WHEEL_DIAMETER * 3.1415);
    private static final double SIDE_COUNTS_PER_INCH = FORWARD_COUNTS_PER_INCH * 1.5;
    private static final double PIVOT_COUNTS_PER_DEGREE = FORWARD_COUNTS_PER_INCH / 4.5;
    private static final double SPEED = 0.8;
    private static final double TIMEOUT = 5;

    @Override
    public void runOpMode() throws InterruptedException {

        Motor0 = hardwareMap.dcMotor.get("Motor0");
        Motor1 = hardwareMap.dcMotor.get("Motor1");
        Motor2 = hardwareMap.dcMotor.get("Motor2");
        Motor3 = hardwareMap.dcMotor.get("Motor3");
        LiftMotor = hardwareMap.dcMotor.get("LiftMotor");
        ArmMotor = hardwareMap.dcMotor.get("ArmMotor");
        Motor0.setDirection(DcMotor.Direction.REVERSE);
        Motor1.setDirection(DcMotor.Direction.REVERSE);
        ArmMotor.setDirection(DcMotor.Direction.REVERSE);
        Motor0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LiftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        ArmMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor0.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Motor3.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        LiftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        ArmMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        MarkerServo = hardwareMap.servo.get("MarkerServo");
        MarkerServo.setPosition(0);

        initVuforia();
        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
        }

        waitForStart();

        start();

        if (tfod != null) {
            tfod.activate();
        }

        //Land
        LiftMotor.setTargetPosition(4000);
        LiftMotor.setPower(1.0);
        while(opModeIsActive() && LiftMotor.isBusy()) {
            telemetry.addData("LiftMotor","RAISING");
            telemetry.addData("Position",Integer.toString(LiftMotor.getCurrentPosition()));
            telemetry.update();
        }
        LiftMotor.setPower(0);

        //Move to sampling position
        move(Direction.RIGHT,7.5);
        move(Direction.COUNTERCLOCKWISE,90);

        //Sample!
        runtime.reset();
        while (!detected && !isStopRequested() && opModeIsActive()) {
            side = getMinerals();
            if (runtime.seconds() > 3) {
                side = "Center";
                telemetry.addData("Gold Mineral Position", side);
                telemetry.addData("ERROR", "Could not find mineral");
                telemetry.update();
                detected = true;
            }
        }

        //Lower lift arm
        LiftMotor.setTargetPosition(10);
        LiftMotor.setPower(0.2);

        //Move to pushing position
        move(Direction.CLOCKWISE,90,false);
        move(Direction.FORWARD,24,false);

        //Move in front of the gold
        switch (side) {
            case "Right":
                move(Direction.RIGHT,9,false);
                break;
            case "Center":
                move(Direction.LEFT,9,false);
                break;
            case "Left":
                move(Direction.LEFT,24,false);
                break;
        }

        //Push gold and get into marker drop position
        switch (side) {
            case "Right":
                move(Direction.FORWARD,30);
                move(Direction.COUNTERCLOCKWISE,45);
                move(Direction.FORWARD,24);
                move(Direction.COUNTERCLOCKWISE,90);
                break;
            case "Center":
                move(Direction.FORWARD,48);
                move(Direction.COUNTERCLOCKWISE,135);
                break;
            case "Left":
                move(Direction.FORWARD,30);
                move(Direction.CLOCKWISE,45);
                move(Direction.FORWARD,24);
                move(Direction.CLOCKWISE,180);
                break;
        }

        //Drop Marker!
        MarkerServo.setPosition(1.0);
        sleep(1000);
        MarkerServo.setPosition(0);

        //Move to the crater and lift basket
        ArmMotor.setTargetPosition(2000);
        ArmMotor.setPower(1.0);
        move(Direction.FORWARD,102);

        //Stop basket
        ArmMotor.setPower(0);

    }

    /**
     * Moves the robot using encoder values and omnidirectional pivoting
     * @param direction - the direction to move {FORWARD, BACKWARD, LEFT, RIGHT, CLOCKWISE, COUNTERCLOCKWISE}
     * @param distance - How far to travel in inches or pivot in degrees
     * @param print - [optional] Will we display telemetry? (true default)
     */
    public void move(Direction direction, double distance, boolean print) {

        List<Integer> motorSpeeds;
        double driveMult;

        switch (direction) {
            case FORWARD:
                motorSpeeds = Arrays.asList(1,1,1,1);
                driveMult = FORWARD_COUNTS_PER_INCH;
                break;
            case BACKWARD:
                motorSpeeds = Arrays.asList(-1,-1,-1,-1);
                driveMult = FORWARD_COUNTS_PER_INCH;
                break;
            case LEFT:
                motorSpeeds = Arrays.asList(-1,1,-1,1);
                driveMult = SIDE_COUNTS_PER_INCH;
                break;
            case RIGHT:
                motorSpeeds = Arrays.asList(1,-1,1,-1);
                driveMult = SIDE_COUNTS_PER_INCH;
                break;
            case CLOCKWISE:
                motorSpeeds = Arrays.asList(1,1,-1,-1);
                driveMult = PIVOT_COUNTS_PER_DEGREE;
                break;
            case COUNTERCLOCKWISE:
                motorSpeeds = Arrays.asList(-1,-1,1,1);
                driveMult = PIVOT_COUNTS_PER_DEGREE;
                break;
            default:
                motorSpeeds = Arrays.asList(0,0,0,0);
                driveMult = 0;
                break;
        }

        Motor0.setTargetPosition(Motor0.getCurrentPosition() + (int) (motorSpeeds.get(0) * distance * driveMult));
        Motor1.setTargetPosition(Motor1.getCurrentPosition() + (int) (motorSpeeds.get(1) * distance * driveMult));
        Motor2.setTargetPosition(Motor2.getCurrentPosition() + (int) (motorSpeeds.get(2) * distance * driveMult));
        Motor3.setTargetPosition(Motor3.getCurrentPosition() + (int) (motorSpeeds.get(3) * distance * driveMult));

        runtime.reset();
        Motor0.setPower(SPEED);
        Motor1.setPower(SPEED);
        Motor2.setPower(SPEED);
        Motor3.setPower(SPEED);

        while (opModeIsActive() && (runtime.seconds() < TIMEOUT && Motor0.isBusy() && Motor1.isBusy() && Motor2.isBusy() && Motor3.isBusy())) {
            if (print) {
                telemetry.addData("Direction",direction.toString());
                telemetry.addData("Distance",Double.toString(distance));
                telemetry.update();
            }
        }

        Motor0.setPower(0);
        Motor1.setPower(0);
        Motor2.setPower(0);
        Motor3.setPower(0);

    }

    /**
     * Moves the robot using encoder values and omnidirectional pivoting
     * @param direction - the direction to move {FORWARD, BACKWARD, LEFT, RIGHT, CLOCKWISE, COUNTERCLOCKWISE}
     * @param distance - How far to travel in inches or pivot in degrees
     */
    public void move(Direction direction, double distance) {
        move(direction, distance, true);
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
