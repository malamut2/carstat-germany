package com.github.malamut2.carstat_germany.addition_statistics;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.Objects;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class DataPoint {

    private String date;
    private Model model;
    private int total;
    private int diesel;
    private int bev;
    private int phev;
    private int business;

    // Json constructor
    DataPoint() {
    }

    public DataPoint(String date, Model model, int total, int diesel, int bev, int phev) {
        this.date = date;
        this.model = model;
        this.total = total;
        this.diesel = diesel;
        this.bev = bev;
        this.phev = phev;
    }

    public int getTotal() {
        return total;
    }

    public int getDiesel() {
        return diesel;
    }

    public int getBev() {
        return bev;
    }

    public int getPhev() {
        return phev;
    }

    @Override
    public String toString() {
        return model + " on " + date + ": (total=" + total +
                ", diesel=" + diesel +
                ", bev=" + bev +
                ", phev=" + phev +
                ", business=" + business +
                ")";
    }

    public void merge(DataPoint dataPoint) {
        if (!date.equals(dataPoint.date) || !model.equals(dataPoint.model)) {
            throw new IllegalArgumentException("Cannot merge data for dates " + date + " vs " + dataPoint.date
                    + " on " + model + " vs " + dataPoint.model);
        }
        total += dataPoint.total;
        diesel += dataPoint.diesel;
        bev += dataPoint.bev;
        phev += dataPoint.phev;
    }

    public void addBusiness(int business) {
        this.business += business;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPoint dataPoint = (DataPoint) o;
        return total == dataPoint.total && diesel == dataPoint.diesel && bev == dataPoint.bev && phev == dataPoint.phev && business == dataPoint.business && date.equals(dataPoint.date) && model.equals(dataPoint.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, model, total, diesel, bev, phev, business);
    }

}
