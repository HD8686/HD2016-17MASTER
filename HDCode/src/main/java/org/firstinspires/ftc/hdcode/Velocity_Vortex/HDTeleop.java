package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.hdlib.Controls.HDGamepad;
import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 5/7/2016.
 */

@TeleOp(name = "HDTeleop", group = "Teleop")
public class HDTeleop extends HDOpMode implements HDGamepad.HDButtonMonitor{

    private enum DriveMode{
        TANK_DRIVE,
        MECANUM_FIELD_CENTRIC,
    }

    double lastGyroValueKeepPos = 0.0;
    double keepPositionTime = 0.0;
    double beaconAngle = 0.0;
    double speed = 0.6;
    HDDiagnosticDisplay diagnosticDisplay;
    HDRobot robot;
    DriveMode driveMode;
    HDGamepad driverGamepad;
    HDGamepad servoBoyGamepad;
    Alliance alliance;

    @Override
    public void initialize() {
        alliance = Alliance.retrieveAlliance(hardwareMap.appContext); //Retrieve Last Alliance from Autonomous.
        robot = new HDRobot(alliance);
        diagnosticDisplay = new HDDiagnosticDisplay(mDisplay, robot.driveHandler);
        driverGamepad = new HDGamepad(gamepad1, this);
        servoBoyGamepad = new HDGamepad(gamepad2, this);

        switch (alliance) {
            case RED_ALLIANCE:
                beaconAngle = 90.0;
                break;
            case BLUE_ALLIANCE:
                beaconAngle = -90.0;
                break;
            case UNKNOWN:
                break;
        }
    }

    @Override
    public void initializeLoop() {

    }


    @Override
    public void Start(){
        if(robot.navX.getSensorData().isCalibrating()) {
            driveMode = DriveMode.TANK_DRIVE;
        } else {
            driveMode = DriveMode.MECANUM_FIELD_CENTRIC;
        }
        driverGamepad.setGamepad(gamepad1);
        servoBoyGamepad.setGamepad(gamepad2);
    }

    @Override
    public void continuousRun(double elapsedTime) {
        diagnosticDisplay.addProgramSpecificTelemetry(1, "Alliance: %s", alliance.toString());
        diagnosticDisplay.addProgramSpecificTelemetry(2, "Drive Mode: %s", driveMode.toString());
        diagnosticDisplay.addProgramSpecificTelemetry(3, "Drive Speed: "+ speed*100);
        robotDrive(elapsedTime);
    }

    private void robotDrive(double elapsedTime){
        if(Math.abs(gamepad1.left_stick_y) > 0 || Math.abs(gamepad1.right_stick_y) > 0) {
            switch (driveMode) {
                case TANK_DRIVE:
                    robot.driveHandler.tankDrive(gamepad1.left_stick_y*speed, gamepad1.right_stick_y*speed);
                    break;
                case MECANUM_FIELD_CENTRIC:
                    if(gamepad1.a){
                        robot.driveHandler.mecanumDrive_Cartesian_keepFrontPos(gamepad1.left_stick_x*speed, gamepad1.left_stick_y*speed, beaconAngle, robot.navX.getYaw());
                    }else {
                        robot.driveHandler.mecanumDrive_Cartesian(gamepad1.left_stick_x*speed, gamepad1.left_stick_y*speed, gamepad1.right_stick_x, robot.navX.getYaw());
                    }
                    break;
            }
            keepPositionTime = elapsedTime;
            lastGyroValueKeepPos = robot.navX.getYaw();
        } else if((elapsedTime - keepPositionTime) < 1){
            lastGyroValueKeepPos = robot.navX.getYaw();
            robot.driveHandler.motorBreak();
        } else{
            robot.driveHandler.gyroTurn(lastGyroValueKeepPos);
        }
    }

    @Override
    public void buttonChange(HDGamepad instance, HDGamepad.gamepadButtonChange button, boolean pressed) {
        if(instance == driverGamepad){
            switch (button) {
                case A:
                    break;
                case B:
                    if(pressed)
                        robot.navX.zeroYaw();
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
                    if(pressed){
                        speed = speed + 0.2;
                        speed = Range.clip(speed, .4,1);
                    }
                    break;
                case DPAD_DOWN:
                    if(pressed){
                        speed = speed - 0.2;
                        speed = Range.clip(speed, .4,1);
                    }
                    break;
                case LEFT_BUMPER:
                    break;
                case RIGHT_BUMPER:
                    break;
                case LEFT_TRIGGER:
                    if(pressed)
                        driveMode = DriveMode.TANK_DRIVE;
                    break;
                case RIGHT_TRIGGER:
                    if(pressed && !robot.navX.getSensorData().isCalibrating())
                        driveMode = DriveMode.MECANUM_FIELD_CENTRIC;
                    break;
            }
        }else if(instance == servoBoyGamepad){
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
                case LEFT_TRIGGER:
                    break;
                case RIGHT_TRIGGER:
                    break;
            }
        }
    }
}
