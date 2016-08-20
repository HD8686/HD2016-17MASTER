package org.firstinspires.ftc.teamcode.HDFiles.HDLib.Telemetry;

import org.firstinspires.ftc.teamcode.HDFiles.HDLib.OpModeManagement.HDLoopInterface;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Drive.DriveHandler;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Servo.HDServo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

/**
 * Created by Akash on 8/18/2016.
 */
public class HDAutoDiagnostics implements HDLoopInterface.LoopTimer{
    HDOpMode curOpMode;
    HDDashboard curDashboard;
    DriveHandler curDrive;
    public static List<HDServo> servoList;
    private static HDAutoDiagnostics instance = null;
    private ElapsedTime runtime = new ElapsedTime();
    private static String[] ProgramSpecificDisplay = new String[20];

    public HDAutoDiagnostics(HDOpMode OpModeInstance, HDDashboard DBInstance){
        instance = this;
        this.curDashboard = DBInstance;
        this.curOpMode = OpModeInstance;
        this.curDrive = DriveHandler.getInstance();
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
        int curLine = 0;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "HD Library Running");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "-Program Specific Telemetry-");
        curLine++;
        for (int i = 0; i < ProgramSpecificDisplay.length; i++)
        {
            if(ProgramSpecificDisplay[i] != null) {
                HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, ProgramSpecificDisplay[i]);
                curLine++;
            }
        }
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "----Diagnostics----");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Left, "Program Runtime: " + runtime.toString());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "---------Motors--------");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Left, "Current Encoder Counts: " + curDrive.getEncoderCountDiag());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Left, "Average Encoder Count: " + curDrive.getEncoderCount());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Left, "Current Motor Speeds: " + curDrive.getMotorSpeedDiag());
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "---------Servos--------");
        curLine++;
        for (HDServo curInstance: servoList) {
            HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Left, curInstance.getServoName() + " current pos: " + curInstance.getCurrPosition());
            curLine++;
        }
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "---------Sensors--------");
        curLine++;
        HDDashboard.getInstance().displayPrintf(curLine, HDDashboard.textPosition.Centered, "Basic Sensor Data WIP");
    }

}
