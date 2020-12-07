package se.inera.intyg.infra.integration.hsatk.model;

import lombok.Data;

import javax.xml.datatype.XMLGregorianCalendar;

@Data
public class TimeSpan {
    private String fromDay;
    private XMLGregorianCalendar fromTime;
    private String toDay;
    private XMLGregorianCalendar toTime;
    private String comment;
    private String fromDate;
    private String toDate;
}
