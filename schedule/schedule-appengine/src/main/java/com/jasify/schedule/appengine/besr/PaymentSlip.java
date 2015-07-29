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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author krico
 * @since 24/07/15.
 */
public class PaymentSlip {
    public static final String OCR_B_TRUE_TYPE = "BESR/fonts/OCR-B1.ttf";
    public static final String GROTESK_BOLD_TRUE_TYPE = "BESR/fonts/AlteHaasGroteskBold.ttf";
    public static final String GROTESK_REGULAR_TRUE_TYPE = "BESR/fonts/micross.ttf";
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
    private BaseFont formFontBold;
    private BaseFont formFontRegular;

    public static void main(String[] args) throws Exception {
        new PaymentSlip().render(new File("/tmp/test.pdf"));
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
        inFavorOfUlx = urx - 35 * Points.Column;
        rightUpperSquareY = ury - (7 * Points.Line);
        codeLineFont = BaseFont.createFont(OCR_B_TRUE_TYPE, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        formFontBold = BaseFont.createFont(GROTESK_BOLD_TRUE_TYPE, BaseFont.WINANSI, BaseFont.EMBEDDED);
        formFontRegular = BaseFont.createFont(GROTESK_REGULAR_TRUE_TYPE, BaseFont.WINANSI, BaseFont.EMBEDDED);
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
        under.setLineWidth(1);
        under.setColorStroke(Color.black);
        under.moveTo(llx, lly - 22);
        under.lineTo(llx, lly - 8);
        under.moveTo(llx + 595, lly - 22);
        under.lineTo(llx + 595, lly - 8);
        under.stroke();
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

    private void positioningEdges() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(1f);
        float edgeBase = lly + Points.Inch + 1;
        under.moveTo(giroUlx + Points.Column + Points.Millimeter, edgeBase + 0.6f * Points.Line);
        under.lineTo(giroUlx + Points.Column + Points.Millimeter, edgeBase);
        under.lineTo(giroUlx + 3 * Points.Column + Points.Millimeter, edgeBase);

        under.moveTo(lrx - (2 * Points.Column + Points.Millimeter), edgeBase + 0.6f * Points.Line);
        under.lineTo(lrx - (2 * Points.Column + Points.Millimeter), edgeBase);
        under.lineTo(lrx - (4 * Points.Column + Points.Millimeter), edgeBase);
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

    private void topRightCircle() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Colors.BackgroundPlain);
        under.setLineWidth(0.4f);
        under.circle(lrx - 6.5f * Points.Column, ury - (2 * Points.Line + 3.5f * Points.Column), 3.5f * Points.Column);
        under.setLineDash(1, 2);
        under.stroke();
        under.restoreState();
    }

    private void bottomLeftCircle() {
        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Colors.BackgroundPlain);
        under.setLineWidth(0.4f);
        under.circle(giroUlx - (13 * Points.Column + 3.5f * Points.Column), lly + (0.35f * Points.Line + 3.5f * Points.Column), 3.5f * Points.Column);
        under.setLineDash(1, 2);
        under.stroke();
        under.restoreState();
    }

    private void receiptTitle() {
        String text = "Empfangsschein / Récépissé / Ricevuta";
        float fontSize = 8f;
        float ascentPoint = formFontBold.getAscentPoint(text, fontSize);
        float textWidth = formFontBold.getWidthPoint(text, fontSize);
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontBold, fontSize);
        over.setTextMatrix(ulx + (Points.ReceiptWidth - textWidth) / 2, uly - Points.Line + (ascentPoint / 2));
        over.showText(text);
        over.endText();
        over.restoreState();

    }

    private void giroTitle() {
        String text1 = "Einzahlung Giro";
        String text2 = "Versement Virement";
        String text3 = "Versamento Girata";

        String spc = "                                 ";

        String text = text1 + spc + text2 + spc + "  " + text3;

        float fontSize = 8;
        float ascentPoint = formFontBold.getAscentPoint(text, fontSize);
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontBold, fontSize);
        over.setTextMatrix((urx - Points.GiroWidth) + Points.Column * 1.5f, uly - Points.Line + (ascentPoint / 2));
        over.showText(text);
        over.endText();
        over.restoreState();

    }

    private void layoutCode() {
        String text = "609";
        float fontSize = 10;
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(codeLineFont, fontSize);
        over.setTextMatrix(lrx - Points.GiroWidth + 4 * Points.Column, lly + 7 * Points.Line);
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
        over.showText(text);
        over.endText();
        over.restoreState();
    }

    private void acceptingOffice() {
        String text1 = "Die Annahmestelle";
        String text2 = "L’office de dépôt";
        String text3 = "L’ufficio d’accettazione";


        float fontSize = 6;
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);

        float ascentPoint = formFontRegular.getAscentPoint(text1, fontSize);
        float offsetY = uly - (21 * Points.Line + ascentPoint / 2);

        over.setTextMatrix(giroUlx - 11 * Points.Column, offsetY);
        over.showText(text1);

        ascentPoint = formFontRegular.getAscentPoint(text2, fontSize);
        offsetY -= (ascentPoint + 1);
        over.setTextMatrix(giroUlx - 11 * Points.Column, offsetY);
        over.showText(text2);

        ascentPoint = formFontRegular.getAscentPoint(text3, fontSize);
        offsetY -= (ascentPoint + 2);
        over.setTextMatrix(giroUlx - 11 * Points.Column, offsetY);
        over.showText(text3);

        over.endText();
        over.restoreState();

    }

    private void onBehalfOf() {
        String text = "Einzahlung für / Versement pour / Versamento per";


        float fontSize = 6f;
        float ascentPoint = formFontRegular.getAscentPoint(text, fontSize);
        float offsetY = uly - (2 * Points.Line - ascentPoint / 2);

        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        over.setColorFill(Colors.BackgroundPlain);

        over.setTextMatrix(ulx + 1 * Points.Column, offsetY);
        over.showText(text);

        over.setTextMatrix(giroUlx + 1 * Points.Column, offsetY);
        over.showText(text);

        over.endText();
        over.restoreState();

    }

    private void noCommunications() {
        String text1 = "Keine Mitteilungen anbringen";
        String text2 = "Pas de communications";
        String text3 = "Non aggiungete comunicazioni";


        float fontSize = 6f;
        float ascentPoint = formFontRegular.getAscentPoint(text1, fontSize);
        float offsetY = uly - (3.5f * Points.Line - ascentPoint / 2);

        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        over.setColorFill(Colors.BackgroundPlain);

        float offX = inFavorOfUlx + 1.5f * Points.Column;
        over.setTextMatrix(offX, offsetY);
        over.showText(text1);

        offsetY -= Points.Line;
        over.setTextMatrix(offX, offsetY);
        over.showText(text2);

        offsetY -= Points.Line;
        over.setTextMatrix(offX, offsetY);
        over.showText(text3);

        over.endText();
        over.restoreState();

    }

    private void printDate() {

        String text = new SimpleDateFormat("MM.YYYY").format(new Date());

        float fontSize = 5f;
        PdfContentByte over = writer.getDirectContent();
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        float ascentPoint = formFontRegular.getAscentPoint(text, fontSize);

        over.setTextMatrix(0, 1, -1, 0, urx - Points.Column - (ascentPoint / 2), ury - 5 * Points.Line);
        over.showText(text);
        over.endText();
        over.restoreState();

    }


    private void orangeReferenceCodeBox() {
        String text = "Referenz-Nr./Nº de référence/Nº di riferimento";
        float fontSize = 5f;

        PdfContentByte under = writer.getDirectContentUnder();
        under.saveState();
        under.setColorStroke(Colors.BackgroundPlain);
        under.setLineWidth(0.4f);
        float boxWidth = 33 * Points.Column;
        float boxHeight = 3 * Points.Column;
        float boxLLY = rightUpperSquareY - (0.5f * Points.Line + boxHeight);
        float boxLLX = inFavorOfUlx + ((urx - inFavorOfUlx) - boxWidth) / 2;
        under.rectangle(boxLLX, boxLLY, boxWidth, boxHeight);
        under.stroke();

        under.beginText();
        under.setFontAndSize(formFontRegular, fontSize);
        under.setColorFill(Colors.BackgroundPlain);
        float ascentPoint = formFontRegular.getAscentPoint(text, fontSize);
        float textWidth = formFontRegular.getWidthPoint(text, fontSize);
        float textX = boxLLX + (boxWidth - textWidth) / 2;
        float textY = boxLLY + boxHeight - ascentPoint / 2;
        under.setTextMatrix(textX, textY);
        under.showText(text);
        under.endText();

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

            fillA5Model(); //TODO: remove

            whiteSquare();
            borderLines();
            solidTopHorizontalLine();
            dottedSeparatorVerticalLine();
            solidSeparatorVerticalLine();
            solidInFavorOfSeparatorVerticalLine();
            solidHorizontalRightLine();
            solidVerticalCircleSquareLine();
            topRightCircle();
            bottomLeftCircle();
            positioningEdges();

            receiptTitle();
            giroTitle();
            layoutCode();
            acceptingOffice();
            printDate();
            onBehalfOf();
            noCommunications();

            orangeReferenceCodeBox();

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
