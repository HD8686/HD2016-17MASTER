package org.firstinspires.ftc.teamcode.HDFiles.HDLib.StateMachines;

import org.firstinspires.ftc.teamcode.HDFiles.HDLib.HDGeneralLib;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Drive.DriveHandler;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Sensors.HDMRGyro;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.Telemetry.HDAutoDiagnostics;

/**
 * Created by Akash on 8/16/2016.
 */

/**
 * This is our state machine library
 * it manages the changing of states and different waiting types for each sensor
 *
 */
public class StateMachine {
    Object State;
    Object nextState;
    public boolean waitingActive = false;
    public double timerExpire = 0.0;
    public double targetEncoder = 0.0;
    boolean hasRun = false;
    DriveHandler rDrive;
    HDNavX navX;
    WaitTypes currWaitType = WaitTypes.Nothing;

    public StateMachine(DriveHandler robotD, HDNavX navX){
        this.rDrive = robotD;
        this.navX = navX;
    }

    public void setState(Object sL){
        State = sL;
    }

    public void setNextState(Object sL, WaitTypes typetoWait, double Argument){
        if(!waitingActive) {
            currWaitType = typetoWait;
            switch (typetoWait) {
                case Timer:
                    waitingActive = true;
                    timerExpire = HDGeneralLib.getCurrentTimeSeconds() + Argument;
                    break;
                case EncoderCounts:
                    waitingActive = true;
                    targetEncoder = Argument;
                    break;
                case PIDTarget:
                    waitingActive = true;
                    break;
                case Nothing:
                    break;
            }
            nextState = sL;
        }
    }

    public void setNextState(Object sL, WaitTypes typetoWait){
        setNextState(sL, typetoWait, 0);
    }

    public boolean ready(){
        boolean HDGyroWhatToReturn = true;
        if(HDMRGyro.getInstance() != null){
            HDGyroWhatToReturn = HDMRGyro.isReady;
        }
        if(navX.getSensorData().isCalibrating()){
            HDOpMode.instance.telemetry.addData("navX-Micro", "Startup Calibration in Progress");
        }
        return !navX.getSensorData().isCalibrating() && HDGyroWhatToReturn;
    }


    public void resetValues()
    {
        rDrive.firstRun = true;
        navX.yawPIDController.enable(false);
        waitingActive = false;
        timerExpire = 0.0;
        targetEncoder = 0.0;
        currWaitType = WaitTypes.Nothing;
        hasRun = false;
    }

    public void runOnce(Runnable code){
        if(!hasRun){
            code.run();
            hasRun = true;
        }
    }

    public Object getState(){
        if(this.waitingActive){
            switch(this.currWaitType){
                case EncoderCounts:
                    if(HDGeneralLib.isDifferenceWithin(this.targetEncoder,DriveHandler.getInstance().getEncoderCount(),50)){
                        this.resetValues();
                        State = nextState;
                    } else{
                        HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(2,"Degrees Left: " + (String.valueOf(Math.abs(this.targetEncoder - DriveHandler.getInstance().getEncoderCount()))));
                      }
                    break;
                case Timer:
                    if(this.timerExpire <= HDGeneralLib.getCurrentTimeSeconds()){
                        this.resetValues();
                        State = nextState;
                    }else{
                        HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(2,"Delay Left: " + (String.valueOf(Math.round(this.timerExpire - HDGeneralLib.getCurrentTimeSeconds()))));
                        }
                    break;
                case PIDTarget:
                    if(navX.yawPIDResult.isOnTarget()){
                        this.resetValues();
                        State = nextState;
                    }else{
                        HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(2,"PID Error: " + (String.valueOf(Math.round(navX.yawPIDController.getError()))));
                    }
                    break;
                case Nothing:
                    this.resetValues();
                    State = nextState;
                    break;
            }

        }
        HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(1,"Current State Running: " + State.toString());
        return State;
    }
}
