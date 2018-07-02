/*
 * Copyright (C) 2018 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.monitoring.annotation;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import se.inera.intyg.infra.monitoring.MonitoringConfiguration;
import se.inera.intyg.infra.monitoring.TestController;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MonitoringConfiguration.class, TestController.class})
public class TimeMethodTest {

    CollectorRegistry registry = CollectorRegistry.defaultRegistry;

    @Autowired
    TestController testController;

    @Test
    public void instrumented_named_method() throws InterruptedException {
        this.testController.named();

        final Optional<Collector.MetricFamilySamples> sample = Collections.list(registry.metricFamilySamples()).stream()
                .filter(s -> TestController.SAMPLE_NAME.equals(s.name))
                .findFirst();

        assertTrue(sample.isPresent());
        assertFalse(sample.get().samples.isEmpty());
        assertNotNull(sample.get().help);
    }

    @Test
    public void instrumented_unnamed_method() throws InterruptedException {
        this.testController.unnamed("", Collections.EMPTY_LIST);

        final Optional<Collector.MetricFamilySamples> sample = Collections.list(registry.metricFamilySamples()).stream()
                .filter(s -> s.name.startsWith("api_"))
                .findFirst();

        assertTrue(sample.isPresent());
        assertFalse(sample.get().samples.isEmpty());
        assertNotNull(sample.get().help);
    }

    @Test
    public void sig_to_key() {
        assertEquals("api_infra_monitoring_TestController_testMethod_calls", MethodTimer.signatureToKeyName(TestController.class.getName(),
                "testMethod", new Object[0]));

        assertEquals("api_infra_monitoring_TestController_testMethod_arg1_calls", MethodTimer.signatureToKeyName(TestController.class.getName(),
                "testMethod", new Object[] { "arg" }));
    }
}
