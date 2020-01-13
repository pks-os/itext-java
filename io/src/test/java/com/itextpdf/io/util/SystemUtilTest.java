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
package com.itextpdf.io.util;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class SystemUtilTest extends ExtendedITextTest {

    private final static String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/util/SystemUtilTest/";

    // This is empty file that used to check the logic for existed execution file
    private final static String STUB_EXEC_FILE = SOURCE_FOLDER + "folder with space/stubFile";

    @Test
    public void prepareProcessArgumentsStubExecFileTest() {
        List<String> processArguments = SystemUtil.prepareProcessArguments(STUB_EXEC_FILE, "param1 param2");
        Assert.assertEquals(Arrays.asList(
                "./src/test/resources/com/itextpdf/io/util/SystemUtilTest/folder with space/stubFile", "param1",
                "param2"),
                processArguments);
    }

    @Test
    public void prepareProcessArgumentsStubExecFileInQuotesTest() {
        String testLine = "\"" + STUB_EXEC_FILE + "\"" + " compare";
        List<String> processArguments = SystemUtil.prepareProcessArguments(testLine, "param1 param2");
        Assert.assertEquals(Arrays.asList(
                "./src/test/resources/com/itextpdf/io/util/SystemUtilTest/folder with space/stubFile", "compare",
                "param1", "param2"),
                processArguments);
    }

    @Test
    public void prepareProcessArgumentsGsTest() {
        List<String> processArguments = SystemUtil.prepareProcessArguments("gs", "param1 param2");
        Assert.assertEquals(Arrays.asList(
                "gs", "param1", "param2"),
                processArguments);
    }

    @Test
    public void prepareProcessArgumentsMagickCompareTest() {
        List<String> processArguments = SystemUtil.prepareProcessArguments("magick compare", "param1 param2");
        Assert.assertEquals(Arrays.asList(
                "magick", "compare", "param1", "param2"),
                processArguments);
    }

    @Test
    public void splitIntoProcessArgumentsPathInQuotesTest() {
        List<String> processArguments = SystemUtil
                .splitIntoProcessArguments("\"C:\\Test directory with spaces\\file.exe\"");
        Assert.assertEquals(Collections.singletonList(
                "C:\\Test directory with spaces\\file.exe"),
                processArguments);
    }

    @Test
    public void splitIntoProcessArgumentsGsParamsTest() {
        List<String> processArguments = SystemUtil.splitIntoProcessArguments(
                " -dSAFER -dNOPAUSE -dBATCH -sDEVICE=png16m -r150 -sOutputFile='./target/test/com/itextpdf/kernel/utils/CompareToolTest/cmp_simple_pdf_with_space .pdf-%03d.png' './src/test/resources/com/itextpdf/kernel/utils/CompareToolTest/cmp_simple_pdf_with_space .pdf'");
        Assert.assertEquals(Arrays.asList(
                "-dSAFER", "-dNOPAUSE", "-dBATCH", "-sDEVICE=png16m", "-r150",
                "-sOutputFile=./target/test/com/itextpdf/kernel/utils/CompareToolTest/cmp_simple_pdf_with_space .pdf-%03d.png",
                "./src/test/resources/com/itextpdf/kernel/utils/CompareToolTest/cmp_simple_pdf_with_space .pdf"),
                processArguments);
    }

    @Test
    public void splitIntoProcessArgumentsMagickCompareParamsTest() {
        List<String> processArguments = SystemUtil.splitIntoProcessArguments(
                "'D:\\itext\\java\\itextcore\\kernel\\.\\target\\test\\com\\itextpdf\\kernel\\utils\\CompareToolTest\\simple_pdf.pdf-001.png' 'D:\\itext\\java\\itextcore\\kernel\\.\\target\\test\\com\\itextpdf\\kernel\\utils\\CompareToolTest\\cmp_simple_pdf_with_space .pdf-001.png' './target/test/com/itextpdf/kernel/utils/CompareToolTest/diff_simple_pdf.pdf_1.png'");
        Assert.assertEquals(Arrays.asList(
                "D:\\itext\\java\\itextcore\\kernel\\.\\target\\test\\com\\itextpdf\\kernel\\utils\\CompareToolTest\\simple_pdf.pdf-001.png",
                "D:\\itext\\java\\itextcore\\kernel\\.\\target\\test\\com\\itextpdf\\kernel\\utils\\CompareToolTest\\cmp_simple_pdf_with_space .pdf-001.png",
                "./target/test/com/itextpdf/kernel/utils/CompareToolTest/diff_simple_pdf.pdf_1.png"),
                processArguments);
    }

    @Test
    // There is no similar test in the C# version, since no way was found to test the Process class.
    public void printProcessErrorsOutputTest() throws IOException {
        StringBuilder stringBuilder = SystemUtil.printProcessErrorsOutput(new TestProcess());
        Assert.assertEquals("This is error info", stringBuilder.toString());
    }

    @Test
    // There is no similar test in the C# version, since no way was found to test the Process class.
    public void getProcessOutputTest() throws IOException {
        String result = SystemUtil.getProcessOutput(new TestProcess());
        Assert.assertEquals("This is process info\n"
                + "This is error info", result);
    }

    @Test
    // There is no similar test in the C# version, since no way was found to test the Process class.
    public void getProcessOutputEmptyTest() throws IOException {
        String result = SystemUtil.getProcessOutput(new EmptyTestProcess());
        Assert.assertEquals("This is error info", result);
    }


    static class TestProcess extends Process {

        @Override
        public OutputStream getOutputStream() {
            return new ByteArrayOutputStream();
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream("This is process info".getBytes());
        }

        @Override
        public InputStream getErrorStream() {
            return new ByteArrayInputStream("This is error info".getBytes());
        }

        @Override
        public int waitFor() {
            return 0;
        }

        @Override
        public int exitValue() {
            return 0;
        }

        @Override
        public void destroy() {

        }
    }

    static class EmptyTestProcess extends TestProcess {

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(new byte[0]);
        }
    }
}
