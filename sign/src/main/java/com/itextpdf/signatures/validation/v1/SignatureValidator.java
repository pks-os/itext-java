/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.IssuingCertificateRetriever;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Validator class, which is expected to be used for signatures validation.
 */
class SignatureValidator {
    static final String TIMESTAMP_VERIFICATION = "Timestamp verification check.";
    static final String SIGNATURE_VERIFICATION = "Signature verification check.";
    static final String CERTS_FROM_DSS = "Certificates from DSS check.";
    static final String CANNOT_PARSE_CERT_FROM_DSS =
            "Certificate {0} stored in DSS dictionary cannot be parsed.";
    static final String CANNOT_VERIFY_SIGNATURE = "Signature {0} cannot be mathematically verified.";
    static final String DOCUMENT_IS_NOT_COVERED = "Signature {0} doesn't cover entire document.";
    static final String CANNOT_VERIFY_TIMESTAMP = "Signature timestamp attribute cannot be verified";

    private final PdfDocument document;
    private final ValidationContext baseValidationContext;
    private final CertificateChainValidator certificateChainValidator;
    private final IssuingCertificateRetriever certificateRetriever;
    private final SignatureValidationProperties properties;

    /**
     * Create new instance of {@link SignatureValidator}.
     *
     * @param builder See {@link  ValidatorChainBuilder}
     */
    SignatureValidator(PdfDocument document, ValidatorChainBuilder builder) {
        this.document = document;
        this.certificateRetriever = builder.getCertificateRetriever();
        this.properties = builder.getProperties();
        this.certificateChainValidator = builder.getCertificateChainValidator();
        this.baseValidationContext = new ValidationContext(ValidatorContext.SIGNATURE_VALIDATOR,
                CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    }

    /**
     * Validate the latest signature in the document.
     *
     * @return {@link ValidationReport} which contains detailed validation results
     */
    public ValidationReport validateLatestSignature() {
        ValidationReport validationReport = new ValidationReport();
        PdfPKCS7 pkcs7 = mathematicallyVerifySignature(validationReport);
        if (stopValidation(validationReport, baseValidationContext)) {
            return validationReport;
        }

        List<Certificate> certificatesFromDss = getCertificatesFromDss(validationReport);
        certificateRetriever.addKnownCertificates(certificatesFromDss);

        if (pkcs7.isTsp()) {
            return validateTimestampChain(validationReport, pkcs7.getCertificates(), pkcs7.getSigningCertificate());
        }

        Date signingDate = DateTimeUtil.getCurrentTimeDate();
        if (pkcs7.getTimeStampTokenInfo() != null) {
            try {
                if (!pkcs7.verifyTimestampImprint()) {
                    validationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION, CANNOT_VERIFY_TIMESTAMP,
                            ReportItemStatus.INVALID));
                }
            } catch (GeneralSecurityException e) {
                validationReport.addReportItem(new ReportItem(TIMESTAMP_VERIFICATION, CANNOT_VERIFY_TIMESTAMP, e,
                        ReportItemStatus.INVALID));
            }
            if (stopValidation(validationReport, baseValidationContext)) {
                return validationReport;
            }
            Certificate[] timestampCertificates = pkcs7.getTimestampCertificates();
            validateTimestampChain(validationReport, timestampCertificates, (X509Certificate) timestampCertificates[0]);
            if (stopValidation(validationReport, baseValidationContext)) {
                return validationReport;
            }
            signingDate = pkcs7.getTimeStampDate().getTime();
        }

        Certificate[] certificates = pkcs7.getCertificates();
        certificateRetriever.addKnownCertificates(Arrays.asList(certificates));
        X509Certificate signingCertificate = pkcs7.getSigningCertificate();

        return certificateChainValidator.validate(validationReport,
                baseValidationContext,
                signingCertificate, signingDate);
    }

    private PdfPKCS7 mathematicallyVerifySignature(ValidationReport validationReport) {
        SignatureUtil signatureUtil = new SignatureUtil(document);
        List<String> signatures = signatureUtil.getSignatureNames();
        String latestSignatureName = signatures.get(signatures.size() - 1);
        PdfPKCS7 pkcs7 = signatureUtil.readSignatureData(latestSignatureName);
        if (!signatureUtil.signatureCoversWholeDocument(latestSignatureName)) {
            validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION,
                    MessageFormatUtil.format(DOCUMENT_IS_NOT_COVERED, latestSignatureName), ReportItemStatus.INVALID));
        }
        try {
            if (!pkcs7.verifySignatureIntegrityAndAuthenticity()) {
                validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                        CANNOT_VERIFY_SIGNATURE, latestSignatureName), ReportItemStatus.INVALID));
            }
        } catch (GeneralSecurityException e) {
            validationReport.addReportItem(new ReportItem(SIGNATURE_VERIFICATION, MessageFormatUtil.format(
                    CANNOT_VERIFY_SIGNATURE, latestSignatureName), e, ReportItemStatus.INVALID));
        }
        return pkcs7;
    }

    private ValidationReport validateTimestampChain(ValidationReport validationReport, Certificate[] knownCerts,
            X509Certificate signingCert) {
        certificateRetriever.addKnownCertificates(Arrays.asList(knownCerts));
        Date signingDate = DateTimeUtil.getCurrentTimeDate();

        return certificateChainValidator.validate(validationReport,
                baseValidationContext.setCertificateSource(CertificateSource.TIMESTAMP),
                signingCert, signingDate);
    }

    private List<Certificate> getCertificatesFromDss(ValidationReport validationReport) {
        PdfDictionary dss = document.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        List<Certificate> certificatesFromDss = new ArrayList<>();
        if (dss != null) {
            PdfArray certs = dss.getAsArray(PdfName.Certs);
            if (certs != null) {
                for (int i = 0; i < certs.size(); ++i) {
                    PdfStream certStream = certs.getAsStream(i);
                    try {
                        certificatesFromDss.add(CertificateUtil.generateCertificate(
                                new ByteArrayInputStream(certStream.getBytes())));
                    } catch (GeneralSecurityException e) {
                        validationReport.addReportItem(new ReportItem(CERTS_FROM_DSS, MessageFormatUtil.format(
                                CANNOT_PARSE_CERT_FROM_DSS, certStream), e, ReportItemStatus.INFO));
                    }
                }
            }
        }
        return certificatesFromDss;
    }

    private boolean stopValidation(ValidationReport result, ValidationContext validationContext) {
        return !properties.getContinueAfterFailure(validationContext)
                && result.getValidationResult() != ValidationReport.ValidationResult.VALID;
    }
}
