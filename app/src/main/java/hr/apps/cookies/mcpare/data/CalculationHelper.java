package hr.apps.cookies.mcpare.data;

import java.util.Calendar;

/**
 * Created by lmita_000 on 8.8.2015..
 */
public class CalculationHelper {

    //da li je dani mjesec prije trenutnog mjeseca
    public static boolean isMonthBefore(long datum){

        Calendar currentDate = Calendar.getInstance();
        Calendar givenDate = Calendar.getInstance();
        givenDate.setTimeInMillis(datum);

        if ((currentDate.get(Calendar.MONTH) == Calendar.DECEMBER)
                && (givenDate.get(Calendar.MONTH) == Calendar.JANUARY)
                && ((currentDate.get(Calendar.YEAR) + 1) ==  givenDate.get(Calendar.YEAR)))
            return false;
        else
            return currentDate.get(Calendar.MONTH) > givenDate.get(Calendar.MONTH);
    }

    //da li je dani mjesec 2 ili više mjeseca udaljen od trenutnog
    public static boolean isMonthTooFarForward(long datum){

        Calendar currentDate = Calendar.getInstance();
        Calendar givenDate = Calendar.getInstance();
        givenDate.setTimeInMillis(datum);

        return (currentDate.get(Calendar.MONTH) +1) < givenDate.get(Calendar.MONTH);
    }
}
