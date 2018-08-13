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
package se.inera.intyg.infra.monitoring;

import java.util.List;
import org.springframework.stereotype.Controller;
import se.inera.intyg.infra.monitoring.annotation.PrometheusTimeMethod;

@Controller
public class TestController {

    public static final String SAMPLE_NAME = "service_method_duration_seconds";

    @PrometheusTimeMethod(name = SAMPLE_NAME, help = "API endpoint for test")
    public void named() throws InterruptedException {
        Thread.currentThread().sleep(10);
    }

    @PrometheusTimeMethod(name = SAMPLE_NAME, help = "API endpoint for test")
    public void named2() throws InterruptedException {
        Thread.currentThread().sleep(10);
    }

    @PrometheusTimeMethod
    public void unnamed(String s, List<?> l) throws InterruptedException {
    }
}
