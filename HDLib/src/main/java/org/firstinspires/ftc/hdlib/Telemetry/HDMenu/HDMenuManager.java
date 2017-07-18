package org.firstinspires.ftc.hdlib.Telemetry.HDMenu;

import org.firstinspires.ftc.hdlib.OpModeManagement.HDOpMode;
import org.firstinspires.ftc.hdlib.Telemetry.HDDashboard;

/**
 * Created by Akash on 10/23/2016.
 */
public abstract class HDMenuManager {

    public abstract void runMenu();

    public abstract HDMenuManager getNextMenu();

    public abstract String getSelectionDisplay();

    public static void runMenus(HDMenuManager firstMenu){
        HDMenuManager currMenu = firstMenu;
        while(currMenu != null && !HDOpMode.getInstance().isStopRequested()){
            currMenu.runMenu();
            currMenu = currMenu.getNextMenu();
            HDOpMode.getInstance().idle();
            HDOpMode.getInstance().mDisplay.clearDisplay();
        }
    }

    public static void displaySelections(HDMenuManager firstMenu, int lineToStart){
        HDOpMode.getInstance().mDisplay.displayPrintf(lineToStart, HDDashboard.textPosition.Centered, "Selected Options: ");
        lineToStart++;
        HDMenuManager currMenu = firstMenu;
        while(currMenu != null  && !HDOpMode.getInstance().isStopRequested()){
            HDOpMode.getInstance().mDisplay.displayPrintf(lineToStart, HDDashboard.textPosition.Centered, currMenu.getSelectionDisplay());
            currMenu = currMenu.getNextMenu();
            HDOpMode.getInstance().idle();
            HDDashboard.getInstance().refreshDisplay();
            lineToStart++;
        }
    }

}
