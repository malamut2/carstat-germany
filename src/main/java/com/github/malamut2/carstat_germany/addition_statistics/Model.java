package com.github.malamut2.carstat_germany.addition_statistics;

import java.util.Objects;

public record Model(String maker, String model) implements Comparable<Model> {

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
