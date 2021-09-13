package com.github.malamut2.carstat_germany.addition_statistics;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.Objects;

@JsonDeserialize(keyUsing = Model.ModelKeyDeserializer.class)
public record Model(String maker, String model) implements Comparable<Model> {

    public static class ModelKeyDeserializer extends KeyDeserializer {
        @Override
        public Object deserializeKey(String s, DeserializationContext deserializationContext) {
            String[] pair = s.split("\u001f");
            if (pair.length == 1) {
                pair = new String[]{pair[0], ""};
            }
            if (pair.length != 2) {
                throw new IllegalArgumentException("Expected maker FS model, but found: " + s);
            }
            return new Model(pair[0], pair[1]);
        }
    }

    public static class ModelKeySerializer extends JsonSerializer<Model> {
        @Override
        public void serialize(Model model, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeFieldName(model.maker + "\u001f" + model.model);
        }
    }

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
        if ("".equals(model)) {
            return "SONSTIGE";
        }
        if (model.startsWith(maker + " ")) {
            model = model.substring(maker.length() + 1);
        }
        if ("ALPINE".equals(maker)) {
            return "";
        }
        if ("AUDI".equals(maker)) {
            if ("RS5".equals(model)) {
                return "A5, S5, RS5";
            }
            return switch (model.substring(0, 2)) {
                case "A1" -> "A1, S1";
                case "A3" -> "A3, S3, RS3";
                case "A4" -> "A4, S4, RS4";
                case "A5" -> "A5, S5, RS5";
                case "A6", "A7" -> "A6, S6, RS6, A7, S7, RS7";
                default -> model;
            };
        }
        if ("FIAT".equals(maker)) {
            return switch (model) {
                case "BRAVO" -> "BRAVO, BRAVA";
                case "SCUDO" -> "ULYSSE, SCUDO";
                default -> model;
            };
        }
        if ("MERCEDES".equals(maker)) {
            return switch (model) {
                case "GLK" -> "GLK, GLC";
                case "SLK" -> "SLK, SLC";
                case "ML-KLASSE" -> "ML-KLASSE, GLE";
                default -> model;
            };
        }
        if ("MITSUBISHI".equals(maker)) {
            return switch (model) {
                case "MIRAGE" -> "MIRAGE, SPACE STAR";
                case "PAJERO" -> "PAJERO, MONTERO";
                default -> model;
            };
        }
        if ("VW".equals(maker)) {
            return switch (model) {
                case "VW GOLF" -> "GOLF, JETTA";
                case "TRANSPORTER" -> "TRANSPORTER, CARAVELLE";
                default -> model;
            };
        }
        if ("TRANSIT, TOURNEO".equals(model)) {
            return "TRANSIT TOURNEO";
        }
        if ("ALPINA B 3".equals(model)) {
            return "ALPINA B3";
        }
        if (model.startsWith("ALFA ")) {
            return model.substring(5);
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
        int result = toString().compareTo(o.toString());
        if (result != 0) {
            if ("SONSTIGE".equals(model)) {
                return 1;
            }
            if ("SONSTIGE".equals(o.model)) {
                return -1;
            }
        }
        return result;
    }

}
