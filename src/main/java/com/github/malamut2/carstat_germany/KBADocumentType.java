package com.github.malamut2.carstat_germany;

import java.util.Locale;

public enum KBADocumentType {

    fz10, fz11;

    public String getRemoteName(String year, String month) {
        String date = year + month;
        String dir = year + "_monatlich/" + name().toUpperCase(Locale.ROOT) + "/";
        String prefix = name() + "_" + year + "_" + month;
        String extension = "201812".compareTo(date) < 0 ? "_xls.xls"
                : ("202101".compareTo(date) < 0 ? "_xlsx.xlsx" : ".xlsx");
        return dir + prefix + extension + "?__blob=publicationFile";
    }

}
