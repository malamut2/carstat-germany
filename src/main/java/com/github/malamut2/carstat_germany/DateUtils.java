package com.github.malamut2.carstat_germany;

public class DateUtils {

    public static String monthBefore(String date) {
        int resultAsNum = Integer.parseInt(date) - 1;
        if (resultAsNum % 100 == 0) {
            resultAsNum -= 88;
        }
        return Integer.toString(resultAsNum);
    }

    public static boolean isValidDate(String date) {
        try {
            int dateAsNum = Integer.parseInt(date);
            int month = dateAsNum % 100;
            return date.length() == 6 && month >= 1 && month <= 12 && dateAsNum > 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

}
