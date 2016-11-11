package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDVexMotor;

/**
 * Created by Height Differential on 11/9/2016.
 */
@Autonomous
public class Collector_Testing extends HDOpMode {

    HDRobot robot;

    @Override
    public void Start() {

    }

    @Override
    public void initialize() {
        robot = new HDRobot(Alliance.BLUE_ALLIANCE);
        robot.shooter.raiseCollector();
    }

    @Override
    public void initializeLoop() {

    }

    @Override
    public void continuousRun(double elapsedTime) {
        robot.shooter.lowerCollector();
        if(gamepad1.a) {
            robot.shooter.setFlywheelPower(0.10);
        }else{
            robot.shooter.setFlywheelPower(0.0);
        }
        if(gamepad1.b){
            robot.shooter.setCollectorPower(.25);
        }else{
            robot.shooter.setCollectorPower(0);
        }
        if(gamepad1.y){
            robot.shooter.setAcceleratorPower(1);
        } else{
            robot.shooter.setAcceleratorPower(0);
        }
    }
}
