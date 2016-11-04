package org.firstinspires.ftc.hdlib.OpModeManagement;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.hdlib.RobotHardwareLib.Sensors.HDNavX;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;


/**
 * Created by Akash on 5/8/2016.
 */
public abstract class HDOpMode extends LinearOpMode {
    public HDDashboard mDisplay;
    private static HDOpMode instance = null;
    HDLoopInterface hdLoopInterface;
    public HDDiagnosticBackend diagnosticBackend;
    public ElapsedTime elapsedTime = new ElapsedTime();

    public HDOpMode() {
        super();
        instance = this;
    }

    public static HDOpMode getInstance()
    {
        return instance;
    }   //getInstance

    public abstract void initialize();

    public abstract void initializeLoop();

    public abstract void Start();

    public abstract void continuousRun(double elapsedTime);

    @Override
    public void runOpMode() {
        try {
            diagnosticBackend = new HDDiagnosticBackend();
            mDisplay = new HDDashboard(telemetry);
            hdLoopInterface = new HDLoopInterface();
            initialize();
            mDisplay.refreshDisplay();
            hdLoopInterface.runInitializeLoopInterface();
            while (!opModeIsActive() && !isStopRequested()) {
                hdLoopInterface.runInitializeLoopInterface();
                initializeLoop();
                mDisplay.refreshDisplay();
                idle();
            }

            waitForStart();
            elapsedTime.reset();
            Start();
            hdLoopInterface.runStartInterface();

            while (opModeIsActive()  && !isStopRequested()) {
                mDisplay.refreshDisplay();
                continuousRun(elapsedTime.seconds());
                hdLoopInterface.runContinuousRunInterface();
                idle();
            }
        }finally {
            if(HDNavX.getInstance() != null){
                HDNavX.getInstance().getSensorData().close();
            }
        }
    }




}
