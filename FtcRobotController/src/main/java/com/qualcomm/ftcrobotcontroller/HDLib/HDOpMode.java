package com.qualcomm.ftcrobotcontroller.HDLib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import java.util.List;

/**
 * Created by Akash on 5/8/2016.
 */
public abstract class HDOpMode extends LinearOpMode {
    public static HDOpMode instance = null;
    private double waitTime = 0;
    public List<HDServo> ServoObjList;

    public HDOpMode() {
        super();
        instance = this;
    }

    public static HDOpMode getInstance()
    {
        return instance;
    }   //getInstance


    public void Initialize(){
    }

    public void InitializeLoop(){
    }

    public void Start(){

    }

    public void continuousRun(){

    }

    @Override
    public void runOpMode() throws InterruptedException {
        ServoObjList.clear();
        Initialize();
        waitTime = System.currentTimeMillis();
        while(!opModeIsActive()){
            if(System.currentTimeMillis() >= waitTime){
                InitializeLoop();
                waitTime = waitTime + Names.initLoopTime;
            }
        }
        waitTime = 0;
        waitForStart();
        Start();
        while(opModeIsActive()){
            continuousRun();
            for(HDServo m: ServoObjList){
                m.SpeedPositionManager();
            }
        }
    }
}
