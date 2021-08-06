package com.github.malamut2.carstat_germany.addition_statistics;

public class DataPoint {

    private final String date;
    private final Model model;
    private int total;
    private int diesel;
    private int bev;
    private int phev;
    private int business;

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

}
