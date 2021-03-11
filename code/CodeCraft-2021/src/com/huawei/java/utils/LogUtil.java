package com.huawei.java.utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LogUtil
{
    static class Time
    {
        private static final long start = System.currentTimeMillis();

        public Time()
        {
        }

        public long getTimeDelay()
        {
            long current = System.currentTimeMillis();
            return current - start;
        }

        public long getStart()
        {
            return start;
        }
    }

    public static void printLog(final String log)
    {

        String logTemp = "{0} date/time is: {1} \r\nuse time is {2} s {3} ms.";
        String logTempStart = "{0} date/time is: {1}.";

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(date);

        Time time = new Time();
        long delay = time.getTimeDelay();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(delay);

        if (calendar.get(Calendar.SECOND) == 0 && calendar.get(Calendar.MILLISECOND) == 0)
            System.err.println(MessageFormat.format(logTempStart, log, dateString));
        else
            System.err.println(MessageFormat.format(logTemp, log, dateString,
                    calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND)));
    }
}
