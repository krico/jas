package com.jasify.schedule.appengine.besr;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class CheckDigitTest {

    @Test
    public void testWellFormed() throws Exception {
        TestHelper.assertUtilityClassWellDefined(CheckDigit.class);
    }

    @Test
    public void testIsValid() throws Exception {
        assertTrue(CheckDigit.isValid("319"));
        assertTrue(CheckDigit.isValid("042"));
        assertTrue(CheckDigit.isValid("120000000000234478943216899"));
        assertTrue(CheckDigit.isValid("12 00000 00000 23447 89432 16899"));
        assertTrue(CheckDigit.isValid("030001625"));
        assertTrue(CheckDigit.isValid("0100003949753"));
    }

    @Test
    public void testComplete() throws Exception {
        assertEquals("319", CheckDigit.complete("31"));
        assertEquals("120000000000234478943216899", CheckDigit.complete("12000000000023447894321689"));
        assertEquals("12 00000 00000 23447 89432 16899", CheckDigit.complete("12 00000 00000 23447 89432 1689"));
        assertEquals("030001625", CheckDigit.complete("03000162"));
    }

    @Test
    public void testOnlyDigits() throws Exception {
        assertEquals("", CheckDigit.onlyDigits("abcDEF,+-tk@#$%IV"));
        assertEquals("00", CheckDigit.onlyDigits("abc0DEF,+-tk@#$%I0V"));
        assertEquals("123", CheckDigit.onlyDigits("1 2,3 "));
        assertEquals("12033", CheckDigit.onlyDigits("CHF 120.33"));
    }
}