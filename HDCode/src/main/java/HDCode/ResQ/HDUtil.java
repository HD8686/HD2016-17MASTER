package HDCode.ResQ;

import java.math.BigDecimal;

/**
 * Created by Akash on 8/29/2016.
 */
public class HDUtil {

    public static void HDSleep(long sleepT){ //Sleep function which allows us to wait
        long TimeUntilDone = System.currentTimeMillis() + sleepT;   //Add sleeptime to Systemtime to find when to stop waiting
        while(sleepT > 0){  //While sleep time is larger than 0, (Still needs to sleep)
            try {
                Thread.sleep(sleepT);   //Try to sleep for sleep time.
            }
            catch (InterruptedException e){
            }
            sleepT = TimeUntilDone - System.currentTimeMillis();    //Calculates sleep time, SleepTime-Current time
        }
    }
    public static double round(double value, int Digits){  //Function that rounds decimal for easier to read telemetry.
        BigDecimal BD = new BigDecimal(value);  //Create big decimal class
        BD = BD.setScale(Digits, BigDecimal.ROUND_HALF_UP); //Set class to round half up
        return BD.doubleValue();    //ReturnRoundedValue
    }
}