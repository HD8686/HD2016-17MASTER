package org.firstinspires.ftc.hdcode.Velocity_Vortex;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.hdlib.General.Alliance;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDAuto;
import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDMenu.HDMenuManager;
import org.firstinspires.ftc.hdlib.Telemetry.HDMenu.HDNumberMenu;
import org.firstinspires.ftc.hdlib.Telemetry.HDMenu.HDTextMenu;

/**
 * Created by Akash on 10/20/2016.
 */

@Autonomous(name = "HDAutonomous", group = "Autonomous")
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

        HDNumberMenu delayMenu = new HDNumberMenu("Delay", 0, 30, 1, 0, "Seconds", null, gamepad1);

        HDTextMenu strategyMenu = new HDTextMenu("Strategy", delayMenu, gamepad1);
        strategyMenu.addChoice("Do Nothing", Strategy.DO_NOTHING);
        strategyMenu.addChoice("Beacon and Cap Ball", Strategy.BEACON_CAP_BALL);

        HDTextMenu allianceMenu = new HDTextMenu("Alliance", strategyMenu, gamepad1);
        allianceMenu.addChoice("Red Alliance", Alliance.RED_ALLIANCE);
        allianceMenu.addChoice("Blue Alliance", Alliance.BLUE_ALLIANCE);

        HDMenuManager.runMenus(allianceMenu);

        delay = delayMenu.getValue();
        alliance = (Alliance) allianceMenu.getChoice();

        HDMenuManager.displaySelections(allianceMenu, 1);

        switch (strategy){
            case DO_NOTHING:
                mHDAuto = new AutoDoNothing(alliance);
                break;
            case BEACON_CAP_BALL:
                mHDAuto = new AutoBeaconCapBall(delay, alliance, startPosition);
                break;
        }
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
