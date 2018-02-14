package it.handroix.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by andrealucibello on 11/12/14.
 */
public class HdxDateUtility {

    /**
     * @return the actual date hour converted in milliseconds
     */
    public static long getNowInMilliseconds() {
        return (new Date()).getTime();
    }


    public static Date convertStringToDate(String dateInput, String dateFormat) {
        Date returnDate = null;

        DateFormat formatNowInString = new SimpleDateFormat(dateFormat);
        try {
            returnDate = formatNowInString.parse(dateInput);
        } catch (ParseException e) {
            e.printStackTrace();
            returnDate = null;
        }

        return returnDate;
    }


    public static String convertDateToString(Date date, String dateFormat) {
        String returnDate;

        DateFormat formatNowInString = new SimpleDateFormat(dateFormat);
        try {
            returnDate = formatNowInString.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            returnDate = null;
        }

        return returnDate;
    }


    public static int getMillisecondsBetweenDates(Date earlierDate, Date laterDate)
    {
        if( earlierDate == null || laterDate == null ) return 0;
        return (int)(laterDate.getTime() - earlierDate.getTime());
    }

}
