package Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String getCurrentTime(){
        /**
        * @method: getCurrentTime
        * @Params: []
        * @Return: java.lang.String
        * @Description: get current time
        */

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
        Date curDate =  new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    
}
