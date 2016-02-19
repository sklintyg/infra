package se.inera.intyg.common.logmessages;

import java.io.Serializable;

/**
 * Created by eriklupander on 2016-02-19.
 */
public class IntygDataLogMessage extends AbstractLogMessage implements Serializable {

    private static final long serialVersionUID = -4683928451142580674L;

    public IntygDataLogMessage(String intygId) {
        super(ActivityType.READ, RESOURCE_TYPE_OVERSIKT);
        setActivityLevel(intygId);
    }
}
