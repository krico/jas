package com.jasify.schedule.appengine.util;

import com.jasify.schedule.appengine.TestHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static junit.framework.TestCase.assertEquals;

public class CurrencyUtilTest {
    private static Locale originalLocale;

    @BeforeClass
    public static void getLocale() {
        originalLocale = Locale.getDefault();
    }

    @AfterClass
    public static void fixLocale() {
        Locale.setDefault(originalLocale);
    }

    @Test
    public void testAssertUtilityClassWellDefined() throws Exception {
        TestHelper.assertUtilityClassWellDefined(CurrencyUtil.class);
    }

    @Test(expected = NullPointerException.class)
    public void testFormatCurrencyNumberNullCurrency() throws Exception {
        CurrencyUtil.formatCurrencyNumber(null, 0d);
    }

    @Test(expected = NullPointerException.class)
    public void testFormatCurrencyNumberNullAmount() throws Exception {
        CurrencyUtil.formatCurrencyNumber("CHF", null);
    }

    @Test(expected = NullPointerException.class)
    public void testFormatCurrencyNumberEmptyCurrency() throws Exception {
        CurrencyUtil.formatCurrencyNumber("", 0d);
    }

    @Test
    public void testFormatCurrencyNumber() throws Exception {
        assertEquals("0.00", CurrencyUtil.formatCurrencyNumber("CHF", 0d));
        assertEquals("0.01", CurrencyUtil.formatCurrencyNumber("CHF", 0.011d));
        assertEquals("120.50", CurrencyUtil.formatCurrencyNumber("USD", 120.50d));
    }

    //Reproduces #392
    @Test
    public void testFormatCurrencyNumberWithLocale() throws Exception {
        Locale.setDefault(new Locale("pl"));
        assertEquals("0.00", CurrencyUtil.formatCurrencyNumber("CHF", 0d));
        assertEquals("0.01", CurrencyUtil.formatCurrencyNumber("CHF", 0.011d));
        assertEquals("120.50", CurrencyUtil.formatCurrencyNumber("USD", 120.50d));
    }
}