/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.svg.processors.impl;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.font.FontSet;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class SvgProcessorResultUnitTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void contextParameterCannotBeNullTest() {
        Map<String, ISvgNodeRenderer> namedObjects = new HashMap<>();
        ISvgNodeRenderer root = new SvgTagSvgNodeRenderer();

        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(SvgExceptionMessageConstant.PARAMETER_CANNOT_BE_NULL);

        SvgProcessorResult result = new SvgProcessorResult(namedObjects, root, null);
    }

    @Test
    public void getFontProviderTest() {
        Map<String, ISvgNodeRenderer> namedObjects = new HashMap<>();
        ISvgNodeRenderer root = new SvgTagSvgNodeRenderer();
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        SvgProcessorResult result = new SvgProcessorResult(namedObjects, root, context);
        FontProvider fontProviderFromResult = result.getFontProvider();
        Assert.assertNotNull(fontProviderFromResult);
        Assert.assertSame(context.getFontProvider(), fontProviderFromResult);
    }

    @Test
    public void getTempFontsTest() throws IOException {
        Map<String, ISvgNodeRenderer> namedObjects = new HashMap<>();
        ISvgNodeRenderer root = new SvgTagSvgNodeRenderer();
        SvgProcessorContext context = new SvgProcessorContext(new SvgConverterProperties());
        FontProgram fp = FontProgramFactory.createFont(StandardFonts.HELVETICA);
        context.addTemporaryFont(fp, PdfEncodings.IDENTITY_H, "");
        SvgProcessorResult result = new SvgProcessorResult(namedObjects, root, context);
        FontSet tempFontsFromResult = result.getTempFonts();
        Assert.assertNotNull(tempFontsFromResult);
        Assert.assertSame(context.getTempFonts(), tempFontsFromResult);
    }
}