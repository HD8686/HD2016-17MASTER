package com.qualcomm.ftcrobotcontroller.HDLib.StateMachines;

import android.util.Log;

import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDGeneralLib;
import com.qualcomm.ftcrobotcontroller.HDLib.HDOpMode;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDGyro;
import com.qualcomm.robotcore.util.Range;


/**
 * Created by Akash on 5/17/2016.
 */
public class StateMachine {
    Object State;
    Object nextState;
    public boolean waitingActive = false;
    public double timerExpire = 0.0;
    public double targetEncoder = 0.0;
    DriveHandler rDrive;
    WaitTypes currWaitType = WaitTypes.Nothing;

    public StateMachine(DriveHandler robotD){
        this.rDrive = robotD;
    }

    public void setState(Object sL){
        State = sL;
    }

    public void setNextState(Object sL, WaitTypes typetoWait, double Argument){
        currWaitType = typetoWait;
        switch(typetoWait){
            case Timer:
                waitingActive = true;
                timerExpire = HDGeneralLib.getCurrentTimeSeconds() + Argument;
                break;
            case EncoderCounts:
                waitingActive = true;
                targetEncoder = Argument;
                break;
            case Nothing:
                break;
        }
        State = sL;
    }

    public boolean ready(){
        boolean sTwhatToReturn = true;
        boolean HDGyroWhatToReturn = true;

        if(this.waitingActive){
            switch(this.currWaitType){
                case EncoderCounts:
                        if(HDGeneralLib.isDifferenceWithin(this.targetEncoder,DriveHandler.getInstance().getEncoderCount(),50)){
                            this.resetValues();
                            sTwhatToReturn = true;
                        } else{
                            HDDashboard.getInstance().displayPrintf(2, HDDashboard.textPosition.Centered, "Degrees Left: " + (String.valueOf(Math.abs(this.targetEncoder - DriveHandler.getInstance().getEncoderCount()))));
                            sTwhatToReturn = false;
                        }
                    break;
                case Timer:
                    if(this.timerExpire <= HDGeneralLib.getCurrentTimeSeconds()){
                        this.resetValues();
                        sTwhatToReturn = true;
                    }else{
                        HDDashboard.getInstance().displayPrintf(2, HDDashboard.textPosition.Centered, "Delay Left: " + (String.valueOf(Math.round(this.timerExpire - HDGeneralLib.getCurrentTimeSeconds()))));
                        sTwhatToReturn = false;
                    }
                    break;
                case Nothing:
                    this.resetValues();
                    sTwhatToReturn = true;
                    break;
            }

        }

        if(HDGyro.getInstance() != null){
            HDGyroWhatToReturn = HDGyro.isReady;
        }
        return sTwhatToReturn && HDGyroWhatToReturn;
    }


    public void resetValues()
    {
        waitingActive = false;
        timerExpire = 0.0;
        targetEncoder = 0.0;
        currWaitType = WaitTypes.Nothing;
    }



    public Object getState(){
        HDDashboard.getInstance().displayPrintf(1, HDDashboard.textPosition.Centered, "Current State Running: " + State.toString());
        return State;
    }
}


