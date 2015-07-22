package com.jasify.schedule.appengine.besr;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.TestCase.*;
import static org.hamcrest.core.StringStartsWith.startsWith;

public class CodeLineTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

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

    @Test
    public void testLeftPad() {
        assertEquals("123", CodeLine.leftPad("123", 3));
        assertEquals("0123", CodeLine.leftPad("123", 4));
        assertEquals("123", CodeLine.leftPad("123", 2));
        assertEquals("0000", CodeLine.leftPad("", 4));
        assertEquals("", CodeLine.leftPad("", 0));
        assertEquals("0", CodeLine.leftPad("", 1));
        assertEquals("02", CodeLine.leftPad("2", 2));
        assertEquals("020", CodeLine.leftPad("20", 3));
        assertEquals("020", CodeLine.leftPad("020", 3));
    }

    @Test
    public void testAmountLengthExceedsThrows() {
        thrown.expect(IllegalCodeLineException.class);
        thrown.expectMessage(startsWith("Amount length exceeds"));

        //AAAAAAAAAAA
        new CodeLine(SlipTypeEnum.ISR_CHF, "12345678901", "12345678901234567890123456", "12345678").toCodeLine();
    }

    @Test
    public void testReferenceLengthExceedsThrows() {
        thrown.expect(IllegalCodeLineException.class);
        thrown.expectMessage(startsWith("Reference length exceeds"));

        //RRRRRRRRRRRRRRRRRRRRRRRRRRR
        new CodeLine(SlipTypeEnum.ISR_CHF, "1234567890", "123456789012345678901234567", "12345678").toCodeLine();
    }

    @Test
    public void testSubscriberLengthExceedsThrows() {
        thrown.expect(IllegalCodeLineException.class);
        thrown.expectMessage(startsWith("Subscriber length exceeds"));

        //SSSSSSSSS
        new CodeLine(SlipTypeEnum.ISR_CHF, "1234567890", "12345678901234567890123456", "123456789").toCodeLine();
    }

    @Test
    public void testAmountRequiredForWithAmountTypesEmpty() {
        thrown.expect(IllegalCodeLineException.class);
        thrown.expectMessage(startsWith("Amount is required"));

        //SSSSSSSSS
        new CodeLine(SlipTypeEnum.ISR_CHF, "", "12345678901234567890123456", "123456789").toCodeLine();
    }

    @Test
    public void testAmountRequiredForWithAmountTypesZeros() {
        thrown.expect(IllegalCodeLineException.class);
        thrown.expectMessage(startsWith("Amount is required"));

        //SSSSSSSSS
        new CodeLine(SlipTypeEnum.ISR_CHF, "00000", "12345678901234567890123456", "123456789").toCodeLine();
    }

    @Test
    public void testAmountRequiredForWithAmountTypesNull() {
        thrown.expect(IllegalCodeLineException.class);
        thrown.expectMessage(startsWith("Amount is required"));

        //SSSSSSSSS
        new CodeLine(SlipTypeEnum.ISR_CHF, null, "12345678901234567890123456", "123456789").toCodeLine();
    }

    @Test
    public void testNoAmountReferenceLengthExceedsThrows() {
        thrown.expect(IllegalCodeLineException.class);
        thrown.expectMessage(startsWith("Reference length exceeds"));

        //RRRRRRRRRRRRRRRRRRRRRRRRRRR
        new CodeLine(SlipTypeEnum.ISR_Plus_CHF, null, "123456789012345678901234567", "12345678").toCodeLine();
    }

    @Test
    public void testNoAmountSubscriberLengthExceedsThrows() {
        thrown.expect(IllegalCodeLineException.class);
        thrown.expectMessage(startsWith("Subscriber length exceeds"));

        //SSSSSSSSS
        new CodeLine(SlipTypeEnum.ISR_Plus_CHF, null, "12345678901234567890123456", "123456789").toCodeLine();
    }

    @Test
    public void testISR_Plus_CHFExample() {
        CodeLine line = new CodeLine(SlipTypeEnum.ISR_Plus_CHF, null, "12000000000023447894321689", "01000162");
        String codeLine = line.toCodeLine();
        assertNotNull(codeLine);
        assertEquals("042>120000000000234478943216899+ 010001628>", codeLine);
    }

    @Test
    public void testISR_EURExample() {
        CodeLine line = new CodeLine(SlipTypeEnum.ISR_EUR, "0000044000", "96111690000000660000000928", "03000162");
        String codeLine = line.toCodeLine();
        assertNotNull(codeLine);
        assertEquals("2100000440001>961116900000006600000009284+ 030001625>", codeLine);
    }

    @Test
    public void testISR_Plus_EURExample() {
        CodeLine line = new CodeLine(SlipTypeEnum.ISR_Plus_EUR, null, "96111690000000660000000928", "03000162");
        String codeLine = line.toCodeLine();
        assertNotNull(codeLine);
        assertEquals("319>961116900000006600000009284+ 030001625>", codeLine);
    }
}