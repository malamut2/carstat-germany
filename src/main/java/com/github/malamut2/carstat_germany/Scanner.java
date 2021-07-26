package com.github.malamut2.carstat_germany;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;

import java.io.File;
import java.io.IOException;
import java.util.function.Predicate;

public class Scanner {

    public AdditionStatistics parse(File fz10, File fz11) throws IOException {

        AdditionStatistics result = new AdditionStatistics();

        try (Workbook wb = WorkbookFactory.create(fz10, null, true)) {

            int numSheets = wb.getNumberOfSheets();
            Sheet sheet = wb.getSheetAt(numSheets - 1);
            CellAddress marke = find(sheet, s -> s.trim().startsWith("Marke"));
            CellAddress modellreihe = find(sheet, s -> s.trim().endsWith("Modellreihe"));

            System.out.println(fz10.getName() + ": " + marke + " - " + modellreihe);

            // !kgb
            return result;

        }

    }

    public CellAddress find(Sheet sheet, Predicate<String> f) {
        for (Row row : sheet) {
            for (int i = 0; i < 3; i++) {
                Cell cell = row.getCell(i);
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

}
