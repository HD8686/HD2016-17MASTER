package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;

/**
 * Created by Height Differential on 11/9/2016.
 */
@Autonomous
public class Collector_Testing extends HDOpMode {

    HDServo leftCollector;
    HDServo rightCollector;
    DcMotor flywheel1;
    DcMotor flywheel2;


    @Override
    public void Start() {

    }

    @Override
    public void initialize() {
        leftCollector = new HDServo("leftCollector", Values.ServoSpeedStats.HS_755MG, 0.54, 0, 1, Servo.Direction.REVERSE);
        rightCollector = new HDServo("rightCollector", Values.ServoSpeedStats.HS_755MG, 0.54, 0, 1, Servo.Direction.FORWARD);
        flywheel1 = hardwareMap.dcMotor.get("Flywheel_1");
        flywheel2 = hardwareMap.dcMotor.get("Flywheel_2");
        flywheel2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        flywheel1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flywheel2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    @Override
    public void initializeLoop() {

    }

    @Override
    public void continuousRun(double elapsedTime) {
        leftCollector.setPosition(.27);
        rightCollector.setPosition(.27);
        flywheel1.setPower(10);
        flywheel2.setPower(10);
    }
}
