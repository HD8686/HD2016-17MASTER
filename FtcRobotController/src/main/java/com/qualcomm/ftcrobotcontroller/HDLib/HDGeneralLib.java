package com.qualcomm.ftcrobotcontroller.HDLib;

/**
 * Created by Akash on 5/15/2016.
 * This class houses the general methods and functions used in most classes, which doesn't really belong in a specific class due to its wide usage.
 */

public class HDGeneralLib {
    public static double getCurrentTimeSeconds(){
        return System.currentTimeMillis()/1000.0;
    }


}
