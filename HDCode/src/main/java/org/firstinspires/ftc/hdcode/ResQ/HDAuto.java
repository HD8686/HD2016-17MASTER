package org.firstinspires.ftc.hdcode.ResQ;

/**
 * Created by Height Differential on 1/17/2016.
 */

import android.graphics.Color;
import android.util.Log;
import android.view.View;

import com.qualcomm.hardware.modernrobotics.ModernRoboticsI2cGyro;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.PWMOutput;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.util.Arrays;
import java.util.List;

/**
 * Define hardware for autonomous
 */
public abstract class HDAuto extends LinearOpMode {
    public View relativeLayout;
    public HDDashboard_2015 dashboard;
    public Boolean LEDON = true;
    public Servo PenguinRight,PenguinLeft,ClimberServo,WinchServo,Pinion,Tilt,WinchLock,AllClear,AllClear2;
    public DcMotor frontLeft, frontRight, backLeft, backRight;
    public OpticalDistanceSensor optical,optical2,optical3;
    public TouchSensor Touch1,Touch2;
    public ColorSensor mColor;
    public ModernRoboticsI2cGyro mGyro;
    public PWMOutput LED1,LED2;
    public ElapsedTime LEDTimer = new ElapsedTime();
    public ElapsedTime ProgramRunTime = new ElapsedTime();
    public ElapsedTime GeneralTime = new ElapsedTime();
    public ElapsedTime GyroTimer = new ElapsedTime();
    public int  delay = 0;
    public double ClimberServoPos = 0.78, WhiteLineSub = 0, TimeOut = 0,ClimberWait = 0, EncBackUp = 0, BackUpAmount = 0, DefenseTiles = 0, EncForward = 0;
    public Boolean Done = false, goalReached = false, isRed = false, isBlue = false, climbermadeit = false, Finished = false, LastValR = false, LastValL = false, LastValA = false;

    /**
     * Menu Configurations
     */
    public enum Alliance
    {
        RED_ALLIANCE,
        BLUE_ALLIANCE;
        private static Alliance[] vals = values();
        public Alliance next(){
            return vals[(this.ordinal()+1)% vals.length];
        }
    }
    public enum AutonomousConfigs
    {
        Offense,
        Defense;
        private static AutonomousConfigs[] vals = values();
        public AutonomousConfigs next(){
            return vals[(this.ordinal()+1)% vals.length];
        }
    }
    public enum OffenseConfigs
    {
        OffenseStay,
        OffenseLeaveDefense,
        OffenseLeaveFloorGoal,
        OffenseLeaveCorner,
        OffenseLeaveBackup;
        private static OffenseConfigs[] vals = values();
        public OffenseConfigs next(){
            return vals[(this.ordinal()+1)% vals.length];
        }
    }
    public enum BallConfigs
    {
        BallClear,
        NoBallClear;
        private static BallConfigs[] vals = values();
        public BallConfigs next(){
            return vals[(this.ordinal()+1)% vals.length];
        }
    }
    public enum DefenseConfigs
    {
        SideDefend,
        ForwardAndDefend,
        Protect;
        private static DefenseConfigs[] vals = values();
        public DefenseConfigs next(){
            return vals[(this.ordinal()+1)% vals.length];
        }
    }
    public Alliance alliance = Alliance.RED_ALLIANCE;
    public AutonomousConfigs autonconfigs = AutonomousConfigs.Offense;
    public OffenseConfigs offenseconfigs = OffenseConfigs.OffenseStay;
    public BallConfigs ballconfig = BallConfigs.NoBallClear;
    public DefenseConfigs defenseconfigs = DefenseConfigs.SideDefend;

    /**
     * Robot states for blue climbers
     */
    public enum BlueClimbers
    {
        STATE_Delay, //Give the gyro time to calibrate
        STATE_DriveForward1, //Drive forward so we can turn
        STATE_GyroTurn1, //Turn to be parallel with the mountain
        STATE_DriveForward2, //Drive forward right before the white line in front of the rescue beacon
        STATE_LookForWhite, //Drive forward until we find the white line
        STATE_ClearBallArc,//Clear balls out of the way
        STATE_ClearBallArcBack,//Turn back to white line
        STATE_DriveBack1,//Drive back so we are on the left side of the white line
        STATE_ArcTurn,//Turn to white line
        STATE_DriveStraight,//Line follow
        STATE_DrivePinion,//Extend button push
        STATE_Hit_Button,//Hit button
        STATE_BackUp,//Back up to drop climber
        STATE_ClimberDrop,//Drop climbers
        STATE_Retract,//Retract botton push
        STATE_RetractFailsafe,//If the touch sensors aren't pressed, don't drop climbers
        STATE_WAIT,//Choose leave option
        STATE_BackUp2,//Back up to turn
        STATE_Turn,//Turn to get out of the way
        STATE_Defence,//Turn to line up with opposing beacon
        STATE_DriveForward4,//Drive forward into opposing rescue beacon zone
        STATE_DriveBackward,//Back up to be out of the way
    }

    public enum ForwardAndDefend
    {
        STATE_Delay, //Give the gyro time to calibrate
        STATE_DriveForward1,//Drive forward so we can turn
        STATE_Wait,//Wait for the 10 second rule
        STATE_DriveForward2//Drive forward to defend
    }
    public enum ProtectStates
    {
        STATE_Delay,//Give the gyro time to calibrate
        STATE_DriveForward1,//Drive forward so we can turn
        STATE_GyroTurn1,//Turn to be parrallel with the center line
        STATE_DriveForward2//Drive forward to be in front of the rescue beacon zone
    }
    public enum SideDefend
    {
        STATE_Delay, //Give the gyro time to calibrate
        STATE_DriveForward1,//Drive forward so we can turn
        STATE_Wait,//Wait for the 10 second rule
        STATE_DriveForward2,//Drive forward to be in the opposing floor goal
        STATE_GyroTurn1,//Turn to face the rescue beacon
        STATE_DriveForward3//Drive forward into the rescue beacon zone
    }

    /**
     * Robot states for red climbers
     */
    public enum RedClimbers
    {
        STATE_Delay, //Give the gyro time to calibrate
        STATE_DriveForward1,//Drive forward so we can turn
        STATE_GyroTurn1,//Turn to be parallel with the mountain
        STATE_DriveForward2, //Drive forward right before the white line in front of the rescue beacon
        STATE_LookForRED,//Drive forward to look for the red line
        STATE_DrivePastLine,//Drive forward to look for white
        STATE_LookForWhite,//Drive forward until we find the white line
        STATE_ClearBallArc,//Clear balls out of the way
        STATE_ClearBallArcBack,//Turn back to white line
        STATE_DriveBack1,//Drive back so we are on the left side of the white line
        STATE_ArcTurn,//Turn to white line
        STATE_DriveStraight,//Line follow
        STATE_DrivePinion,//Extend button push
        STATE_Hit_Button,//Hit button
        STATE_BackUp,//Back up to drop climber
        STATE_ClimberDrop,//Drop climbers
        STATE_Retract,//Retract botton push
        STATE_RetractFailsafe,//If the touch sensors aren't pressed, don't drop climbers
        STATE_WAIT,//Choose leave option
        STATE_BackUp2,//Back up to turn
        STATE_Turn,//Turn to get out of the way
        STATE_Defence,//Turn to line up with opposing beacon
        STATE_DriveForward4,//Drive forward into opposing rescue beacon zone
        STATE_DriveBackward,//Back up to be out of the way
    }


    /**
     * Blue climber program
     */
    public void CLIMBERBLUE(SteveAutonomous auton) throws InterruptedException {
        BlueClimbers mCurrState = BlueClimbers.STATE_Delay;
        /**
         * Run all motors with encoders
         * Set direction for each motor
         */
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        GeneralTime.reset();
        while(mGyro.isCalibrating()){
            waitForNextHardwareCycle();
            telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time()));//Display how much time is left of our delay
        }
        final double EncoderToInch = 71;//How many encoder counts are in 1 inch
        while (auton.opModeIsActive() && Finished == false) {
            LEDSubsystem();
            switch (mCurrState) {
                case STATE_Delay://Our current robot state: wait for the gyro to calibrate
                    if (GeneralTime.time() > delay) {
                        mCurrState = BlueClimbers.STATE_DriveForward1;//Move to next robot state
                    } else
                        telemetry.addData("1: ", "Delay Left(GyroDone) " + String.valueOf(delay - GeneralTime.time()));//Display how much time is left of our delay
                    break;
                case STATE_DriveForward1://Our current robot state: drive forward from start
                    telemetry.addData("2", mGyro.getHeading());
                    if (AvgEncoder() < 20 * EncoderToInch) {
                        VLF(1, 0);
                    } else {
                        StopMotors();
                        mCurrState = BlueClimbers.STATE_GyroTurn1;//Move to next robot state
                    }
                    break;
                case STATE_GyroTurn1://Our current robot state: turn parallel with the mountain
                    if (goalReached == false)
                        GyroTurn(45);
                    else {
                        mCurrState = BlueClimbers.STATE_DriveForward2;//Move to next robot state
                        goalReached = false;
                    }
                    break;
                case STATE_DriveForward2://Our current robot state: drive forward close to the white line
                    telemetry.addData("2", mGyro.getHeading());
                    if (AvgEncoder() < 68 * EncoderToInch) {
                        VLF(1, 45);
                    } else {
                        mCurrState = BlueClimbers.STATE_LookForWhite;//Move to next robot state
                    }
                    break;
                case STATE_LookForWhite://Our current robot state: drive forward until we find the white line
                    if (optical.getRawLightDetected() < 10) {
                        VLF(1, 45);
                    } else {
                        if(ballconfig == BallConfigs.BallClear) {
                            mCurrState = BlueClimbers.STATE_ClearBallArc;//Move to next robot state
                        }
                        else{
                            GeneralTime.reset();
                            mCurrState = BlueClimbers.STATE_DriveBack1;//Move to next robot state
                        }
                        StopMotors();
                    }
                    break;
                case STATE_ClearBallArc:
                    if (mGyro.getIntegratedZValue() < 3) {
                        telemetry.addData("Gero", mGyro.getIntegratedZValue());
                        frontLeft.setPower(.4);
                        backLeft.setPower(.4);
                    } else {
                        StopMotors();
                        mCurrState = BlueClimbers.STATE_ClearBallArcBack;
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_ClearBallArcBack:
                    if (optical.getRawLightDetected() < 10) {
                        telemetry.addData("Gero", mGyro.getIntegratedZValue());
                        frontLeft.setPower(-.4);
                        backLeft.setPower(-.4);
                    } else {
                        StopMotors();
                        GeneralTime.reset();
                        mCurrState = BlueClimbers.STATE_ArcTurn;
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DriveBack1:
                    if(GeneralTime.time() < .2) {
                        VLF(-1, 45);
                    }
                    else {
                        StopMotors();
                        GeneralTime.reset();
                        mCurrState = BlueClimbers.STATE_ArcTurn;
                    }
                    break;
                case STATE_ArcTurn: //Our current robot state: turn parallel with the mountain
                    if (mGyro.getIntegratedZValue() > -80) {
                        telemetry.addData("Gero", mGyro.getIntegratedZValue());
                        frontLeft.setPower(-.4);
                        backLeft.setPower(-.4);
                    } else {
                        StopMotors();
                        mCurrState = BlueClimbers.STATE_DriveStraight;
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DriveStraight:
                    if (GeneralTime.time() < .1) {
                    } else if (GeneralTime.time() < 4.25) {
                        if (optical3.getRawLightDetected() < 20) {
                            td4motor(frontLeft, frontRight, backLeft, backRight, 0, -.8, 1);
                        } else {
                            td4motor(frontLeft, frontRight, backLeft, backRight, -.8, 0, 1);
                        }
                    } else {
                        StopMotors();
                        mCurrState = BlueClimbers.STATE_DrivePinion;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DrivePinion:
                    LEDON = false;
                    if(GeneralTime.time() < 3.6) {
                        if (!Touch1.isPressed() && !Touch2.isPressed()) {
                            Pinion.setPosition(.1);

                        } else {
                            TimeOut = GeneralTime.time();
                            Pinion.setPosition(.5);
                            int redValue = mColor.red();
                            int greenValue = mColor.green();
                            int blueValue = mColor.blue();
                            isRed = redValue > blueValue && redValue > greenValue;
                            isBlue = blueValue > redValue && blueValue > greenValue;
                            GeneralTime.reset();
                            mCurrState = BlueClimbers.STATE_Hit_Button;
                        }
                    }
                    else{
                        GeneralTime.reset();
                        mCurrState = BlueClimbers.STATE_RetractFailsafe;
                    }
                    break;
                case STATE_Hit_Button:
                    if (GeneralTime.time() < .5) {
                        if (isRed) {
                            Pinion.setPosition(.5);
                            telemetry.addData("ColoRead", "Red");
                            Tilt.setPosition(.9);
                            LED2.setPulseWidthPeriod(20000);
                            LED2.setPulseWidthOutputTime(10000);
                        } else if (isBlue) {
                            Pinion.setPosition(.5);
                            telemetry.addData("ColoRead", "Blue");
                            Tilt.setPosition(.4);
                            LED1.setPulseWidthPeriod(20000);
                            LED1.setPulseWidthOutputTime(10000);
                        }
                    } else {
                        BackUpAmount = (-215.9733777*TimeOut)+700.5557404;
                        EncBackUp = (AvgEncoder()) - BackUpAmount;
                        Tilt.setPosition(.68);
                        GeneralTime.reset();
                        mCurrState = BlueClimbers.STATE_Retract;
                    }
                    break;
                case STATE_Retract:
                    if (GeneralTime.time() < TimeOut) {
                        Pinion.setPosition(0.9);
                        if (AvgEncoder() >= EncBackUp) {
                            frontLeft.setPower(-.2);
                            frontRight.setPower(-.2);
                            backLeft.setPower(-.2);
                            backRight.setPower(-.2);
                        }
                        else{
                            ClimberServoPos = ClimberServoPos - .005;
                            ClimberServoPos = Range.clip(ClimberServoPos, .1, .8);
                            ClimberServo.setPosition(Range.clip(ClimberServoPos, .1, .8));
                            if(ClimberServo.getPosition() == .1){
                                climbermadeit = true;
                            }
                            StopMotors();
                        }

                    } else {
                        Pinion.setPosition(.5);
                        GeneralTime.reset();
                        mCurrState = BlueClimbers.STATE_BackUp;
                    }
                    break;
                case STATE_BackUp:
                    LEDON = true;
                    if (AvgEncoder() >= EncBackUp) {
                        frontLeft.setPower(-.2);
                        frontRight.setPower(-.2);
                        backLeft.setPower(-.2);
                        backRight.setPower(-.2);
                    } else {
                        if(climbermadeit == true){
                            ClimberWait = .5;
                        }
                        else{
                            ClimberWait = 2.5;
                        }
                        Done = false;
                        StopMotors();
                        mCurrState = BlueClimbers.STATE_ClimberDrop;//Move to next robot state
                    }
                    break;
                case STATE_ClimberDrop:
                    if (GeneralTime.time() < ClimberWait) {
                        StopMotors();
                        ClimberServoPos = ClimberServoPos - .005;
                        ClimberServoPos = Range.clip(ClimberServoPos, .1, .8);
                        ClimberServo.setPosition(Range.clip(ClimberServoPos, .1, .8));
                    } else if (GeneralTime.time() < 3.5) {
                        ClimberServoPos = ClimberServoPos + .008;
                        ClimberServoPos = Range.clip(ClimberServoPos, .1, .8);
                        ClimberServo.setPosition(Range.clip(ClimberServoPos, .1, .8));

                    } else {
                        GeneralTime.reset();
                        goalReached = false;
                        mCurrState = BlueClimbers.STATE_WAIT;
                    }
                    break;
                case STATE_WAIT:
                    if(GeneralTime.time() < 1){
                        goalReached = false;
                        if(offenseconfigs == OffenseConfigs.OffenseLeaveDefense){
                            mCurrState = BlueClimbers.STATE_Defence;
                        }else if(offenseconfigs == OffenseConfigs.OffenseLeaveCorner){
                            mCurrState = BlueClimbers.STATE_Turn;
                        }else if(offenseconfigs == OffenseConfigs.OffenseLeaveFloorGoal){
                            mCurrState = BlueClimbers.STATE_Turn;
                        }else if(offenseconfigs == OffenseConfigs.OffenseLeaveBackup){
                            GeneralTime.reset();
                            mCurrState = BlueClimbers.STATE_DriveBackward;
                        }
                    }
                    else{
                        Finished = true;
                    }
                    break;
                case STATE_RetractFailsafe:
                    if (GeneralTime.time() < 3.5) {
                        Pinion.setPosition(0.9);

                    } else {
                        Pinion.setPosition(.5);
                        Finished = true;
                    }
                    break;
                case STATE_Defence:
                    if (mGyro.getIntegratedZValue() > -130) {
                        telemetry.addData("Gero", mGyro.getIntegratedZValue());
                        frontLeft.setPower(-.65);
                        backLeft.setPower(-.65);
                    } else {
                        StopMotors();
                        mCurrState = BlueClimbers.STATE_BackUp2;
                        GeneralTime.reset();
                        EncBackUp = (AvgEncoder()) - 4500;
                    }
                    break;
                case STATE_BackUp2:
                    if(offenseconfigs == OffenseConfigs.OffenseLeaveDefense) {
                        if (AvgEncoder() >= EncBackUp) {
                            frontLeft.setPower(-.5);
                            frontRight.setPower(-.5);
                            backLeft.setPower(-.5);
                            backRight.setPower(-.5);
                        } else {
                            StopMotors();
                            Finished = true;
                        }
                    }
                    break;
                case STATE_Turn:
                    if (goalReached == false)
                        GyroTurn(-1);
                    else {
                        StopMotors();
                        mCurrState = BlueClimbers.STATE_DriveForward4;
                        EncBackUp = (AvgEncoder()) - 2000;
                        EncForward = (AvgEncoder()) + 3500;
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DriveForward4:
                    if(GeneralTime.time() > .5) {
                        if (offenseconfigs == OffenseConfigs.OffenseLeaveCorner) {
                            if (AvgEncoder() <= EncForward) {
                                frontLeft.setPower(.5);
                                frontRight.setPower(.5);
                                backLeft.setPower(.5);
                                backRight.setPower(.5);
                            } else {
                                StopMotors();
                                Finished = true;
                            }
                        } else if (offenseconfigs == OffenseConfigs.OffenseLeaveFloorGoal) {
                            if (AvgEncoder() >= EncBackUp) {
                                frontLeft.setPower(-.5);
                                frontRight.setPower(-.5);
                                backLeft.setPower(-.5);
                                backRight.setPower(-.5);
                            } else {
                                StopMotors();
                                Finished = true;
                            }
                        } else {
                            StopMotors();
                            Finished = true;
                        }
                    }
                    break;
                case STATE_DriveBackward:
                    if(GeneralTime.time() < 3.5) {
                        VLF(-1, 90);
                    }
                    else {
                        StopMotors();
                        Finished = true;
                    }
                    break;
            }
            dashboard.displayPrintf(0, "State: %s", mCurrState); //Display our current state
            waitOneFullHardwareCycle();
        }
        StopMotors();
    }


    /**
     * Red climber program
     */
    public void CLIMBERRED(SteveAutonomous auton) throws InterruptedException {
        RedClimbers mCurrState = RedClimbers.STATE_Delay;
        /**
         * Run all motors with encoders
         * Set direction for each motor
         */
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        Log.w("Test", "Test");
        GeneralTime.reset();
        while(mGyro.isCalibrating()){
            waitForNextHardwareCycle();
            telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time())); //Display how much time is left of our delay
        }
        final double EncoderToInch = 71; //How many encoder counts are in 1 inch
        while (auton.opModeIsActive() && Finished == false) {
            LEDSubsystem();
            switch (mCurrState) {
                case STATE_Delay: //Our current robot state: waitfor the gyro to calibrate
                    if (GeneralTime.time() > delay) {
                        mCurrState = RedClimbers.STATE_DriveForward1; //Move to next robot state
                    } else
                        telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time())); //Display how much time is left of our delay
                    break;
                case STATE_DriveForward1: //Our current robot state: drive forward from start
                    telemetry.addData("2", mGyro.getHeading());
                    if (AvgEncoder() < 20 * EncoderToInch) {
                        VLF(1, 0);
                    } else {
                        StopMotors();
                        mCurrState = RedClimbers.STATE_GyroTurn1; //Move to next robot state
                    }
                    break;
                case STATE_GyroTurn1: //Our current robot state: turn parallel with the mountain
                    if (goalReached == false)
                        GyroTurn(-45);
                    else {
                        mCurrState = RedClimbers.STATE_DriveForward2; //Move to next robot state
                        goalReached = false;
                    }
                    break;
                case STATE_DriveForward2: //Our current robot state: drive forward close to the white line
                    telemetry.addData("2", mGyro.getHeading());
                    if (AvgEncoder() < 68 * EncoderToInch) {
                        VLF(1, -45);
                    } else {
                        mCurrState = RedClimbers.STATE_LookForRED; //Move to next robot state
                    }
                    break;
                case STATE_LookForRED: //Our current robot state: drive forward until we find the red line
                    if (optical2.getRawLightDetected() < 60) {
                        VLF(1, -45);
                    } else {
                        StopMotors();
                        mCurrState = RedClimbers.STATE_DrivePastLine; //Move to next robot state
                    }
                    break;
                case STATE_DrivePastLine: //Our current robot state: drive over the second red line
                    if (Done == false) {
                        WhiteLineSub = (AvgEncoder()) + 12 * EncoderToInch;
                        Done = true;
                    }
                    if (AvgEncoder() <= WhiteLineSub) {
                        VLF(1, -45);
                    } else {
                        WhiteLineSub = 0;
                        Done = false;
                        StopMotors();
                        mCurrState = RedClimbers.STATE_LookForWhite; //Move to next robot state
                    }
                    break;
                case STATE_LookForWhite: //Our current robot state: drive forward until we find the white line
                    if (optical2.getRawLightDetected() < 60) {
                        VLF(1, -45);
                    } else {
                        StopMotors();
                        if(ballconfig == BallConfigs.BallClear) {
                            mCurrState = RedClimbers.STATE_ClearBallArc; //Move to next robot state
                        }
                        else{
                            GeneralTime.reset();
                            mCurrState = RedClimbers.STATE_DriveBack1;
                        }
                    }
                    break;
                case STATE_ClearBallArc:
                    if (mGyro.getIntegratedZValue() > 3) {
                        telemetry.addData("Gero", mGyro.getIntegratedZValue());
                        frontRight.setPower(1);
                        backRight.setPower(1);
                    } else {
                        StopMotors();
                        mCurrState = RedClimbers.STATE_ClearBallArcBack;
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_ClearBallArcBack:
                    if (optical.getRawLightDetected() < 10) {
                        telemetry.addData("Gero", mGyro.getIntegratedZValue());
                        frontRight.setPower(-1);
                        backRight.setPower(-1);
                    } else {
                        StopMotors();
                        mCurrState = RedClimbers.STATE_ArcTurn;
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DriveBack1:
                    if(GeneralTime.time() < .4) {
                        VLF(-1, -45);
                    }
                    else {
                        StopMotors();
                        GeneralTime.reset();
                        mCurrState = RedClimbers.STATE_ArcTurn;
                    }
                    break;
                case STATE_ArcTurn: //Our current robot state: turn parallel with the mountain
                    if (-mGyro.getIntegratedZValue() > -90) {
                        telemetry.addData("Gero", mGyro.getIntegratedZValue());
                        frontRight.setPower(-1);
                        backRight.setPower(-1);
                    } else {
                        StopMotors();
                        mCurrState = RedClimbers.STATE_DriveStraight;
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DriveStraight:
                    if (GeneralTime.time() < .1) {
                    } else if (GeneralTime.time() < 4.75) {
                        if (optical3.getRawLightDetected() < 20) {
                            td4motor(frontLeft, frontRight, backLeft, backRight, 0, -.8, 1);
                        } else {
                            td4motor(frontLeft, frontRight, backLeft, backRight, -.8, 0, 1);
                        }
                    } else {
                        StopMotors();
                        mCurrState = RedClimbers.STATE_DrivePinion;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DrivePinion:
                    LEDON = false;
                    if(GeneralTime.time() < 3.6) {
                        if (!Touch1.isPressed() && !Touch2.isPressed()) {
                            Pinion.setPosition(.1);
                        } else {
                            TimeOut = GeneralTime.time();
                            Pinion.setPosition(.5);
                            int redValue = mColor.red();
                            int greenValue = mColor.green();
                            int blueValue = mColor.blue();
                            isRed = redValue > blueValue && redValue > greenValue;
                            isBlue = blueValue > redValue && blueValue > greenValue;
                            GeneralTime.reset();
                            mCurrState = RedClimbers.STATE_Hit_Button;
                        }
                    }
                    else{
                        GeneralTime.reset();
                        mCurrState = RedClimbers.STATE_RetractFailsafe;
                    }
                    break;
                case STATE_Hit_Button:
                    if (GeneralTime.time() < .5) {
                        if (isRed) {
                            Pinion.setPosition(.5);
                            telemetry.addData("ColoRead", "Red");
                            Tilt.setPosition(.4);
                            LED2.setPulseWidthPeriod(20000);
                            LED2.setPulseWidthOutputTime(10000);
                        } else if (isBlue) {
                            Pinion.setPosition(.5);
                            telemetry.addData("ColoRead", "Blue");
                            Tilt.setPosition(.9);
                            LED1.setPulseWidthPeriod(20000);
                            LED1.setPulseWidthOutputTime(10000);
                        }
                    } else {
                        BackUpAmount = (-215.9733777*TimeOut)+700.5557404;
                        EncBackUp = (AvgEncoder()) - BackUpAmount;
                        Tilt.setPosition(.68);
                        GeneralTime.reset();
                        mCurrState = RedClimbers.STATE_Retract;
                    }
                    break;
                case STATE_Retract:
                    if (GeneralTime.time() < TimeOut) {
                        Pinion.setPosition(0.9);
                        if (AvgEncoder() >= EncBackUp) {
                            frontLeft.setPower(-.2);
                            frontRight.setPower(-.2);
                            backLeft.setPower(-.2);
                            backRight.setPower(-.2);
                        }
                        else{
                            ClimberServoPos = ClimberServoPos - .005;
                            ClimberServoPos = Range.clip(ClimberServoPos, .1, .8);
                            ClimberServo.setPosition(Range.clip(ClimberServoPos, .1, .8));
                            if(ClimberServo.getPosition() == .1){
                                climbermadeit = true;
                            }
                            StopMotors();
                        }

                    } else {
                        Pinion.setPosition(.5);
                        GeneralTime.reset();
                        mCurrState = RedClimbers.STATE_BackUp;
                    }
                    break;
                case STATE_BackUp:
                    LEDON = true;
                    if (AvgEncoder() >= EncBackUp) {
                        frontLeft.setPower(-.2);
                        frontRight.setPower(-.2);
                        backLeft.setPower(-.2);
                        backRight.setPower(-.2);
                    } else {
                        if(climbermadeit == true){
                            ClimberWait = .5;
                        }
                        else{
                            ClimberWait = 2.5;
                        }
                        Done = false;
                        StopMotors();
                        mCurrState = RedClimbers.STATE_ClimberDrop; //Move to next robot state
                    }
                    break;
                case STATE_ClimberDrop:
                    if (GeneralTime.time() < ClimberWait) {
                        StopMotors();
                        ClimberServoPos = ClimberServoPos - .005;
                        ClimberServoPos = Range.clip(ClimberServoPos, .1, .8);
                        ClimberServo.setPosition(Range.clip(ClimberServoPos, .1, .8));
                    } else if (GeneralTime.time() < 3.5) {
                        ClimberServoPos = ClimberServoPos + .008;
                        ClimberServoPos = Range.clip(ClimberServoPos, .1, .8);
                        ClimberServo.setPosition(Range.clip(ClimberServoPos, .1, .8));

                    }  else {
                        GeneralTime.reset();
                        goalReached = false;
                        mCurrState = RedClimbers.STATE_WAIT;
                    }
                    break;
                case STATE_WAIT:
                    if(GeneralTime.time() < 1){
                        goalReached = false;
                        if(offenseconfigs == OffenseConfigs.OffenseLeaveDefense){
                            mCurrState = RedClimbers.STATE_Defence;
                        }else if(offenseconfigs == OffenseConfigs.OffenseLeaveCorner){
                            mCurrState = RedClimbers.STATE_Turn;
                        }else if(offenseconfigs == OffenseConfigs.OffenseLeaveFloorGoal){
                            mCurrState = RedClimbers.STATE_Turn;
                        }else if(offenseconfigs == OffenseConfigs.OffenseLeaveBackup){
                            GeneralTime.reset();
                            mCurrState = RedClimbers.STATE_DriveBackward;
                        }
                    }
                    else{
                        Finished = true;
                    }
                    break;
                case STATE_Defence:
                    if (mGyro.getIntegratedZValue() < 130) {
                        telemetry.addData("Gero", mGyro.getIntegratedZValue());
                        frontRight.setPower(-.65);
                        backRight.setPower(-.65);
                    } else {
                        StopMotors();
                        mCurrState = RedClimbers.STATE_BackUp2;
                        GeneralTime.reset();
                        EncBackUp = (AvgEncoder()) - 4500;
                    }
                    break;
                case STATE_BackUp2:
                    if(offenseconfigs == OffenseConfigs.OffenseLeaveDefense) {
                        if (AvgEncoder() >= EncBackUp) {
                            frontLeft.setPower(-.5);
                            frontRight.setPower(-.5);
                            backLeft.setPower(-.5);
                            backRight.setPower(-.5);
                        } else {
                            StopMotors();
                            Finished = true;
                        }
                    }
                    break;
                case STATE_Turn:
                    if (goalReached == false)
                        GyroTurn(1);
                    else {
                        StopMotors();
                        mCurrState = RedClimbers.STATE_DriveForward4;
                        EncBackUp = (AvgEncoder()) - 2000;
                        EncForward = (AvgEncoder()) + 3500;
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DriveForward4:
                    if(GeneralTime.time() > .5) {
                        if (offenseconfigs == OffenseConfigs.OffenseLeaveCorner) {
                            if (AvgEncoder() <= EncForward) {
                                frontLeft.setPower(.5);
                                frontRight.setPower(.5);
                                backLeft.setPower(.5);
                                backRight.setPower(.5);
                            } else {
                                StopMotors();
                                Finished = true;
                            }
                        } else if (offenseconfigs == OffenseConfigs.OffenseLeaveFloorGoal) {
                            if (AvgEncoder() >= EncBackUp) {
                                frontLeft.setPower(-.5);
                                frontRight.setPower(-.5);
                                backLeft.setPower(-.5);
                                backRight.setPower(-.5);
                            } else {
                                StopMotors();
                                Finished = true;
                            }
                        } else {
                            StopMotors();
                            Finished = true;
                        }
                    }
                    break;
                case STATE_DriveBackward:
                    if(GeneralTime.time() < 3.5) {
                        VLF(-1, -90);
                    }
                    else {
                        StopMotors();
                        Finished = true;
                    }
                    break;
            }
            dashboard.displayPrintf(0, "State: %s", mCurrState); //Display our current state
            dashboard.displayPrintf(4, "Program Run Time: %s", String.valueOf(ProgramRunTime.time()));
            dashboard.displayPrintf(6, "CurrentClimberPos: %s", String.valueOf(ClimberServo.getPosition()));
            waitOneFullHardwareCycle();
        }
        StopMotors();
    }

    public void Protect(SteveAutonomous auton) throws InterruptedException {
        ProtectStates mCurrState = ProtectStates.STATE_Delay;
        GeneralTime.reset();
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        final double EncoderToInch = 71; //How many encoder counts are in 1 inch
        while(mGyro.isCalibrating()){
            waitForNextHardwareCycle();
            telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time())); //Display how much time is left of our delay
        }
        while (auton.opModeIsActive() && Finished == false) {
            dashboard.displayPrintf(0, "State: %s", mCurrState); //Display our current state
            waitForNextHardwareCycle();
            LEDSubsystem();
            switch (mCurrState) {
                case STATE_Delay: //Our current robot state: waitfor the gyro to calibrate
                    if (GeneralTime.time() > delay) {
                        mCurrState = ProtectStates.STATE_DriveForward1; //Move to next robot state
                        PenguinLeft.setPosition(1);
                        PenguinRight.setPosition(0);
                    } else
                        telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time())); //Display how much time is left of our delay
                    break;
                case STATE_DriveForward1: //Our current robot state: drive forward from start
                    telemetry.addData("2", mGyro.getHeading());
                    if (AvgEncoder() < (4 * EncoderToInch)) {
                        VLF(1, 0);
                    } else {
                        StopMotors();
                        goalReached=false;
                        mCurrState = ProtectStates.STATE_GyroTurn1; //Move to next robot state
                    }
                    break;
                case STATE_GyroTurn1:
                    if (goalReached == false){
                        if(alliance == Alliance.BLUE_ALLIANCE)
                            GyroTurn(45);
                        else
                            GyroTurn(-45);
                    }
                    else {
                        mCurrState = ProtectStates.STATE_DriveForward2; //Move to next robot state
                        goalReached = false;
                    }
                    break;
                case STATE_DriveForward2:
                    if (Done == false) {
                        WhiteLineSub = (AvgEncoder()) + 150 * EncoderToInch;
                        Done = true;
                    }
                    if (AvgEncoder() <= WhiteLineSub) {
                        if(alliance == Alliance.BLUE_ALLIANCE)
                            VLF(1,45);
                        else
                            VLF(1, -45);
                    } else {
                        WhiteLineSub = 0;
                        Done = false;
                        StopMotors();
                        Finished=true; //Move to next robot state
                    }
                    break;
            }
        }
        StopMotors();
    }

    public void ForwardAndDefend(SteveAutonomous auton) throws InterruptedException {
        ForwardAndDefend mCurrState = ForwardAndDefend.STATE_Delay;
        GeneralTime.reset();
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        final double EncoderToInch = 71; //How many encoder counts are in 1 inch
        while(mGyro.isCalibrating()){
            waitForNextHardwareCycle();
            telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time())); //Display how much time is left of our delay
        }
        while (auton.opModeIsActive() && Finished == false) {
            dashboard.displayPrintf(0, "State: %s", mCurrState); //Display our current state
            waitForNextHardwareCycle();
            LEDSubsystem();
            switch (mCurrState) {
                case STATE_Delay: //Our current robot state: waitfor the gyro to calibrate
                    if (GeneralTime.time() > delay) {
                        mCurrState = ForwardAndDefend.STATE_DriveForward1; //Move to next robot state
                    } else
                        telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time())); //Display how much time is left of our delay
                    break;
                case STATE_DriveForward1: //Our current robot state: drive forward from start
                    telemetry.addData("2", mGyro.getHeading());
                    if (AvgEncoder() < ((DefenseTiles*35) * EncoderToInch)) {
                        VLF(1, 0);
                    } else {
                        StopMotors();
                        mCurrState = ForwardAndDefend.STATE_Wait; //Move to next robot state
                    }
                    break;
                case STATE_Wait:
                    if (ProgramRunTime.time() > 10.5) {
                        mCurrState = ForwardAndDefend.STATE_DriveForward2; //Move to next robot state
                        Done = false;
                    } else
                        telemetry.addData("1: ", "Delay Left " + String.valueOf(10.5 - ProgramRunTime.time())); //Display how much time is left of our delay
                    break;
                case STATE_DriveForward2:
                    if (Done == false) {
                        WhiteLineSub = ((4.75*35) * EncoderToInch);
                        Done = true;
                        Log.w("WhiteLineSub",String.valueOf(WhiteLineSub));
                        Log.w("Current",String.valueOf(((AvgEncoder()))));
                    }
                    if (AvgEncoder() < WhiteLineSub) {
                        telemetry.addData("WhiteLineSub", WhiteLineSub);
                        telemetry.addData("CurrPos", ((AvgEncoder())));
                        VLF(1, 0);

                    } else {
                        Done = false;
                        StopMotors();
                        Finished = true;
                    }
                    break;
            }
        }
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    public void SideDefend(SteveAutonomous auton) throws InterruptedException {
        SideDefend mCurrState = SideDefend.STATE_Delay;
        GeneralTime.reset();
        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.FORWARD);
        frontLeft.setDirection(DcMotor.Direction.FORWARD);
        final double EncoderToInch = 71; //How many encoder counts are in 1 inch
        while(mGyro.isCalibrating()){
            waitForNextHardwareCycle();
            telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time())); //Display how much time is left of our delay
        }
        while (auton.opModeIsActive() && Finished == false) {
            dashboard.displayPrintf(0, "State: %s", mCurrState); //Display our current state
            waitForNextHardwareCycle();
            LEDSubsystem();
            switch (mCurrState) {
                case STATE_Delay: //Our current robot state: waitfor the gyro to calibrate
                    if (GeneralTime.time() > delay) {
                        mCurrState = SideDefend.STATE_DriveForward1; //Move to next robot state
                    } else
                        telemetry.addData("1: ", "Delay Left " + String.valueOf(delay - GeneralTime.time())); //Display how much time is left of our delay
                    break;
                case STATE_DriveForward1: //Our current robot state: drive forward from start
                    telemetry.addData("2", mGyro.getHeading());
                    if (AvgEncoder() < ((DefenseTiles*35) * EncoderToInch)) {
                        VLF(1, 0);
                    } else {
                        StopMotors();
                        mCurrState = SideDefend.STATE_Wait; //Move to next robot state
                    }
                    break;
                case STATE_Wait:
                    if (ProgramRunTime.time() > 10.5) {
                        mCurrState = SideDefend.STATE_DriveForward2; //Move to next robot state
                        Done = false;
                    } else
                        telemetry.addData("1: ", "Delay Left " + String.valueOf(10.5 - ProgramRunTime.time())); //Display how much time is left of our delay
                    break;
                case STATE_DriveForward2:
                    if (Done == false) {
                        WhiteLineSub = ((4.75*35) * EncoderToInch);
                        Done = true;
                        Log.w("WhiteLineSub",String.valueOf(WhiteLineSub));
                        Log.w("Current",String.valueOf(((AvgEncoder()))));
                    }
                    if (AvgEncoder() < WhiteLineSub) {
                        telemetry.addData("WhiteLineSub",WhiteLineSub);
                        telemetry.addData("CurrPos",((AvgEncoder())));
                        VLF(1, 0);

                    } else {
                        Done = false;
                        StopMotors();
                        mCurrState = SideDefend.STATE_GyroTurn1;
                    }
                    break;
                case STATE_GyroTurn1:
                    if (!goalReached){
                        if(alliance == Alliance.BLUE_ALLIANCE)
                            GyroTurn(65);
                        else
                            GyroTurn(-65);
                    }
                    else {
                        mCurrState = SideDefend.STATE_DriveForward3; //Move to next robot state
                        goalReached = false;
                        GeneralTime.reset();
                    }
                    break;
                case STATE_DriveForward3:
                    if(GeneralTime.time() < 1.5){
                        frontRight.setPower(.5);
                        frontLeft.setPower(.5);
                        backLeft.setPower(.5);
                        backRight.setPower(.5);

                    }
                    else{
                        StopMotors();
                        Finished = true;
                    }
            }
        }
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    public static void td4motor(DcMotor leftFront, DcMotor rightFront, DcMotor leftBack, DcMotor rightBack, double leftValue, double rightValue, double Speed) {
        leftFront.setPower(-leftValue / Speed);
        rightFront.setPower(-rightValue / Speed);
        leftBack.setPower(-leftValue / Speed);
        rightBack.setPower(-rightValue / Speed);
    }
    /**
     * Stop
     */
    public void StopMotors() {
        frontRight.setPower(0);
        frontLeft.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    /**
     * Drives straight using gyro
     *
     * @param Distance Distance to go in inches
     */
    public void VLF(double Distance,int startangle){
        /**
         * Custom array for gyro sensor
         */
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, -179, -178, -177, -176, -175, -174, -173, -172, -171, -170, -169, -168, -167, -166, -165, -164, -163, -162, -161, -160, -159, -158, -157, -156, -155, -154, -153, -152, -151, -150, -149, -148, -147, -146, -145, -144, -143, -142, -141, -140, -139, -138, -137, -136, -135, -134, -133, -132, -131, -130, -129, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -76, -75, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63, -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, -43, -42, -41, -40, -39, -38, -37, -36, -35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1);
        int testHeading = list.get(mGyro.getHeading());
        double MotorPower;
        double data = testHeading - startangle;
        double PrepGain = 0.07;
        /**
         * Changes motor power to correct to the line if the robot deviates from the desired angle
         */
        if (Distance < 0) {
            MotorPower = -0.5; //WAS AT .5
            testHeading = list.get(mGyro.getHeading());
            double MotorCorrection = PrepGain * (data);
            data = testHeading - startangle;
            frontLeft.setPower(Range.clip(MotorPower + MotorCorrection, -1, 1));
            backRight.setPower(Range.clip(MotorPower - MotorCorrection, -1, 1));
            frontRight.setPower(Range.clip(MotorPower - MotorCorrection, -1, 1));
            backLeft.setPower(Range.clip(MotorPower + MotorCorrection, -1, 1));
        }
        else {
            MotorPower = .5;
            testHeading = list.get(mGyro.getHeading());
            double MotorCorrection = PrepGain * (data);
            data = testHeading - startangle;
            frontLeft.setPower(Range.clip(MotorPower + MotorCorrection, -1, 1));
            backRight.setPower(Range.clip(MotorPower - MotorCorrection, -1, 1));
            frontRight.setPower(Range.clip(MotorPower - MotorCorrection, -1, 1));
            backLeft.setPower(Range.clip(MotorPower + MotorCorrection, -1, 1));
        }
    }


    public void GyroTurn(double target_angle_degrees){
        double error_degrees = target_angle_degrees - -mGyro.getIntegratedZValue();
        double motor_output;
        /**
         * Changes the motor power as the current angle get closer to the desired angle
         */
        if(target_angle_degrees < 0){
            if(!goalReached) {
                error_degrees = target_angle_degrees - (mGyro.getIntegratedZValue() * -1);
                motor_output = (-.000000000409326 * (Math.abs(error_degrees)*Math.abs(error_degrees)*Math.abs(error_degrees) * Math.abs(error_degrees))) + 0.00000036418382 * (Math.abs(error_degrees)*Math.abs(error_degrees)*Math.abs(error_degrees)) - 0.0001129734 * (Math.abs(error_degrees)*Math.abs(error_degrees)) + 0.0153428243 * (Math.abs(error_degrees)) - 0.0014679156;
                if(motor_output < .1)
                    motor_output = .1;
                frontLeft.setPower(motor_output);
                frontRight.setPower(-motor_output);
                backLeft.setPower(motor_output);
                backRight.setPower(-motor_output);
                if(error_degrees > 1){
                    goalReached = true;
                }
            }
            else{
                StopMotors();
            }
        }
        else {
            if (!goalReached) {
                error_degrees = target_angle_degrees - -mGyro.getIntegratedZValue();
                motor_output = (-.000000000409326 * (error_degrees*error_degrees*error_degrees * error_degrees)) + 0.00000036418382 * (error_degrees*error_degrees*error_degrees) - 0.0001129734 * (error_degrees*error_degrees) + 0.0153428243 * (error_degrees) - 0.0014679156;
                if(motor_output < .1)
                    motor_output = .1;
                frontLeft.setPower(-motor_output);
                frontRight.setPower(motor_output);
                backLeft.setPower(-motor_output);
                backRight.setPower(motor_output);
                if(error_degrees < 1){
                    goalReached = true;
                }
            }
            else{
                StopMotors();
            }
        }

    }

    /**
     * Drives straight using gyro
     * Slows down so we can find the lines on the mat
     * @param Distance Distance to go in inches
     */
    public void SlowVLF(double Distance,int startangle){
        List<Integer> list = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123, 124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135, 136, 137, 138, 139, 140, 141, 142, 143, 144, 145, 146, 147, 148, 149, 150, 151, 152, 153, 154, 155, 156, 157, 158, 159, 160, 161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 173, 174, 175, 176, 177, 178, 179, 180, -179, -178, -177, -176, -175, -174, -173, -172, -171, -170, -169, -168, -167, -166, -165, -164, -163, -162, -161, -160, -159, -158, -157, -156, -155, -154, -153, -152, -151, -150, -149, -148, -147, -146, -145, -144, -143, -142, -141, -140, -139, -138, -137, -136, -135, -134, -133, -132, -131, -130, -129, -128, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -117, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -106, -105, -104, -103, -102, -101, -100, -99, -98, -97, -96, -95, -94, -93, -92, -91, -90, -89, -88, -87, -86, -85, -84, -83, -82, -81, -80, -79, -78, -77, -76, -75, -74, -73, -72, -71, -70, -69, -68, -67, -66, -65, -64, -63, -62, -61, -60, -59, -58, -57, -56, -55, -54, -53, -52, -51, -50, -49, -48, -47, -46, -45, -44, -43, -42, -41, -40, -39, -38, -37, -36, -35, -34, -33, -32, -31, -30, -29, -28, -27, -26, -25, -24, -23, -22, -21, -20, -19, -18, -17, -16, -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1);
        int testHeading = list.get(mGyro.getHeading());
        double MotorPower;
        double data = testHeading - startangle;
        double PrepGain = 0.07;
        if (Distance < 0) {
            MotorPower = -0.2;
            testHeading = list.get(mGyro.getHeading());
            double MotorCorrection = PrepGain * (data);
            data = testHeading - startangle;
            frontLeft.setPower(Range.clip(MotorPower + MotorCorrection, -1, 1));
            backRight.setPower(Range.clip(MotorPower - MotorCorrection, -1, 1));
            frontRight.setPower(Range.clip(MotorPower - MotorCorrection, -1, 1));
            backLeft.setPower(Range.clip(MotorPower + MotorCorrection, -1, 1));
        }
        else {
            MotorPower = .2;
            testHeading = list.get(mGyro.getHeading());
            double MotorCorrection = PrepGain * (data);
            data = testHeading - startangle;
            frontLeft.setPower(Range.clip(MotorPower + MotorCorrection, -1, 1));
            backRight.setPower(Range.clip(MotorPower - MotorCorrection, -1, 1));
            frontRight.setPower(Range.clip(MotorPower - MotorCorrection, -1, 1));
            backLeft.setPower(Range.clip(MotorPower + MotorCorrection, -1, 1));
        }
    }

    /**
     * Commands for our LED heartbeat
     * Flashes red and blue LEDs to tell us when we are connected to the robot controller
     */
    public void LEDSubsystem(){
        if(LEDON == true) {
            if (LEDTimer.time() < .25) {
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
            if (LEDTimer.time() > .25) {
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
            if (LEDTimer.time() > .5) {
                LEDTimer.reset();
            }
        }
        else{
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
    }
    private int AvgEncoder() {
        return (frontLeft.getCurrentPosition() + frontRight.getCurrentPosition() + backRight.getCurrentPosition() + backLeft.getCurrentPosition()) / 4;
    }
}