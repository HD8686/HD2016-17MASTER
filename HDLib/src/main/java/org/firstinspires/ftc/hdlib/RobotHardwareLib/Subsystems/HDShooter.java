package org.firstinspires.ftc.hdlib.RobotHardwareLib.Subsystems;

import android.util.Log;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.ServoEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDLoopInterface;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDVexMotor;

/**
 * Created by Akash on 11/10/2016.
 */
public class HDShooter implements HDLoopInterface.LoopTimer{

    final double leftCollectorDown = .94;
    final double rightCollectorDown = .93;
    final double leftCollectorUp = 0.42;
    final double rightCollectorUp = 0.43;
    HDServo leftCollectorServo;
    HDServo rightCollectorServo;
    DcMotor collectorMotor;
    DcMotor flywheel1;
    DcMotor flywheel2;
    HDVexMotor accelerator1;
    HDVexMotor accelerator2;
    ElapsedTime intervalRun;

    //Flywheel RPM Calc Variables

    // Encoder
    private double encoderCounts;         ///< current encoder count
    private double encoderCountsPrev;    ///< current encoder count

    // velocity measurement
    private double motorRPM;         ///< current velocity in rpm
    private double prevCalcTime;          ///< Time of last velocity calculation

    static double flywheelTicksperRev = 38; //Ticks per revolution of wheel shaft (ticks per motor revolution is 28, pulses per revolution is 7) Should turn shaft and test the actual as well.

    public HDShooter(HDServo leftCollectorServo, HDServo rightCollectorServo, DcMotor collectorMotor, DcMotor flywheel1, DcMotor flywheel2, HDVexMotor accelerator1, HDVexMotor accelerator2){
        this.leftCollectorServo = leftCollectorServo;
        this.rightCollectorServo = rightCollectorServo;
        this.collectorMotor = collectorMotor;
        this.flywheel1 = flywheel1;
        this.flywheel2 = flywheel2;
        this.accelerator1 = accelerator1;
        this.accelerator2 = accelerator2;

        this.leftCollectorServo.setPosition(leftCollectorUp);
        this.rightCollectorServo.setPosition(rightCollectorUp);

        resetEncoders();

        this.collectorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.collectorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.collectorMotor.setPower(0);


        this.flywheel1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.flywheel2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.flywheel1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.flywheel2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.flywheel1.setPower(0);
        this.flywheel2.setPower(0);

        this.accelerator1.setPower(0);
        this.accelerator2.setPower(0);

        intervalRun = new ElapsedTime();

        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.ContinuousRun);
        HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.Start);
    }

    public double getFlywheelEncoderCount(){
        return ((this.flywheel1.getCurrentPosition() + this.flywheel2.getCurrentPosition()) / 2);
    }

    public double getCollectorEncoderCount(){
        return this.collectorMotor.getCurrentPosition();
    }

    public void resetEncoders(){
        this.flywheel1.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.flywheel2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.flywheel1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.flywheel2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        this.collectorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.collectorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void lowerCollector(){
        leftCollectorServo.setPosition(leftCollectorDown);
        rightCollectorServo.setPosition(rightCollectorDown);
    }

    public void raiseCollector(){
        leftCollectorServo.setPosition(leftCollectorUp);
        rightCollectorServo.setPosition(rightCollectorUp);
    }

    public void setFlywheelPower(double power){
        power = Range.clip(power, -1, 1);
        flywheel1.setPower(power);
        flywheel2.setPower(power);
    }

    public void setAcceleratorPower(double power){
        power = Range.clip(power, -1, 1);
        accelerator1.setPower(-power);
        accelerator2.setPower(-power);
    }

    public void setCollectorPower(double power){
        power = Range.clip(power, -1, 1);
        collectorMotor.setPower(power);
    }

    public double getRPM(){
        return motorRPM;
    }

    @Override
    public void InitializeLoopOp() {

    }

    @Override
    public void continuousCallOp() {
        if(intervalRun.milliseconds() > 15){
            intervalRun.reset();
            calculateFlywheelVelocity();
        }
    }

    @Override
    public void StartOp() {
        intervalRun.reset();
    }

    private void calculateFlywheelVelocity(){
        double delta_ms;
        double delta_enc;

        encoderCounts = getFlywheelEncoderCount();

        delta_ms = System.currentTimeMillis() - prevCalcTime;
        prevCalcTime = System.currentTimeMillis();

        delta_enc = (encoderCounts - encoderCountsPrev);

        encoderCountsPrev = encoderCounts;

        motorRPM = (1000.0 / delta_ms) * delta_enc * 60 / flywheelTicksperRev;


    }
}
