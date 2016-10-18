package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;
import org.firstinspires.ftc.hdlib.Values;


/**
 * Created by Akash on 10/10/2016.
 */

@Autonomous(name = "Range Testing", group = "Testing")
public class Range_Testing extends HDOpMode {

    HDDiagnosticDisplay mHDDiagnosticDisplay;
    HDNavX navX;
    HDDriveHandler robotDrive;
    HDStateMachine SM;
    HDRange mRange;
    OpticalDistanceSensor ODS_Back;
    @Override
    public void Initialize() {
        ODS_Back = hardwareMap.opticalDistanceSensor.get(Values.HardwareMapKeys.Right_ODS);
        mRange = new HDRange(Values.HardwareMapKeys.Range);
        navX = new HDNavX();
        robotDrive = new HDDriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDDiagnosticDisplay = new HDDiagnosticDisplay(this, mDisplay,robotDrive);
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(HDDriveHandler.Side.Left);
    }

    @Override
    public void Start() {

    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
            mHDDiagnosticDisplay.addProgramSpecificTelemetry(3, "Range Sensor: " + mRange.getUSValue());
            mHDDiagnosticDisplay.addProgramSpecificTelemetry(4, "ODS: " + ODS_Back.getRawLightDetected());
        }
    }

}
