package com.jasify.schedule.appengine.besr;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class PaymentSlipTest {

    private PaymentSlip build() {
        //WARNING: these are not proper values
        PaymentSlipBuilder builder = new PaymentSlipBuilder();
        builder.account = "00-00000-0";
        builder.referenceCode = "00 00000 00000 00000 00000 00000";
        builder.codeLine = "2100000000000>000000000000000000000000000+ 000000000>";
        builder.recipient = "J.A.S. GmbH\nEuropastrasse 2\n1234 Opfikon";
        builder.amount = "000000000";
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