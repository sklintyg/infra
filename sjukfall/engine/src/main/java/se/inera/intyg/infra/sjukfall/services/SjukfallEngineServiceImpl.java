/*
 * Copyright (C) 2017 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.sjukfall.services;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.sjukfall.dto.DiagnosKod;
import se.inera.intyg.infra.sjukfall.dto.Formaga;
import se.inera.intyg.infra.sjukfall.dto.IntygData;
import se.inera.intyg.infra.sjukfall.dto.IntygParametrar;
import se.inera.intyg.infra.sjukfall.dto.Lakare;
import se.inera.intyg.infra.sjukfall.dto.Patient;
import se.inera.intyg.infra.sjukfall.dto.Sjukfall;
import se.inera.intyg.infra.sjukfall.dto.Vardenhet;
import se.inera.intyg.infra.sjukfall.dto.Vardgivare;
import se.inera.intyg.infra.sjukfall.engine.AktivtIntyg;
import se.inera.intyg.infra.sjukfall.engine.AktivtIntygResolver;
import se.inera.intyg.infra.sjukfall.engine.SjukfallLangdCalculator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Magnus Ekstrand on 2017-02-10.
 */
@Service("sjukfallEngineService")
public class SjukfallEngineServiceImpl implements SjukfallEngineService {

    private static final Logger LOG = LoggerFactory.getLogger(SjukfallEngineServiceImpl.class);

    protected Clock clock;

    private AktivtIntygResolver resolver = new AktivtIntygResolver();

    public SjukfallEngineServiceImpl() {
        clock = Clock.system(ZoneId.of("Europe/Paris"));
    }


    // api

    @Override
    public List<Sjukfall> beraknaSjukfall(List<IntygData> intygData, IntygParametrar parameters) {
        LOG.debug("Start calculation of sjukfall...");

        int maxIntygsGlapp = parameters.getMaxIntygsGlapp();
        LocalDate activeDate = parameters.getAktivtDatum();

        Map<String, List<AktivtIntyg>> resolvedIntygsData =
                resolver.resolve(intygData, maxIntygsGlapp, activeDate);

        // Assemble Sjukfall objects
        List<Sjukfall> result = assemble(resolvedIntygsData, parameters);

        LOG.debug("...stop calculation of sjukfall.");
        return result;
    }


    // package scope

    DiagnosKod getDiagnosKod(IntygData intyg) {
        return new DiagnosKod(intyg.getDiagnosKod());
    }

    Sjukfall buildSjukfall(List<AktivtIntyg> values, AktivtIntyg aktivtIntyg, LocalDate aktivtDatum) {
        Sjukfall sjukfall = new Sjukfall();
        sjukfall.setVardgivare(getVardgivare(aktivtIntyg));
        sjukfall.setVardenhet(getVardenhet(aktivtIntyg));
        sjukfall.setLakare(getLakare(aktivtIntyg));
        sjukfall.setPatient(getPatient(aktivtIntyg));
        sjukfall.setDiagnosKod(getDiagnosKod(aktivtIntyg));
        sjukfall.setStart(getMinimumDate(values));
        sjukfall.setSlut(getMaximumDate(values));
        sjukfall.setDagar(SjukfallLangdCalculator.getEffectiveNumberOfSickDays(values));
        sjukfall.setIntyg(values.size());
        sjukfall.setGrader(getGrader(aktivtIntyg.getFormagor()));
        sjukfall.setAktivGrad(getAktivGrad(aktivtIntyg.getFormagor(), aktivtDatum));

        return sjukfall;
    }


    // private scope

    private Patient getPatient(IntygData intyg) {
        String id = StringUtils.trim(intyg.getPatientId());
        String namn = intyg.getPatientNamn();

        return new Patient(id, namn);
    }

    private List<Sjukfall> assemble(Map<String, List<AktivtIntyg>> resolvedIntygsData, IntygParametrar parameters) {
        LOG.debug("  - Assembling 'sjukfall'");

        return resolvedIntygsData.entrySet().stream()
                .map(e -> toSjukfall(e.getValue(), parameters.getAktivtDatum()))
                .collect(Collectors.toList());
    }

    private Sjukfall toSjukfall(List<AktivtIntyg> list, LocalDate aktivtDatum) {
        // Find the active object
        AktivtIntyg aktivtIntyg = list.stream()
                .filter(o -> o.isAktivtIntyg())
                .findFirst()
                .orElseThrow(() -> new SjukfallEngineServiceException("Unable to find a 'aktivt intyg'"));

        // Build Sjukfall object
        return buildSjukfall(list, aktivtIntyg, aktivtDatum);
    }

    private Vardgivare getVardgivare(AktivtIntyg aktivtIntyg) {
        return new Vardgivare(aktivtIntyg.getVardgivareId(), aktivtIntyg.getVardgivareNamn());
    }

    private Vardenhet getVardenhet(AktivtIntyg aktivtIntyg) {
        return new Vardenhet(aktivtIntyg.getVardenhetId(), aktivtIntyg.getVardenhetNamn());
    }

    private Lakare getLakare(AktivtIntyg aktivtIntyg) {
        return new Lakare(aktivtIntyg.getLakareId(), aktivtIntyg.getLakareNamn());
    }

    private Integer getAktivGrad(List<Formaga> list, LocalDate aktivtDatum) {
        LOG.debug("  - Lookup 'aktiv grad'");
        return list.stream()
                .filter(f -> f.getStartdatum().compareTo(aktivtDatum) < 1 && f.getSlutdatum().compareTo(aktivtDatum) > -1)
                .findFirst()
                .orElseThrow(() -> new SjukfallEngineServiceException("Unable to find an active 'arbetsförmåga'"))
                .getNedsattning();
    }

    private List<Integer> getGrader(List<Formaga> list) {
        LOG.debug("  - Lookup all 'aktiva grader'");
        return list.stream()
                .sorted((f1, f2) -> f1.getStartdatum().compareTo(f2.getStartdatum()))
                .map(f -> f.getNedsattning()).collect(Collectors.toList());
    }

    private LocalDate getMinimumDate(List<AktivtIntyg> list) {
        return list.stream().min((d1, d2) -> d1.getStartDatum().compareTo(d2.getStartDatum())).get().getStartDatum();
    }

    private LocalDate getMaximumDate(List<AktivtIntyg> list) {
        return list.stream().max((d1, d2) -> d1.getSlutDatum().compareTo(d2.getSlutDatum())).get().getSlutDatum();
    }

}
