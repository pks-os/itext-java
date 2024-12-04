package com.itextpdf.signatures;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.mocks.MockResourceRetriever;
import com.itextpdf.styledxmlparser.resolver.resource.IResourceRetriever;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IssuingCertificateRetrieverTest extends ExtendedITextTest {

    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    @Test
    public void testResourceRetrieverUsage() throws CertificateException, IOException {

        Certificate[] cert = PemFileHelper.readFirstChain(CERTS_SRC + "intermediate.pem");
        final List<URL> urlsCalled = new ArrayList<>();

        MockResourceRetriever mockRetriever = new MockResourceRetriever();
        mockRetriever.onGetInputStreamByUrl(u ->  {
            urlsCalled.add(u);
            try {
                return FileUtil.getInputStreamForFile(CERTS_SRC + "root.pem");
            } catch (IOException e) {
                throw new RuntimeException("Error reading certificate.", e);
            }
        });
        ValidatorChainBuilder builder = new ValidatorChainBuilder().withResourceRetriever(() -> mockRetriever);
        builder.getCertificateRetriever().retrieveIssuerCertificate(cert[0]);

        Assertions.assertEquals(1, urlsCalled.size());
        Assertions.assertEquals("http://test.example.com/example-ca/certs/ca/ca.crt", urlsCalled.get(0).toString());

    }
}