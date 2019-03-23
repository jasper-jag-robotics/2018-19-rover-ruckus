package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="TeleOpMain")
public class TeleOpMain extends LinearOpMode{

    private DcMotor Motor0;
    private DcMotor Motor1;
    private DcMotor Motor2;
    private DcMotor Motor3;
    private DcMotor LiftMotor;
    private DcMotor ArmMotor;
    private DcMotor FlipMotor;
    private final double SLOW = 0.2;
    private final double NORMAL = 0.5;
    private final double FAST = 0.8;

    @Override
    public void runOpMode() throws InterruptedException{

        int mode = 0; //0(x): Pickup Minerals, 1(y): Lift Robot
        double speed = NORMAL;
        Motor0 = hardwareMap.dcMotor.get("Motor0");
        Motor1 = hardwareMap.dcMotor.get("Motor1");
        Motor2 = hardwareMap.dcMotor.get("Motor2");
        Motor3 = hardwareMap.dcMotor.get("Motor3");
        Motor0.setDirection(DcMotor.Direction.REVERSE);
        Motor1.setDirection(DcMotor.Direction.REVERSE);
        LiftMotor = hardwareMap.dcMotor.get("LiftMotor");
        ArmMotor = hardwareMap.dcMotor.get("ArmMotor");
        FlipMotor = hardwareMap.dcMotor.get("FlipMotor");
        Motor0.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor3.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Motor0.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Motor1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Motor2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        Motor3.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        waitForStart();

        while (opModeIsActive()) {

            //drive code
            float M1 = gamepad1.left_stick_y - gamepad1.left_stick_x;
            float M2 = gamepad1.left_stick_y + gamepad1.left_stick_x;

            if (gamepad1.left_trigger > 0 || gamepad1.right_trigger > 0) {

                if (gamepad1.left_trigger > 0 && gamepad1.right_trigger == 0) {
                    pivot(gamepad1.left_trigger, speed);
                } else if (gamepad1.left_trigger == 0 && gamepad1.right_trigger > 0) {
                    pivot(-gamepad1.right_trigger, speed);
                }

            }
            else {

                Motor0.setPower((M1) * speed);
                Motor1.setPower((M2) * speed);
                Motor2.setPower((-M1) * speed);
                Motor3.setPower((-M2) * speed);

            }

            //Speed modes
            if (gamepad1.dpad_up) {
                speed = FAST;
            }
            else if (gamepad1.dpad_down) {
                speed = SLOW;
            }
            else if (gamepad1.dpad_left || gamepad1.dpad_right) {
                speed = NORMAL;
            }

            //change arm mode bw Lifting Arm and Mineral Pickup Arm
            if (gamepad2.x) {
                mode = 0;
            }
            else if (gamepad2.y) {
                mode = 1;
            }

            //Switch between
            if (mode == 0) {
                //Mineral Pickup arm
                if (gamepad2.right_stick_y > 0) {
                    FlipMotor.setPower(gamepad2.right_stick_y * .3); //down
                }
                else{
                    FlipMotor.setPower(gamepad2.right_stick_y * .65); //up
                }

                ArmMotor.setPower(-gamepad2.left_stick_y * .6);

            }
            else {
                //Robot lifting arm
                LiftMotor.setPower(-gamepad2.left_stick_y * .7);
            }

        }
    }

    private void pivot(float target, double multiplier){

        Motor0.setPower(target * multiplier);
        Motor1.setPower(target * multiplier);
        Motor2.setPower(target * multiplier);
        Motor3.setPower(target * multiplier);

    }

}
