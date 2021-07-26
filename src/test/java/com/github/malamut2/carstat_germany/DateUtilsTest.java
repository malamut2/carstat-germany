package com.github.malamut2.carstat_germany;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void monthBefore() {
        assertEquals("202104", DateUtils.monthBefore("202105"));
        assertEquals("202012", DateUtils.monthBefore("202101"));
    }

    @Test
    void isValidDate() {

        assertTrue(DateUtils.isValidDate("202105"));
        assertTrue(DateUtils.isValidDate("100001"));
        assertTrue(DateUtils.isValidDate("999912"));

        assertFalse(DateUtils.isValidDate("999913"));
        assertFalse(DateUtils.isValidDate("99991"));
        assertFalse(DateUtils.isValidDate("9999111"));
        assertFalse(DateUtils.isValidDate("10001"));
        assertFalse(DateUtils.isValidDate("1000011"));
        assertFalse(DateUtils.isValidDate("10001a"));

    }

}
