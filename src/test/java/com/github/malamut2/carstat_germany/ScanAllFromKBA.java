package com.github.malamut2.carstat_germany;

import java.io.File;
import java.io.IOException;

import static com.github.malamut2.carstat_germany.DateUtils.isValidDate;
import static com.github.malamut2.carstat_germany.DateUtils.monthBefore;

class ScanAllFromKBA {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner();
        for (String date = KBADocumentType.getNewestAvailableAdditionDate();
             isValidDate(date) && date.compareTo(KBADocumentType.getOldestAvailableAdditionDate()) >= 0;
             date = monthBefore(date)) {
            scanner.parse(new File("data",
                    KBADocumentType.fz10.getLocalName(date.substring(0, 4), date.substring(4))), null);
        }

    }

}