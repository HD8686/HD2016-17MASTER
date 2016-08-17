package com.qualcomm.ftcrobotcontroller.HDLib.StateMachines;

import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDGeneralLib;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDMRGyro;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDNavX;
import com.qualcomm.ftcrobotcontroller.HDLib.Values;

/**
 * Created by Akash on 8/16/2016.
 */
public class StateMachine {
    Object State;
    Object nextState;
    public boolean waitingActive = false;
    public double timerExpire = 0.0;
    public double targetEncoder = 0.0;
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
        navX.yawPIDController.enable(false);
        waitingActive = false;
        timerExpire = 0.0;
        targetEncoder = 0.0;
        currWaitType = WaitTypes.Nothing;
    }

    public Object getState(){
        if(this.waitingActive){
            switch(this.currWaitType){
                case EncoderCounts:
                    if(HDGeneralLib.isDifferenceWithin(this.targetEncoder,DriveHandler.getInstance().getEncoderCount(),50)){
                        this.resetValues();
                        State = nextState;
                    } else{
                        HDDashboard.getInstance().displayPrintf(2, HDDashboard.textPosition.Centered, "Degrees Left: " + (String.valueOf(Math.abs(this.targetEncoder - DriveHandler.getInstance().getEncoderCount()))));
                    }
                    break;
                case Timer:
                    if(this.timerExpire <= HDGeneralLib.getCurrentTimeSeconds()){
                        this.resetValues();
                        State = nextState;
                    }else{
                        HDDashboard.getInstance().displayPrintf(2, HDDashboard.textPosition.Centered, "Delay Left: " + (String.valueOf(Math.round(this.timerExpire - HDGeneralLib.getCurrentTimeSeconds()))));
                    }
                    break;
                case PIDTarget:
                    if(navX.yawPIDResult.isOnTarget()){
                        this.resetValues();
                        State = nextState;
                    }else{
                        HDDashboard.getInstance().displayPrintf(2, HDDashboard.textPosition.Centered, "PID Error: " + (String.valueOf(Math.round(navX.yawPIDController.getError()))));
                    }
                    break;
                case Nothing:
                    this.resetValues();
                    State = nextState;
                    break;
            }

        }
        HDDashboard.getInstance().displayPrintf(1, HDDashboard.textPosition.Centered, "Current State Running: " + State.toString());
        return State;
    }
}
