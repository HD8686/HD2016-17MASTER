package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.hdlib.General.Values;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDServo;
import org.firstinspires.ftc.hdlib.RobotHardwareLib.Servo.HDVexMotor;

public class Flywheelteleop extends LinearOpMode {
    public DcMotor collectorMotor;
    private DcMotor shooter;
    public Servo accelerator1;
    public Servo accelerator2;
    public Servo leftCollectorServo;
    public Servo rightCollectorServo;
    final double leftCollectorDown = .94;
    final double rightCollectorDown = .93;
    final double leftCollectorUp = 0.42;
    final double rightCollectorUp = 0.43;
    private boolean shooter_on = false;
    private double kP = 9000000.0;
    private double kI = 0.0;
    private double kD = 10000.0;

    private double integral = 0.0;
    private double derivative = 0.0;

    private double motorOut = 0.0;
    private final double fTarget = 1.2e-6;
    private double fVelocity = 0.0;
    private double fError = 0.0;
    private double fLastError = 0.0;

    private int fEncoder = 0;
    private int fLastEncoder = 0;

    private long fVelocityTime = 0;
    private long fLastVelocityTime = 0;

    private static final String TAG = "MyActivity";

    @Override
    public void runOpMode() throws InterruptedException {

        this.shooter = hardwareMap.dcMotor.get("Flywheel_1");
        collectorMotor = hardwareMap.dcMotor.get("Collector1");
        this.collectorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        this.collectorMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        this.collectorMotor.setPower(0);
        accelerator1 = hardwareMap.servo.get("Vex1");
        accelerator2 = hardwareMap.servo.get("Vex2");
        this.shooter.setDirection(DcMotor.Direction.FORWARD);
        this.shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftCollectorServo = hardwareMap.servo.get("leftCollector");
        rightCollectorServo = hardwareMap.servo.get("rightCollector");
        leftCollectorServo.setDirection(Servo.Direction.REVERSE);
        lowerCollector();
        waitForStart();

        while (opModeIsActive()){
            telemetry.addData("Text", "*** Robot Data***");
            telemetry.addData("FlyWheel_Error", fError);
            telemetry.addData("Shooter_Power", shooter.getPower());
            telemetry.update();
            if (gamepad2.x) { //WARM UP
                shooter_on = true;
                //shooter.setPower(0.8f);

                shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                calculatePID(0.8);
                sleep(100);
            }
            if(gamepad2.y){
                if(shooter_on){
                    shootBall(200);
                    sleep(250);
                    shootBall(200);
                    sleep(250);
                    shootBall(320);
                    sleep(250);
                }else{
                    collectorMotor.setPower(.3);
                    accelerator1.setPosition(0);
                    accelerator2.setPosition(0);
                }
            }else{
                collectorMotor.setPower(0);
                accelerator1.setPosition(.5);
                accelerator2.setPosition(0.5);
            }
            if (gamepad2.b) { //COOL DOWN
                //led.enable(false);
                shooter_on = false;
                shooter.setPower(0);
                shooter.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            }
        }

    }

    private void calculatePID(double shooter_power)
    {
        fVelocityTime = System.nanoTime();
        fEncoder = shooter.getCurrentPosition();
        fVelocity = (double)(fEncoder - fLastEncoder) / (fVelocityTime - fLastVelocityTime);
        fError = fTarget - fVelocity;

        integral += fError;
        if(fError == 0)
        {
            integral = 0;
        }

        if(Math.abs(fError) > 50)
        {
            integral = 0;
        }

        derivative = fError - fLastError;

        fLastError = fError;
        fLastEncoder = fEncoder;
        fLastVelocityTime = fVelocityTime;

        motorOut = (kP * fError) + (kI * integral) + (kD * derivative);

        motorOut = Range.clip(motorOut, 0.0, shooter_power);

        Log.wtf(TAG, String.valueOf(fError));

        setFPower(motorOut);
    }

    private void setFPower(double power)
    {
        shooter.setPower(power);
    }
    public void lowerCollector(){
        leftCollectorServo.setPosition(leftCollectorDown);
        rightCollectorServo.setPosition(rightCollectorDown);
    }

    public void raiseCollector(){
        leftCollectorServo.setPosition(leftCollectorUp);
        rightCollectorServo.setPosition(rightCollectorUp);
    }

    public void shootBall(int shoot_enc){
        /*collectorMotor.setPower(-.6);
        accelerator1.setPosition(0);
        accelerator2.setPosition(0);
        while(collectorMotor.getCurrentPosition() > -300){
            sleep(1);
        }
        collectorMotor.setPower(collectorPower);
        accelerator1.setPosition(1);
        accelerator2.setPosition(1);
        while(collectorMotor.getCurrentPosition() < 100){
            sleep(1);
        }
        collectorMotor.setPower(.6);
        accelerator1.setPosition(0);
        accelerator2.setPosition(0);*/
        shoot(50, -1, false);
        shoot(shoot_enc, 1, true);
    }


    private void shoot(int distance, double speed, boolean forward){
        this.collectorMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if (!opModeIsActive()){
            return;
        }
        while (opModeIsActive()) {
            this.collectorMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            if (Math.abs(collectorMotor.getCurrentPosition()) > distance) {
                this.collectorMotor.setPower(0);
                accelerator1.setPosition(0.5);
                accelerator2.setPosition(0.5);
                break;
            } else {
                this.collectorMotor.setPower(speed);
                if(forward){
                    accelerator1.setPosition(1);
                    accelerator2.setPosition(1);
                }else{
                    accelerator1.setPosition(0);
                    accelerator2.setPosition(0);
                }
            }
        }
    }


}