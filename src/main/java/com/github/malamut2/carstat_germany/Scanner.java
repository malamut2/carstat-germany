package com.github.malamut2.carstat_germany;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class Scanner {

    public AdditionStatistics parse(String date, File fz10, File fz11) throws IOException {

        AdditionStatistics result = new AdditionStatistics();

        try (Workbook wb = WorkbookFactory.create(fz10, null, true)) {

            int numSheets = wb.getNumberOfSheets();
            Sheet sheet = wb.getSheetAt(numSheets - 1);
            CellAddress makers = find(sheet, s -> s.trim().startsWith("Marke"));
            CellAddress models = find(sheet, s -> s.trim().endsWith("Modellreihe"));
            CellAddress total = find(sheet, s -> s.trim().equals("Insgesamt"));
            CellAddress diesel = find(sheet, s -> s.trim().endsWith("mit Dieselantrieb"));
            CellAddress bev = find(sheet, s -> s.trim().startsWith("mit Elektroantrieb"));
            CellAddress phev = find(sheet, s -> s.trim().equals("Plug-in-Hybridantrieb"));

            for (ModelScanPos pos : new ModelIterable(sheet, makers, models)) {
                int nTotal = pos.getInt(sheet, total);
                int nDiesel = pos.getInt(sheet, diesel);
                int nBev = pos.getInt(sheet, bev);
                int nphev = pos.getInt(sheet, phev);
                result.append(date, pos.maker, pos.model, nTotal, nDiesel, nBev, nphev);
            }

            // !kgb add FZ 11: car classes (as sum of models in class), commercial owners (as sum for model)

            return result;

        }

    }

    public CellAddress find(Sheet sheet, Predicate<String> f) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell != null) {
                    try {
                        if (f.test(cell.getStringCellValue())) {
                            return cell.getAddress();
                        }
                    } catch (Exception ignored) {
                        // we do not care for non-existent or non-string cells
                    }
                }
            }
        }
        return null;
    }

    private record ModelScanPos(String maker, String model, int row) {

        public int getInt(Sheet sheet, CellAddress col) {
            if (col == null)
                return 0;
            try {
                Cell cell = sheet.getRow(row).getCell(col.getColumn());
                return switch(cell.getCellType()) {
                    case NUMERIC -> ((int) cell.getNumericCellValue());
                    case STRING -> Integer.parseInt(cell.getStringCellValue());
                    default -> 0;
                };
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }

    }

    private static class ModelIterable implements Iterable<ModelScanPos> {

        private final List<ModelScanPos> models;

        public ModelIterable(Sheet sheet, CellAddress makers, CellAddress models) {
            this.models = makers.equals(models) ? createList(sheet, makers) : createList(sheet, makers, models);
        }

        private List<ModelScanPos> createList(Sheet sheet, CellAddress makers, CellAddress models) {

            List<ModelScanPos> result = new ArrayList<>();
            int initialRow = makers.getRow();
            if (initialRow != models.getRow()) {
                throw new IllegalArgumentException("Conflict between makers and models row: " + initialRow + " vs " + models.getRow());
            }
            int makersCol = makers.getColumn();
            int modelsCol = models.getColumn();
            int lastRowNum = sheet.getLastRowNum();
            String maker = null;

            for (int rowNum = initialRow + 1; rowNum < lastRowNum; rowNum++) {

                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }

                Cell makerCell = row.getCell(makersCol, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (makerCell != null && makerCell.getCellType() == CellType.STRING) {
                    maker = makerCell.getStringCellValue();
                    if (maker.contains("ZUSAMMEN") || maker.contains("INSGESAMT")) {
                        maker = null;
                        continue;
                    }
                }

                Cell modelCell = row.getCell(modelsCol, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (modelCell != null && modelCell.getCellType() == CellType.STRING) {
                    String model = modelCell.getStringCellValue();
                    result.add(new ModelScanPos(maker, model, rowNum));
                }
            }

            return result;

        }

        private List<ModelScanPos> createList(Sheet sheet, CellAddress makersAndModels) {
            List<ModelScanPos> result = new ArrayList<>();
            // !kgb
            return result;
        }

        @Override
        public Iterator<ModelScanPos> iterator() {
            return models.iterator();
        }

    }

}
