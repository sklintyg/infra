package se.inera.intyg.infra.certificate.builder;

import java.time.LocalDateTime;
import java.util.List;
import se.inera.intyg.infra.certificate.dto.DiagnosedCertificate;

public class DiagnosedCertificateBuilder {

    DiagnosedCertificate certificate;

    public DiagnosedCertificateBuilder(String certificateId) {
        this.certificate = new DiagnosedCertificate();
        certificate.setCertificateId(certificateId);
    }

    public DiagnosedCertificateBuilder certificateType(String certificateType) {
        certificate.setCertificateType(certificateType);
        return this;
    }

    public DiagnosedCertificateBuilder personId(String civicRegistrationNumber) {
        certificate.setPersonId(civicRegistrationNumber);
        return this;
    }

    public DiagnosedCertificateBuilder patientFullName(String patientFullName) {
        certificate.setPatientFullName(patientFullName);
        return this;
    }

    public DiagnosedCertificateBuilder careProviderId(String careProviderId) {
        certificate.setCareProviderId(careProviderId);
        return this;
    }

    public DiagnosedCertificateBuilder careUnitId(String careUnitId) {
        certificate.setCareUnitId(careUnitId);
        return this;
    }

    public DiagnosedCertificateBuilder careUnitName(String careUnitName) {
        certificate.setCareUnitName(careUnitName);
        return this;
    }

    public DiagnosedCertificateBuilder personalHsaId(String personalHsaId) {
        certificate.setPersonalHsaId(personalHsaId);
        return this;
    }

    public DiagnosedCertificateBuilder signingDoctorName(String signingDoctorName) {
        certificate.setPersonalFullName(signingDoctorName);
        return this;
    }

    public DiagnosedCertificateBuilder signingDateTime(LocalDateTime signingDateTime) {
        certificate.setSigningDateTime(signingDateTime);
        return this;
    }

    public DiagnosedCertificateBuilder diagnoseCode(String diagnoseCode) {
        certificate.setDiagnoseCode(diagnoseCode);
        return this;
    }

    public DiagnosedCertificateBuilder secondaryDiagnoseCodes(List<String> diagnoseCodes) {
        certificate.setSecondaryDiagnoseCodes(diagnoseCodes);
        return this;
    }

    public DiagnosedCertificateBuilder deleted(boolean deleted) {
        certificate.setDeleted(deleted);
        return this;
    }

    public DiagnosedCertificateBuilder testCertificate(boolean isTestCertificate) {
        certificate.setTestCertificate(isTestCertificate);
        return this;
    }

    public DiagnosedCertificate build() {
        return certificate;
    }


}
