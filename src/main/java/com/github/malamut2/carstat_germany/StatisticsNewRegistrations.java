package com.github.malamut2.carstat_germany;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.malamut2.carstat_germany.addition_statistics.DataPoint;
import com.github.malamut2.carstat_germany.addition_statistics.Model;
import com.github.malamut2.carstat_germany.addition_statistics.SingleMonthData;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class StatisticsNewRegistrations {

    @JsonProperty
    SortedMap<String, SingleMonthData> date2data = new TreeMap<>();

    public void append(String date, String maker, String model, int total, int diesel, int bev, int phev) {
        SingleMonthData data = date2data.computeIfAbsent(date, SingleMonthData::new);
        data.append(maker, model, total, diesel, bev, phev);
    }

    public void mergeFZ11Data(String date, Map<String, Map<String, Integer>> fz11data) {
        SingleMonthData data = date2data.computeIfAbsent(date, SingleMonthData::new);
        data.mergeFZ11Data(fz11data);
    }

    public SortedMap<String, SortedSet<Model>> getAllModels() {
        SortedMap<String, SortedSet<Model>> result = new TreeMap<>();
        for (SingleMonthData data : date2data.values()) {
            SortedMap<String, SortedSet<Model>> newModels = data.getAllModels();
            for (Map.Entry<String, SortedSet<Model>> entry : newModels.entrySet()) {
                SortedSet<Model> currentModels = result.computeIfAbsent(entry.getKey(), x -> new TreeSet<>());
                currentModels.addAll(entry.getValue());
            }
        }
        return result;
    }

    public SortedMap<String, DataPoint> getTimeSeriesByModel(String maker, String model) {
        SortedMap<String, DataPoint> result = new TreeMap<>();
        for (Map.Entry<String, SingleMonthData> entry : date2data.entrySet()) {
            String date = entry.getKey();
            DataPoint dataPoint = entry.getValue().get(new Model(maker, model));
            if (dataPoint != null) {
                result.put(date, dataPoint);
            }
        }
        return result;
    }

    public void merge(StatisticsNewRegistrations data) {
        for (Map.Entry<String, SingleMonthData> entry : data.date2data.entrySet()) {
            String date = entry.getKey();
            SingleMonthData smd = entry.getValue();
            SingleMonthData previous = date2data.get(date);
            if (previous == null) {
                date2data.put(date, smd);
            } else {
                previous.merge(smd);
            }
        }
    }

    public void saveToDisk(File dir) throws IOException {
        File fOut = new File(dir, "new-registrations.json");
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer().with(SerializationFeature.INDENT_OUTPUT);
        writer.writeValue(fOut, this);
    }

    public static StatisticsNewRegistrations getFromDisk(File dir) throws IOException {
        File fIn = new File(dir, "new-registrations.json");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.reader().readValue(fIn, StatisticsNewRegistrations.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatisticsNewRegistrations that = (StatisticsNewRegistrations) o;
        return date2data.equals(that.date2data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date2data);
    }
}
