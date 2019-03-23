package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.Arrays;
import java.util.List;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name="EncoderTest")
public class EncoderTest extends LinearOpMode {

    private DcMotor Motor0;
    private DcMotor Motor1;
    private DcMotor Motor2;
    private DcMotor Motor3;
    private DcMotor LiftMotor;
    private DcMotor ArmMotor;
    private Servo MarkerServo;
    private ElapsedTime runtime = new ElapsedTime();

    static final double COUNTS_PER_REV = 1120.0;
    static final double WHEEL_DIAMETER = 4.0;
    static final double FORWARD_COUNTS_PER_INCH = (COUNTS_PER_REV) / (WHEEL_DIAMETER * 3.1415);
    static final double SIDE_COUNTS_PER_INCH = FORWARD_COUNTS_PER_INCH * 1.5;
    static final double PIVOT_COUNTS_PER_DEGREE = FORWARD_COUNTS_PER_INCH / 4.5;
    static final double SPEED = 0.8;
    static final double TIMEOUT = 5;

    @Override
    public void runOpMode() throws InterruptedException{

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

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.dpad_up) {
                move(Direction.FORWARD,12);
            }
            if (gamepad1.dpad_down) {
                move(Direction.BACKWARD,12);
            }
            if (gamepad1.dpad_left) {
                move(Direction.LEFT,12);
            }
            if (gamepad1.dpad_right) {
                move(Direction.RIGHT,12);
            }
            if (gamepad1.left_trigger > 0) {
                move(Direction.COUNTERCLOCKWISE,90);
            }
            if (gamepad1.right_trigger > 0) {
                move(Direction.CLOCKWISE,90);
            }
            if (gamepad1.y) {
                LiftMotor.setTargetPosition(4000);
                LiftMotor.setPower(1.0);
                while (opModeIsActive() && LiftMotor.isBusy()) {
                    telemetry.addData("LiftMotor","RAISING");
                    telemetry.addData("Position",Integer.toString(LiftMotor.getCurrentPosition()));
                    telemetry.update();
                }
                LiftMotor.setPower(0);
            }
            if (gamepad1.a) {
                LiftMotor.setTargetPosition(10);
                LiftMotor.setPower(1.0);
                while (opModeIsActive() && LiftMotor.isBusy()) {
                    telemetry.addData("LiftMotor","LOWERING");
                    telemetry.addData("Position",Integer.toString(LiftMotor.getCurrentPosition()));
                    telemetry.update();
                }
                LiftMotor.setPower(0);
            }



        }

    }

    public void move(Direction direction, double distance) {

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
            telemetry.addData("Direction",direction.toString());
            telemetry.addData("Distance",Double.toString(distance));
            telemetry.update();
        }

        Motor0.setPower(0);
        Motor1.setPower(0);
        Motor2.setPower(0);
        Motor3.setPower(0);

    }

}