package ru.job4j.date;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SqlRuDateFormatter {
    private final static String TODAY = "сегодня";
    private final static String YESTERDAY = "вчера";
    private final static int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;
    private final static Map<String, Integer> MONTH_MAP = new HashMap<>();
    private final static int YEAR_ADDITION = 2000;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private Calendar date;

    static {
        MONTH_MAP.put("янв", Calendar.JANUARY);
        MONTH_MAP.put("фев", Calendar.FEBRUARY);
        MONTH_MAP.put("мар", Calendar.MARCH);
        MONTH_MAP.put("апр", Calendar.APRIL);
        MONTH_MAP.put("май", Calendar.MAY);
        MONTH_MAP.put("июн", Calendar.JUNE);
        MONTH_MAP.put("июл", Calendar.JULY);
        MONTH_MAP.put("авг", Calendar.AUGUST);
        MONTH_MAP.put("сен", Calendar.SEPTEMBER);
        MONTH_MAP.put("окт", Calendar.OCTOBER);
        MONTH_MAP.put("ноя", Calendar.NOVEMBER);
        MONTH_MAP.put("дек", Calendar.DECEMBER);
    }

    public String format(String inDateStr) {
        String[] parts = inDateStr.split(",");
        parts[0] = parts[0].trim();
        parts[1] = parts[1].trim();
        Calendar date = Calendar.getInstance();
        parseDate(date, parts[0]);
        parseTime(date, parts[1]);
        date.set(year, month, day, hour, min);
        return format(date);
    }

    public String format(Calendar date) {
        this.date = date;
        return new SimpleDateFormat("dd.MM.yyyy kk:mm").format(date.getTime());
    }

    private void parseDate(Calendar date, String inDateStr) {
        if (inDateStr.equals(TODAY) || inDateStr.equals(YESTERDAY)) {
            if (inDateStr.equals(YESTERDAY)) {
                date.setTimeInMillis(date.getTimeInMillis() - MILLIS_IN_DAY);
            }
            year = date.get(Calendar.YEAR);
            month = date.get(Calendar.MONTH);
            day = date.get(Calendar.DATE);
        } else {
            String[] parts = inDateStr.split(" ");
            day =  Integer.parseInt(parts[0]);
            month =  MONTH_MAP.get(parts[1]);
            year =  Integer.parseInt(parts[2]) + YEAR_ADDITION;
        }
    }

    private void parseTime(Calendar date, String inDateStr) {
        String[] parts = inDateStr.split(":");
        hour =  Integer.parseInt(parts[0]);
        min =  Integer.parseInt(parts[1]);
    }

    public Calendar getDate() {
        return date;
    }
}
