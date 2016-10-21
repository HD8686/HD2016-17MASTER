package org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.hdlib.HDGeneralLib;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDLoopInterface;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;


/**
 * Created by Akash on 5/7/2016.
 */

/**
 * This class encompasses the Servo class and adds stepping controls to it.
 * When you initialize this class, you need to pass 3 main values. The servoName in the hardware map, the max servo speed from HDValues, and the servo position you want the servo to init to
 *
 */
public class HDServo implements HDLoopInterface.LoopTimer {
    private Servo mServo;
    private double steppingRate = 0.0;
    private double currPosition = 0.0;
    private double targetPosition = 0.0;
    private double prevTime = 0.0;
    private double maxSpeed = 0.0;
    private String servoHMName = "";
    private boolean running = false;

    public HDServo(String servoName, double servoStats, double servoInitPosition, boolean reverse){
        if(HDOpMode.getInstance().hardwareMap.servo.get(servoName) == null){
            throw new NullPointerException("Servo is null");
        }
        this.servoHMName = servoName;
        HDOpMode.getInstance().hdDiagnosticBackend.addServo(this);
        this.mServo = HDOpMode.getInstance().hardwareMap.servo.get(servoName);
        if(!reverse) {
            mServo.setDirection(Servo.Direction.FORWARD);
        }else {
            mServo.setDirection(Servo.Direction.REVERSE);
        }
        this.maxSpeed = ((1/servoStats) * 60.0)/360;
        this.mServo.setPosition(servoInitPosition);
        this.currPosition = servoInitPosition;
    }

    public double getCurrPosition(){
        return currPosition;
    }

    public String getName(){
        return servoHMName;
    }

    public void setPosition(double Position){
        HDLoopInterface.getInstance().deregister(this,HDLoopInterface.registrationTypes.ContinuousRun);
        running = false;
        this.mServo.setPosition(Position);
    }

    public void setPosition(double Position, double Speed){
        if(!running) {
            this.targetPosition = Range.clip(Position, 0, 1);
            this.prevTime = HDGeneralLib.getCurrentTimeSeconds();
            this.steppingRate = Math.abs(Range.clip(Speed, 0, 1)) * this.maxSpeed;
            this.currPosition = mServo.getPosition();
            HDLoopInterface.getInstance().register(this, HDLoopInterface.registrationTypes.ContinuousRun);
        }
    }

    public void stopServo(){
        targetPosition = currPosition;
        running = false;
    }

    public void scaleRange(double min, double max){
        mServo.scaleRange(min,max);
    }



    @Override
    public void continuousCallOp() {
        if(targetPosition != currPosition){
            double currTime = HDGeneralLib.getCurrentTimeSeconds();
            double posChange = steppingRate * (currTime - prevTime);

            if(currPosition < targetPosition){
                currPosition = currPosition + posChange;
                if(currPosition > targetPosition){
                    currPosition = targetPosition;
                }
            } else if(currPosition > targetPosition){
                currPosition = currPosition - posChange;
                if(currPosition < targetPosition){
                    currPosition = targetPosition;
                }
            }else{
                running = false;
                HDLoopInterface.getInstance().deregister(this,HDLoopInterface.registrationTypes.ContinuousRun);
            }
            prevTime = currTime;
            mServo.setPosition(currPosition);
        }
    }

    @Override
    public void InitializeLoopOp() {

    }

    @Override
    public void StartOp() {

    }
}

