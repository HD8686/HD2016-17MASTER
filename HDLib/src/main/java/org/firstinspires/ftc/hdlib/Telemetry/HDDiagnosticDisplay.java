package org.firstinspires.ftc.hdlib.Telemetry;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDLoopInterface;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDVexMotor;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems.HDDriveHandler;
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
    HDDashboard curDashboard;
    HDDriveHandler curDrive;
    HardwareMap mHardwareMap;
    private static HDDiagnosticDisplay instance = null;

    private static String[] ProgramSpecificDisplay = new String[20];
    private static String[] LibrarySpecificDisplay = new String[20];
    private int curLine;

    public HDDiagnosticDisplay(HDDashboard DBInstance, HDDriveHandler driveHandler){
        instance = this;
        this.curDashboard = DBInstance;
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

    public void addLibrarySpecificTelemetry(int lineNum, String format, Object... args){
        LibrarySpecificDisplay[lineNum] = String.format(format, args);
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
        HDDashboard.getInstance().clearDisplay();
        HDDashboard.getInstance().displayPrintf(0, HDDashboard.textPosition.Centered, "HD Library Running");
        HDDashboard.getInstance().refreshDisplay();
    }

    @Override
    public void continuousCallOp() {
        DecimalFormat df = new DecimalFormat("#.##");
        curLine = 0;
        displayCenteredText("HD Library Running");
        displayCenteredText("--------------------Library Specific Telemetry--------------------");
        boolean LibrarySpecificDisplayEmpty = true;
        for (int i = 0; i < LibrarySpecificDisplay.length; i++)
        {
            if(LibrarySpecificDisplay[i] != null) {
                LibrarySpecificDisplayEmpty = false;
                displayCenteredText(LibrarySpecificDisplay[i]);
            }
        }
        if(LibrarySpecificDisplayEmpty){
            displayCenteredText("No Library Specific Telemetry");
        }
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
            displayCenteredText("No Program Specific Telemetry");
        }
        displayCenteredText("--------------------------------Diagnostics--------------------------------");
        displayCenteredText("Program Runtime: " + df.format(HDOpMode.getInstance().elapsedTime.seconds()));
        displayCenteredText("------------------------------------Drive-----------------------------------");
        displayCenteredText("Current Encoder Counts: ");
        displayCenteredText(curDrive.getEncoderCountDiagFront());
        displayCenteredText(curDrive.getEncoderCountDiagBack());
        displayCenteredText("Average Encoder Count: " + curDrive.getEncoderCount());
        displayCenteredText("Current Drive Motor Speeds: ");
        displayCenteredText(curDrive.getMotorSpeedDiagFront());
        displayCenteredText(curDrive.getMotorSpeedDiagBack());
        displayCenteredText("------------------------------------Servos-----------------------------------");
        displayServoDiag(df);
        displayCenteredText("Vex Motors: ");
        displayVexMotorDiag(df);
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
        if(HDOpMode.getInstance().diagnosticBackend.getServo().isEmpty()){
            displayCenteredText("No Servo's Detected");
        }else{
            for (HDServo curInstance: HDOpMode.getInstance().diagnosticBackend.getServo()) {
                displayCenteredText(curInstance.getName() + " Current Pos: " + curInstance.getCurrPosition());
            }}
    }

    private void displayVexMotorDiag(DecimalFormat df){
        if(HDOpMode.getInstance().diagnosticBackend.getServo().isEmpty()){
            displayCenteredText("No Vex Motors's Detected");
        }else{
            for (HDVexMotor curInstance: HDOpMode.getInstance().diagnosticBackend.getVexMotor()) {
                displayCenteredText(curInstance.getName() + " Current Power: " + df.format(curInstance.getCurrPower()));
            }}
    }



    private void displayRangeDiag(DecimalFormat df){
        displayCenteredText("Range Sensor: ");
        if(HDOpMode.getInstance().diagnosticBackend.getRange().isEmpty()){
            displayCenteredText("No Range Sensor's Detected");
        }else{
            for (HDMRRange curInstance: HDOpMode.getInstance().diagnosticBackend.getRange()) {
                displayCenteredText(curInstance.getName() + "  Values: " + " US: " + df.format(curInstance.getUSValue())+ ", ODS: " + df.format(curInstance.getODSValue()));
            }
        }
    }

    private void displayColorDiag(DecimalFormat df){
        displayCenteredText("Color Sensor: ");
        if(HDOpMode.getInstance().diagnosticBackend.getColor().isEmpty()){
            displayCenteredText("No Color Sensor's Detected");
        }else{
            for (HDMRColor curInstance: HDOpMode.getInstance().diagnosticBackend.getColor()) {
                displayCenteredText(curInstance.getName() + " Values: " + " Red: " + df.format(curInstance.getSensor().red())+ ", Green: " + df.format(curInstance.getSensor().green())+ ", Blue: " + df.format(curInstance.getSensor().blue()));
            }
        }
    }

    private void displayODSDiag(DecimalFormat df){
        displayCenteredText("Optical Distance Sensor: ");
        if(HDOpMode.getInstance().diagnosticBackend.getODS().isEmpty()){
            displayCenteredText("No Optical Distance Sensor's Detected");
        }else{
            for (HDMROpticalDistance curInstance: HDOpMode.getInstance().diagnosticBackend.getODS()) {
                displayCenteredText(curInstance.getName() + " Value: " + df.format(curInstance.getRawLightDetected()));
            }
        }
    }





}
