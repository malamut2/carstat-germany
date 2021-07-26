package com.github.malamut2.carstat_germany;

import com.aldaviva.easter4j.Easter4J;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Calendar.*;

public enum KBADocumentType {

    fz10, fz11;

    public String getRemoteName(String year, String month) {
        String date = year + month;
        String dir = year + "_monatlich/" + name().toUpperCase(Locale.ROOT) + "/";
        String prefix = name() + "_" + year + "_" + month;
        String extension = "201812".compareTo(date) > 0 ? "_xls.xls"
                : ("202101".compareTo(date) > 0 ? "_xlsx.xlsx" : ".xlsx");
        return dir + prefix + extension + "?__blob=publicationFile";
    }

    public String getLocalName(String year, String month) {
        String date = year + month;
        return name().toUpperCase(Locale.ROOT) + "-" + year + "-" + month
                + ("201812".compareTo(date) > 0 ? ".xls" : ".xlsx");
    }

    public static String getOldestAvailableAdditionDate() {
        return "200901";
    }

    public static String getNewestAvailableAdditionDate() {

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.HOUR_OF_DAY, 20);
        cal.set(Calendar.DAY_OF_MONTH, 1);

        // new year's eve
        if (cal.get(Calendar.MONTH) == GregorianCalendar.JANUARY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // labor day
        if (cal.get(Calendar.MONTH) == GregorianCalendar.MAY) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        // day of German Unity
        if (cal.get(Calendar.MONTH) == GregorianCalendar.OCTOBER) {
            cal.set(Calendar.DAY_OF_MONTH, 3);
            if (cal.get(DAY_OF_WEEK) == SUNDAY || cal.get(DAY_OF_WEEK) == SATURDAY) {
                cal.set(Calendar.DAY_OF_MONTH, 1);
            } else {
                cal.set(Calendar.DAY_OF_MONTH, 2);
            }
        }

        // allow four work days of processing time.
        skipWeekend(cal);
        for (int i = 0; i < 4; i++) {
            cal.add(DAY_OF_WEEK, 1);
            skipWeekend(cal);
        }

        cal.add(DAY_OF_WEEK, numEasterRelatedHolidaysInMonthOnOrBeforeDate(cal));
        if (!cal.before(Calendar.getInstance())) {
            cal.add(Calendar.MONTH, -1);
        }
        cal.add(Calendar.MONTH, -1);

        DateFormat df = new SimpleDateFormat("yyyyMM");
        return df.format(cal.getTime());
    }

    private static void skipWeekend(Calendar cal) {
        while (cal.get(DAY_OF_WEEK) == SUNDAY || cal.get(DAY_OF_WEEK) == SATURDAY) {
            cal.add(DAY_OF_WEEK, 1);
        }
    }

    private static int numEasterRelatedHolidaysInMonthOnOrBeforeDate(Calendar cal) {

        Calendar holiday = Easter4J.getEaster(cal.get(Calendar.YEAR));
        cal.set(Calendar.HOUR_OF_DAY, 12);
        int result = 0;

        // Good Friday
        holiday.add(Calendar.DAY_OF_MONTH, -2);
        if (isBeforeInSameMonth(holiday, cal)) {
            result++;
        }

        // Easter Monday
        holiday.add(Calendar.DAY_OF_MONTH, 3);
        if (isBeforeInSameMonth(holiday, cal)) {
            result++;
        }

        // Ascension of Christ
        holiday.add(Calendar.DAY_OF_MONTH, 39);
        if (isBeforeInSameMonth(holiday, cal)) {
            result++;
        }

        // Whit Monday
        holiday.add(Calendar.DAY_OF_MONTH, 10);
        if (isBeforeInSameMonth(holiday, cal)) {
            result++;
        }

        return result;

    }

    private static boolean isBeforeInSameMonth(Calendar holiday, Calendar cal) {
        return holiday.before(cal) && holiday.get(Calendar.MONTH) == cal.get((Calendar.MONTH));
    }

}
