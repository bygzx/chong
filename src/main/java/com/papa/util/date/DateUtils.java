package com.papa.util.date;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by eric on 2018/1/4.
 */
@Slf4j
public class DateUtils {
//    public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
//    public static final String YYYYMMDD = "yyyy-MM-dd";
//    public static final String YYYYMMDD_CH = "yyyy年MM月dd";
//    public static final String MMDD = "MM-dd";
//    public static final String YYYYMMDD_2 = "yyyyMMdd";

	
	public static final long ONE_DAY_IN_MILLS = 24 * 60 * 60 * 1000;

    /**
     * 一小时的秒数
     */
    private static final int HOUR_SECOND = 60 * 60;

    /**
     * 一分钟的秒数
     */
    private static final int MINUTE_SECOND = 60;

    public static Integer parseDateToInt(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        String dateStr = df.format(date);
        return Integer.parseInt(dateStr);
    }

    public static Date parseDate(String dateStr, String pattern) throws ParseException {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.parse(dateStr);
    }

    public static String addDays(int days, String dateStr, String pattern) throws ParseException {
        DateFormat df = new SimpleDateFormat(pattern);
        Date date = df.parse(dateStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        date = calendar.getTime();
        return df.format(date);
    }

    public static Date addDays(int days, Date date)  {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        date = calendar.getTime();
        return date;
    }

    public static Date addMin(int min, Date date)  {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        date = calendar.getTime();
        return date;
    }

    public static long addMin(int min, long dateLong)  {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(dateLong);
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        date = calendar.getTime();
        return date.getTime();
    }

    /**
     * 跳过周末获取
     * @param min
     * @param dateLong
     * @return
     */
    public static long addMinSkipWeekend(int min, long dateLong)  {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(dateLong);
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, min);
        date = calendar.getTime();
        return date.getTime();
    }

    public static Date getDayLastTime( Date date)  {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 999);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR,23);
        date = calendar.getTime();
        return date;
    }
    
    public static String addDays(int days, Date date, String pattern) throws ParseException {
        DateFormat df = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        date = calendar.getTime();
        return df.format(date);
    }

    public static long millisDiff(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        if (diff < 0L) {
            diff = 0L;
        }
        return diff;
    }

    public static int minsDiff(Date startDate, Date endDate) {
        long diff = endDate.getTime() - startDate.getTime();
        if (diff < 0L) {
            diff = 0L;
        }

        int minsDiff;
        int secondsDiff = new Long(diff / 1000L).intValue();
        if (secondsDiff >= 55 && secondsDiff < 60) {
            minsDiff = 1;
        } else {
            minsDiff = new Long(diff / 1000L / 60L).intValue();
        }

        return minsDiff;
    }

    public static int getDatePoor(long endDate, long nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate - nowDate;
        /*// 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟*/
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        log.info("两个时间差了 {} 分钟",min);
        return (int)min ;
    }

    public static String format(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(date);
    }

    /**
     * @param date
     * @return 当天的23:59:59.999
     */
    public static Date getMidNight(Date date) {
    	Calendar ret = Calendar.getInstance();
    	ret.setTime(date);
    	ret.set(Calendar.MILLISECOND, 999);
    	ret.set(Calendar.SECOND, 59);
    	ret.set(Calendar.MINUTE, 59);
    	ret.set(Calendar.HOUR_OF_DAY, 23);
    	
    	return ret.getTime();
    }

    /**
     * @param date
     * @return 当天的00:00:00.000
     */
    public static Date getMidNightZero(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTime();
    }

    /**
     * 切换指定日期的时分秒
     * @param date
     * @param hour
     * @param min
     * @param second
     * @return
     */
    public static Date changeDate(Date date,int hour,int min,int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, second);

        return calendar.getTime();
    }

    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     * @param timeStart
     * @param timeEnd
     * @return
     */
    public static List<String> collectLocalDates(String timeStart, String timeEnd){
        return collectLocalDates(LocalDate.parse(timeStart), LocalDate.parse(timeEnd));
    }

    public static List<String> collectBeforeOneDayLocalDates(String timeStart, String timeEnd){
        return collectLocalDates(LocalDate.parse(timeStart).minusDays(1), LocalDate.parse(timeEnd).minusDays(1));
    }
    /**
     * 收集起始时间到结束时间之间所有的时间并以字符串集合方式返回
     * @param start
     * @param end
     * @return
     */
    public static List<String> collectLocalDates(LocalDate start, LocalDate end){
        // 用起始时间作为流的源头，按照每次加一天的方式创建一个无限流
        return Stream.iterate(start, localDate -> localDate.plusDays(1))
                // 截断无限流，长度为起始时间和结束时间的差+1个
                .limit(ChronoUnit.DAYS.between(start, end) + 1)
                // 由于最后要的是字符串，所以map转换一下
                .map(LocalDate::toString)
                // 把流收集为List
                .collect(Collectors.toList());
    }

    /**
     *
     * @param dayCount 推迟的周数，1本周，-1向前推迟一周，2下周，依次类推
     * @param calendar 想周几，这里就传几Calendar.MONDAY（TUESDAY...）
     * @return yyyy-MM-dd
     */
    public static String getTheDay(int dayCount,int calendar){
        Calendar cal = Calendar.getInstance();
        int n = dayCount;
        String theDay;
        cal.add(Calendar.DATE, n*7);
        cal.set(Calendar.DAY_OF_WEEK,calendar);
        theDay = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        return theDay;
    }

    /**
     * 根据日期获取当天是这周的第几天
     * @param datetime
     * @return 0-周日 1-周一 ………………6-周六
     */
    public static int dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
        System.out.println(w);
        if (w < 0) {
            w = 0;
        }
        return w;
    }



    /**
     * 根据开始结束日期返回以周为集合的日期组合
     * @param a yyyy-MM-dd
     * @param b yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public static Map<Integer, List<String>> getResultsMap(String a, String b) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date ad = sdf.parse(a);
        Date bd = sdf.parse(b);
        Calendar cl1 = Calendar.getInstance();
        cl1.setTime(ad);
        Calendar cl2 = Calendar.getInstance();
        cl2.setTime(bd);
        Map<Integer, List<String>> result = new HashMap<Integer, List<String>>();
        int num = 0;
        while (true) {
            if (cl2.compareTo(cl1) <= 0) {
                System.out.println(sdf.format(cl2.getTime()));
                break;
            }

            int count = 7;

            if (num == 0) {
                System.out.println(sdf.format(cl1.getTime()));
                int day = cl1.get(Calendar.DAY_OF_WEEK) - 1;
                //System.out.println(day);
                count = 7 - day;
            }
            //System.out.println(num);
            if (!result.containsKey(num)) {
                result.put(num, new ArrayList<String>());
            }

            for (int i = 0; i < count; i++) {
                result.get(num).add(sdf.format(cl1.getTime()));
                cl1.add(Calendar.DAY_OF_WEEK, 1);
                if (cl2.compareTo(cl1) <= 0) {
                    break;
                }
            }
            num++;
        }
        /*List<String> time = new ArrayList<String>();
        for (Map.Entry<Integer, List<String>> entry : result.entrySet()) {
            List<String> temp = entry.getValue();
            //System.out.println(j+":"+temp.size()+":"+temp.toString());
            if(temp!=null &&temp.size()>0) {
                String str = temp.get(0) + "~" + temp.get(temp.size() - 1);
                time.add(str);
            }
        }*/
        return result;
    }

    /**
     * 根据开始结束日期返回以周为集合的日期组合
     * @param a yyyy-MM-dd
     * @param b yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public static List<String> getResultsList(String a, String b) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date ad = sdf.parse(a);
        Date bd = sdf.parse(b);
        Calendar cl1 = Calendar.getInstance();
        cl1.setTime(ad);
        Calendar cl2 = Calendar.getInstance();
        cl2.setTime(bd);
        Map<Integer, List<String>> result = new HashMap<Integer, List<String>>();
        int num = 0;
        while (true) {
            if (cl2.compareTo(cl1) <= 0) {
                System.out.println(sdf.format(cl2.getTime()));
                break;
            }

            int count = 7;

            if (num == 0) {
                System.out.println(sdf.format(cl1.getTime()));
                int day = cl1.get(Calendar.DAY_OF_WEEK) - 1;
                //System.out.println(day);
                count = 7 - day;
            }
            //System.out.println(num);
            if (!result.containsKey(num)) {
                result.put(num, new ArrayList<String>());
            }

            for (int i = 0; i < count; i++) {
                result.get(num).add(sdf.format(cl1.getTime()));
                cl1.add(Calendar.DAY_OF_WEEK, 1);
                if (cl2.compareTo(cl1) <= 0) {
                    break;
                }
            }
            num++;
        }
        List<String> time = new ArrayList<String>();
        int j = 0;
        for (Map.Entry<Integer, List<String>> entry : result.entrySet()) {
            j++;
            List<String> temp = entry.getValue();
            //System.out.println(j+":"+temp.size()+":"+temp.toString());
            if(temp!=null &&temp.size()>0) {
                String str = temp.get(0) + "~" + temp.get(temp.size() - 1);
                time.add(str);
            }
        }
        return time;
    }

    /**
     * 秒转换成时间格式
     * @param second
     * @return
     */
    public static String secondToDate(int second) {
        if (second <= 0) {

            return "00:00:00";
        }

        StringBuilder sb = new StringBuilder();
        int hours = second / HOUR_SECOND;
        if (hours > 0) {

            second -= hours * HOUR_SECOND;
        }

        int minutes = second / MINUTE_SECOND;
        if (minutes > 0) {

            second -= minutes * MINUTE_SECOND;
        }

        return (hours >= 10 ? (hours + "")
                : ("0" + hours) + ":" + (minutes >= 10 ? (minutes + "") : ("0" + minutes)) + ":"
                + (second >= 10 ? (second + "") : ("0" + second)));
    }
    //判断两个日期是否是同一天
    public static boolean areSameDay(Date dateA,Date dateB) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(dateA);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(dateB);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                &&  calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    public static Date TimestampToDate(Integer timestamp){
        //Timestamp ts = new Timestamp(timestamp);
        Date date = null;
        try {
            date = new Date(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return date;
        }
    }

    public static Date TimestampToDate(Long timestamp){
        //Timestamp ts = new Timestamp(timestamp);
        Date date = null;
        try {
            date = new Date(timestamp);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return date;
        }
    }

}
