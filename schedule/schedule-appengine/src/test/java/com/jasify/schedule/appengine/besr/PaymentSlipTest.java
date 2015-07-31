package com.jasify.schedule.appengine.besr;

import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class PaymentSlipTest {

    private PaymentSlip build() {
        //WARNING: these are not proper values
        PaymentSlipBuilder builder = new PaymentSlipBuilder();
        builder.account = "01-39139-1";
        builder.referenceCode = "96 11169 00000 00660 00000 09284";
        builder.codeLine = "2100000440001>961116900000006600000009284+ 030001625>";
        builder.recipient = "J.A.S. GmbH\nEuropastrasse 2\n1234 Opfikon";
        builder.amount = "100000038";
        return builder.build();
    }

    @Test
    public void testRenderToFile() throws Exception {
        PaymentSlip slip = build();
        File tempFile = File.createTempFile(getClass().getSimpleName(), ".pdf");
        tempFile.deleteOnExit();
        long length = tempFile.length();

        slip.render(tempFile);

        assertTrue(tempFile.exists());
        assertTrue(length < tempFile.length());
    }

    @Test
    public void testRenderToBytes() throws Exception {
        PaymentSlip slip = build();

        byte[] renderedData = slip.renderToBytes();

        assertNotNull(renderedData);
        assertTrue("Should be way more than 10 bytes", renderedData.length > 10);
    }
}