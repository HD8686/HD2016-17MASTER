package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Akash on 5/8/2016.
 */
public abstract class HDOpMode extends LinearOpMode {
    public static HDOpMode instance = null;
    HDLoopInterface hdLoopInterface;
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
        hdLoopInterface = new HDLoopInterface();
        Initialize();
        HDDashboard.getInstance().displayPrintf(0, HDDashboard.textPosition.Centered,"HDLib Running");
        hdLoopInterface.runInitializeLoopInterface();
        while(!opModeIsActive()){
                waitForNextHardwareCycle();
                hdLoopInterface.runInitializeLoopInterface();
                InitializeLoop();
                HDDashboard.getInstance().refreshDisplay();
        }

        waitForStart();

        Start();
        hdLoopInterface.runStartInterface();

        while(opModeIsActive()){
            HDDashboard.getInstance().refreshDisplay();
            continuousRun();
            hdLoopInterface.runContinuousRunInterface();
        }
    }


}
