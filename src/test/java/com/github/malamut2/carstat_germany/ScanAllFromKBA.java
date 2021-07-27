package com.github.malamut2.carstat_germany;

import com.github.malamut2.carstat_germany.addition_statistics.DataPoint;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;

import static com.github.malamut2.carstat_germany.DateUtils.isValidDate;
import static com.github.malamut2.carstat_germany.DateUtils.monthBefore;

class ScanAllFromKBA {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner();
        AdditionStatistics statistics = new AdditionStatistics();

        for (String date = KBADocumentType.getNewestAvailableAdditionDate();
             isValidDate(date) && date.compareTo(KBADocumentType.getOldestAvailableAdditionDate()) >= 0;
             date = monthBefore(date)) {

            statistics.merge(scanner.parse(date, new File("data",
                    KBADocumentType.fz10.getLocalName(date.substring(0, 4), date.substring(4))), null));

        }

        SortedMap<String, DataPoint> teslaM3Additions = statistics.getTimeSeriesByModel("TESLA", "MODEL 3");
        System.out.println("Timeseries for Tesla Model 3 additions to market in Germany:");
        for (Map.Entry<String, DataPoint> entry : teslaM3Additions.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        SortedMap<String, DataPoint> golfAdditions = statistics.getTimeSeriesByModel("VW", "GOLF");
        System.out.println("Timeseries for Volkswagen Golf additions to market in Germany:");
        for (Map.Entry<String, DataPoint> entry : golfAdditions.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

    }

}
