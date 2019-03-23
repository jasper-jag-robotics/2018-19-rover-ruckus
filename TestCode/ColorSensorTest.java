package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp(name = "ColorSensorTest")
public class ColorSensorTest extends LinearOpMode{

    private ColorSensor Picker;

    @Override
    public void runOpMode() throws InterruptedException {

        Picker = hardwareMap.colorSensor.get("Picker");
        int red;
        int blue;
        int green;

        waitForStart();

        while (opModeIsActive()) {
            blue = Picker.blue();
            red = Picker.red();
            green = Picker.green();

            telemetry.addData("RGB","(" + red + ", " + blue + ", " + green + ")");
            telemetry.update();
        }
    }
}
