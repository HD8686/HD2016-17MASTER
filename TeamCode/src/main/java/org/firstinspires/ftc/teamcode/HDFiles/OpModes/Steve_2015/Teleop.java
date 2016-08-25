package org.firstinspires.ftc.teamcode.HDFiles.OpModes.Steve_2015;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorController;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.HDFiles.HDLib.Telemetry.HDDashboard;
import org.firstinspires.ftc.teamcode.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Height Differential on 1/17/2016.
 */


@TeleOp(name = "2015-16 RES-Q Teleop", group = "OldSteve")
public class Teleop extends OpMode implements SensorEventListener {
    /**
     * Define hardware and variables for teleop
     */
    //Define Motors
    DcMotor frontLeft, frontRight, backLeft, backRight, WinchMotor;
    //Define Servos
    public Servo WinchServo,PenguinRight,PenguinLeft,WinchLock,AllClear,AllClear2,AllClear3,AllClear4;
    public Servo ClimberServo;
    public Servo Pinion,Tilt;
    //Define Servo Start Positions
    public double ClimberServoPos = 0.8;
    public double CurrPosition = 0.88;
    double WinchMax = 1;
    double WinchMin = -1;
    double HoldActive = 0;
    double Speed = 1;
    double  LastBumpState = 0;
    //Define Booleans
    public Boolean WinchEnable = true;
    Boolean LeftPenguinUp = true;
    Boolean LastLBumperVal = false;
    Boolean LastRBumperVal = false;
    Boolean AllClearUp = false;
    Boolean WinchLockUp = false;
    Boolean RightPenguinUp = true;
    public String TiltMode = "Abort";
    String ClimberMode = "down";
    View relativeLayout;
    //Define Timers
    private ElapsedTime AllClearT = new ElapsedTime();
    private ElapsedTime LEDTimer = new ElapsedTime();
    private ElapsedTime runtime = new ElapsedTime();
    private ElapsedTime WinchLockT = new ElapsedTime();
    //Define LED Heartbeat
    PWMOutput LED1;
    PWMOutput LED2;
    //Define Display Dashboard
    public HDDashboard dashboard;
    private String startDate;
    //Define Phone Sensors
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;
    private float roll = 0.0f;//Value in radians
    private float[] mGravity;//Latest sensor values
    private float[] mGeomagnetic;//Latest sensor values
    //Reset Timers
    @Override
    public void init_loop() {
        runtime.reset();
        LEDTimer.reset();
        startDate = new SimpleDateFormat("MM/dd/yyyy 'at' hh:mm:ss a").format(new Date());
    }

    @Override
    public void init() {
        //Configure Phone Sensors
        mSensorManager = (SensorManager) hardwareMap.appContext.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        //Configure Display
        dashboard = new HDDashboard(telemetry);
        dashboard.clearDisplay();
        relativeLayout = ((Activity) hardwareMap.appContext).findViewById(R.id.RelativeLayout);
        //Configure LED Heartbeat
        LED1 = hardwareMap.pwmOutput.get("LED");
        LED2 = hardwareMap.pwmOutput.get("LED2");
        //Configure Servos
        WinchMotor = hardwareMap.dcMotor.get("winch");//Winch Motor
        ClimberServo = hardwareMap.servo.get("servo_4");//Climber Servo
        PenguinLeft = hardwareMap.servo.get("servo_2");//Left Zipline
        PenguinRight = hardwareMap.servo.get("servo_3");//Right Zipline
        WinchLock = hardwareMap.servo.get("servo_5");//Winch Lock
        WinchServo = hardwareMap.servo.get("servo_1");//Winch Tilt
        Pinion = hardwareMap.servo.get("pinion");//Button Push Pinion
        Tilt = hardwareMap.servo.get("tilt");//Button Push Tilt
        AllClear = hardwareMap.servo.get("allclear");//Right All Clear First Beam
        AllClear2 = hardwareMap.servo.get("allclear2");//Left All Clear First Beam
        AllClear3 = hardwareMap.servo.get("allclear3");//Right All Clear Second Beam
        AllClear4 = hardwareMap.servo.get("allclear4");//Left All Clear Second Beam
        //Configure Motors
        frontLeft = hardwareMap.dcMotor.get("motor_1");//Front Left
        frontRight = hardwareMap.dcMotor.get("motor_3");//Front Right
        backLeft = hardwareMap.dcMotor.get("motor_2");//Back Left
        backRight = hardwareMap.dcMotor.get("motor_4");//Back Right
        //Configure Motor Direction and Motor Encoders
        frontRight.setDirection(DcMotor.Direction.REVERSE);//Set Front Right to Reverse
        backRight.setDirection(DcMotor.Direction.REVERSE);//Set Back Right to Reverse
        frontLeft.setDirection(DcMotor.Direction.FORWARD);//Set Front Left to Forward
        backLeft.setDirection(DcMotor.Direction.FORWARD);//Set Back Left to Forward
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//Run Front Right With Encoders
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//Run Back Right With Encoders
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//Run Front Left With Encoders
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//Run Back Left With Encoders
        WinchMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);//Run Winch With Encoders
        //Set Init Positions
        LED2.setPulseWidthPeriod(20000);
        LED2.setPulseWidthOutputTime(19500);
        LED1.setPulseWidthPeriod(20000);
        LED1.setPulseWidthOutputTime(19500);
        ClimberServo.setPosition(0.8);
        PenguinRight.setPosition(0);
        PenguinLeft.setPosition(1);
        WinchServo.setPosition(.27);
        WinchLock.setPosition(1);
        Tilt.setPosition(.68);
        Pinion.setPosition(.5);
        AllClear.setPosition(0.495);
        AllClear2.setPosition(.52);
        AllClear3.setPosition(.35);
        AllClear4.setPosition(.95);
    }

    /**
     * Run the different teleop subsystems
     */
    @Override
    public void loop() {
        HDSleep(20);
        DriveSub();//Drivetrain
        TelemetrySub();//Telemetry Display
        ServoSub();//Servos
        WinchSub();//Winch
        LEDSubsystem();//LED Heartbeat
        ClimberSubsystem();//Climbers
    }

    /**
     * Subsystem for basic drive commands
     */
    public void DriveSub(){
        //Speed Controls
        if(gamepad1.left_bumper)
            Speed = 2;
        else if(gamepad1.left_trigger > .5)
            Speed = 1;
        //Holding Force for Mountain
        if(gamepad1.right_bumper){
            td4motor(frontLeft,frontRight,backLeft,backRight,-.25,-.25, 1);
            HoldActive = 1;
        } else{
            td4motor(frontLeft,frontRight,backLeft,backRight,gamepad1.right_stick_y,gamepad1.left_stick_y, Speed);
            HoldActive = 0;
        }
    }

    /**
     * Subsystem for the winch
     */
    public void WinchSub(){
        //Winch Tilt Options
        if(gamepad2.right_stick_y > .5 ||gamepad2.right_stick_y < -.5)
            TiltMode = "Abort";
        if(gamepad2.dpad_left) {
            if (gamepad2.x)
                TiltMode = "HangOver";
            else if (gamepad2.b)
                TiltMode = "PullOver";
            else if (gamepad2.y)
                TiltMode = "Hang";
            else if (gamepad2.a)
                TiltMode = "Pull";
        }
        //Allows Set Tilt Positions to be Aborted
        if(TiltMode.equals("Abort")) {
            if (gamepad2.right_stick_y > .5) {
                CurrPosition = CurrPosition + 0.005;
                WinchServo.setPosition(Range.clip(CurrPosition, 0.27, 1));
                CurrPosition = Range.clip(CurrPosition, .27, 1);
            } else if (gamepad2.right_stick_y < -.5) {
                CurrPosition = CurrPosition - 0.005;
                WinchServo.setPosition(Range.clip(CurrPosition, .27, 1));
                CurrPosition = Range.clip(CurrPosition, .27, 1);
            }
        }
        //Set Tilt Angle for Pull
        else if(TiltMode.equals("Pull")) { //Reduce by .1
            CurrPosition = .9;
        }
        //Set Tilt Angle for Pull Over
        else if(TiltMode.equals("PullOver")){
            CurrPosition = 0.775;
        }
        //Set Tilt Angle for Hang Over
        else if(TiltMode.equals("HangOver")){
            CurrPosition = .235;
        }
        //Set Tilt Angle for Hang
        else if(TiltMode.equals("Hang")){
            if(Math.round(Math.toDegrees(roll)) <= 33){
                CurrPosition = 0.235;
            }
            else if(Math.round(Math.toDegrees(roll)) <= 39){
                CurrPosition = 0.39;
            }
            else if(Math.round(Math.toDegrees(roll)) <= 47){
                CurrPosition = 0.415;
            }
            else if(Math.round(Math.toDegrees(roll)) <= 59){
                CurrPosition = 0.55;
            }
            else if(Math.round(Math.toDegrees(roll)) <= 80){
                CurrPosition = .9;
            }
        }
        if(gamepad2.start){
            WinchLockUp = false;
            WinchMotor.setPower(-gamepad2.left_stick_y);
        }
        //Set Winch Encoder Limits and Winch Speeds
        else{
            if (WinchMotor.getCurrentPosition() > 22000) {
                WinchMax = 0;
                WinchMin = -1;
            } else if (WinchMotor.getCurrentPosition() < 100) {
                WinchMax = 1;
                WinchMin = 0;
            } else if (WinchMotor.getCurrentPosition() < 2000) {
                WinchMax = 1;
                WinchMin = -.3;
            } else if (WinchMotor.getCurrentPosition() < 5000) {
                WinchMax = 1;
                WinchMin = -.6;
            } else if (WinchMotor.getCurrentPosition() < 10000) {
                WinchMax = 1;
                WinchMin = -.9;
            } else {
                WinchMax = 1;
                WinchMin = -1;
            }
            if (WinchEnable && WinchLockT.time() > .5) {
                WinchMotor.setPower(Range.clip(-gamepad2.left_stick_y, WinchMin, WinchMax));
            } else {
                WinchMotor.setPower(0);
            }
        }
        WinchServo.setPosition(Range.clip(CurrPosition, .255, 1));
    }

    /**
     * Subsystem for the all of the servos
     */
    public void ServoSub(){
        //All Clear
        if(AllClearUp){
            if(AllClearT.time() > 2){
                AllClearUp = false;
                AllClear3.setPosition(.35);//Right 2
                AllClear4.setPosition(.95);//Left 2
                AllClear.setPosition(0.495);//Right
                AllClear2.setPosition(.52);//Left
            }else if(AllClearT.time() > .55){
                AllClear3.setPosition(1);//Right 2
                AllClear4.setPosition(.3);//Left 2
            }
            else if(AllClearT.time() > 0){
                AllClear.setPosition(0.62);//Right
                AllClear2.setPosition(.395);//Left
            }
        }
        if(gamepad1.y){
            AllClearT.reset();
            AllClearUp = true;
        }
        //Climber
        if(gamepad2.dpad_down)
            ClimberMode = "down";
        if(gamepad2.dpad_up)
            ClimberMode = "up";
        //Ziplines
        if(RightPenguinUp == true) {
            PenguinRight.setPosition(0);
        }
        else {
            if(Math.round(Math.toDegrees(roll)) > 65 && TiltMode.equals("Hang")){
                PenguinRight.setPosition(.9);
            } else if(TiltMode.equals("Pull")) {
                PenguinRight.setPosition(.70);
            } else {
                PenguinRight.setPosition(.75);//.75
            }
        }
        if(LeftPenguinUp == true) {
            PenguinLeft.setPosition(1);
        }
        else  {
            if(Math.round(Math.toDegrees(roll)) > 65 && TiltMode.equals("Hang")){
                PenguinLeft.setPosition(.1);
            } else if(TiltMode.equals("Pull")){
                PenguinLeft.setPosition(.30);
            } else {
                PenguinLeft.setPosition(.25);//.25
            }
        }
        if (gamepad2.left_bumper == true && LastLBumperVal == false) {
            if (LeftPenguinUp) {
                LeftPenguinUp = false;
            }
            else {
                LeftPenguinUp = true;
            }
        }
        LastLBumperVal = gamepad2.left_bumper;
        if (gamepad2.right_bumper == true && LastRBumperVal == false) {
            if(RightPenguinUp) {
                RightPenguinUp = false;
            }
            else {
                RightPenguinUp = true;
            }
        }
        //Winch
        if(WinchLockUp == true) {
            WinchLock.setPosition(.32);
        }
        else {
            WinchLock.setPosition(1);
        }
        LastRBumperVal = gamepad2.right_bumper;
        if((gamepad2.left_trigger>.5) && LastBumpState<.5 && (gamepad2.right_trigger>.5)){
            if(WinchLockUp) {
                WinchLockUp = false;
                WinchEnable = true;
                WinchLockT.reset();
            }
            else {
                WinchLockUp = true;
                WinchEnable = false;
            }
        }
        LastBumpState = gamepad2.right_trigger;
    }

    /**
     * Display our telemetry data for teleop
     */
    public void TelemetrySub(){
        dashboard.displayPrintf(0, HDDashboard.textPosition.Centered, "Teleop");
        dashboard.displayPrintf(2, HDDashboard.textPosition.Centered,"TeleOp Started At : %s", startDate);
        dashboard.displayPrintf(3,HDDashboard.textPosition.Centered,"Current Run Time : %.2f", runtime.time());
        dashboard.displayPrintf(4,HDDashboard.textPosition.Centered,"Current WinchLock Status : %b", WinchLockUp);
        dashboard.displayPrintf(5,HDDashboard.textPosition.Centered,"Current WinchEnc Status : %d", WinchMotor.getCurrentPosition());
        dashboard.displayPrintf(6,HDDashboard.textPosition.Centered, "Current Roll: %d", Math.round(Math.toDegrees(roll)));
        dashboard.displayPrintf(7,HDDashboard.textPosition.Centered, "Current Tilt Mode: %s, Current Winch Pos: %f", TiltMode,CurrPosition);
        dashboard.displayPrintf(8,HDDashboard.textPosition.Centered,"Current WinchMin : %f", WinchMin);
        if (HoldActive == 1) {
            dashboard.displayPrintf(1,HDDashboard.textPosition.Centered, "Current Drive Speed: HOLDING FORCE");
        }
        else if(Speed == 2) {
            dashboard.displayPrintf(1,HDDashboard.textPosition.Centered, "Current Drive Speed: 50%%");
        }
        else {
            dashboard.displayPrintf(1,HDDashboard.textPosition.Centered, "Current Drive Speed: 100%% ");
        }

    }

    public static void HDSleep(long sleepT){ //Sleep function which allows us to wait
        long TimeUntilDone = System.currentTimeMillis() + sleepT;   //Add sleeptime to Systemtime to find when to stop waiting
        while(sleepT > 0){  //While sleep time is larger than 0, (Still needs to sleep)
            try {
                Thread.sleep(sleepT);   //Try to sleep for sleep time.
            }
            catch (InterruptedException e){
            }
            sleepT = TimeUntilDone - System.currentTimeMillis();    //Calculates sleep time, SleepTime-Current time
        }
    }

    /**
     * Subsystem for the LED heartbeat
     */
    public void LEDSubsystem(){
        if(LEDTimer.time() < .25) {
            LED1.setPulseWidthPeriod(20000);
            LED1.setPulseWidthOutputTime(10);
            LED2.setPulseWidthPeriod(20000);
            LED2.setPulseWidthOutputTime(10000);
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.RED);
                }
            });
        }
        if(LEDTimer.time() > .25) {
            LED1.setPulseWidthPeriod(20000);
            LED1.setPulseWidthOutputTime(10000);
            LED2.setPulseWidthPeriod(20000);
            LED2.setPulseWidthOutputTime(10);
            relativeLayout.post(new Runnable() {
                public void run() {
                    relativeLayout.setBackgroundColor(Color.BLUE);
                }
            });
        }
        if(LEDTimer.time() > .5){
            LEDTimer.reset();
        }
    }
    /**
     * Subsystem for CLimbers
     */
    public void ClimberSubsystem(){
        if(ClimberMode.equals("up"))
            ClimberServoPos = ClimberServoPos - .015;
        if(ClimberMode.equals("down"))
            ClimberServoPos = ClimberServoPos + .025;
        ClimberServoPos = Range.clip(ClimberServoPos,.1,.8);
        ClimberServo.setPosition(Range.clip(ClimberServoPos,.1,.8));
    }
    /**
     * Subsystem for Drivetrain
     */
    public static void td4motor(DcMotor leftFront, DcMotor rightFront, DcMotor leftBack, DcMotor rightBack, double leftValue, double rightValue, double Speed) {
        leftFront.setPower(-leftValue / Speed);
        rightFront.setPower(-rightValue / Speed);
        leftBack.setPower(-leftValue / Speed);
        rightBack.setPower(-rightValue / Speed);
    }
    //Function to Turn Heartbeat Off
    public void stop() {
        mSensorManager.unregisterListener(this);
        LED1.setPulseWidthPeriod(20000);
        LED1.setPulseWidthOutputTime(1);
        LED2.setPulseWidthPeriod(20000);
        LED2.setPulseWidthOutputTime(1);
        relativeLayout.post(new Runnable() {
            public void run() {
                relativeLayout.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        //We need both sensor values to calculate orientation
        //Only one value will have changed when this method called, we assume we can still use the other value.
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity = event.values;
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic = event.values;
        }
        if (mGravity != null && mGeomagnetic != null) {  //make sure we have both before calling getRotationMatrix
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                roll = orientation[1];
            }
        }
    }
}