package HDCode.HDSamples;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;

import hdlib.OpModeManagement.HDOpMode;
import hdlib.RobotHardwareLib.Drive.DriveHandler;
import hdlib.RobotHardwareLib.Sensors.HDNavX;
import hdlib.StateMachines.HDStateMachine;
import hdlib.Telemetry.HDAutoDiagnostics;

/**
 * Created by Akash on 10/10/2016.
 */

@Disabled
@Autonomous(name = "EmptyOpMode", group = "HDSamples")
public class EmptyOpMode extends HDOpMode {

    HDAutoDiagnostics mHDAutoDiagnostics;
    HDNavX navX;
    DriveHandler robotDrive;
    HDStateMachine SM;

    @Override
    public void Initialize() {
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

        }
    }

}
