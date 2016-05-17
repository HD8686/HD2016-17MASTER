package com.qualcomm.ftcrobotcontroller.HDLib;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by akash on 5/9/2016.
 */
public class HDLoopInterface {

    private static HDLoopInterface instance = null;
    Set<LoopTimer> loopTimerSet = new HashSet<LoopTimer>();

    public HDLoopInterface(){
        instance = this;
    }


    public static HDLoopInterface getInstance()
    {
        return instance;
    }   //getInstance


    public interface LoopTimer
    {
        void continuousCall();
    }

    public void register(LoopTimer lT){
        loopTimerSet.add(lT);
    }

    public void deregister(LoopTimer lT){
        loopTimerSet.remove(lT);
    }


    public void runWaitingLoops(){
        for(LoopTimer tempLoop: loopTimerSet){
            tempLoop.continuousCall();
        }
    }

}
