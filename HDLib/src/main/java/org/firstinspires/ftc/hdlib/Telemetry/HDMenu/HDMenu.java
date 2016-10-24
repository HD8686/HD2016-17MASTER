package org.firstinspires.ftc.hdlib.Telemetry.HDMenu;

/**
 * Created by Akash on 10/23/2016.
 */
public abstract class HDMenu {

    public abstract void runMenu();

    public abstract HDMenu getNextMenu();

    public static void runMenus(HDMenu firstMenu){
        HDMenu currMenu = firstMenu;
        while(currMenu != null){
            currMenu.runMenu();
            currMenu = currMenu.getNextMenu();
        }
    }

}
