package se.inera.intyg.infra.certificate.builder;

import java.time.LocalDateTime;
import java.util.List;
import se.inera.intyg.infra.certificate.dto.SickLeaveCertificate;

public class SickLeaveCertificateBuilder {

    SickLeaveCertificate certificate;

    public SickLeaveCertificateBuilder(String certificateId) {
        this.certificate = new SickLeaveCertificate();
        certificate.setCertificateId(certificateId);
    }

    public SickLeaveCertificateBuilder certificateType(String certificateType) {
        certificate.setCertificateType(certificateType);
        return this;
    }

    public SickLeaveCertificateBuilder personId(String civicRegistrationNumber) {
        certificate.setPersonId(civicRegistrationNumber);
        return this;
    }

    public SickLeaveCertificateBuilder patientFullName(String patientFullName) {
        certificate.setPatientFullName(patientFullName);
        return this;
    }

    public SickLeaveCertificateBuilder careProviderId(String careProviderId) {
        certificate.setCareProviderId(careProviderId);
        return this;
    }

    public SickLeaveCertificateBuilder careUnitId(String careUnitId) {
        certificate.setCareUnitId(careUnitId);
        return this;
    }

    public SickLeaveCertificateBuilder careUnitName(String careUnitName) {
        certificate.setCareUnitName(careUnitName);
        return this;
    }

    public SickLeaveCertificateBuilder personalHsaId(String personalHsaId) {
        certificate.setPersonalHsaId(personalHsaId);
        return this;
    }

    public SickLeaveCertificateBuilder signingDoctorName(String signingDoctorName) {
        certificate.setPersonalFullName(signingDoctorName);
        return this;
    }

    public SickLeaveCertificateBuilder signingDateTime(LocalDateTime signingDateTime) {
        certificate.setSigningDateTime(signingDateTime);
        return this;
    }

    public SickLeaveCertificateBuilder diagnoseCode(String diagnoseCode) {
        certificate.setDiagnoseCode(diagnoseCode);
        return this;
    }

    public SickLeaveCertificateBuilder secondaryDiagnoseCodes(List<String> diagnoseCodes) {
        certificate.setSecondaryDiagnoseCodes(diagnoseCodes);
        return this;
    }

    public SickLeaveCertificateBuilder deleted(boolean deleted) {
        certificate.setDeleted(deleted);
        return this;
    }

    public SickLeaveCertificateBuilder workCapacityList(List<SickLeaveCertificate.WorkCapacity> workCapacities) {
        certificate.setWorkCapacityList(workCapacities);
        return this;
    }

    public SickLeaveCertificateBuilder occupation(String occupation) {
        certificate.setOccupation(occupation);
        return this;
    }

    public SickLeaveCertificateBuilder testCertificate(boolean isTestCertificate) {
        certificate.setTestCertificate(isTestCertificate);
        return this;
    }

    public SickLeaveCertificate build() {
        return certificate;
    }
}
