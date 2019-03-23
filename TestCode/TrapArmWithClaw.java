package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TrapArmWithClaw")

public class TrapArmWithClaw extends LinearOpMode{

    public DcMotor Motor1;
    public DcMotor Motor2;
    public DcMotor Motor3;
    public DcMotor Motor4;
    public DcMotor Parm;
    public Servo ClawL;
    public Servo ClawR;

    public void runOpMode() throws InterruptedException{

        int position = 0;
        boolean other = true;
        double speed = 0.8;
        Motor1 = hardwareMap.dcMotor.get("Motor1");
        Motor2 = hardwareMap.dcMotor.get("Motor2");
        Motor3 = hardwareMap.dcMotor.get("Motor3");
        Motor4 = hardwareMap.dcMotor.get("Motor4");
        Parm = hardwareMap.dcMotor.get("Parm");
        ClawL = hardwareMap.servo.get("ClawL");
        ClawR = hardwareMap.servo.get("ClawR");

        Motor2.setDirection(DcMotorSimple.Direction.REVERSE);
        ClawL.setPosition(1.0);
        ClawR.setPosition(0);
        Parm.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        Parm.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        Parm.setPower(0.3);
        Parm.setTargetPosition(0);

        waitForStart();

        while(opModeIsActive()) {

            //Movement
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

                Motor1.setPower((M1) * speed);
                Motor2.setPower((M2) * speed);
                Motor3.setPower((-M1) * speed);
                Motor4.setPower((-M2) * speed);

            }

            //Fast/slow modes
            if (gamepad1.dpad_down) {
                speed = 0.8;
            }
            else if (gamepad1.dpad_up) {
                speed = 0.4;
            }

            //Move arm using PID
            if (-gamepad2.left_stick_y > 0 && other) {
                position += 1;
                Parm.setTargetPosition(position);
            }
            else if (-gamepad2.left_stick_y < 0 && other) {
                position -= 1;
                Parm.setTargetPosition(position);
                other = !other;
            }
            else {
                Parm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                other = !other;
            }

            //Claw control
            if (gamepad2.b) {
                ClawL.setPosition(0.45);
                ClawR.setPosition(0.55);
            }
            else {
                ClawL.setPosition(1.0);
                ClawR.setPosition(0);
            }

        }
    }

    public void pivot(float target, double multiplier){

        Motor1.setPower(target * multiplier);
        Motor2.setPower(target * multiplier);
        Motor3.setPower(target * multiplier);
        Motor4.setPower(target * multiplier);

    }
}
