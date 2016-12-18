package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    double speed = 0.6;
    HDDiagnosticDisplay diagnosticDisplay;
    HDRobot robot;
    DriveMode driveMode;
    HDGamepad driverGamepad;
    HDGamepad servoBoyGamepad;
    Alliance alliance;
    static double FlywheelSpeed = 0.325;
    boolean collecting = true;
    boolean shooting = false;
    ElapsedTime shooterTimer;
    boolean flywheelRun = false;

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
        shooterTimer = new ElapsedTime();
        shooterTimer.reset();
        robot.shooter.resetEncoders();
    }

    @Override
    public void continuousRun(double elapsedTime) {
        diagnosticDisplay.addProgramSpecificTelemetry(1, "Alliance: %s", alliance.toString());
        diagnosticDisplay.addProgramSpecificTelemetry(2, "Drive Mode: %s", driveMode.toString());
        diagnosticDisplay.addProgramSpecificTelemetry(3, "Drive Speed: "+ String.valueOf(speed*100) + " Percent");
        robotDrive();
        shooterSubsystem();
    }

    private void shooterSubsystem() {
        if (flywheelRun) {
            robot.shooter.setFlywheelPower(FlywheelSpeed);
        } else {
            robot.shooter.setFlywheelPower(0);
        }
        if (collecting) {
            robot.shooter.setCollectorPower(.35);
            robot.shooter.setAcceleratorPower(-1);
        }if (shooting) {
                robot.shooter.setCollectorPower(1);
                robot.shooter.setAcceleratorPower(1);
            } else {
                robot.shooter.setCollectorPower(0);
                robot.shooter.setAcceleratorPower(0);
                shooting = false;
                collecting = true;
            }
        }

        /*else if (shooting) {
            if (shooterTimer.milliseconds() < 450) {
                robot.shooter.setCollectorPower(-.50);
                robot.shooter.setAcceleratorPower(-1.0);
            }  else if (shooterTimer.milliseconds() < 1250) {
                    robot.shooter.setCollectorPower(1);
                    robot.shooter.setAcceleratorPower(1);
                } else if (gamepad1.right_trigger > 0.5 && shooterTimer.milliseconds() > 1300) {
                    shooterTimer.reset();
                } else if (gamepad1.right_trigger > 0.5) {
                    robot.shooter.setCollectorPower(0);
                    robot.shooter.setAcceleratorPower(0);
                } else {
                    robot.shooter.setCollectorPower(0);
                    robot.shooter.setAcceleratorPower(0);
                    shooting = false;
                    collecting = true;
                }
            } else {
                robot.shooter.setCollectorPower(0);
                robot.shooter.setAcceleratorPower(0);
            }
            if (!shooting) {
                shooterTimer.reset();
            }
        }*/

    private void robotDrive(){
            switch (driveMode) {
                case TANK_DRIVE:
                    robot.driveHandler.tankDrive(-gamepad1.left_stick_y*speed, -gamepad1.right_stick_y*speed);
                    break;
                case MECANUM_FIELD_CENTRIC:
                    if(gamepad1.y){
                        robot.driveHandler.mecanumDrive_Cartesian_keepFrontPos(gamepad1.left_stick_x*.5, gamepad1.left_stick_y*.5, 180.0, robot.navX.getYaw());
                    }else if(gamepad1.b){
                        robot.driveHandler.mecanumDrive_Cartesian_keepFrontPos(gamepad1.left_stick_x*.5, gamepad1.left_stick_y*.5, -90.0, robot.navX.getYaw());
                    }else{
                        robot.driveHandler.mecanumDrive_Cartesian(gamepad1.left_stick_x * speed, gamepad1.left_stick_y * speed, gamepad1.right_stick_x * speed, robot.navX.getYaw());
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
                    if(!pressed)
                    robot.driveHandler.firstRun = true;
                    break;
                case X:
                    break;
                case Y:
                    if(!pressed)
                    robot.driveHandler.firstRun = true;
                    break;
                case DPAD_LEFT:
                    break;
                case DPAD_RIGHT:
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
                        speed = speed - 0.2;
                        speed = Range.clip(speed, 0.2,1);
                    }
                    break;
                case RIGHT_BUMPER:
                    if(pressed){
                        speed = speed + 0.2;
                        speed = Range.clip(speed, 0.2,1);
                    }
                    break;
                case LEFT_TRIGGER:
                    if(pressed){
                        robot.shooter.setCollectorPower(-.50);
                        robot.shooter.setAcceleratorPower(-1.0);
                    }
                    break;
                case RIGHT_TRIGGER:
                    if(pressed) {
                        shooting = true;
                        collecting = true;
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
                    if(pressed) {
                        robot.shooter.setCollectorPower(0.0);
                    }
                    break;
                case X:
                    break;
                case Y:
                    if(pressed) {
                        robot.shooter.setCollectorPower(-.35);
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
                    if(pressed) {
                        robot.shooter.lowerCollector();
                    }
                    break;
                case RIGHT_BUMPER:
                    if(pressed) {
                        robot.shooter.raiseCollector();
                    }
                    break;
                case LEFT_TRIGGER:
                    if(pressed) {
                        flywheelRun = !flywheelRun;
                    }
                    break;
                case RIGHT_TRIGGER:
                    break;
                case START:
                    break;
            }
        }
    }
}
