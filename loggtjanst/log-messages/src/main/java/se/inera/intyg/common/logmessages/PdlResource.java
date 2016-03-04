package se.inera.intyg.common.logmessages;

import java.io.Serializable;

/**
 * Defines a single PDL logged "resource", e.g. which patient on which care unit that was logged and what
 * type of information ({@link ResourceType}) that was logged about the patient.
 *
 * Created by eriklupander on 2016-03-02.
 */
public class PdlResource implements Serializable {

    private Patient patient;
    private String resourceType;
    private Enhet resourceOwner;

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Enhet getResourceOwner() {
        return resourceOwner;
    }

    public void setResourceOwner(Enhet resourceOwner) {
        this.resourceOwner = resourceOwner;
    }
}
