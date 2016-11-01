package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 5/7/2016.
 */

@TeleOp(name = "HDTeleop", group = "Teleop")
public class HDTeleop extends HDOpMode {

    private enum DriveMode{
        TANK_DRIVE,
        MECANUM_FIELD_CENTRIC,
    }

    double lastGyroValueKeepPos = 0.0;
    double lastGyroValueNear90 = 0.0;
    double keepPositionTime = 0.0;

    HDDiagnosticDisplay diagnosticDisplay;
    HDRobot robot;
    DriveMode driveMode;

    @Override
    public void initialize() {
        robot = new HDRobot(Alliance.BLUE_ALLIANCE); //Default as we program on blue, reverse for red.
        diagnosticDisplay = new HDDiagnosticDisplay(mDisplay, robot.driveHandler);
    }

    @Override
    public void initializeLoop() {

    }


    @Override
    public void Start(){
        if(robot.navX.getSensorData().isCalibrating())
            driveMode = DriveMode.TANK_DRIVE;
        else
            driveMode = DriveMode.MECANUM_FIELD_CENTRIC;
    }

    @Override
    public void continuousRun(double elapsedTime) {
        diagnosticDisplay.addProgramSpecificTelemetry(1, "Drive Mode: %s", driveMode.toString());
        robotDrive(elapsedTime);



    }

    private void robotDrive(double elapsedTime){
        if(Math.abs(gamepad1.left_stick_y) > 0 || Math.abs(gamepad1.right_stick_y) > 0) {
            switch (driveMode) {
                case TANK_DRIVE:
                    robot.driveHandler.tankDrive(gamepad1.left_stick_y, gamepad1.right_stick_y);
                    break;
                case MECANUM_FIELD_CENTRIC:
                    if(gamepad1.a){
                        robot.driveHandler.mecanumDrive_Cartesian_keepFrontPos(gamepad1.left_stick_x, gamepad1.left_stick_y, 90*(Math.round(lastGyroValueNear90/90)), robot.navX.getYaw());
                    }else {
                        lastGyroValueNear90 = robot.navX.getYaw();
                        robot.driveHandler.mecanumDrive_Cartesian(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x, robot.navX.getYaw());
                    }
                    break;
            }
            keepPositionTime = elapsedTime;
            lastGyroValueKeepPos = robot.navX.getYaw();
        } else if(elapsedTime - keepPositionTime < 1.25){
            lastGyroValueKeepPos = robot.navX.getYaw();
            robot.driveHandler.motorBreak();
        } else{
            robot.driveHandler.gyroTurn(lastGyroValueKeepPos);
        }

        if(gamepad1.left_trigger > .5)
            driveMode = DriveMode.TANK_DRIVE;
        else if(gamepad1.right_trigger > .5)
            driveMode = DriveMode.MECANUM_FIELD_CENTRIC;
    }





}
