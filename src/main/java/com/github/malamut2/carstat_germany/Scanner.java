package com.github.malamut2.carstat_germany;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

public class Scanner {

    private static final Logger logger = LoggerFactory.getLogger(Scanner.class);

    public StatisticsNewRegistrations parse(String date, File fz10, File fz11) throws IOException {

        StatisticsNewRegistrations result = new StatisticsNewRegistrations();

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
                int nPhev = pos.getInt(sheet, phev);
                result.append(date, pos.maker, pos.model, nTotal, nDiesel, nBev, nPhev);
            }

        }

        try (Workbook wb = WorkbookFactory.create(fz11, null, true)) {

            int numSheets = wb.getNumberOfSheets();
            Sheet sheet = wb.getSheetAt(numSheets - 1);
            CellAddress segment = find(sheet, s -> s.trim().startsWith("Segment"));
            CellAddress models = find(sheet, s -> {
                String m = s.trim();
                return m.endsWith("Modellreihe") || m.endsWith("Modellreihe 1)") || m.endsWith("Modellreihe 2)");
            });
            if (models == null) {
                logger.warn("Modellreihe missing in FZ11 on " + date);
                return result;
            }
            CellAddress total = find(sheet, s -> "Anzahl".equals(s.trim()));
            CellAddress business = find(sheet, s -> s.trim().contains("gewerbl."));
            Map<String, Map<String, Integer>> fz11data = new HashMap<>();

            for (ModelScanPos pos : new ModelIterable(sheet, segment, models)) {
                if (pos.maker == null) {
                    continue;
                }
                int nTotal = pos.getInt(sheet, total);
                double rBusiness = pos.getDouble(sheet, business);
                int nBusiness = (int)Math.round(nTotal * rBusiness / 100d);
                Map<String, Integer> members = fz11data.computeIfAbsent(pos.maker, a -> new HashMap<>());
                members.put(pos.model, nBusiness);
            }
            result.mergeFZ11Data(date, fz11data);

        }

        return result;

    }

    public CellAddress find(Sheet sheet, Predicate<String> f) {
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell != null) {
                    try {
                        if (f.test(cell.getStringCellValue().trim())) {
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
                    case STRING -> Integer.parseInt(cell.getStringCellValue().trim());
                    default -> 0;
                };
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }

        public double getDouble(Sheet sheet, CellAddress col) {
            if (col == null)
                return 0;
            try {
                Cell cell = sheet.getRow(row).getCell(col.getColumn());
                return switch(cell.getCellType()) {
                    case NUMERIC -> cell.getNumericCellValue();
                    case STRING -> Double.parseDouble(cell.getStringCellValue().trim());
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

        private boolean isNoContentLine(String text) {
            return text.contains("ZUSAMMEN")
                    || text.contains("INSGESAMT")
                    || text.contains("Revidierte")
                    || "".equals(text);
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
                    maker = makerCell.getStringCellValue().trim();
                    if (isNoContentLine(maker)) {
                        maker = null;
                        continue;
                    }
                }

                Cell modelCell = row.getCell(modelsCol, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (modelCell != null && modelCell.getCellType() == CellType.STRING) {
                    String model = modelCell.getStringCellValue().trim();
                    result.add(new ModelScanPos(maker, model, rowNum));
                }
            }

            return result;

        }

        private List<ModelScanPos> createList(Sheet sheet, CellAddress makersAndModels) {

            List<ModelScanPos> result = new ArrayList<>();
            int initialRow = makersAndModels.getRow();
            int textCol = makersAndModels.getColumn();
            int lastRowNum = sheet.getLastRowNum();
            String maker = null;

            for (int rowNum = initialRow + 1; rowNum < lastRowNum; rowNum++) {

                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    maker = null;
                    continue;
                }

                Cell textCell = row.getCell(textCol, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                if (textCell != null && textCell.getCellType() == CellType.STRING) {
                    String text = textCell.getStringCellValue().trim();
                    if (isNoContentLine(text)) {
                        maker = null;
                        continue;
                    }
                    if (maker == null || isBold(sheet, textCell)) {
                        if (text.startsWith("SONSTIGE")) {
                            result.add(new ModelScanPos("SONSTIGE", "", rowNum));
                            continue;
                        }
                        maker = text;
                        continue;
                    }
                    result.add(new ModelScanPos(maker, text, rowNum));
                } else {
                    maker = null;
                }

            }

            return result;

        }

        private boolean isBold(Sheet sheet, Cell cell) {
            Font font = sheet.getWorkbook().getFontAt(cell.getCellStyle().getFontIndex());
            return font != null && font.getBold();
        }

        @Override
        public Iterator<ModelScanPos> iterator() {
            return models.iterator();
        }

    }

}
