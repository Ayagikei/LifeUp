package net.sarasarasa.lifeup.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    //判断选择的日期是否是本周
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    //判断选择的日期是否是今天
    public static boolean isToday(long time) {
        return isThisTime(time, "yyyy-MM-dd");
    }

    //判断选择的日期是否是明天
    public static boolean isTomorrow(long time) {
        return isThisTime(time - 86400000, "yyyy-MM-dd");
    }

    //判断选择的日期是否是本月
    public static boolean isThisMonth(long time) {
        return isThisTime(time, "yyyy-MM");
    }

    private static boolean isThisTime(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        return param.equals(now);
    }

    /**
     * 获取当前日期是星期几
     *
     * @param time
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(long time) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;

        return weekDays[w];
    }

    public static int getIntWeekOfDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);

        int dayForWeek = 0;
        if (cal.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else dayForWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;

        return dayForWeek;
    }



    public static int getDiscrepantDays(Date dateStart, Date dateEnd) {
        return (int) ((dateEnd.getTime() - dateStart.getTime()) / 1000 / 60 / 60 / 24);
    }

    public static ArrayList<String> listStringDatePastDays(Integer days) {
        ArrayList<String> stringDateList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < days; i++) {
            stringDateList.add(simpleDateFormat.format(cal.getTime()));
            cal.add(Calendar.DATE, -1);
        }
        Collections.reverse(stringDateList);
        return stringDateList;
    }

}