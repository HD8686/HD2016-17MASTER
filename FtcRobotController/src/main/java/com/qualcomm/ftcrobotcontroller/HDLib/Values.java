package com.qualcomm.ftcrobotcontroller.HDLib;


/**
 * Created by akash on 5/1/2016.
 */
public final class Values {
    //HDOpMode Settings

    public final class NavX{
        public static final byte NAVX_DEVICE_UPDATE_RATE_HZ = 50;
    }

    public final class PIDSettings{
        public static final double TOLERANCE_DEGREES = 2.0;
        public static final double MIN_MOTOR_OUTPUT_VALUE = -1.0;
        public static final double MAX_MOTOR_OUTPUT_VALUE = 1.0;
        public static final double YAW_PID_P = 0.005;
        public static final double YAW_PID_I = 0.0;
        public static final double YAW_PID_D = 0.0;
    }

    public final class HardwareMapKeys {

    //Servos
    public static final String climberServo = "servo_4";

    //Motors
    public static final String frontRight = "rightFront";
    public static final String frontLeft = "leftFront";
    public static final String backRight = "rightBack";
    public static final String backLeft = "leftBack";

    //Sensors
    public static final String DeviceInterfaceModule = "dim";
    public static final String Gyro = "gyro";
    public static final String Range = "range";
    public static final int NAVX_I2C = 1;
    }

    public final class ServoInit {
        //Servo Init Values
        public static final double climberServoInit = .8;



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

