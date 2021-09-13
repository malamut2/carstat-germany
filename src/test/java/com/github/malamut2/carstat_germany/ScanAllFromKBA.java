package com.github.malamut2.carstat_germany;

import com.github.malamut2.carstat_germany.addition_statistics.DataPoint;
import com.github.malamut2.carstat_germany.addition_statistics.Model;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

import static com.github.malamut2.carstat_germany.DateUtils.monthBefore;

class ScanAllFromKBA {

    public static void main(String[] args) throws IOException {

        Scanner scanner = new Scanner();
        StatisticsNewRegistrations statistics = new StatisticsNewRegistrations();

        final String start = KBADocumentType.getNewestAvailableAdditionDate();
        final String stop = KBADocumentType.getOldestAvailableAdditionDate();

        for (String date = start; date.compareTo(stop) >= 0; date = monthBefore(date)) {
            File fz10 = new File("data", KBADocumentType.fz10.getLocalName(date.substring(0, 4), date.substring(4)));
            File fz11 = new File("data", KBADocumentType.fz11.getLocalName(date.substring(0, 4), date.substring(4)));
            statistics.merge(scanner.parse(date, fz10, fz11));
        }
        statistics.saveToDisk(new File("data"));

        StatisticsNewRegistrations statsFromDisk = StatisticsNewRegistrations.getFromDisk(new File("data"));
        System.out.println("Data serialization successful: " + statsFromDisk.equals(statistics));
        System.out.println();

        SortedMap<String, SortedSet<Model>> models = statistics.getAllModels();
        System.out.println("List of all makers and models we have data of:");
        for (Map.Entry<String, SortedSet<Model>> entry : models.entrySet()) {
            System.out.println("** " + entry.getKey());
            for (Model model : entry.getValue()) {
                System.out.println(" - " + model.model());
            }
            System.out.println();
        }

        SortedMap<String, DataPoint> teslaM3Additions = statistics.getTimeSeriesByModel("TESLA", "MODEL 3");
        System.out.println("Time series for Tesla Model 3 additions to market in Germany:");
        for (Map.Entry<String, DataPoint> entry : teslaM3Additions.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();

        SortedMap<String, DataPoint> golfAdditions = statistics.getTimeSeriesByModel("VW", "GOLF, JETTA");
        System.out.println("Time series for Volkswagen Golf additions to market in Germany:");
        for (Map.Entry<String, DataPoint> entry : golfAdditions.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();

    }

}
