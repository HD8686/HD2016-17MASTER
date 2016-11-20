package org.firstinspires.ftc.hdcode.HDSamples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.HDRobot;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 10/10/2016.
 */

@Disabled
@Autonomous()
public class EmptyOpMode extends HDOpMode {

    HDDiagnosticDisplay diagnosticDisplay;
    HDStateMachine SM;
    HDRobot robot;

    @Override
    public void initialize() {
        robot = new HDRobot();
        SM = new HDStateMachine(robot.driveHandler, robot.navX);
        diagnosticDisplay = new HDDiagnosticDisplay(HDDashboard.getInstance(), robot.driveHandler);
    }

    @Override
    public void initializeLoop() {
    }

    @Override
    public void Start() {

    }

    @Override
    public void continuousRun(double elapsedTime) {
        if(SM.ready()){
        }
    }

}
