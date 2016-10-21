package org.firstinspires.ftc.hdlib.Telemetry;

import android.util.Log;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDLoopInterface;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Drive.HDDriveHandler;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRColor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMROpticalDistance;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDMRRange;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;

import java.text.DecimalFormat;


/**
 * Created by Akash on 8/18/2016.
 */
public class HDDiagnosticDisplay implements HDLoopInterface.LoopTimer{
    HDOpMode curOpMode;
    HDDashboard curDashboard;
    HDDriveHandler curDrive;
    HardwareMap mHardwareMap;
    private static HDDiagnosticDisplay instance = null;
    private ElapsedTime runtime = new ElapsedTime();
    private static String[] ProgramSpecificDisplay = new String[20];
    private int curLine;

    public HDDiagnosticDisplay(HDOpMode OpModeInstance, HDDashboard DBInstance, HDDriveHandler driveHandler){
        instance = this;
        this.curDashboard = DBInstance;
        this.curOpMode = OpModeInstance;
        this.curDrive = driveHandler;
        this.mHardwareMap = HDOpMode.getInstance().hardwareMap;
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.ContinuousRun);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.InitializeLoop);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.Start);
    }

    public static HDDiagnosticDisplay getInstance(){
        return instance;
    }

    public void addProgramSpecificTelemetry(int lineNum, String format, Object... args){
        ProgramSpecificDisplay[lineNum] = String.format(format, args);
    }

    public void clearProgramSpecificTelemetry(){
        ProgramSpecificDisplay = new String[20];
    }

    private void displayCenteredText(String text){
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, text);
        curLine++;
    }

    @Override
    public void InitializeLoopOp() {
        HDDashboard.getInstance().displayPrintf(0, HDDashboard.textPosition.Centered, "HD Library Initialized");
        HDDashboard.getInstance().refreshDisplay();
    }

    @Override
    public void StartOp() {
        HDDashboard.getInstance().displayPrintf(0, HDDashboard.textPosition.Centered, "HD Library Running");
        HDDashboard.getInstance().refreshDisplay();
        runtime.reset();
    }

    @Override
    public void continuousCallOp() {
        DecimalFormat df = new DecimalFormat("#.##");
        curLine = 0;
        displayCenteredText("HD Library Running");
        displayCenteredText("--------------------Program Specific Telemetry--------------------");
        boolean ProgramSpecificDisplayEmpty = true;
        for (int i = 0; i < ProgramSpecificDisplay.length; i++)
        {
            if(ProgramSpecificDisplay[i] != null) {
                ProgramSpecificDisplayEmpty = false;
                displayCenteredText(ProgramSpecificDisplay[i]);
            }
        }
        if(ProgramSpecificDisplayEmpty){
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "No Program Specific Telemetry");
            curLine++;
        }
        displayCenteredText("--------------------------------Diagnostics--------------------------------");
        displayCenteredText("Program Runtime: " + df.format(runtime.seconds()));
        displayCenteredText("------------------------------------Motors-----------------------------------");
        displayCenteredText("Current Encoder Counts: ");
        displayCenteredText(curDrive.getEncoderCountDiagFront());
        displayCenteredText(curDrive.getEncoderCountDiagBack());
        displayCenteredText("Average Encoder Count: " + curDrive.getEncoderCount());
        displayCenteredText("Current Drive Motor Speeds: ");
        displayCenteredText(curDrive.getMotorSpeedDiagFront());
        displayCenteredText(curDrive.getMotorSpeedDiagBack());
        displayCenteredText("------------------------------------Servos-----------------------------------");
        displayServoDiag(df);
        displayCenteredText("-----------------------------------Sensors----------------------------------");
        displayNavXDiag(df);
        displayRangeDiag(df);
        displayColorDiag(df);
        displayODSDiag(df);
    }



    private void displayNavXDiag(DecimalFormat df){
        if(HDNavX.getInstance() != null) {
            displayCenteredText("NavX: ");
            displayCenteredText("Status: " + (HDNavX.getInstance().getSensorData().isConnected() ? "Connected" : "Disconnected")
                    + ", " + (HDNavX.getInstance().getSensorData().isCalibrating() ? "Currently Calibrating" : "Calibration Complete"));
            displayCenteredText("Gyro Yaw: " + df.format(HDNavX.getInstance().getSensorData().getYaw()));
            displayCenteredText("Gyro Pitch: " + df.format(HDNavX.getInstance().getSensorData().getPitch()));
            displayCenteredText("Gyro Roll: " + df.format(HDNavX.getInstance().getSensorData().getRoll()));
        }
    }

    private void displayServoDiag(DecimalFormat df){
        if(HDOpMode.getInstance().hdDiagnosticBackend.getServo().isEmpty()){
            displayCenteredText("No Servo's Detected");
        }else{
            for (HDServo curInstance: HDOpMode.getInstance().hdDiagnosticBackend.getServo()) {
                displayCenteredText(curInstance.getName() + " Current Pos: " + curInstance.getCurrPosition());
            }}
    }



    private void displayRangeDiag(DecimalFormat df){
        displayCenteredText("Range Sensor: ");
        if(HDOpMode.getInstance().hdDiagnosticBackend.getRange().isEmpty()){
            displayCenteredText("No Range Sensor's Detected");
        }else{
            for (HDMRRange curInstance: HDOpMode.getInstance().hdDiagnosticBackend.getRange()) {
                displayCenteredText(curInstance.getName() + " Current Values: " + " US: " + df.format(curInstance.getUSValue())+ ", ODS: " + df.format(curInstance.getODSValue()));
            }
        }
    }

    private void displayColorDiag(DecimalFormat df){
        displayCenteredText("Color Sensor: ");
        if(HDOpMode.getInstance().hdDiagnosticBackend.getColor().isEmpty()){
            displayCenteredText("No Color Sensor's Detected");
        }else{
            for (HDMRColor curInstance: HDOpMode.getInstance().hdDiagnosticBackend.getColor()) {
                Log.w("Color", HDOpMode.getInstance().hdDiagnosticBackend.getColor().toString());
                displayCenteredText(curInstance.getName() + " Current Values: " + " Red: " + df.format(curInstance.getData().red())+ ", Green: " + df.format(curInstance.getData().green())+ ", Blue: " + df.format(curInstance.getData().blue()));
            }
        }
    }

    private void displayODSDiag(DecimalFormat df){
        displayCenteredText("Optical Distance Sensor: ");
        if(HDOpMode.getInstance().hdDiagnosticBackend.getODS().isEmpty()){
            displayCenteredText("No Optical Distance Sensor's Detected");
        }else{
            for (HDMROpticalDistance curInstance: HDOpMode.getInstance().hdDiagnosticBackend.getODS()) {
                displayCenteredText(curInstance.getName() + " Current Value: " + df.format(curInstance.getRawLightDetected()));
            }
        }
    }





}
