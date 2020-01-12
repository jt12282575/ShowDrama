package dada.com.showdrama.Util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UtilFunction {
    public static String fromISO8601UTC(String dateStr) {

        String targetFormat = "yyyy-MM-dd HH:mm:ss";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        format = new SimpleDateFormat(targetFormat);
        if(date!= null){
            String dateString = format.format(date);
            return dateString;
        }else{
            return "";
        }

    }
}
