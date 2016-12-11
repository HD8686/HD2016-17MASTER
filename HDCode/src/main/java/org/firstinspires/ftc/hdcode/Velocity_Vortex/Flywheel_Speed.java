package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.hdlib.Controls.HDGamepad;
import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;

/**
 * Created by Height Differential on 11/9/2016.
 */
@TeleOp(name = "Flywheel Speed Testing")
public class Flywheel_Speed extends HDOpMode implements HDGamepad.HDButtonMonitor{

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
        robot.shooter.resetEncoders();
    }

    @Override
    public void initializeLoop() {

    }

    @Override
    public void continuousRun(double elapsedTime) {
        if(elapsedTime < 10){
            robot.shooter.setFlywheelPower(1);
            mDisplay.displayPrintf(9, HDDashboard.textPosition.Centered, "Time Elapsed In Test:" + String.valueOf(elapsedTime));
            mDisplay.displayPrintf(8, HDDashboard.textPosition.Centered, "Encoder Counts: " + robot.shooter.getFlywheelEncoderCount());
        }else{
            robot.shooter.setFlywheelPower(0);
            mDisplay.displayPrintf(10, HDDashboard.textPosition.Centered, "Average Motor RPM:" + String.valueOf(robot.shooter.getFlywheelEncoderCount() * 6 / 140));
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
                break;
            case DPAD_DOWN:
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
