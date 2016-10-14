package org.firstinspires.ftc.hdlib.StateMachines;



/**
 * Created by Akash on 8/16/2016.
 */


import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;

import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.DriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRGyro;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.Telemetry.HDAutoDiagnostics;

/**
 * This is our Height Differential state machine library.
 * It controls the state of Autonomous and general robot checks like gyro(either NavX or Modern Robotics) calibration which is why we use it in Teleop and Autonomous.
 * The purpose of this library is to make it very easy to define end conditions, and as they are developed they are easy to reuse.
 */
public class HDStateMachine {

    Object State;
    Object nextState;
    OpticalDistanceSensor currODS;
    public boolean waitingActive = false;
    public double timerExpire = 0.0;
    public double targetEncoder = 0.0;
    boolean hasRun = false;
    DriveHandler rDrive;
    HDNavX navX;
    HDWaitTypes currWaitType = HDWaitTypes.Nothing;


    /**
     * This is the definition for the State Machine Class
     * @param robotD The Robot Drive handler for the robot, which controls the drive base.
     * @param navX The HD NavX class, which provides NavX data.
     */
    public HDStateMachine(DriveHandler robotD, HDNavX navX){
        this.rDrive = robotD;
        this.navX = navX;
    }

    /**
     * Use this function to set the current state of the state machine
     * @param sL This is the state that you want to set the state machine to
     */
    public void setState(Object sL){
        State = sL;
    }

    /**
     * Use this function to set a condition to wait for, and then switch to the next state which you set
     * @param sL This is the next state you want it to switch to once your end condition has been met
     * @param typetoWait The type of end condition you want, could be Time, Encoder Counts, PID Target(gyroTurn), etc.
     * @param Argument The argument that goes with the end condition you want for example Seconds, Encoder Ticks, Degrees and others.
     */
    public void setNextState(Object sL, HDWaitTypes typetoWait, Object Argument){
        if(!waitingActive) {
            currWaitType = typetoWait;
            switch (typetoWait) {
                case Timer:
                    waitingActive = true;
                    timerExpire = HDGeneralLib.getCurrentTimeSeconds() + ((double) Argument);
                    break;
                case EncoderCounts:
                    waitingActive = true;
                    targetEncoder = ((double) Argument);
                    break;
                case PIDTarget:
                    waitingActive = true;
                    break;
                case ODStoLine:
                    waitingActive = true;
                    currODS = ((OpticalDistanceSensor) Argument);
                    break;
                case ODStoField:
                    waitingActive = true;
                    currODS = ((OpticalDistanceSensor) Argument);
                    break;
                case Nothing:
                    break;
            }
            nextState = sL;
        }
    }

    /**
     *Use this function to set a condition to wait for, and then switch to the next state which you set
     * @param sL This is the next state you want it to switch to once your end condition has been met
     * @param typetoWait The type of end condition you want, could be Time, Encoder Counts, PID Target(gyroTurn), etc.
     * This function is for the wait types that don't require a argument.
     */
    public void setNextState(Object sL, HDWaitTypes typetoWait){
        setNextState(sL, typetoWait, 0);
    }

    public boolean ready(){
        boolean HDGyroWhatToReturn = true;
        if(HDMRGyro.getInstance() != null){
            HDGyroWhatToReturn = HDMRGyro.isReady;
        }
        if(navX.getSensorData().isCalibrating()){
            HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(1, "NavX is currently configuring, please wait...");
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
        currODS = null;
        currWaitType = HDWaitTypes.Nothing;
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
                case ODStoLine:
                    if(currODS.getRawLightDetected() > .75){
                        this.resetValues();
                        State = nextState;
                    }else{
                        HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(2,"ODS Value: " + (String.valueOf(currODS.getRawLightDetected())));
                    }
                    break;
                case ODStoField:
                    if(currODS.getRawLightDetected() < .75){
                        this.resetValues();
                        State = nextState;
                    }else{
                        HDAutoDiagnostics.getInstance().addProgramSpecificTelemetry(2,"ODS Value: " + (String.valueOf(currODS.getRawLightDetected())));
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
