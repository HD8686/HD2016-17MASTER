package com.qualcomm.ftcrobotcontroller.HDLib.StateMachines;

import com.qualcomm.ftcrobotcontroller.HDLib.HDDashboard;
import com.qualcomm.ftcrobotcontroller.HDLib.HDGeneralLib;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Drive.DriveHandler;
import com.qualcomm.ftcrobotcontroller.HDLib.RobotHardwareLib.Sensors.HDGyro;
import com.qualcomm.robotcore.util.Range;


/**
 * Created by Akash on 5/17/2016.
 */
public class StateMachine {
    Object State;
    Object nextState;
    StateTracker sT;

    public StateMachine(StateTracker stateTracker){
        this.sT = stateTracker;
    }

    public void setState(Object sL){
        State = sL;
    }

    public boolean ready(){
        boolean sTwhatToReturn = true;
        HDDashboard.getInstance().displayPrintf(3, String.valueOf(sT.waitingActive));
        HDDashboard.getInstance().displayPrintf(2, String.valueOf(sT.currWaitType));
        boolean HDGyroWhatToReturn = true;

        if(sT.waitingActive){
            switch(sT.currWaitType){
                case EncoderCounts:
                        if(HDGeneralLib.isDifferenceWithin(sT.targetEncoder,DriveHandler.getInstance().getEncoderCount(),100)){
                            sT.resetValues();
                            sTwhatToReturn = true;
                        } else{
                            sTwhatToReturn = false;
                        }
                    break;
                case Timer:
                    HDDashboard.getInstance().displayPrintf(4, String.valueOf(sT.timerExpire - HDGeneralLib.getCurrentTimeSeconds()));
                    if(sT.timerExpire <= HDGeneralLib.getCurrentTimeSeconds()){
                        sT.resetValues();
                        sTwhatToReturn = true;
                    }else{
                        sTwhatToReturn = false;
                    }
                    break;
                case Nothing:
                    sT.resetValues();
                    sTwhatToReturn = true;
                    break;
            }

        }

        if(HDGyro.getInstance() != null){
            HDGyroWhatToReturn = HDGyro.isReady;
        }
        HDDashboard.getInstance().displayPrintf(5, String.valueOf(sTwhatToReturn));
        return sTwhatToReturn && HDGyroWhatToReturn;
    }






    public Object getState(){
        return State;
    }
}


