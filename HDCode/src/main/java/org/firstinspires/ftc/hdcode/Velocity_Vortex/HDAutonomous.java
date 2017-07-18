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
        BEACON_CAP_BALL,
        BEACON_CORNER_VORTEX,
        BEACON,
        First_Beacon_Corner,
        Second_Beacon_Cap,
        First_Beacon_Far,
        Second_Beacon_Defense,
    }

    public enum Shoot
    {
        SHOOT,
        NOSHOOT,
    }

    public enum autoType
    {
        ST_LOUIS,
        FOC,
    }

    private HDAuto mHDAuto = null;
    private double delay = 0.0;
    private Strategy strategy = Strategy.BEACON_CAP_BALL;
    private Alliance alliance = Alliance.RED_ALLIANCE;
    private Shoot shoot = Shoot.SHOOT;
    private autoType aType = autoType.ST_LOUIS;

    @Override
    public void initialize() {
        HDTextMenu strategyMenu;


        HDTextMenu autoMenu = new HDTextMenu("Auto Menu", null);
        autoMenu.addChoice("St. Louis Autos", autoType.ST_LOUIS);
        autoMenu.addChoice("Festival Of Champ Autos", autoType.FOC);

        HDMenuManager.runMenus(autoMenu);

        aType = (autoType) autoMenu.getChoice();

        HDNumberMenu delayMenu = new HDNumberMenu("Delay", 0, 30, 1, 0, "Seconds", null);

        HDTextMenu shootMenu = new HDTextMenu("Shoot", delayMenu);
        shootMenu.addChoice("Do Shoot", Shoot.SHOOT);
        shootMenu.addChoice("Don't Shoot", Shoot.NOSHOOT);

        if(aType == autoType.ST_LOUIS) {
            strategyMenu = new HDTextMenu("Strategy", shootMenu);
            strategyMenu.addChoice("Beacons", Strategy.BEACON);
            strategyMenu.addChoice("Beacons and Cap Ball", Strategy.BEACON_CAP_BALL);
            strategyMenu.addChoice("Beacons and Corner Vortex", Strategy.BEACON_CORNER_VORTEX);
        }else {
            strategyMenu = new HDTextMenu("Strategy", delayMenu);
            strategyMenu.addChoice("First Beacon Corner Vortex", Strategy.First_Beacon_Corner);
            strategyMenu.addChoice("Second Beacon Cap Ball", Strategy.Second_Beacon_Cap);
            strategyMenu.addChoice("First Beacon Far Corner Vortex", Strategy.First_Beacon_Far);
            strategyMenu.addChoice("Second Beacon Defense", Strategy.Second_Beacon_Defense );
        }

        HDTextMenu allianceMenu = new HDTextMenu("Alliance", strategyMenu);
        allianceMenu.addChoice("Red Alliance", Alliance.RED_ALLIANCE);
        allianceMenu.addChoice("Blue Alliance", Alliance.BLUE_ALLIANCE);

        HDMenuManager.runMenus(allianceMenu);

        delay = delayMenu.getValue();
        alliance = (Alliance) allianceMenu.getChoice();
        strategy = (Strategy) strategyMenu.getChoice();
        if(aType == autoType.ST_LOUIS) {
            shoot = (Shoot) shootMenu.getChoice();
        }
        else {
            shoot = Shoot.SHOOT;
        }

        Alliance.storeAlliance(hardwareMap.appContext, alliance);

        HDMenuManager.displaySelections(allianceMenu, 1);

        switch (strategy){
            case BEACON:
                mHDAuto = new AutoBeacon(delay, shoot, alliance);
                break;
            case BEACON_CAP_BALL:
                mHDAuto = new AutoBeaconCapBall(delay, shoot, alliance);
                break;
            case BEACON_CORNER_VORTEX:
                mHDAuto = new AutoBeaconCornerVortex(delay, shoot, alliance);
                break;
            case First_Beacon_Corner:
                mHDAuto = new AutoFirstBeacon(delay, alliance);
                break;
            case Second_Beacon_Cap:
                mHDAuto = new AutoSecondBeacon(delay, alliance);
                break;
            case First_Beacon_Far:
                mHDAuto = new AutoFirstBeaconFar(delay, alliance);
                break;
            case Second_Beacon_Defense:
                mHDAuto = new AutoSecondBeaconDefense(delay, alliance);
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