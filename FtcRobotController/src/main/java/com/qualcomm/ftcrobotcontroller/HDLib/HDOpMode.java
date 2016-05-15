package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.ftcrobotcontroller.HDVals.Keys;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

/**
 * Created by Akash on 5/8/2016.
 */
public abstract class HDOpMode extends LinearOpMode {
    public static HDOpMode instance = null;
    private double waitTime = 0;

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
        HDLoopInterface hdLoopInterface = new HDLoopInterface();
        Initialize();
        waitTime = System.currentTimeMillis();

        while(!opModeIsActive()){
            if(System.currentTimeMillis() >= waitTime){
                InitializeLoop();
                waitTime = waitTime + Keys.initLoopTime;
            }
        }

        waitTime = 0;

        waitForStart();

        Start();

        while(opModeIsActive()){
            continuousRun();
            hdLoopInterface.runWaitingLoops();
        }
    }
}
