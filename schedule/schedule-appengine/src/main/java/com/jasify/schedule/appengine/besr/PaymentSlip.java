package com.jasify.schedule.appengine.besr;

import com.google.code.appengine.awt.Color;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krico
 * @since 24/07/15.
 */
public class PaymentSlip {
    public static final String OCR_B_TRUE_TYPE = "BESR/fonts/OCR-B1.ttf";
    public static final String GROTESK_BOLD_TRUE_TYPE = "BESR/fonts/AlteHaasGroteskBold.ttf";
    public static final String GROTESK_REGULAR_TRUE_TYPE = "BESR/fonts/micross.ttf";
    //A5 (210 x 148mm)
    private static final Logger log = LoggerFactory.getLogger(PaymentSlip.class);

    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^([0-9]{1,9})([0-9][0-9])$");

    private final String account;
    private final String codeLine;
    private final String referenceCode;
    private final String currency;
    private final String recipient;
    private final String amount;

    private Document document;
    private PdfWriter writer;
    private PdfContentByte over;
    private PdfContentByte under;
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
    private float referenceCodeBoxLLX;
    private BaseFont codeLineFont;
    private BaseFont formFontBold;
    private BaseFont formFontRegular;

    PaymentSlip(PaymentSlipBuilder builder) {
        account = builder.account;
        codeLine = builder.codeLine;
        referenceCode = builder.referenceCode;
        currency = builder.currency;
        recipient = builder.recipient;
        amount = builder.amount;

    }

    String getCodeLine() {
        return codeLine;
    }

    String getReferenceCode() {
        return referenceCode;
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
        under = writer.getDirectContentUnder();
        over = writer.getDirectContent();
    }

    private void fillOrangeBackground() {
        under.saveState();
        under.setColorFill(Colors.Background);
        under.rectangle(llx, lly, Points.Width, Points.Height);
        under.fill();
        under.restoreState();
    }

//    private void fillA5Model() {
//        under.saveState();
//        under.setColorFill(Color.GREEN);
//        under.rectangle(llx, lly - 20, 595, 10);
//        under.fill();
//        under.setLineWidth(1);
//        under.setColorStroke(Color.black);
//        under.moveTo(llx, lly - 22);
//        under.lineTo(llx, lly - 8);
//        under.moveTo(llx + 595, lly - 22);
//        under.lineTo(llx + 595, lly - 8);
//        under.stroke();
//        under.restoreState();
//    }

    private void whiteSquare() {
        under.saveState();
        under.setColorFill(Color.WHITE);
        under.rectangle(giroUlx, lly, Points.GiroWidth, Points.Inch);
        under.fill();
        under.restoreState();
    }

    private void borderLines() {
        under.saveState();
        under.setColorStroke(Color.BLACK);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(llx, lly);
        under.lineTo(lrx, lry);
        under.lineTo(urx, ury);
        under.lineTo(ulx, uly);
        under.closePath();
        under.stroke();

        under.setLineWidth(1f);
        under.moveTo(lrx - 4.5f * Points.Column, lry);
        under.lineTo(lrx, lry);
        under.lineTo(lrx, lry + 4.5f * Points.Column);
        under.stroke();
        under.restoreState();
    }

    private void dottedSeparatorVerticalLine() {
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
        under.saveState();
        under.setColorStroke(LineColors.SeparatorVerticalLine);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(giroUlx, uly - Points.Line);
        under.lineTo(giroUlx, uly);
        under.stroke();
        under.restoreState();
    }

    private void solidInFavorOfSeparatorVerticalLine() {
        under.saveState();
        under.setColorStroke(LineColors.InFavorOfSeparatorVerticalLine);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(inFavorOfUlx, lly + Points.Inch);
        under.lineTo(inFavorOfUlx, uly - Points.Line);
        under.stroke();
        under.restoreState();
    }

    private void solidTopHorizontalLine() {
        under.saveState();
        under.setColorStroke(LineColors.TopHorizontalLine);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(ulx, uly - Points.Line);
        under.lineTo(urx, ury - Points.Line);
        under.stroke();
        under.restoreState();
    }

    private void solidHorizontalRightLine() {
        under.saveState();
        under.setColorStroke(LineColors.HorizontalRightLine);
        under.setLineWidth(Points.SolidLineWidth);
        under.moveTo(inFavorOfUlx, rightUpperSquareY);
        under.lineTo(urx, rightUpperSquareY);
        under.stroke();
        under.restoreState();
    }

    private void positioningEdges() {
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
        under.saveState();
        under.setColorStroke(Colors.BackgroundPlain);
        under.setLineWidth(0.4f);
        under.circle(lrx - 6.5f * Points.Column, ury - (2 * Points.Line + 3.5f * Points.Column), 3.5f * Points.Column);
        under.setLineDash(1, 2);
        under.stroke();
        under.restoreState();
    }

    private void bottomLeftCircle() {
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
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontBold, fontSize);
        over.setTextMatrix(ulx + (Points.ReceiptWidth - textWidth) / 2, uly - Points.Line + (ascentPoint / 2));
        over.showTextKerned(text);
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
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontBold, fontSize);
        over.setTextMatrix((urx - Points.GiroWidth) + Points.Column * 1.5f, uly - Points.Line + (ascentPoint / 2));
        over.showTextKerned(text);
        over.endText();
        over.restoreState();

    }

    private void layoutCode() {
        String text = "609";
        float fontSize = 10;
        over.saveState();
        over.beginText();
        over.setFontAndSize(codeLineFont, fontSize);
        over.setTextMatrix(lrx - Points.GiroWidth + 4 * Points.Column, lly + 7 * Points.Line);
        over.showText(text);
        over.endText();
        over.restoreState();
    }

    private void codeLine() {

        String text = codeLine;
        float fontSize = 10;
        float textWidth = codeLineFont.getWidthPoint(text, fontSize);
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
        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);

        float ascentPoint = formFontRegular.getAscentPoint(text1, fontSize);
        float offsetY = uly - (21 * Points.Line + ascentPoint / 2);

        over.setTextMatrix(giroUlx - 11 * Points.Column, offsetY);
        over.showTextKerned(text1);

        ascentPoint = formFontRegular.getAscentPoint(text2, fontSize);
        offsetY -= (ascentPoint + 1);
        over.setTextMatrix(giroUlx - 11 * Points.Column, offsetY);
        over.showTextKerned(text2);

        ascentPoint = formFontRegular.getAscentPoint(text3, fontSize);
        offsetY -= (ascentPoint + 2);
        over.setTextMatrix(giroUlx - 11 * Points.Column, offsetY);
        over.showTextKerned(text3);

        over.endText();
        over.restoreState();

    }

    private void onBehalfOf() {
        String text = "Einzahlung für / Versement pour / Versamento per";


        float fontSize = 6f;
        float ascentPoint = formFontRegular.getAscentPoint(text, fontSize);
        float offsetY = uly - (2 * Points.Line - ascentPoint / 2);

        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        over.setColorFill(Colors.BackgroundPlain);

        over.setTextMatrix(ulx + 1 * Points.Column, offsetY);
        over.showTextKerned(text);

        over.setTextMatrix(giroUlx + 1 * Points.Column, offsetY);
        over.showTextKerned(text);

        over.setColorFill(Color.BLACK);
        over.setFontAndSize(formFontBold, 8f);
        offsetY -= Points.Line - formFontBold.getAscentPoint("T", 8f) / 2;

        for (String line : recipient.split("\n")) {
            offsetY -= Points.Line - formFontBold.getAscentPoint(line, 8f) / 2;
            over.setTextMatrix(ulx + 1 * Points.Column, offsetY);
            over.showText(line);

            over.setTextMatrix(giroUlx + 1 * Points.Column, offsetY);
            over.showText(line);
        }


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

        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        over.setColorFill(Colors.BackgroundPlain);

        float offX = inFavorOfUlx + 1.5f * Points.Column;
        over.setTextMatrix(offX, offsetY);
        over.showTextKerned(text1);

        offsetY -= Points.Line;
        over.setTextMatrix(offX, offsetY);
        over.showTextKerned(text2);

        offsetY -= Points.Line;
        over.setTextMatrix(offX, offsetY);
        over.showTextKerned(text3);

        over.endText();
        over.restoreState();

    }

    private void printDate() {

        String text = new SimpleDateFormat("MM.YYYY").format(new Date());

        float fontSize = 5f;
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
        float fontSize = 5.5f;

        under.saveState();

        float boxWidth = 33 * Points.Column;
        float boxHeight = 3 * Points.Column;
        float boxLLY = rightUpperSquareY - (0.5f * Points.Line + boxHeight);
        referenceCodeBoxLLX = inFavorOfUlx + ((urx - inFavorOfUlx) - boxWidth) / 2;
        float ascentPoint = formFontRegular.getAscentPoint(text, fontSize);
        float textWidth = formFontRegular.getWidthPoint(text, fontSize);
        float textX = referenceCodeBoxLLX + (boxWidth - textWidth) / 2;
        float textY = boxLLY + boxHeight - ascentPoint / 2;

        under.beginText();
        under.setFontAndSize(formFontRegular, fontSize);
        under.setColorFill(Colors.BackgroundPlain);
        under.setTextMatrix(textX, textY);
        under.showTextKerned(text);

        under.endText();

        under.setColorStroke(Colors.BackgroundPlain);
        under.setLineWidth(0.4f);
        under.moveTo(textX - Points.Column, boxLLY + boxHeight);
        under.lineTo(referenceCodeBoxLLX, boxLLY + boxHeight);
        under.lineTo(referenceCodeBoxLLX, boxLLY);
        under.lineTo(referenceCodeBoxLLX + boxWidth, boxLLY);
        under.lineTo(referenceCodeBoxLLX + boxWidth, boxLLY + boxHeight);
        under.lineTo(textX + textWidth + Points.Column, boxLLY + boxHeight);
        under.stroke();


        under.restoreState();

        over.saveState();
        over.beginText();
        over.setColorFill(Color.BLACK);
        over.setFontAndSize(codeLineFont, 10f);
        float referenceTextWidth = codeLineFont.getWidthPoint(referenceCode, 10f);
        float referenceTextHeight = codeLineFont.getAscentPoint(referenceCode, 10f);
        over.setTextMatrix(referenceCodeBoxLLX + (boxWidth - referenceTextWidth) / 2, boxLLY + (boxHeight - referenceTextHeight) / 2);
        over.showText(referenceCode);
        over.endText();
        over.restoreState();

    }

    private void giroPaidBy() {
        String text = "Einbezahlt von / Versé par / Versato da";
        float fontSize = 6f;
        float ascentPoint = formFontRegular.getAscentPoint(text, fontSize);

        float offsetY = uly - (11 * Points.Line - ascentPoint / 2);
        float offsetX = referenceCodeBoxLLX;


        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        over.setColorFill(Colors.BackgroundPlain);

        over.setTextMatrix(offsetX, offsetY);
        over.showTextKerned(text);

        over.endText();
        over.restoreState();
    }

    private void receiptAccount() {
        String text = "Konto / Compte / Conto";
        float fontSize = 6f;
        float ascentPoint = formFontRegular.getAscentPoint(text, fontSize);
        float textWidth = formFontRegular.getWidthPoint(text, fontSize);

        float offsetY = uly - (11 * Points.Line - ascentPoint / 2);
        float offsetX = llx + 1 * Points.Column;


        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        over.setColorFill(Colors.BackgroundPlain);

        over.setTextMatrix(offsetX, offsetY);
        over.showTextKerned(text);


        over.setColorFill(Color.BLACK);
        over.setFontAndSize(formFontBold, 8f);

        over.setTextMatrix(offsetX, offsetY - (Points.Line - ascentPoint));
        over.showTextKerned(currency);

        over.setTextMatrix(offsetX + textWidth + 2 * Points.Column, offsetY);
        over.showTextKerned(account);

        over.endText();
        over.restoreState();
    }

    private void receiptAmountBox() {
        Matcher matcher = AMOUNT_PATTERN.matcher(amount);
        if (!matcher.matches()) {
            throw new IllegalCodeLineException("Bad amount [" + amount + "]");
        }
        String amountWhole = matcher.group(1);
        String amountCents = matcher.group(2);

        float fontSize = 8f;
        float ascentPoint = formFontBold.getAscentPoint(amountWhole, fontSize);
        float amountWholeWidth = formFontBold.getWidthPoint(amountWhole, fontSize);

        float offsetY = uly - (13 * Points.Line - ascentPoint / 2);
        float offsetX = llx + 1 * Points.Column;


        over.saveState();

        over.setColorStroke(Colors.BackgroundPlain);
        over.setLineWidth(1.7f);
        float boxWidth = 16f * Points.Column;
        float boxLLY = uly - 13.25f * Points.Line;
        over.rectangle(offsetX, boxLLY, boxWidth, 1.5f * Points.Line);
        float centBoxLLX = offsetX + 18f * Points.Column;
        over.rectangle(centBoxLLX, boxLLY, 4f * Points.Column, 1.5f * Points.Line);
        over.stroke();

        over.beginText();
        over.setFontAndSize(formFontBold, fontSize);
        over.setColorFill(Color.BLACK);

        over.setTextMatrix(offsetX + boxWidth - Points.Millimeter - amountWholeWidth, offsetY);
        over.showTextKerned(amountWhole);

        over.setTextMatrix(centBoxLLX + Points.Millimeter, offsetY);
        over.showTextKerned(amountCents);

        over.setFontAndSize(formFontBold, 10f);
        over.setTextMatrix(offsetX + boxWidth + 1 * Points.Column - formFontBold.getWidthPoint(".", 10f) / 2, boxLLY);
        over.showText(".");

        over.endText();
        over.restoreState();
    }

    private void giroAmountBox() {
        Matcher matcher = AMOUNT_PATTERN.matcher(amount);
        if (!matcher.matches()) {
            throw new IllegalCodeLineException("Bad amount [" + amount + "]");
        }
        String amountWhole = matcher.group(1);
        String amountCents = matcher.group(2);

        float fontSize = 8f;
        float ascentPoint = formFontBold.getAscentPoint(amountWhole, fontSize);
        float amountWholeWidth = formFontBold.getWidthPoint(amountWhole, fontSize);

        float offsetY = uly - (13 * Points.Line - ascentPoint / 2);
        float offsetX = giroUlx + 1 * Points.Column;


        over.saveState();

        over.setColorStroke(Colors.BackgroundPlain);
        over.setLineWidth(1.7f);
        float boxWidth = 16f * Points.Column;
        float boxLLY = uly - 13.25f * Points.Line;
        over.rectangle(offsetX, boxLLY, boxWidth, 1.5f * Points.Line);
        float centBoxLLX = offsetX + 18f * Points.Column;
        over.rectangle(centBoxLLX, boxLLY, 4f * Points.Column, 1.5f * Points.Line);
        over.stroke();

        over.beginText();
        over.setFontAndSize(formFontBold, fontSize);
        over.setColorFill(Color.BLACK);

        over.setTextMatrix(offsetX + boxWidth - Points.Millimeter - amountWholeWidth, offsetY);
        over.showTextKerned(amountWhole);

        over.setTextMatrix(centBoxLLX + Points.Millimeter, offsetY);
        over.showTextKerned(amountCents);

        over.setFontAndSize(formFontBold, 10f);
        over.setTextMatrix(offsetX + boxWidth + 1 * Points.Column - formFontBold.getWidthPoint(".", 10f) / 2, boxLLY);
        over.showText(".");

        over.endText();
        over.restoreState();
    }

    private void receiptPaidBy() {
        String text = "Einbezahlt von / Versé par / Versato da";
        float fontSize = 6f;

        float offsetY = uly - (14 * Points.Line);
        float offsetX = llx + 1 * Points.Column;


        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        over.setColorFill(Colors.BackgroundPlain);

        over.setTextMatrix(offsetX, offsetY);
        over.showTextKerned(text);

        over.setColorFill(Color.BLACK);
        over.setTextMatrix(offsetX, offsetY - Points.Line);
        over.setFontAndSize(formFontRegular, fontSize);
        over.showText(referenceCode);

        over.endText();
        over.restoreState();
    }

    private void giroAccount() {
        String text = "Konto / Compte / Conto";
        float fontSize = 6f;
        float ascentPoint = formFontRegular.getAscentPoint(text, fontSize);
        float textWidth = formFontRegular.getWidthPoint(text, fontSize);

        float offsetY = uly - (11 * Points.Line - ascentPoint / 2);
        float offsetX = giroUlx + 1 * Points.Column;


        over.saveState();
        over.beginText();
        over.setFontAndSize(formFontRegular, fontSize);
        over.setColorFill(Colors.BackgroundPlain);

        over.setTextMatrix(offsetX, offsetY);
        over.showTextKerned(text);

        over.setColorFill(Color.BLACK);
        over.setFontAndSize(formFontBold, 8f);

        over.setTextMatrix(offsetX, offsetY - (Points.Line - ascentPoint));
        over.showTextKerned(currency);

        over.setTextMatrix(offsetX + textWidth + 2 * Points.Column, offsetY);
        over.showTextKerned(account);

        over.endText();
        over.restoreState();
    }

    public void render(File file) throws IOException, DocumentException {
        log.info("Rendering to file: {}", file);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            render(fos);
        }
        log.info("Rendered to file: {}", file);
    }

    public byte[] renderToBytes() throws IOException, DocumentException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        render(bos);
        return bos.toByteArray();
    }

    private void render(OutputStream bos) throws DocumentException, IOException {
        document = new Document(Points.PageSize);
        writer = PdfWriter.getInstance(document, bos);
        document.open();

        calculate();

        fillOrangeBackground();

//            fillA5Model();

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
        giroPaidBy();
        receiptPaidBy();
        receiptAccount();
        receiptAmountBox();
        giroAccount();
        giroAmountBox();

        codeLine();

        document.close();
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
        float GiroWidthColumns = 59;
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
        float Margin = 0;
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
