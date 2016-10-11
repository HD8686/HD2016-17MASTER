package HDCode.ResQ;

/**
 * Created by Akash on 8/29/2016.
 */

import org.firstinspires.ftc.robotcore.external.Telemetry;

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
public class HDDashboard_2015
{
    private static final String moduleName = "HalDashboard";
    private static final boolean debugEnabled = false;
    public static final int MAX_NUM_TEXTLINES = 16;

    private static final String displayKeyFormat = "%02d";
    private static Telemetry telemetry = null;
    private static HDDashboard_2015 instance = null;
    private static String[] display = new String[MAX_NUM_TEXTLINES];

    /**
     * Constructor: Creates an instance of the object.
     * There should only be one global instance of this object.
     * Typically, only the FtcOpMode object should construct an
     * instance of this object via getInstance(telemetry) and
     * nobody else.
     *
     * @param telemetry specifies the Telemetry object.
     */
    public HDDashboard_2015(Telemetry telemetry)
    {

        instance = this;
        this.telemetry = telemetry;
        telemetry.clearAll();
        clearDisplay();
    }   //HalDashboard

    public static HDDashboard_2015 getInstance()
    {
        return instance;
    }   //getInstance

    /**
     * This method displays a formatted message to the display on the Driver Station.
     *
     * @param lineNum specifies the line number on the display.
     * @param format specifies the format string.
     * @param args specifies variable number of substitution arguments.
     */
    public void displayPrintf(int lineNum, String format, Object... args)
    {
        if (lineNum >= 0 && lineNum < display.length)
        {
            display[lineNum] = String.format(format, args);
            telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
        }
        telemetry.update();
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
