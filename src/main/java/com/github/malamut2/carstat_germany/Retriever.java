package com.github.malamut2.carstat_germany;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedHashMap;

@Component
public class Retriever {

    @Value("${kba.base-url:https://www.kba.de/SharedDocs/Publikationen/DE/Statistik/Fahrzeuge/FZ/}")
    protected String baseUrl;

    /**
     * Downloads all files of type FZ10 and FZ11 in the given time frame (inclusive).
     * @param fromDate the earliest date to retrieve, in format yyyyMM
     * @param toDate the latest date to retrieve, in format yyyyMM
     * @param refresh if true, retrieve files from remote site even if they already reside in our data folder
     * @return all downloaded files, including files which already existed before download. The files are organized
     *   in a linked hash map, using yyyyMM-type format as the key, in descending order.
     */
    public LinkedHashMap<String, File> downloadMonthlyAdditions(String fromDate, String toDate, boolean refresh) {
        LinkedHashMap<String, File> result = new LinkedHashMap<>();
        for (String date = toDate; isValidDate(date) && date.compareTo(fromDate) >= 0; date = monthBefore(date)) {
            File fz10 = download(KBADocumentType.fz10, date, refresh);
            File fz11 = download(KBADocumentType.fz11, date, refresh);
            if (fz10 != null && fz11 != null) {  // only accept months with complete data
                result.put(date + "-fz10", fz10);
                result.put(date + "-fz11", fz11);
            }
        }
        return result;
    }

    protected File download(KBADocumentType docType, String date, boolean refresh) {

        if (!refresh) {
            // !kgb check whether we already have both files. If yes -> return
        }

        String year = date.substring(0, 4);
        String month = date.substring(4);
        String remoteName = docType.getRemoteName(year, month);
        String url = baseUrl + remoteName;

        // !kgb

        return null;

    }

    public String monthBefore(String date) {
        int resultAsNum = Integer.parseInt(date) - 1;
        if (resultAsNum % 100 == 0) {
            resultAsNum -= 88;
        }
        return Integer.toString(resultAsNum);
    }

    public boolean isValidDate(String date) {
        try {
            int dateAsNum = Integer.parseInt(date);
            int month = dateAsNum % 100;
            return date.length() == 6 && month >= 1 && month <= 12 && dateAsNum > 0;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

}
