# Monitoring

Monitoring används för att instrumentera API-tjänster och logga på ett gemensamt sätt på openshift-plattformen, och då handlar det främst om:

* Instrumentering och API-tjänst för Prometheus.
* Strömmande koggning till stdout där loggarna slutligen konsolideras till Elasticsearch.

### Konfiguration

Monitoring innehåller för närvarande en _MonitoringConfiguration_ som initialiserar och ger åtkomst till funktionerna. Denna initieras i respektive app-context antingen genom annotering eller xml-konfig (component-scan).  

#### Loggning

Det finns ett servlet filter som respektive openshift applikation ska använda.

* `logMDCServletFilter` - sätter upp log-kontext med trace-id etc. Läggs först i filterkedjan för att omfatta alla log event i ett anrop.

Exempel Java:

```$java
import se.inera.intyg.infra.monitoring.logging.LogMDCServletFilter;

@Autowired
LogMDCServletFilter logMDCServletFilter;

```
Exempel web.xml:

```$xml
<filter>
    <filter-name>LogMDCFilter</filter-name>
    <filter-class>se.inera.intyg.infra.monitoring.logging.LogMDCServletFilter</filter-class>
</filter>

<filter-mapping>
    <filter-name>LogMDCFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```


Se även statistik eller ib-backend applikationerna för användning.

För loggning finns en bas-konfiguration som lämpligen inkluderas i applikationens logback.xml fil, exempel:

```$xml

    <!-- APP NAME is used in log records and shall be defined -->
    <property name="APP_NAME" value="${APP_NAME:-default-app-name}"/>

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
* MONITORING (för särskild `Marker` filter med namn "Monitoring", se exempel nedan)
* PROCESS
* ACCESS
* FRONTEND

För att även stödja trace för jobbkörningar etc. finns även en helper för att starta ett trace-kontext där trace-id kan anges explicit eller implicit.

* `LogMDCHelper` - helper för att få trace vid rekursiva metodanrop.

Exempel Java lambda:

```$java
import se.inera.intyg.infra.monitoring.logging.LogMDCHelper;
import se.inera.intyg.infra.monitoring.logging.MarkerFilter;

@Autowired
LogMDCHelper logMDCHelper;

logMDCHelper.run(() -> {
    LOG.info(MarkerFilter.MONITORING, "some useful stuff");
    // do_something
});
```

#### Monitorering

En `@Controller` metod instrumenteras genom att annotera med `@PrometheusTimeMethod` där namn och hjälptext ska anges.

Exempel:

```$java
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;

@PrometheusTimeMethod(name = "api_invoke", help = "API endpoint for test")
public void invoke() {
	// code
}
```

Namnet måste vara unikt inom applikationen (annars kastas `IllegalArgumentException`). Rekommendationen är att använda `snake_case` och börja med ett prefix som anger om det är api, job etc.

För att exponera en prometheus endpoint används servlet:

* `MetricsServlet` - prometheus endpoint binds lämpligen till path `/metrics`

Exempel Java:

```$java
import io.prometheus.client.exporter.MetricsServlet;

@Autowired
MetricsServlet metricsServlet;
```    

Exempel web.xml:

```$xml
<servlet>
    <servlet-name>metrics</servlet-name>
    <servlet-class>io.prometheus.client.exporter.MetricsServlet</servlet-class>
</servlet>

<servlet-mapping>
    <servlet-name>metrics</servlet-name>
    <url-pattern>/metrics</url-pattern>
</servlet-mapping>
```













