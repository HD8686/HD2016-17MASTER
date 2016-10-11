
package hdlib.Telemetry;


import android.app.Activity;
import android.text.TextPaint;
import android.widget.TextView;

import org.firstinspires.ftc.hdlib.R;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import hdlib.OpModeManagement.HDOpMode;


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
    TextPaint mPaint;
    public static final int SCREEN_WIDTH = 667;
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
        mPaint = ((TextView) ((Activity) HDOpMode.getInstance().hardwareMap.appContext).findViewById(R.id.textOpMode)).getPaint();
        instance = this;
        this.telemetry = telemetry;
        telemetry.clearAll();
        clearDisplay();
    }


    public static HDDashboard getInstance()
    {
        return instance;
    }

    /**
     * This method displays telemetry on the driver station
     * @param lineNum Sets the line number
     * @param tP Sets the text position
     * @param format String
     * @param args String formatting settings
     */
    public void displayPrintf(int lineNum, textPosition tP, String format, Object... args)
    {
        display[lineNum] = String.format(format, args);
        if (lineNum >= 0 && lineNum < display.length)
        {
            if(tP == textPosition.Centered){
                display[lineNum] = centerText(mPaint, SCREEN_WIDTH, display[lineNum]);
                telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
            } else if(tP == textPosition.Right){
                display[lineNum] = justifyTextRight(mPaint, SCREEN_WIDTH, display[lineNum]);
                telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
            } else if(tP == textPosition.Left){
                telemetry.addData(String.format(displayKeyFormat, lineNum), display[lineNum]);
            }

        }
    }




    /**
     * This method clears all the display lines.
     */
    public void clearDisplay()
    {
        for (int i = 0; i < display.length; i++)
        {
            display[i] = "";
        }
        refreshDisplay();
    }

    /**
     * This method refresh the display lines to the Driver Station.
     */
    public void refreshDisplay()
    {
        telemetry.clearAll();
        for (int i = 0; i < display.length; i++)
        {
            telemetry.addData(String.format(displayKeyFormat, i), display[i]);
        }
        telemetry.update();
    }

    public String centerText(TextPaint paint, float width, String text)
    {
        float textWidth = paint.measureText(text);
        int paddingSpaces = Math.round((width - textWidth)/2/paint.measureText(" "));
        String format = "%" + (paddingSpaces + text.length()) + "s";
        return String.format(format, text);
    }

    public String justifyTextRight(TextPaint paint, float width, String text)
    {
        float textWidth = paint.measureText(text);
        int paddingSpaces = Math.round((width - textWidth)/paint.measureText(" "));
        String format = "%" + (paddingSpaces + text.length()) + "s";
        return String.format(format, text);
    }

}