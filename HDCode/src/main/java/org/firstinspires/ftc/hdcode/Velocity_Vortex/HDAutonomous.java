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
        BEACON_CORNER_VORTEX,
        BEACON,
    }

    public enum StartPosition
    {
        TILE_1,
        TILE_2,
    }

    private HDAuto mHDAuto = null;
    private double delay = 0.0;
    private Strategy strategy = Strategy.BEACON_CAP_BALL;
    private Alliance alliance = Alliance.RED_ALLIANCE;
    private StartPosition startPosition = StartPosition.TILE_1;

    @Override
    public void initialize() {

        HDNumberMenu delayMenu = new HDNumberMenu("Delay", 0, 30, 1, 0, "Seconds", null);

        HDTextMenu startPositionMenu = new HDTextMenu("Start Position", delayMenu);
        startPositionMenu.addChoice("Tile 1", StartPosition.TILE_1);
        startPositionMenu.addChoice("Tile 2", StartPosition.TILE_2);


        HDTextMenu strategyMenu = new HDTextMenu("Strategy", startPositionMenu);
        strategyMenu.addChoice("Do Nothing", Strategy.DO_NOTHING);
        strategyMenu.addChoice("Beacons", Strategy.BEACON);
        strategyMenu.addChoice("Beacons and Cap Ball", Strategy.BEACON_CAP_BALL);
        strategyMenu.addChoice("Beacons and Corner Vortex", Strategy.BEACON_CORNER_VORTEX);

        HDTextMenu allianceMenu = new HDTextMenu("Alliance", strategyMenu);
        allianceMenu.addChoice("Red Alliance", Alliance.RED_ALLIANCE);
        allianceMenu.addChoice("Blue Alliance", Alliance.BLUE_ALLIANCE);

        HDMenuManager.runMenus(allianceMenu);

        delay = delayMenu.getValue();
        alliance = (Alliance) allianceMenu.getChoice();
        strategy = (Strategy) strategyMenu.getChoice();
        startPosition = (StartPosition) startPositionMenu.getChoice();

        Alliance.storeAlliance(hardwareMap.appContext, alliance);

        HDMenuManager.displaySelections(allianceMenu, 1);

        switch (strategy){
            case DO_NOTHING:
                mHDAuto = new AutoDoNothing(alliance);
                break;
            case BEACON:
                mHDAuto = new AutoBeacon(delay, alliance, startPosition);
                break;
            case BEACON_CAP_BALL:
                mHDAuto = new AutoBeaconCapBall(delay, alliance, startPosition);
                break;
            case BEACON_CORNER_VORTEX:
                mHDAuto = new AutoBeaconCornerVortex(delay, alliance, startPosition);
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
