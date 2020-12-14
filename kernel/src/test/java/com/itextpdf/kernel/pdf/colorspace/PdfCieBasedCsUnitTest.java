/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.kernel.pdf.colorspace;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.CalGray;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.CalRgb;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs.Lab;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PdfCieBasedCsUnitTest extends ExtendedITextTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void whitePointOfCalGrayIsIncorrectEmptyTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalGray(new float[] {});
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectEmptyTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalRgb(new float[] {});
    }

    @Test
    public void whitePointOfLabIsIncorrectEmptyTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new Lab(new float[] {});
    }

    @Test
    public void whitePointOfCalGrayIsIncorrectTooLittlePointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalGray(new float[] {1, 2});
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectTooLittlePointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalRgb(new float[] {1, 2});
    }

    @Test
    public void whitePointOfLabIsIncorrectTooLittlePointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new Lab(new float[] {1, 2});
    }

    @Test
    public void whitePointOfCalGrayIsIncorrectTooMuchPointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalGray(new float[] {1, 2, 3, 4});
    }

    @Test
    public void whitePointOfCalRgbIsIncorrectTooMuchPointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new CalRgb(new float[] {1, 2, 3, 4});
    }

    @Test
    public void whitePointOfLabIsIncorrectTooMuchPointsTest() {
        junitExpectedException.expect(PdfException.class);
        junitExpectedException.expectMessage(KernelExceptionMessageConstant.WHITE_POINT_IS_INCORRECTLY_SPECIFIED);

        PdfCieBasedCs basedCs = new Lab(new float[] {1, 2, 3, 4});
    }
}
