package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Akash on 5/8/2016.
 */
public abstract class HDOpMode extends LinearOpMode {
    public static HDOpMode instance = null;
    private double waitTime = 0;
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
        waitTime = System.currentTimeMillis();

        while(!opModeIsActive()){
            if(System.currentTimeMillis() >= waitTime){
                HDDashboard.getInstance().clearDisplay();
                hdLoopInterface.runInitializeLoopInterface();
                InitializeLoop();
                waitTime = waitTime + Values.initLoopTime;
            }
        }

        waitTime = 0;

        waitForStart();

        Start();
        hdLoopInterface.runStartInterface();

        while(opModeIsActive()){
            continuousRun();
            hdLoopInterface.runContinuousRunInterface();
            HDDashboard.getInstance().clearDisplay();
        }
    }
}
