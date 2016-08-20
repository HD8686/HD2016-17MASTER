package org.firstinspires.ftc.teamcode.HDFiles.HDLib.OpModeManagement;

import org.firstinspires.ftc.teamcode.HDFiles.HDLib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.Telemetry.HDAutoDiagnostics;
import org.firstinspires.ftc.teamcode.HDFiles.HDLib.Telemetry.HDDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Akash on 5/8/2016.
 */
public abstract class HDOpMode extends LinearOpMode {
    public HDDashboard mDisplay;
    public static HDOpMode instance = null;
    HDLoopInterface hdLoopInterface;
    HDAutoDiagnostics mHDAutoDiagnostics;
    public HDOpMode() {
        super();
        instance = this;
    }

    public static HDOpMode getInstance()
    {
        return instance;
    }   //getInstance

    public abstract void Initialize();

    public abstract void InitializeLoop();

    public abstract void Start();

    public abstract void continuousRun();

    @Override
    public void runOpMode() throws InterruptedException {
        try {
            mDisplay = new HDDashboard(telemetry);
            hdLoopInterface = new HDLoopInterface();
            mHDAutoDiagnostics = new HDAutoDiagnostics(this, mDisplay);
            Initialize();
            hdLoopInterface.runInitializeLoopInterface();
            while (!opModeIsActive()) {
                waitForNextHardwareCycle();
                hdLoopInterface.runInitializeLoopInterface();
                InitializeLoop();
                HDDashboard.getInstance().refreshDisplay();
            }

            waitForStart();

            Start();
            hdLoopInterface.runStartInterface();

            while (opModeIsActive()) {
                HDDashboard.getInstance().refreshDisplay();
                continuousRun();
                hdLoopInterface.runContinuousRunInterface();
            }
        }catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }finally {
            if(HDNavX.instance != null){
                HDNavX.instance.getSensorData().close();
            }
        }
    }




}
