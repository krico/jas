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
    private float inFavorOfUlx;
    private float rightUpperSquareY;
    private BaseFont codeLineFont;
    private BaseFont formFont;

    public static void main(String[] args) throws Exception {
        new PaymentSlip().render(new File("/tmp/test.pdf"));
    }

    private BaseFont loadFont(String fontName) throws IOException, DocumentException {
        return BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    }

    private void calculate() throws IOException, DocumentException {
        float width = document.getPageSize().getWidth();
        float height = document.getPageSize().getHeight();
        llx = (width - Points.Width) / 2;
        lly = (height - Points.Height) / 2;
        lrx = llx + Points.Width;
        lry = lly;
        ury = lry + Points.Height;
        urx = lrx;
        ulx = llx;
        uly = ury;
        giroUlx = urx - Points.GiroWidth;
        inFavorOfUlx = giroUlx + 24 * Points.Column;
        rightUpperSquareY = ury - (7 * Points.Line);
        codeLineFont = loadFont(OCR_B_TRUE_TYPE);
        formFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
    }

    private void fillOrangeBackground() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorFill(Colors.Background);
        under.rectangle(llx, lly, Points.Width, Points.Height);
        under.fill();
        under.restoreState();
    }

    private void fillA5Model() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorFill(Color.GREEN);
        under.rectangle(llx, lly - 20, 595, 10);
        under.fill();
        under.restoreState();
    }

    private void whiteSquare() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorFill(Color.WHITE);
        under.rectangle(giroUlx, lly, Points.GiroWidth, Points.Inch);
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

    private void dottedSeparatorVerticalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(LineColors.DottedSeparatorVerticalLine);
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
        under.setColorStroke(LineColors.SeparatorVerticalLine);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(giroUlx, uly - Points.Line);
        under.lineTo(giroUlx, uly);
        under.stroke();
        under.restoreState();
    }

    private void solidInFavorOfSeparatorVerticalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(LineColors.InFavorOfSeparatorVerticalLine);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(inFavorOfUlx, lly + Points.Inch);
        under.lineTo(inFavorOfUlx, uly - Points.Line);
        under.stroke();
        under.restoreState();
    }

    private void solidTopHorizontalLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(LineColors.TopHorizontalLine);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(ulx, uly - Points.Line);
        under.lineTo(urx, ury - Points.Line);
        under.stroke();
        under.restoreState();
    }

    private void solidHorizontalRightLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(LineColors.HorizontalRightLine);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(inFavorOfUlx, rightUpperSquareY);
        under.lineTo(urx, rightUpperSquareY);
        under.stroke();
        under.restoreState();
    }

    private void solidVerticalCircleSquareLine() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(LineColors.VerticalCircleSquareLine);
        under.setLineWidth(Points.SolidLineWidth);
        float offX = urx - 13 * Points.Column;
        under.moveTo(offX, ury - Points.Line);
        under.lineTo(offX, rightUpperSquareY);
        under.stroke();
        under.restoreState();
    }

    private void receiptTitle() {
        String text = "Empfangsschein / Récépissé / Ricevuta";
        float fontSize = 8;
        float ascentPoint = formFont.getAscentPoint(text, fontSize);
        float textWidth = formFont.getWidthPoint(text, fontSize);
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFont, fontSize);
        over.setTextMatrix(ulx + (Points.ReceiptWidth - textWidth) / 2, uly - Points.Line + (ascentPoint / 2));
        over.showText(text);
        over.endText();
        over.restoreState();

    }

    private void codeLine() {

        String text = "2100000440001>961116900000006600000009284+ 030001625>";
        float fontSize = 10;
        float textWidth = codeLineFont.getWidthPoint(text, fontSize);
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(codeLineFont, fontSize);
        over.setTextMatrix(lrx - textWidth - 3 * Points.Column, lry + 4 * Points.Line);
        over.moveTo(10, 100);
        over.showText(text);
        over.endText();
        over.restoreState();
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

            fillA5Model(); //TODO: remove

            whiteSquare();
            borderLines();
            solidTopHorizontalLine();
            dottedSeparatorVerticalLine();
            solidSeparatorVerticalLine();
            solidInFavorOfSeparatorVerticalLine();
            solidHorizontalRightLine();
            solidVerticalCircleSquareLine();
            receiptTitle();

            codeLine();

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

    /**
     * I used these to find the lines by changing the colors
     */
    interface LineColors {
        Color HorizontalRightLine = Color.BLACK;
        Color TopHorizontalLine = Color.BLACK;
        Color InFavorOfSeparatorVerticalLine = Color.BLACK;
        Color SeparatorVerticalLine = Color.BLACK;
        Color DottedSeparatorVerticalLine = Color.BLACK;
        Color VerticalCircleSquareLine = Color.BLACK;
    }

    interface Dimensions {
        float HeightMillimeters = 106;
        float WidthMillimeters = 210;

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
        float GiroWidth = Dimensions.GiroWidthColumns * Column - Millimeter;
        float ReceiptWidth = Width - GiroWidth;

        float DottedLineUnitsOn = 0.8f;
        float DottedLinePhase = 2;
    }
}
