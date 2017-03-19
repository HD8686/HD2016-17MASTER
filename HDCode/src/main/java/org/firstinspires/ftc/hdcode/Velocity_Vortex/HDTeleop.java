package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
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

    double driveSpeed = 0.6;
    HDDiagnosticDisplay diagnosticDisplay;
    HDRobot robot;
    DriveMode driveMode;
    HDGamepad driverGamepad;
    HDGamepad servoBoyGamepad;
    Alliance alliance;
    boolean flywheelRunning = false;
    boolean collectorForward = true;
    boolean shooting = false;


    double FlywheelSpeed = 0.32;
    double timerVar = 0.0;

    @Override
    public void initialize() {
        try {
            alliance = Alliance.retrieveAlliance(hardwareMap.appContext); //Retrieve Last Alliance from Autonomous.
        }catch (Exception e){
            alliance = Alliance.BLUE_ALLIANCE;
        }
        robot = new HDRobot(alliance);
        diagnosticDisplay = new HDDiagnosticDisplay(mDisplay, robot.driveHandler);
        driverGamepad = new HDGamepad(gamepad1, this);
        servoBoyGamepad = new HDGamepad(gamepad2, this);
        robot.shooter.raiseCollector();
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
        robot.shooter.lowerCollector();
        robot.shooter.resetEncoders();
    }

    @Override
    public void continuousRun(double elapsedTime) {
        diagnosticDisplay.addProgramSpecificTelemetry(1, "Alliance: %s", alliance.toString());
        diagnosticDisplay.addProgramSpecificTelemetry(2, "Drive Mode: %s", driveMode.toString());
        diagnosticDisplay.addProgramSpecificTelemetry(3, "Drive Speed: "+ String.valueOf(driveSpeed *100) + " Percent");
        diagnosticDisplay.addProgramSpecificTelemetry(4, "Flywheel Speed: %f", FlywheelSpeed);
        robotDrive();
        if(flywheelRunning){
                robot.shooter.setFlywheelPower(FlywheelSpeed);
            //Try Flywheel Indexing;
        }else{
            robot.shooter.setFlywheelPower(0);
        }
        if(shooting){
            if((System.currentTimeMillis() - timerVar) < 400){
                robot.shooter.setCollectorPower(0);
                robot.shooter.setAcceleratorPower(0);
            }
            else if((System.currentTimeMillis() - timerVar) < 500){
                robot.shooter.setCollectorPower(-.6);
                robot.shooter.setAcceleratorPower(-1);
            }else if((System.currentTimeMillis() - timerVar) < 700){
                robot.shooter.setCollectorPower(0);
                robot.shooter.setAcceleratorPower(0);
            }else {
                robot.shooter.setCollectorPower(.6);
                robot.shooter.setAcceleratorPower(1);
            }

        }else{
            if(gamepad1.a){
                robot.shooter.setCollectorPower(-.6);
                robot.shooter.setAcceleratorPower(-1);
            }
            else if(collectorForward){
                robot.shooter.setCollectorPower(.6);
                robot.shooter.setAcceleratorPower(-1);
            }else{
                robot.shooter.setCollectorPower(0);
                robot.shooter.setAcceleratorPower(0);
            }
        }
    }




    private void robotDrive(){
            switch (driveMode) {
                case TANK_DRIVE:
                    robot.driveHandler.tankDrive(-gamepad1.left_stick_y* driveSpeed, -gamepad1.right_stick_y* driveSpeed);
                    break;
                case MECANUM_FIELD_CENTRIC:
                    if(gamepad1.y){
                        robot.driveHandler.mecanumDrive_Cartesian_keepFrontPos(gamepad1.left_stick_x*.2, gamepad1.left_stick_y*.2, 180.0, robot.navX.getYaw());
                    }else if(gamepad1.b){
                        robot.driveHandler.mecanumDrive_Cartesian_keepFrontPos(gamepad1.left_stick_x*.2, gamepad1.left_stick_y*.2, -90.0, robot.navX.getYaw());
                    }else{
                        robot.driveHandler.mecanumDrive_Cartesian(gamepad1.left_stick_x * driveSpeed, gamepad1.left_stick_y * driveSpeed, gamepad1.right_stick_x * driveSpeed, robot.navX.getYaw());
                    }
                    break;
            }
    }

    @Override
    public void buttonChange(HDGamepad instance, HDGamepad.gamepadButtonChange button, boolean pressed) {
        if(instance == driverGamepad){
            switch (button) {
                case A:
                    break;
                case B:
                    if(!pressed){
                        robot.driveHandler.firstRun = true;
                    }
                    break;
                case X:
                    if(pressed){
                        collectorForward = !collectorForward;
                    }
                    break;
                case Y:
                    if(!pressed){
                        robot.driveHandler.firstRun = true;
                    }
                    break;
                case DPAD_LEFT:
                    if(pressed){
                        FlywheelSpeed = FlywheelSpeed - 0.01;
                    }
                    break;
                case DPAD_RIGHT:
                    if(pressed){
                        FlywheelSpeed = FlywheelSpeed + 0.01;
                    }
                    break;
                case DPAD_UP:
                    if(pressed && !robot.navX.getSensorData().isCalibrating())
                        driveMode = DriveMode.MECANUM_FIELD_CENTRIC;
                    break;
                case DPAD_DOWN:
                    if(pressed)
                        driveMode = DriveMode.TANK_DRIVE;
                    break;
                case LEFT_BUMPER:
                    if(pressed){
                        driveSpeed = driveSpeed - 0.2;
                        driveSpeed = Range.clip(driveSpeed, 0.2,1);
                    }
                    break;
                case RIGHT_BUMPER:
                    if(pressed){
                        driveSpeed = driveSpeed + 0.2;
                        driveSpeed = Range.clip(driveSpeed, 0.2,1);
                    }
                    break;
                case LEFT_TRIGGER:
                    if(pressed){
                        flywheelRunning =!flywheelRunning;
                    }
                    break;
                case RIGHT_TRIGGER:
                    if(pressed){
                        shooting = true;
                        timerVar = System.currentTimeMillis();
                    }else{
                        shooting = false;
                    }
                    break;
                case START:
                    if(pressed)
                        robot.navX.zeroYaw();
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
                case START:
                    break;
            }
        }
    }
}
