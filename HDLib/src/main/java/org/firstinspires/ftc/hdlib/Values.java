package org.firstinspires.ftc.hdlib;


/**
 * Created by akash on 5/1/2016.
 */
public final class Values {
    public final class Constants{
        public static final float mmPerInch = 25.4f;
    }

    public final class Vuforia{
        public static final String VuforiaKey = "AZRKRX7/////AAAAGcjcJ3tvlkTjn8e4xEVqyXhl9BWco/Vc+Xkv238" +
                "4x9sMZzG3BzUJLKDyqcaA0txYsQo00NqXurmoRHm90/OJcQYIkWWV9plQZ6nLVv07yFl8PqTGnRNVazOgi1IzxP" +
                "WqGBznN5sGboRXvAUn+VQsdyN3e0KU6lB/Cl5vre2Wi7DtntufCGNcdqU0pdN9LlKpQr6byV4zYQ7p81g3cEHY5" +
                "AkI3egvEy+thpk3NjyUMeFK9SeCfIKgDvHEx9G4bCBFmmj/+knydr5BM0bJ0Jh5GmNRLNErQewCvA+SET/K2jYR" +
                "ZvEcpvZTxSPQS4ho+JBxC4pk7i4KmPwrWHHnHSCqmwboLv301r2njWgHcs6UZK1+";
    }

    public final class NavX{
        public static final byte NAVX_DEVICE_UPDATE_RATE_HZ = 50;
    }

    public final class PIDSettings{
        public static final double DRIVE_SPEED_ON_TARGET = .325;
        public static final double TOLERANCE_DEGREES = 1.0;
        public static final double VLF_MIN_MOTOR_OUTPUT_VALUE = -0.65;
        public static final double VLF_MAX_MOTOR_OUTPUT_VALUE = 0.65;
        public static final double GYRO_MIN_MOTOR_OUTPUT_VALUE = -0.50;
        public static final double GYRO_MAX_MOTOR_OUTPUT_VALUE = 0.50;
        public static final double STURN_MIN_MOTOR_OUTPUT_VALUE = -0.125;
        public static final double STURN_MAX_MOTOR_OUTPUT_VALUE = 0.125;
        public static final double MECANUM_MIN_MOTOR_OUTPUT_VALUE = -1;
        public static final double MECANUM_MAX_MOTOR_OUTPUT_VALUE = 1;
        public static final double YAW_PID_P = 0.006;
        public static final double YAW_PID_I = 0.0;
        public static final double YAW_PID_D = 0.0;
    }

    public final class HardwareMapKeys {

    //Servos
    public static final String climberServo = "servo_4";
    public static final String Servo_Button_Pusher_Right = "Servo_Button_Pusher_Right";
    public static final String Servo_Button_Pusher_Left = "Servo_Button_Pusher_Left";
    //Motors
    public static final String frontRight = "rightFront";
    public static final String frontLeft = "leftFront";
    public static final String backRight = "rightBack";
    public static final String backLeft = "leftBack";

    //Sensors
    public static final String DeviceInterfaceModule = "dim";
    public static final String Gyro = "gyro";
    public static final String Range_Button_Pusher = "Range_Button_Pusher";
    public static final String ODS_Back = "ODS_Back";
    public static final String Color_Left_Button_Pusher = "Color_Left_Button_Pusher";
    public static final String Color_Right_Button_Pusher = "Color_Right_Button_Pusher";
    public static final int NAVX_I2C_PORT = 4;
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

