package com.noqapp.service.report.pdf;

import com.noqapp.common.utils.DateUtil;
import com.noqapp.common.utils.FileUtil;
import com.noqapp.common.utils.RandomString;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * hitender
 * 9/28/20 2:23 PM
 */
public abstract class PdfBoxBase {
    private static final Logger LOG = LoggerFactory.getLogger(PdfBoxBase.class);

    private int defaultFontSize = 11;
    private PDFont defaultFont = PDType1Font.HELVETICA;

    public final static float PADDING_BOTTOM_OF_DOCUMENT = 30f;
    public final static float PADDING_TOP_OF_DOCUMENT = 30f;

    public PDDocument createPDF(PDRectangle pdRectangle, String title) {
        try {
            // Create a document and add a page to it
            PDDocument document = new PDDocument();
            document.setDocumentInformation(populatePDDocumentationInformation(title));
            document.protect(addSecurity());

            PDPage page = populatePage(pdRectangle, document);
            populateReport(document, page);
            addFooter(document);
            return document;
        } catch (IOException e) {
            LOG.error("Error PDF {}", e.getLocalizedMessage(), e);
            return null;
        }
    }

    private void addFooter(PDDocument pdDocument) throws IOException {
        PDPageContentStream footerContentStream;
        int count = 1;

        PDPageTree pdPageTree = pdDocument.getDocumentCatalog().getPages();
        for (PDPage pdPage : pdPageTree) {
            footerContentStream = new PDPageContentStream(pdDocument, pdPage, PDPageContentStream.AppendMode.APPEND, true);
            footerContentStream.beginText();
            footerContentStream.setFont(defaultFont, defaultFontSize - 1);

            footerContentStream.newLineAtOffset((PDRectangle.A4.getUpperRightX() / 7), (PDRectangle.A4.getLowerLeftY() + PADDING_BOTTOM_OF_DOCUMENT));
            footerContentStream.showText("NoQueue ® ©");

            footerContentStream.newLineAtOffset((PDRectangle.A4.getUpperRightX() / 3), (PDRectangle.A4.getLowerLeftY()));
            footerContentStream.showText((count) + " - " + pdPageTree.getCount());

            footerContentStream.newLineAtOffset((PDRectangle.A4.getUpperRightX()) / 5, (PDRectangle.A4.getLowerLeftY()));
            footerContentStream.showText("Confidential. " + DateUtil.dateToString(new Date()));

            footerContentStream.endText();
            footerContentStream.close();

            count++;
        }
    }

    public StandardProtectionPolicy addSecurity() {
        // Define the length of the encryption key.
        // Possible values are 40 or 128 (256 will be available in PDFBox 2.0).
        int keyLength = 256;

        AccessPermission ap = new AccessPermission();

        // Disable printing, everything else is allowed
        ap.setCanPrint(true);
        ap.setCanModify(false);
        ap.setCanExtractContent(false);
        ap.setCanExtractForAccessibility(false);

        // Owner password (to open the file with all permissions) is "12345"
        // User password (to open the file but with restricted permissions, is empty here)
        StandardProtectionPolicy spp = new StandardProtectionPolicy(RandomString.newInstance(10).nextString(), "", ap);
        spp.setEncryptionKeyLength(keyLength);
        spp.setPermissions(ap);

        return spp;
    }

    public PDDocumentInformation populatePDDocumentationInformation(String title) {
        PDDocumentInformation pdDocumentInformation = new PDDocumentInformation();
        pdDocumentInformation.setAuthor("NoQueue");
        pdDocumentInformation.setCreator("noqapp.com");
        pdDocumentInformation.setTitle(title);
        pdDocumentInformation.setCreationDate(Calendar.getInstance());
        pdDocumentInformation.setProducer("NoQueue");

        return pdDocumentInformation;
    }

    private PDPage populatePage(PDRectangle pdRectangle, PDDocument document) {
        PDPage page = new PDPage(pdRectangle);
        //To switch page
        //page.setMediaBox(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()));
        document.addPage(page);
        return page;
    }

    public File generatePDF(PDDocument document) throws IOException {
        // Save the results and ensure that the document is properly closed:
        File pdf = FileUtil.createTempFile(FileUtil.createRandomFilenameOf16Chars(), "pdf");
        document.save(pdf);
        document.close();

        return pdf;
    }

    abstract void populateReport(PDDocument document, PDPage page);

    public void markAsBlankPage(PDDocument document, PDPage page) throws IOException {
        // Start a new content stream which will "hold" the to be created content
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Define a text content stream using the select ted font, moving the cursor and drawing the text "Hello World"
        contentStream.beginText();
        contentStream.setFont(defaultFont, defaultFontSize);
        contentStream.newLineAtOffset(100, 700);
        contentStream.showText("Blank Page");
        contentStream.endText();

        // Make sure that the content stream is closed:
        contentStream.close();
    }
}
