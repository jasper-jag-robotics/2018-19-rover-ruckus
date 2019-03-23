package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name="ScrimmageAuto")
public class ScrimmageAuto extends LinearOpMode{

    private DcMotor Motor0;
    private DcMotor Motor1;
    private DcMotor Motor2;
    private DcMotor Motor3;
    private DcMotor LiftMotor;
    private Servo MarkerServo;

    @Override
    public void runOpMode() {

        Motor0 = hardwareMap.dcMotor.get("Motor0");
        Motor1 = hardwareMap.dcMotor.get("Motor1");
        Motor2 = hardwareMap.dcMotor.get("Motor2");
        Motor3 = hardwareMap.dcMotor.get("Motor3");
        Motor3.setDirection(DcMotor.Direction.REVERSE);
        LiftMotor = hardwareMap.dcMotor.get("LiftMotor");
        MarkerServo = hardwareMap.servo.get("MarkerServo");

        waitForStart();

        start();

        //land from lander
        LiftMotor.setPower(-.7);
        sleep(4000);
        LiftMotor.setPower(0);

        //move holding peg away from hook
        move(0.2,0, 1, 0);
        sleep(500);
        stopMove();

        //lower the lift arm whilst pivoting towards the wall
        LiftMotor.setPower(.7);
        move(0.2,1,0, 0);
        sleep(200);
        stopMove();
        sleep(1000);
        move(.3, 0,0,1);
        sleep(2800);
        LiftMotor.setPower(0);
        stopMove();

    }

    /**
     * Logs "caption:value" to the phone
     *
     * @param caption before the value
     * @param value that is displayed
     */
    private void tele(String caption, String value) {
        telemetry.addData(caption, value);
        telemetry.update();
    }

    /**
     * The move function makes the robot move or pivot
     *
     * @param speed how fast the robot moves
     * @param dx change in x
     * @param dy change in y
     * @param pivot 0 means it moves normally, 1 = left turn, -1 = right turn
     */
    private void move(double speed, double dx, double dy, int pivot) {
        if (pivot == 0) {
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
            tele("pivot", Integer.toString(pivot));
        }
    }

    /**
     * stops movement
     */
    private void stopMove() {
        move(0,0,0,0);
    }

}
