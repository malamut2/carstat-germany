package com.github.malamut2.carstat_germany.addition_statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SingleMonthData {

    private static final Logger logger = LoggerFactory.getLogger(SingleMonthData.class);

    private final SortedMap<String, SortedSet<Model>> maker2models = new TreeMap<>();
    private final SortedMap<Model, DataPoint> models2data = new TreeMap<>();
    private final String date;

    public SingleMonthData(String date) {
        this.date = date;
    }

    public void append(String maker, String model, int total, int diesel, int bev, int phev) {
        Model m = new Model(maker, model);
        SortedSet<Model> models = maker2models.computeIfAbsent(maker, a -> new TreeSet<>());
        models.add(m);
        DataPoint oldDataPoint = models2data.put(m, new DataPoint(date, m, total, diesel, bev, phev));
        if (oldDataPoint != null) {
            logger.warn("Duplicate entry for model " + model + " on " + date);
        }
    }

    public DataPoint get(Model model) {
        return models2data.get(model);
    }

    public void merge(SingleMonthData smd) {
        if (!date.equals(smd.date)) {
            throw new IllegalArgumentException("Cannot merge data for dates " + date + " vs " + smd.date);
        }
        for (Map.Entry<Model, DataPoint> entry : smd.models2data.entrySet()) {
            Model model = entry.getKey();
            DataPoint dataPoint = entry.getValue();
            DataPoint previousDataPoint = models2data.get(model);
            if (previousDataPoint == null) {
                models2data.put(model, dataPoint);
                SortedSet<Model> models = maker2models.computeIfAbsent(model.maker(), a -> new TreeSet<>());
                models.add(model);
            } else {
                previousDataPoint.merge(dataPoint);
            }
        }
    }

    public SortedMap<String, SortedSet<Model>> getAllModels() {
        return Collections.unmodifiableSortedMap(maker2models);
    }

}
