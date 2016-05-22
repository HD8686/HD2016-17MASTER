package com.qualcomm.ftcrobotcontroller.HDLib.StateMachines;

import com.qualcomm.ftcrobotcontroller.HDLib.HDGeneralLib;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;

/**
 * Created by Akash on 5/17/2016.
 */
public class StateTracker {
    public boolean waitingActive = false;
    public double timerExpire = 0.0;
    public double targetEncoder = 0.0;
    DriveHandler rDrive;
    WaitTypes currWaitType = WaitTypes.Nothing;





    public StateTracker(DriveHandler rDrive){
        this.rDrive = rDrive;
    }

    public void resetValues(){
        waitingActive = false;
        timerExpire = 0.0;
        targetEncoder = 0.0;
        currWaitType = WaitTypes.Nothing;
    }



    public void setWait(WaitTypes typetoWait, double Argument){
        currWaitType = typetoWait;
        switch(typetoWait){
            case Timer:
                waitingActive = true;
                timerExpire = HDGeneralLib.getCurrentTimeSeconds() + Argument;
                break;
            case EncoderCounts:
                waitingActive = true;
                targetEncoder = rDrive.getEncoderCount() + Argument;
                break;
            case Nothing:
                break;
        }
    }





}
