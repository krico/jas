package com.jasify.schedule.appengine.http.servlet.pdf;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author krico
 * @since 23/07/15.
 */
public class PdfServlet extends HttpServlet {
    public static final String OCR_B_TRUE_TYPE = "BESR/fonts/OCR-B1.ttf";
    //A5 (210 x 148mm)
    public static final String BESR_A5 = "BESR/images/BESR-A5-RED.jpg";
    private static final Logger log = LoggerFactory.getLogger(PdfServlet.class);

    @Override
    public void init() throws ServletException {
        //Pre-load font into cache
        loadFont(OCR_B_TRUE_TYPE);
    }

    private BaseFont loadFont(String fontName) throws ServletException {
        try {
            return BaseFont.createFont(fontName, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (IOException | DocumentException e) {
            throw new ServletException("Failed to load font", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            Rectangle A5 = PageSize.A5.rotate();
            Document document = new Document(A5, 0, 0, 0, 0);
            PdfWriter writer = PdfWriter.getInstance(document, bos);
            document.open();
            document.resetHeader();


            BaseFont baseFont = loadFont(OCR_B_TRUE_TYPE);
            Image img = Image.getInstance(getClass().getResource("/" + BESR_A5));
            img.scaleToFit(A5.getWidth(), A5.getWidth());
            img.setBorder(0);
            img.setAbsolutePosition((A5.getWidth() - img.getScaledWidth()) / 2, (A5.getHeight() - img.getScaledHeight()) / 2);
            document.add(img);
            PdfContentByte over = writer.getDirectContent();
            over.beginText();
            over.setFontAndSize(baseFont, 8.5f);
            over.setTextMatrix(245,60);
            over.showText("2100000440001>961116900000006600000009284+ 030001625>");
            over.endText();
//            Paragraph statusLine = new Paragraph("2100000440001>961116900000006600000009284+ 030001625>", new Font(baseFont, 8));
//            document.add(statusLine);
            document.close();
        } catch (DocumentException e) {
            throw new ServletException("Failed to generate PDF", e);
        }
        resp.setContentType("application/pdf");
        resp.setContentLength(bos.size());
        resp.getOutputStream().write(bos.toByteArray());
        resp.getOutputStream().flush();
    }
}
