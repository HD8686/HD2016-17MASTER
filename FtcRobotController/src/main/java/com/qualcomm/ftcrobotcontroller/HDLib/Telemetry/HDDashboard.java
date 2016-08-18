
package com.qualcomm.ftcrobotcontroller.HDLib.Telemetry;

import com.qualcomm.robotcore.robocol.Telemetry;

/**
 * This class is a wrapper for the Telemetry class. In addition to providing
 * a way to send named data to the Driver Station to be displayed, it also
 * simulates an LCD display similar to the NXT Mindstorms. The Mindstorms
 * has only 8 lines but this dashboard can support as many lines as the
 * Driver Station can support. By default, we set the number of lines to 16.
 * By changing a constant here, you can have as many lines as you want. This
 * dashboard display is very useful for displaying debug information. In
 * particular, the TrcMenu class uses the dashboard to display a choice menu
 * and interact with the user for choosing autonomous strategies and options.
 */
public class HDDashboard
{
    public static final int MAX_NUM_TEXTLINES = 30;
    private static final int MAX_CHAR = 49;
    private static final String displayKeyFormat = "%02d";
    private static Telemetry telemetry = null;
    private static HDDashboard instance = null;
    private static String[] display = new String[MAX_NUM_TEXTLINES];
    public enum textPosition{
        Left,
        Right,
        Centered
    }


    /**
     * Constructor: Creates an instance of the object.
     * There should only be one global instance of this object.
     * Typically, only the FtcOpMode object should construct an
     * instance of this object via getInstance(telemetry) and
     * nobody else.
     *
     * @param telemetry specifies the Telemetry object.
     */
    public HDDashboard(Telemetry telemetry)
    {

        instance = this;
        this.telemetry = telemetry;
        telemetry.clearData();
        clearDisplay();
    }   //HalDashboard

    /**
     * This static method allows the caller to get an instance of
     * the dashboard so that it can display information on its
     * display. If no instance found, it will create one. Typically,
     * this is called by FtcOpMode to create one global instance
     * of HalDashboard.
     *
     * @param telemetry specifies the Telemetry object.
     * @return global instance of the dashboard object.
     */
    /*
    public static HalDashboard getInstance(Telemetry telemetry)
    {
        if (instance == null)
        {
            instance = new HalDashboard(telemetry);
        }

        return instance;
    }   //getInstance
    */

    /**
     * This static method allows any class to get an instance of
     * the dashboard so that it can display information on its
     * display.
     *
     * @return global instance of the dashboard object.
     */
    public static HDDashboard getInstance()
    {
        return instance;
    }   //getInstance

    /**
     * This method displays a formatted message to the display on the Driver Station.
     *  @param lineNum specifies the line number on the display.
     * @param format specifies the format string.
     * @param args specifies variable number of substitution arguments.
     */
    public void displayPrintf(int lineNum, textPosition tP, String format, Object... args)
    {
        display[lineNum] = String.format(format, args);
        if (lineNum >= 0 && lineNum < display.length)
        {
            if(tP == textPosition.Centered){
                int spacing = Math.round(MAX_CHAR - display[lineNum].length() + (display[lineNum].length() - display[lineNum].replace(" ","").length())) - 3;
                StringBuilder builder = new StringBuilder(display[lineNum]);
                for(int i = 0; i < spacing; i++){
                    builder.insert(0," ");
                }
                display[lineNum] = builder.toString();
                telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
            } else if(tP == textPosition.Right){
                int spacing = Math.round((MAX_CHAR - display[lineNum].length() + (display[lineNum].length() - display[lineNum].replace(" ","").length()))*2)-5;
                StringBuilder builder = new StringBuilder(display[lineNum]);
                for(int i = 0; i < spacing; i++){
                    builder.insert(0," ");
                }
                display[lineNum] = builder.toString();
                telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
            } else if(tP == textPosition.Left){
                display[lineNum] = String.format(format, args);
                telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
            }

        }
    }   //displayPrintf




    /**
     * This method clears all the display lines.
     */
    public void clearDisplay()
    {
        final String funcName = "clearDisplay";


        for (int i = 0; i < display.length; i++)
        {
            display[i] = "";
        }
        refreshDisplay();
    }   //clearDisplay

    /**
     * This method refresh the display lines to the Driver Station.
     */
    public void refreshDisplay()
    {
        final String funcName = "refreshDisplay";


        for (int i = 0; i < display.length; i++)
        {
            telemetry.addData(String.format(displayKeyFormat, i), display[i]);
        }
    }   //refreshDisplay



}   //class HalDashboard