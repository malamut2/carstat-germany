package com.github.malamut2.carstat_germany.addition_statistics;

import java.util.Objects;

public record Model(String maker, String model) implements Comparable<Model> {

    public Model(String maker, String model) {
        this.maker = fixMaker(maker);
        this.model = fixModel(this.maker, model);
    }

    private static String fixMaker(String maker) {
        if (maker == null) {
            return null;
        }
        if (maker.endsWith(" 1)")) {
            return maker.substring(0, maker.length() - 3);
        }
        if ("MINI".equals(maker) || "BMW, MINI".equals(maker)) {
            return "BMW";
        }
        return maker;
    }

    private static String fixModel(String maker, String model) {
        if (model == null) {
            return null;
        }
        if ("ALPINE".equals(maker)) {
            return "";
        }
        if ("VW GOLF, JETTA".equals(model)) {
            return "GOLF";
        }
        if ("ALPINA B 3".equals(model)) {
            return "ALPINA B3";
        }
        if (model.startsWith("ALFA ")) {
            return model.substring(5);
        }
        if (model.startsWith(maker + " ")) {
            return model.substring(maker.length() + 1);
        }
        return model;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model1 = (Model) o;
        return maker.equals(model1.maker) && model.equals(model1.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maker, model);
    }

    @Override
    public String toString() {
        return maker + " " + model;
    }

    @Override
    public int compareTo(Model o) {
        return toString().compareTo(o.toString());
    }

}
