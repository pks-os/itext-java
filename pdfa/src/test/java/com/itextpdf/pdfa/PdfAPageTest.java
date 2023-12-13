/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.pdfa;

import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;

@Category(IntegrationTest.class)
public class PdfAPageTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    private static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfAPageTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfALogMessageConstant.PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED)})
    public void checkThatFlushingPreventedWhenAddingElementToDocument() throws IOException {
        // Expected log message that page flushing was not performed
        String outPdf = destinationFolder + "checkThatFlushingPreventedWhenAddingElementToDocument.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));
        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        Document document = new Document(pdfDoc, PageSize.A4);
        for (int i = 1; i < pageCount; i++) {
            // Adding a area break causes a new page to be added and an attempt to flush the page will occur,
            // but flushing these pages will be prevented due to a condition added to the PdfAPage#flush method
            document.add(new AreaBreak());
        }

        // Before closing document have 3 pages, but no one call of end page event
        Assert.assertEquals(pageCount, document.getPdfDocument().getNumberOfPages());
        Assert.assertEquals(0, eventHandler.getCounter());

        document.close();

        // During the closing event was called on each document page
        Assert.assertEquals(pageCount, eventHandler.getCounter());

        Assert.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = PdfALogMessageConstant.PDFA_PAGE_FLUSHING_WAS_NOT_PERFORMED)})
    public void checkThatFlushingPreventedWithFalseFlushResourcesContentStreams() throws IOException {
        // Expected log message that page flushing was not performed
        String outPdf = destinationFolder + "checkThatFlushingPreventedWithFalseFlushResourcesContentStreams.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        for (int i = 0; i < pageCount; i++) {
            pdfDoc.addNewPage().flush(false);
        }

        Assert.assertEquals(pageCount, pdfDoc.getNumberOfPages());
        Assert.assertEquals(0, eventHandler.getCounter());

        pdfDoc.close();

        Assert.assertEquals(pageCount, eventHandler.getCounter());

        Assert.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    public void checkFlushingWhenPdfDocumentIsClosing() throws IOException {
        String outPdf = destinationFolder + "checkFlushingWhenPdfDocumentIsClosing.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        for (int i = 0; i < pageCount; i++) {
            pdfDoc.addNewPage();
        }

        Assert.assertEquals(pageCount, pdfDoc.getNumberOfPages());
        Assert.assertEquals(0, eventHandler.getCounter());

        pdfDoc.close();

        Assert.assertEquals(pageCount, eventHandler.getCounter());

        Assert.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    public void checkFlushingWithTrueFlushResourcesContentStreams() throws IOException {
        String outPdf = destinationFolder + "checkFlushingWithTrueFlushResourcesContentStreams.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        for (int i = 0; i < pageCount; i++) {
            pdfDoc.addNewPage().flush(true);
        }

        Assert.assertEquals(pageCount, pdfDoc.getNumberOfPages());
        Assert.assertEquals(pageCount, eventHandler.getCounter());

        pdfDoc.close();

        Assert.assertEquals(pageCount, eventHandler.getCounter());

        Assert.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    @Test
    public void checkFlushingOfCheckedPage() throws IOException {
        String outPdf = destinationFolder + "checkFlushingOfCheckedPage.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        PdfADocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1",
                        new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm")));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        EndPageEventHandler eventHandler = new EndPageEventHandler();
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, eventHandler);

        int pageCount = 3;
        for (int i = 0; i < pageCount; i++) {
            PdfPage page = pdfDoc.addNewPage();
            pdfDoc.checker.checkSinglePage(page);
            page.flush(false);
        }

        Assert.assertEquals(pageCount, pdfDoc.getNumberOfPages());
        Assert.assertEquals(pageCount, eventHandler.getCounter());

        pdfDoc.close();

        Assert.assertEquals(pageCount, eventHandler.getCounter());

        Assert.assertNull(new VeraPdfValidator().validate(outPdf));
    }

    static class EndPageEventHandler implements IEventHandler {
        private int counter = 0;

        EndPageEventHandler() {
        }

        public int getCounter() {
            return counter;
        }

        @Override
        public void handleEvent(Event event) {
            counter++;
        }
    }
}
