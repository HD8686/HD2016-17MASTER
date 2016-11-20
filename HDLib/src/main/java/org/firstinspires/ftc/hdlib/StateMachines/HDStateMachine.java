package org.firstinspires.ftc.hdlib.StateMachines;


/**
 * Created by Akash on 8/16/2016.
 */


import org.firstinspires.ftc.hdlib.General.HDGeneralLib;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDButtonPusher;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;

import java.text.DecimalFormat;

/**
 * This is our Height Differential state machine library.
 * It controls the state of Autonomous and general robot checks like gyro(either NavX or Modern Robotics) calibration which is why we use it in Teleop and Autonomous.
 * The purpose of this library is to make it very easy to define end conditions, and as they are developed they are easy to reuse.
 */
public class HDStateMachine {

    public boolean waitingActive = false;
    public double timerExpire = 0.0;
    public double targetEncoder = 0.0;
    public double targetRange = 0.0;
    public double targetGyro = 0.0;
    public double gyroTolerance = 0.0;
    Object State;
    Object nextState;
    HDMROpticalDistance currODS;
    HDMRRange currRange;
    boolean hasRun = false;
    HDDriveHandler rDrive;
    HDNavX navX;
    HDWaitTypes currWaitType = HDWaitTypes.Nothing;
    HDButtonPusher.beaconColor origLeftColor = null;
    HDButtonPusher.beaconColor origRightColor = null;
    HDButtonPusher currHDButtonPusher = null;
    DecimalFormat df;
    private boolean navX_calibration_complete = false;

    /**
     * This is the definition for the State Machine Class
     *
     * @param robotD The Robot Drive handler for the robot, which controls the drive base.
     * @param navX   The HD NavX class, which provides NavX data.
     */
    public HDStateMachine(HDDriveHandler robotD, HDNavX navX) {
        this.rDrive = robotD;
        this.navX = navX;
        df = new DecimalFormat("#.##");
    }

    /**
     * Use this function to set a condition to wait for, and then switch to the next state which you set
     *
     * @param sL         This is the next state you want it to switch to once your end condition has been met
     * @param typetoWait The type of end condition you want, could be Time, Encoder Counts, PID Target(gyroTurn), etc.
     * @param Argument   The argument that goes with the end condition you want for example Seconds, Encoder Ticks, Degrees and others.
     */
    public void setNextState(Object sL, HDWaitTypes typetoWait, Object Argument, Object Argument2) {
        if (!waitingActive) {
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
                    currODS = ((HDMROpticalDistance) Argument);
                    break;
                case ODStoField:
                    waitingActive = true;
                    currODS = ((HDMROpticalDistance) Argument);
                    break;
                case Range:
                    waitingActive = true;
                    currRange = ((HDMRRange) Argument);
                    targetRange = ((double) Argument2);
                    break;
                case ChangeColor:
                    waitingActive = true;
                    currHDButtonPusher = ((HDButtonPusher) Argument);
                    origLeftColor = currHDButtonPusher.readLeftColor();
                    origRightColor = currHDButtonPusher.readRightColor();
                    break;
                case GyroAngle:
                    waitingActive = true;
                    targetGyro = ((double) Argument);
                    gyroTolerance = ((double) Argument2);
                    break;
                case Nothing:
                    break;
            }
            nextState = sL;
        }
    }

    /**
     * Use this function to set a condition to wait for, and then switch to the next state which you set
     *
     * @param sL         This is the next state you want it to switch to once your end condition has been met
     * @param typetoWait The type of end condition you want, could be Time, Encoder Counts, PID Target(gyroTurn), etc.
     * @param Argument   The argument that goes with the end condition you want for example Seconds, Encoder Ticks, Degrees and others.
     */
    public void setNextState(Object sL, HDWaitTypes typetoWait, Object Argument) {
        setNextState(sL, typetoWait, Argument, 0);
    }

    /**
     * Use this function to set a condition to wait for, and then switch to the next state which you set
     *
     * @param sL         This is the next state you want it to switch to once your end condition has been met
     * @param typetoWait The type of end condition you want, could be Time, Encoder Counts, PID Target(gyroTurn), etc.
     *                   This function is for the wait types that don't require a argument.
     */
    public void setNextState(Object sL, HDWaitTypes typetoWait) {
        setNextState(sL, typetoWait, 0);
    }

    public boolean ready() {
        if (!navX_calibration_complete) {
            navX_calibration_complete = !navX.getSensorData().isCalibrating();
            if (navX_calibration_complete) {
                navX.zeroYaw();
            } else {
                HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(1, "NavX is currently configuring, please wait...");
            }
        }

        return navX_calibration_complete;
    }

    public void resetValues() {
        rDrive.resetValues();
        waitingActive = false;
        timerExpire = 0.0;
        targetEncoder = 0.0;
        currODS = null;
        currRange = null;
        currWaitType = HDWaitTypes.Nothing;
        hasRun = false;
        targetRange = 0.0;
        currHDButtonPusher = null;
        origRightColor = null;
        origLeftColor = null;
        targetGyro = 0.0;
        gyroTolerance = 0.0;
    }

    public void runOnce(Runnable code) {
        if (!hasRun) {
            code.run();
            hasRun = true;
        }
    }

    public Object getState() {
        if (this.waitingActive) {
            switch (this.currWaitType) {
                case EncoderCounts:
                    if (HDGeneralLib.isDifferenceWithin(this.targetEncoder, HDDriveHandler.getInstance().getEncoderCount(), 50)) {
                        this.resetValues();
                        State = nextState;
                    } else {
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(2, "Degrees Left: " + (String.valueOf(Math.abs(this.targetEncoder - HDDriveHandler.getInstance().getEncoderCount()))));
                    }
                    break;
                case Timer:
                    if (this.timerExpire <= HDGeneralLib.getCurrentTimeSeconds()) {
                        this.resetValues();
                        State = nextState;
                    } else {
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(2, "Delay Left: " + (String.valueOf(df.format(this.timerExpire - HDGeneralLib.getCurrentTimeSeconds()))));
                    }
                    break;
                case PIDTarget:
                    if (rDrive.yawPIDResult.isOnTarget()) {
                        this.resetValues();
                        State = nextState;
                    } else {
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(2, "PID Error: " + (String.valueOf(df.format(rDrive.yawPIDController.getError()))));
                    }
                    break;
                case ODStoLine:
                    if (currODS.getRawLightDetected() > .4) {
                        this.resetValues();
                        State = nextState;
                    } else {
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(2, "ODS Value: " + (String.valueOf(df.format(currODS.getRawLightDetected()))));
                    }
                    break;
                case ODStoField:
                    if (currODS.getRawLightDetected() < .4) {
                        this.resetValues();
                        State = nextState;
                    } else {
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(2, "ODS Value: " + (String.valueOf(df.format(currODS.getRawLightDetected()))));
                    }
                    break;
                case Range:
                    if (currRange.getUSValue() == targetRange) {
                        this.resetValues();
                        State = nextState;
                    } else {
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(2, "Range_Button_Pusher Value: " + (String.valueOf(currRange.getUSValue())));
                    }
                    break;
                case ChangeColor:
                    if (currHDButtonPusher.readLeftColor() != origLeftColor || currHDButtonPusher.readRightColor() != origRightColor) {
                        this.resetValues();
                        State = nextState;
                    } else {
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(2, "Left Color Sensor Reading: " + currHDButtonPusher.readLeftColor());
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(3, "Right Color Sensor Reading: " + currHDButtonPusher.readRightColor());
                    }
                    break;
                case GyroAngle:
                    if(Math.abs(navX.getYaw() - targetGyro) < gyroTolerance){
                        this.resetValues();
                        State = nextState;
                    }else {
                        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(2, "Gyro Angle Left: " + (gyroTolerance - Math.abs(navX.getYaw() - targetGyro)));
                    }
                    break;
                case Nothing:
                    this.resetValues();
                    State = nextState;
                    break;
            }

        }
        HDDiagnosticDisplay.getInstance().addLibrarySpecificTelemetry(1, "Current State Running: " + State.toString());
        return State;
    }

    /**
     * Use this function to set the current state of the state machine
     *
     * @param sL This is the state that you want to set the state machine to
     */
    public void setState(Object sL) {
        State = sL;
        resetValues();
    }
}
