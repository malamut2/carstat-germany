package com.github.malamut2.carstat_germany;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KBADocumentTypeTest {

    @Test
    void getRemoteName() {
        assertEquals(
                "2021_monatlich/FZ10/fz10_2021_06.xlsx?__blob=publicationFile",
                KBADocumentType.fz10.getRemoteName("2021", "06")
        );
        assertEquals(
                "2019_monatlich/FZ10/fz10_2019_07_xlsx.xlsx?__blob=publicationFile",
                KBADocumentType.fz10.getRemoteName("2019", "07")
        );
        assertEquals(
                "2017_monatlich/FZ10/fz10_2017_08_xls.xls?__blob=publicationFile",
                KBADocumentType.fz10.getRemoteName("2017", "08")
        );
    }

    @Test
    void getLocalName() {
        assertEquals(
                "FZ10-2019-07.xlsx",
                KBADocumentType.fz10.getLocalName("2019", "07")
        );
        assertEquals(
                "FZ10-2017-08.xls",
                KBADocumentType.fz10.getLocalName("2017", "08")
        );
    }

}