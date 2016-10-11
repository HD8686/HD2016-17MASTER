package hdlib.Telemetry;

import com.qualcomm.robotcore.util.ElapsedTime;



import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import hdlib.OpModeManagement.HDLoopInterface;
import hdlib.OpModeManagement.HDOpMode;
import hdlib.RobotHardwareLib.Drive.DriveHandler;
import hdlib.RobotHardwareLib.Sensors.HDNavX;
import hdlib.RobotHardwareLib.Servo.HDServo;

/**
 * Created by Akash on 8/18/2016.
 */
public class HDAutoDiagnostics implements HDLoopInterface.LoopTimer{
    HDOpMode curOpMode;
    HDDashboard curDashboard;
    DriveHandler curDrive;
    public static List<HDServo> servoList = new ArrayList<HDServo>();
    private static HDAutoDiagnostics instance = null;
    private ElapsedTime runtime = new ElapsedTime();
    private static String[] ProgramSpecificDisplay = new String[20];

    public HDAutoDiagnostics(HDOpMode OpModeInstance, HDDashboard DBInstance, DriveHandler driveHandler){
        instance = this;
        this.curDashboard = DBInstance;
        this.curOpMode = OpModeInstance;
        this.curDrive = driveHandler;
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.ContinuousRun);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.InitializeLoop);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.Start);
    }

    public static HDAutoDiagnostics getInstance(){
        return instance;
    }

    public void addProgramSpecificTelemetry(int lineNum, String format, Object... args){
        ProgramSpecificDisplay[lineNum] = String.format(format, args);
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
    }

    @Override
    public void continuousCallOp() {
        DecimalFormat df = new DecimalFormat("#.##");
        int curLine = 0;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "HD Library Running");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "--------------------Program Specific Telemetry--------------------");
        curLine++;
        boolean ProgramSpecificDisplayEmpty = true;
        for (int i = 0; i < ProgramSpecificDisplay.length; i++)
        {
            if(ProgramSpecificDisplay[i] != null) {
                ProgramSpecificDisplayEmpty = false;
                HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, ProgramSpecificDisplay[i]);
                curLine++;
            }
        }
        if(ProgramSpecificDisplayEmpty){
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "No Program Specific Telemetry");
            curLine++;
        }
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "--------------------------------Diagnostics--------------------------------");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Program Runtime: " + df.format(runtime.seconds()));
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "------------------------------------Motors-----------------------------------");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Current Encoder Counts: ");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, curDrive.getEncoderCountDiagFront());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, curDrive.getEncoderCountDiagBack());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Average Encoder Count: " + curDrive.getEncoderCount());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Current Motor Speeds: ");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, curDrive.getMotorSpeedDiagFront());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, curDrive.getMotorSpeedDiagBack());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "------------------------------------Servos-----------------------------------");
        curLine++;
        if(servoList.isEmpty()){
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "No Servo's Detected");
            curLine++;
        }else{
        for (HDServo curInstance: servoList) {
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Left, curInstance.getServoName() + " current pos: " + curInstance.getCurrPosition());
            curLine++;
        }
        }
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "-----------------------------------Sensors----------------------------------");
        curLine++;
        if(HDNavX.getInstance() != null) {
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "NavX: ");
            curLine++;
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Status: " + (HDNavX.getInstance().getSensorData().isConnected() ? "Connected" : "Disconnected")
                    + ", " + (HDNavX.getInstance().getSensorData().isCalibrating() ? "Currently Calibrating" : "Calibration Complete"));
            curLine++;
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Gyro Yaw: " + df.format(HDNavX.getInstance().getSensorData().getYaw()));
            curLine++;
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Gyro Pitch: " + df.format(HDNavX.getInstance().getSensorData().getPitch()));
            curLine++;
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Gyro Roll: " + df.format(HDNavX.getInstance().getSensorData().getRoll()));
            curLine++;
        }

    }


}
