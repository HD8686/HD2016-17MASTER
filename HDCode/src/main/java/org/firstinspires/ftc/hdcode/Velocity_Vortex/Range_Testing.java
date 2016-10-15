package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.DriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.StateMachines.HDStateMachine;
import org.firstinspires.ftc.hdlib.Telemetry.HDAutoDiagnostics;
import org.firstinspires.ftc.hdlib.Values;


/**
 * Created by Akash on 10/10/2016.
 */

@Autonomous(name = "Range Testing", group = "Testing")
public class Range_Testing extends HDOpMode {

    HDAutoDiagnostics mHDAutoDiagnostics;
    HDNavX navX;
    DriveHandler robotDrive;
    HDStateMachine SM;
    HDRange mRange;
    OpticalDistanceSensor ODS_Back;
    @Override
    public void Initialize() {
        ODS_Back = hardwareMap.opticalDistanceSensor.get(Values.HardwareMapKeys.Right_ODS);
        mRange = new HDRange(Values.HardwareMapKeys.Range);
        navX = new HDNavX();
        robotDrive = new DriveHandler(navX);
        SM = new HDStateMachine(robotDrive, navX);
        robotDrive.resetEncoders();
        mHDAutoDiagnostics = new HDAutoDiagnostics(this, mDisplay,robotDrive);
    }

    @Override
    public void InitializeLoop() {
        robotDrive.reverseSide(DriveHandler.Side.Left);
    }

    @Override
    public void Start() {

    }

    @Override
    public void continuousRun() {
        if(SM.ready()){
            mHDAutoDiagnostics.addProgramSpecificTelemetry(3, "Range Sensor: " + mRange.getUSValue());
            mHDAutoDiagnostics.addProgramSpecificTelemetry(4, "ODS: " + ODS_Back.getRawLightDetected());
        }
    }

}
