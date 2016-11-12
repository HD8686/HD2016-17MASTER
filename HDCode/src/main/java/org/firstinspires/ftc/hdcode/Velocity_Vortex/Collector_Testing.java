package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.hdlib.Controls.HDGamepad;
import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDVexMotor;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;

/**
 * Created by Height Differential on 11/9/2016.
 */
@Autonomous
public class Collector_Testing extends HDOpMode implements HDGamepad.HDButtonMonitor{

    HDRobot robot;
    double CollectorSpeed = 0.3;
    HDGamepad gamepadMonitor;

    @Override
    public void Start() {
        robot.shooter.lowerCollector();
        gamepadMonitor.setGamepad(gamepad1);
    }

    @Override
    public void initialize() {
        robot = new HDRobot(Alliance.BLUE_ALLIANCE);
        robot.shooter.raiseCollector();
        gamepadMonitor = new HDGamepad(gamepad1, this);
    }

    @Override
    public void initializeLoop() {

    }

    @Override
    public void continuousRun(double elapsedTime) {
    mDisplay.displayPrintf(5, HDDashboard.textPosition.Centered, "Collector Speed: " + CollectorSpeed);
        if(gamepad1.a) {
            robot.shooter.setFlywheelPower(1);
        }else{
            robot.shooter.setFlywheelPower(0.0);
        }
        if(gamepad1.b){
            robot.shooter.setCollectorPower(CollectorSpeed);
        }else{
            robot.shooter.setCollectorPower(0);
        }
        if(gamepad1.y){
            robot.shooter.setAcceleratorPower(1);
        } else{
            robot.shooter.setAcceleratorPower(0);
        }
    }

    @Override
    public void buttonChange(HDGamepad instance, HDGamepad.gamepadButtonChange button, boolean pressed) {
        switch (button) {
            case A:
                break;
            case B:
                break;
            case X:
                break;
            case Y:
                break;
            case DPAD_LEFT:
                break;
            case DPAD_RIGHT:
                break;
            case DPAD_UP:
                if(pressed)
                    CollectorSpeed = CollectorSpeed + 0.1;
                break;
            case DPAD_DOWN:
                if(pressed)
                    CollectorSpeed = CollectorSpeed - 0.1;
                break;
            case LEFT_BUMPER:
                break;
            case RIGHT_BUMPER:
                break;
            case RIGHT_TRIGGER:
                break;
            case LEFT_TRIGGER:
                break;
            case START:
                break;
        }

    }
}
