package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.hdlib.Controls.HDGamepad;
import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;

/**
 * Created by Height Differential on 11/9/2016.
 */
@TeleOp(name = "Shooter_Testing")
public class Shooter_Testing extends HDOpMode implements HDGamepad.HDButtonMonitor{

    HDRobot robot;
    static double FlywheelSpeed = 0.325;
    boolean collecting = true;
    boolean shooting = false;
    boolean flywheelRun = false;
    ElapsedTime shooterTimer;
    HDGamepad gamepadMonitor;

    @Override
    public void Start() {
        robot.shooter.lowerCollector();
        gamepadMonitor.setGamepad(gamepad1);
        shooterTimer = new ElapsedTime();
        shooterTimer.reset();
        robot.shooter.resetEncoders();
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
        if(flywheelRun) {
            robot.shooter.setFlywheelPower(FlywheelSpeed);
        }else{
            robot.shooter.setFlywheelPower(0);
        }

        if(collecting){
            robot.shooter.setCollectorPower(.35);
            robot.shooter.setAcceleratorPower(-1);
        }
        else if(shooting){
            if(shooterTimer.milliseconds() < 400){
                robot.shooter.setCollectorPower(.6);
                robot.shooter.setAcceleratorPower(1);
            }else if(gamepad1.right_trigger > 0.5 && shooterTimer.milliseconds() > 600){
                shooterTimer.reset();
            }
            else if (gamepad1.right_trigger > 0.5){
                robot.shooter.setCollectorPower(0);
                robot.shooter.setAcceleratorPower(0);
            }else{
                robot.shooter.setCollectorPower(0);
                robot.shooter.setAcceleratorPower(0);
                shooting = false;
            }
        }
        else{
            robot.shooter.setCollectorPower(0);
            robot.shooter.setAcceleratorPower(0);
        }
        if(!shooting){
            shooterTimer.reset();
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
                if(pressed && !shooting) {
                    collecting = !collecting;
                }
                break;
            case DPAD_LEFT:
                break;
            case DPAD_RIGHT:
                break;
            case DPAD_UP:
                break;
            case DPAD_DOWN:
                break;
            case LEFT_BUMPER:
                break;
            case RIGHT_BUMPER:
                break;
            case RIGHT_TRIGGER:
                if(pressed) {
                    shooting = true;
                    collecting = false;
                }
                break;
            case LEFT_TRIGGER:
                if(pressed){
                    flywheelRun = !flywheelRun;
                }
                break;
            case START:
                break;
        }

    }
}
