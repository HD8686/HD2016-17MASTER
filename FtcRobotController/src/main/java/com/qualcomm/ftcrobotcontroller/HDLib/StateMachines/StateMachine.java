package com.qualcomm.ftcrobotcontroller.HDLib.StateMachines;

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
                    if(sT.timerExpire >= HDGeneralLib.getCurrentTimeSeconds()){
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

        return sTwhatToReturn && HDGyroWhatToReturn;
    }






    public Object getState(){
        return State;
    }
}


