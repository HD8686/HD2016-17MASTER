package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDVexMotor;

/**
 * Created by Height Differential on 11/9/2016.
 */
@Autonomous
public class Collector_Testing extends HDOpMode {

    HDServo leftCollector;
    HDServo rightCollector;
    DcMotor flywheel1;
    DcMotor flywheel2;
    DcMotor collectorMotor;
    HDVexMotor accelerator1;
    HDVexMotor accelerator2;

    @Override
    public void Start() {

    }

    @Override
    public void initialize() {
        leftCollector = new HDServo("leftCollector", Values.ServoSpeedStats.HS_755MG, 0.475, 0, 1, Servo.Direction.REVERSE);
        rightCollector = new HDServo("rightCollector", Values.ServoSpeedStats.HS_755MG, 0.54, 0, 1, Servo.Direction.FORWARD);
        flywheel1 = hardwareMap.dcMotor.get("Flywheel_1");
        flywheel2 = hardwareMap.dcMotor.get("Flywheel_2");
        collectorMotor = hardwareMap.dcMotor.get("Collector1");
        flywheel1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        collectorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        collectorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        accelerator1 = new HDVexMotor("Vex1", Servo.Direction.FORWARD);
        accelerator2 = new HDVexMotor("Vex2", Servo.Direction.FORWARD);
        accelerator1.setPower(0);
        accelerator2.setPower(0);
        flywheel1.setPower(0);
        flywheel2.setPower(0);
        collectorMotor.setPower(0);
    }

    @Override
    public void initializeLoop() {

    }

    @Override
    public void continuousRun(double elapsedTime) {
        leftCollector.setPosition(.235);
        rightCollector.setPosition(.3);
        if(gamepad1.a) {
            flywheel1.setPower(10);
            flywheel2.setPower(10);
        }else{
            flywheel1.setPower(0);
            flywheel2.setPower(0);
        }

        if(gamepad1.b){
            collectorMotor.setPower(.5);
        }else{
            collectorMotor.setPower(0);
        }
        accelerator1.setPower(gamepad1.left_stick_y);
        accelerator2.setPower(gamepad1.left_stick_y);
    }
}
