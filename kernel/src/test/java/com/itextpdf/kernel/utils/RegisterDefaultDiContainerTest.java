package com.itextpdf.kernel.utils;

import com.itextpdf.commons.datastructures.ISimpleList;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.di.pagetree.IPageTreeListFactory;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
public class RegisterDefaultDiContainerTest extends ExtendedITextTest {

    @Test
    public void test() {
        RegisterDefaultDiContainer registerDefaultDiContainer = new RegisterDefaultDiContainer();
        assertNotNull(registerDefaultDiContainer);
    }

    @Test
    public void testStaticBlock() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        assertTrue(pdfDocument.getDiContainer().getInstance(IPageTreeListFactory.class) instanceof IPageTreeListFactory);
    }


    @Test
    public void testWithOverWriting() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        assertTrue(pdfDocument.getDiContainer().getInstance(IPageTreeListFactory.class) instanceof IPageTreeListFactory);
    }

    @Test
    public void testWithSettingDocumentProps() {
        DocumentProperties documentProperties = new DocumentProperties();
        documentProperties.registerDependency(IPageTreeListFactory.class, new IPageTreeTestImpl());
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()), documentProperties);
        assertTrue(pdfDocument.getDiContainer().getInstance(IPageTreeListFactory.class) instanceof IPageTreeTestImpl);
    }

    @Test
    public void documentPropsSetWithNullInstance() {
        DocumentProperties documentProperties = new DocumentProperties();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            documentProperties.registerDependency(IPageTreeListFactory.class, null);
        });
    }

    @Test
    public void documentPropsSetWithNullType() {
        DocumentProperties documentProperties = new DocumentProperties();
        Object dummyObject = new Object();
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            documentProperties.registerDependency(null, dummyObject);
        });
    }

    static final class IPageTreeTestImpl implements IPageTreeListFactory {

        @Override
        public <T> ISimpleList<T> createList(PdfDictionary pagesDictionary) {
            return null;
        }
    }


}