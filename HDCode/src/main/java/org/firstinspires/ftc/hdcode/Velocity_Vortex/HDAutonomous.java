package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.hdlib.Alliance;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDAuto;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;
import org.firstinspires.ftc.hdlib.Telemetry.HDDiagnosticDisplay;

/**
 * Created by Akash on 10/20/2016.
 */

@Autonomous(name = "Autonomous", group = "Testing")
public class HDAutonomous extends HDOpMode{

    private enum Strategy
    {
        DO_NOTHING,
        BEACON_CAP_BALL,
    }

    public enum StartPosition
    {
        CORNER_VORTEX,
    }

    private HDAuto mHDAuto = null;
    private double delay = 0.0;
    private Strategy strategy = Strategy.BEACON_CAP_BALL;
    private Alliance alliance = Alliance.RED_ALLIANCE;
    private StartPosition startPosition = StartPosition.CORNER_VORTEX;

    @Override
    public void initialize() {

        //Start with menus (yet to implement)

        switch (strategy){
            case DO_NOTHING:
                mHDAuto = new AutoDoNothing();
                break;
            case BEACON_CAP_BALL:
                mHDAuto = new AutoBeaconCapBall(delay, alliance, startPosition);
                break;
        }

        mDisplay.displayPrintf(1, HDDashboard.textPosition.Centered, "Strategy: %s(alliance=%s, delay=%.0f)", strategy.toString(), alliance.toString(), delay);

    }

    @Override
    public void initializeLoop() {

    }

    @Override
    public void Start() {
        mHDAuto.start();
    }

    @Override
    public void continuousRun(double elapsedTime) {
        mHDAuto.runLoop(elapsedTime);
    }

}
