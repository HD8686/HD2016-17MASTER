package com.qualcomm.ftcrobotcontroller.HDLib;


/**
 * Created by akash on 5/1/2016.
 */
public final class Values {
    //HDOpMode Settings
    public static final double initLoopTime = 50;

    public final class HardwareMapKeys {

    //Servos
    public static final String climberServo = "servo_4";
    public static final String allClearL = "AllClear";

    //Motors
    public static final String frontRight = "motor_3";
    public static final String frontLeft = "motor_1";
    public static final String backRight = "motor_4";
    public static final String backLeft = "motor_2";

    //Sensors
    public static final String Gyro = "gyro";

    }

    public final class ServoInit {
        //Servo Init Values
        public static final double climberServoInit = .8;
        public static final double allClearLInit = .1;



    }



    public final class ServoSpeedStats {
        //Time to travel 60 degrees
        public static final double HS_311 = .19;
        public static final double HS_322HD = .19;
        public static final double HS_422 = .21;
        public static final double HS_425BB = .21;
        public static final double HS_485HB = .20;
        public static final double HS_625MG = .18;
        public static final double HS_645MG = .24;
        public static final double HS_755HB = .28;
        public static final double HS_755MG = .28;
        public static final double HS_765HB = .28;
        public static final double HS_785HB = .28;
        public static final double HS_805BB = .19;
        public static final double HS_805MG = .19;
    }
}

