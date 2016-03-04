package se.inera.intyg.common.logmessages;

/**
 * Kan vara kemlabbsvar, journaltext, remiss, översikt, samtycke, patientrelation, sätta spärr, rapport,
 * Översikt sjukskrivning osv.
 *
 * Created by eriklupander on 2016-03-02.
 */
public enum ResourceType {
    RESOURCE_TYPE_INTYG("Intyg"),
    RESOURCE_TYPE_OVERSIKT_SJUKFALL("Översikt sjukskrivning (diagnos, till- och fråndatum, sjukskrivningsgrad, läkare)");

    private final String resourceTypeName;

    ResourceType(String resourceTypeName) {
        this.resourceTypeName = resourceTypeName;
    }

    public String getResourceTypeName() {
        return resourceTypeName;
    }
}
