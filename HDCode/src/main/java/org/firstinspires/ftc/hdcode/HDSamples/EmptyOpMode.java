package org.firstinspires.ftc.hdcode.HDSamples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 10/10/2016.
 */

@Disabled
@Autonomous(name = "EmptyOpMode", group = "HDSamples")
public class EmptyOpMode extends HDOpMode {

    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDDriveHandler robotDrive;
    HDStateMachine SM;

    @Override
    public void initialize() {
        navX = new HDNavX();
        robotDrive = new HDDriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(mDisplay,robotDrive);
    }

    @Override
    public void initializeLoop() {
        robotDrive.reverseSide(HDDriveHandler.Side.Left);
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
