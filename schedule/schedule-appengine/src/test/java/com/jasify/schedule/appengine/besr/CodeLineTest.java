package com.jasify.schedule.appengine.besr;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class CodeLineTest {

    @Test
    public void testExample1() {
        //Kontonummer: 01-43884-8
        String code1 = "00 00000 40386 84170 01980 03048";
        assertTrue(CodeLine.isValid(code1));
    }

    @Test
    public void testExample2() {
        //Konto: 01-50752-5
        String code2 = "00 00000 0022368760 04283 11533";
        assertTrue(CodeLine.isValid(code2));

    }

    @Test
    public void testExample3() {
        //Konto: 01-50752-5
        String code3 = "00 00000 0022368760 04284 51116";
        assertTrue(CodeLine.isValid(code3));
    }

    @Test
    public void testBadExample() {
        String badCode = "00 00000 0012345678 12345 12345";
        assertFalse(CodeLine.isValid(badCode));
    }
}