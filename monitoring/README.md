# Monitoring

Monitoring används för att instrumentera API-tjänster och logga på ett gemensamt sätt på openshift-plattformen, och då handlar det främst om:

* Instrumentering och API-tjänst för Prometheus.
* Strömmande koggning till stdout där loggarna slutligen konsolideras till Elasticsearch.

### Konfiguration

Monitoring innehåller för närvarande en _MonitoringConfiguration_ som initialiserar och ger åtkomst till funktionerna.

Det finns två servlets (@Bean) som respektive openshift applikation ska använda.

* `logMDCServletFilter` - sätter upp log-kontext med trace-id etc. Läggs först i filterkedjan för att omfatta alla log event i ett anrop.
* `metricsServlet` -  REST-tjänst för monitorering med prometheus. Konventionen är att registreras denna med path `/metrics`

Se även statistik för användning.

För loggning finns en bas-konfiguration som lämpligen inkluderas i applikationens logback.xml fil, exempel:

```$xml

    <!-- APP NAME is used in log records and shall be defined -->
   <property name="APP_NAME" value="test-app"/>

    <!-- Include common openshift logback stuff with formats and context info etc. -->
    <include resource="logback-ocp-base.xml"/>
    
    <!-- Map application stuff to appenders -->

    <logger name="se.inera.intyg.infra.monitoring.logging.LogbackTest" level="INFO">
        <appender-ref ref="PROCESS" />
    </logger>

</configuration>

```

`logback-ocp-base.xml` har för tillfället följande appenders:

* CONSOLE
* MONITORING (har särskild Marker filter med namn "Monitoring")
* PROCESS
* ACCESS
* FRONTEND








