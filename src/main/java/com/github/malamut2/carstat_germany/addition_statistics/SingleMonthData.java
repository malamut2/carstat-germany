package com.github.malamut2.carstat_germany.addition_statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SingleMonthData {

    private static final Logger logger = LoggerFactory.getLogger(SingleMonthData.class);

    private static final Set<String> fz11ModelMismatches = new HashSet<>();

    private final SortedMap<String, SortedSet<Model>> maker2models = new TreeMap<>();
    private final SortedMap<Model, DataPoint> models2data = new TreeMap<>();
    private final SortedMap<String, SortedSet<Model>> segment2models = new TreeMap<>();
    private final String date;

    public SingleMonthData(String date) {
        this.date = date;
    }

    public void append(String maker, String model, int total, int diesel, int bev, int phev) {
        Model m = new Model(maker, model);
        SortedSet<Model> models = maker2models.computeIfAbsent(m.maker(), a -> new TreeSet<>());
        models.add(m);
        DataPoint newData = new DataPoint(date, m, total, diesel, bev, phev);
        DataPoint oldDataPoint = models2data.get(m);
        if (oldDataPoint != null) {
            oldDataPoint.merge(oldDataPoint);
        } else {
            models2data.put(m, newData);
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

    public void mergeFZ11Data(Map<String, Map<String, Integer>> fz11data) {

        for (Map.Entry<String, Map<String, Integer>> entry : fz11data.entrySet()) {

            String segment = entry.getKey();
            Map<String, Integer> model2business = entry.getValue();
            SortedSet<Model> currentModels = segment2models.computeIfAbsent(segment, a -> new TreeSet<>());

            for (Map.Entry<String, Integer> m2bEntry : model2business.entrySet()) {
                String rawModelName = m2bEntry.getKey();
                if ("SONSTIGE".equals(rawModelName) || "".equals(rawModelName) || rawModelName.startsWith("Ausgewiesen")) {
                    continue;
                }
                int business = m2bEntry.getValue();
                Model m = getModelFromRawName(rawModelName);
                DataPoint dataPoint = m == null ? null : models2data.get(m);
                if (dataPoint == null) {
                    if (fz11ModelMismatches.add(rawModelName)) {
                        logger.warn("Could not match processed model to '" + rawModelName + "'");
                    }
                } else {
                    currentModels.add(m);
                    dataPoint.addBusiness(business);
                }
            }

        }

    }

    private Model getModelFromRawName(String rawModelName) {
        for (Map.Entry<String, SortedSet<Model>> entry : maker2models.entrySet()) {
            String maker = entry.getKey();
            if (rawModelName.startsWith(maker)) {
                String model = rawModelName.substring(maker.length() + 1);
                for (Model existingModel : entry.getValue()) {
                    if (existingModel.model().contains(model)) {
                        return existingModel;
                    }
                }
                return new Model(maker, model);
            }
        }
        int sep = rawModelName.indexOf(' ');
        if (sep < 1) {
            return null;
        }
        String maker = rawModelName.substring(0, sep);
        String model = rawModelName.substring(sep + 1);
        return new Model(maker, model);
    }

}
