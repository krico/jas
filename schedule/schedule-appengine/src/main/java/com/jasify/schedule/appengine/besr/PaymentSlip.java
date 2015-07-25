package com.jasify.schedule.appengine.besr;

import com.google.code.appengine.awt.Color;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author krico
 * @since 24/07/15.
 */
public class PaymentSlip {
    public static final String OCR_B_TRUE_TYPE = "BESR/fonts/OCR-B1.ttf";
    //A5 (210 x 148mm)
    public static final String BESR_A5 = "BESR/images/BESR-A5-RED.jpg";
    private static final Rectangle SIZE = new RectangleReadOnly(Utilities.inchesToPoints(11), Utilities.inchesToPoints(11));
    private static final Logger log = LoggerFactory.getLogger(PaymentSlip.class);

    private Document document;
    private PdfWriter writer;
    private float llx;
    private float lly;
    private float lrx;
    private float lry;
    private float urx;
    private float ury;
    private float ulx;
    private float uly;
    private float giroUlx;

    private float width;
    private float height;

    public static void main(String[] args) throws Exception {
        new PaymentSlip().render(new File("/tmp/test.pdf"));
    }

    private BaseFont loadFont(String fontName) throws IOException, DocumentException {
        return BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    private void calculate() {
        width = document.getPageSize().getWidth();
        height = document.getPageSize().getHeight();
        llx = (width - Points.Width) / 2;
        lly = (height - Points.Height) / 2;
        lrx = llx + Points.Width;
        lry = lly;
        ury = lry + Points.Height;
        urx = lrx;
        ulx = llx;
        uly = ury;
        giroUlx = urx - Points.GiroWidth;
    }

    private void fillOrangeBackground() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorFill(Colors.Background);
        under.rectangle(llx, lly, Points.Width, Points.Height);
        under.fill();
        under.restoreState();
    }

    private void borderLines() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(llx, lly);
        under.lineTo(lrx, lry);
        under.lineTo(urx, ury);
        under.lineTo(ulx, uly);
        under.closePath();
        under.stroke();
        under.restoreState();
    }

    private void dottedLeftVerticalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(Points.DottedLineWidth);
        under.moveTo(llx + Points.Millimeter, lly);
        under.lineTo(ulx + Points.Millimeter, uly);
        under.setLineDash(Points.DottedLineUnitsOn, Points.DottedLinePhase);
        under.stroke();
        under.restoreState();
    }

    private void dottedSeparatorVerticalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(Points.DottedLineWidth);
        under.moveTo(giroUlx, lly);
        under.lineTo(giroUlx, uly - Points.Line);
        under.setLineDash(Points.DottedLineUnitsOn, Points.DottedLinePhase);
        under.stroke();
        under.restoreState();
    }

    private void solidSeparatorVerticalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(giroUlx, uly - Points.Line);
        under.lineTo(giroUlx, uly);
        under.stroke();
        under.restoreState();
    }

    private void solidInFavorOfSeparatorVerticalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(Points.SolidLineWidth);
        float off = giroUlx + 24 * Points.Column;
        under.moveTo(off, lly);
        under.lineTo(off, uly - Points.Line);
        under.stroke();
        under.restoreState();
    }

    private void solidLeftVerticalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(ulx + Points.Millimeter, uly - Points.Line);
        under.lineTo(urx, ury - Points.Line);
        under.stroke();
        under.restoreState();
    }

    private void solidTopHorizontalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(ulx + 4 * Points.Column, uly);
        under.lineTo(llx + 4 * Points.Column, lly);
        under.stroke();
        under.restoreState();
    }

    public void render(File file) throws Exception {
        log.info("Generating: {}", file);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            document = new Document(Points.PageSize);
            writer = PdfWriter.getInstance(document, bos);
            document.open();

            calculate();

            fillOrangeBackground();
            borderLines();
            dottedLeftVerticalLine();
            solidLeftVerticalLine();
            solidTopHorizontalLine();
            dottedSeparatorVerticalLine();
            solidSeparatorVerticalLine();
            solidInFavorOfSeparatorVerticalLine();

            document.close();
        } finally {
            log.info("Generated: {}", file);
        }

        log.info("Writing: {}", file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bos.toByteArray());
        } finally {
            log.info("Wrote: {}", file);
        }
    }

    interface Dimensions {
        float HeightMillimeters = 106;
        float WidthMillimeters = 220 + 1;

        float ReceiptWidthColumns = 27;
        float GiroWidthColumns = 59;

        float StatusLineBoxHeightInches = 1f;
    }

    interface Colors {
        Color Background = new Color(0xFE, 0xEC, 0xD8);
        Color BackgroundPlain = new Color(0xDE, 0x64, 0x22);
    }

    /**
     * All dimensionas are in points
     */
    interface Points {
        float SolidLineWidth = 0.5f;
        float DottedLineWidth = 0.25f;
        float Inch = Utilities.inchesToPoints(1.0f);
        float Line = Inch / 6;
        float Margin = Inch;
        float Column = Inch / 10;
        float Millimeter = Utilities.millimetersToPoints(1);
        float Width = Utilities.millimetersToPoints(Dimensions.WidthMillimeters);
        float Height = Utilities.millimetersToPoints(Dimensions.HeightMillimeters);
        Rectangle PageSize = new RectangleReadOnly(Width + Margin, Height + Margin);
        float GiroWidth = Dimensions.GiroWidthColumns * Column;
        float ReceiptWidth = Dimensions.ReceiptWidthColumns * Column;

        float StatusLineBoxWidth = Dimensions.ReceiptWidthColumns * Column;
        float StatusLineBoxHeight = Utilities.inchesToPoints(Dimensions.StatusLineBoxHeightInches);
        float DottedLineUnitsOn = 0.8f;
        float DottedLinePhase = 2;
    }
}
