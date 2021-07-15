package com.github.malamut2.carstat_germany;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class RetrieverTest {

    @Spy
    private Retriever retriever;

    @Test
    void downloadMonthlyAdditions() {
        doReturn(null).when(retriever).download(any(), any(), anyBoolean());
        doReturn(new File("fz10")).when(retriever).download(KBADocumentType.fz10, "202102", false);
        doReturn(new File("fz11")).when(retriever).download(KBADocumentType.fz11, "202102", false);
        assertEquals(
                Map.of("202102-fz10", new File("fz10"), "202102-fz11", new File("fz11")),
                retriever.downloadMonthlyAdditions("200501", "205001", false)
        );
    }

    @Test
    void monthBefore() {
        assertEquals("202104", retriever.monthBefore("202105"));
        assertEquals("202012", retriever.monthBefore("202101"));
    }

    @Test
    void isValidDate() {

        assertTrue(retriever.isValidDate("202105"));
        assertTrue(retriever.isValidDate("100001"));
        assertTrue(retriever.isValidDate("999912"));

        assertFalse(retriever.isValidDate("999913"));
        assertFalse(retriever.isValidDate("99991"));
        assertFalse(retriever.isValidDate("9999111"));
        assertFalse(retriever.isValidDate("10001"));
        assertFalse(retriever.isValidDate("1000011"));
        assertFalse(retriever.isValidDate("10001a"));

    }

}
