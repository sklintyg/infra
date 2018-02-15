package se.inera.intyg.infra.integration.grp.stub;

import se.funktionstjanster.grp.v1.ProgressStatusType;

public class OngoingGrpSignature {

    private String personalNumber;
    private String orderRef;
    private String transactionId;
    private ProgressStatusType grpSignatureStatus;

    public OngoingGrpSignature() {

    }

    public OngoingGrpSignature(String personalNumber, String orderRef, String transactionId, ProgressStatusType grpSignatureStatus) {
        this.orderRef = orderRef;
        this.transactionId = transactionId;
        this.grpSignatureStatus = grpSignatureStatus;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public ProgressStatusType getGrpSignatureStatus() {
        return grpSignatureStatus;
    }

    public void setGrpSignatureStatus(ProgressStatusType grpSignatureStatus) {
        this.grpSignatureStatus = grpSignatureStatus;
    }
}
