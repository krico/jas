package com.jasify.schedule.appengine.http.servlet.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.BaseFont;
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
            throw new ServletException("Failed to preload font", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            BaseFont baseFont = loadFont(OCR_B_TRUE_TYPE);
            Document document = new Document();
            PdfWriter.getInstance(document, bos);
            document.open();
            document.add(new Paragraph("2100000440001>961116900000006600000009284+ 030001625>", new Font(baseFont)));
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
