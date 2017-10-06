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
import se.inera.intyg.infra.sjukfall.dto.SjukfallEnhet;
import se.inera.intyg.infra.sjukfall.dto.SjukfallPatient;
import se.inera.intyg.infra.sjukfall.dto.Vardenhet;
import se.inera.intyg.infra.sjukfall.dto.Vardgivare;
import se.inera.intyg.infra.sjukfall.dto.SjukfallIntyg;
import se.inera.intyg.infra.sjukfall.engine.SjukfallIntygEnhetCreator;
import se.inera.intyg.infra.sjukfall.engine.SjukfallIntygEnhetResolver;
import se.inera.intyg.infra.sjukfall.engine.SjukfallIntygPatientCreator;
import se.inera.intyg.infra.sjukfall.engine.SjukfallIntygPatientResolver;
import se.inera.intyg.infra.sjukfall.engine.SjukfallLangdCalculator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
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

    private SjukfallIntygEnhetResolver resolverEnhet;
    private SjukfallIntygPatientResolver resolverPatient;

    public SjukfallEngineServiceImpl() {
        clock = Clock.system(ZoneId.of("Europe/Paris"));
        resolverEnhet = new SjukfallIntygEnhetResolver(new SjukfallIntygEnhetCreator());
        resolverPatient = new SjukfallIntygPatientResolver(new SjukfallIntygPatientCreator());
    }

    // api

    @Override
    public List<SjukfallEnhet> beraknaSjukfallForEnhet(List<IntygData> intygsData, IntygParametrar parameters) {
        LOG.debug("Start calculation of sjukfall for health care unit...");

        int maxIntygsGlapp = parameters.getMaxIntygsGlapp();
        LocalDate aktivtDatum = parameters.getAktivtDatum();

        Map<String, List<SjukfallIntyg>> resolvedIntygsData =
                resolverEnhet.resolve(intygsData, maxIntygsGlapp, aktivtDatum);

        // Assemble SjukfallEnhet objects
        List<SjukfallEnhet> result = assembleSjukfallEnhetList(resolvedIntygsData, aktivtDatum);

        LOG.debug("...stop calculation of sjukfall for health care unit.");
        return result;
    }

    @Override
    public List<SjukfallPatient> beraknaSjukfallForPatient(List<IntygData> intygData, IntygParametrar parameters) {
        LOG.debug("Start calculation of sjukfall for a patient...");

        int maxIntygsGlapp = parameters.getMaxIntygsGlapp();
        LocalDate aktivtDatum = parameters.getAktivtDatum();

        Map<Integer, List<SjukfallIntyg>> resolvedIntygsData =
                resolverPatient.resolve(intygData, maxIntygsGlapp, aktivtDatum);

        // Reverse order since we need the information in descending order

        // Assemble SjukfallPatient objects
        List<SjukfallPatient> result = assembleSjukfallPatientList(resolvedIntygsData, maxIntygsGlapp, aktivtDatum);

        LOG.debug("...stop calculation of sjukfall for a patient.");
        return result;
    }


    // package scope

    SjukfallEnhet buildSjukfallEnhet(List<SjukfallIntyg> values, SjukfallIntyg aktivtIntyg, LocalDate aktivtDatum) {
        SjukfallEnhet sjukfallEnhet = new SjukfallEnhet();
        sjukfallEnhet.setVardgivare(getVardgivare(aktivtIntyg));
        sjukfallEnhet.setVardenhet(getVardenhet(aktivtIntyg));
        sjukfallEnhet.setLakare(getLakare(aktivtIntyg));
        sjukfallEnhet.setPatient(getPatient(aktivtIntyg));
        sjukfallEnhet.setDiagnosKod(aktivtIntyg.getDiagnosKod());
        sjukfallEnhet.setBiDiagnoser(aktivtIntyg.getBiDiagnoser());
        sjukfallEnhet.setStart(getMinimumDate(values));
        sjukfallEnhet.setSlut(getMaximumDate(values));
        sjukfallEnhet.setDagar(SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(values));
        sjukfallEnhet.setIntyg(values.size());
        sjukfallEnhet.setGrader(getGrader(aktivtIntyg.getFormagor()));
        sjukfallEnhet.setAktivGrad(getAktivGrad(aktivtIntyg.getFormagor(), aktivtDatum));

        return sjukfallEnhet;
    }

    SjukfallPatient buildSjukfallPatient(List<SjukfallIntyg> values) {

        Patient patient = getPatient(values.get(0));
        DiagnosKod diagnosKod = resolveDiagnosKod(values);

        SjukfallPatient sjukfallPatient = new SjukfallPatient();
        sjukfallPatient.setPatient(patient);
        sjukfallPatient.setDiagnosKod(diagnosKod);
        sjukfallPatient.setStart(getMinimumDate(values));
        sjukfallPatient.setSlut(getMaximumDate(values));
        sjukfallPatient.setDagar(SjukfallLangdCalculator.getEffectiveNumberOfSickDaysByIntyg(values));
        sjukfallPatient.setSjukfallIntygList(sortIntyg(values));

        return sjukfallPatient;
    }

    private List<SjukfallIntyg> sortIntyg(List<SjukfallIntyg> intyg) {
        // Make sort order descending
        Comparator<SjukfallIntyg> dateComparator
            = Comparator.comparing(SjukfallIntyg::getStartDatum, Comparator.reverseOrder());

        return intyg.stream()
            .sorted(dateComparator)
            .collect(Collectors.toList());
    }

    private DiagnosKod resolveDiagnosKod(List<SjukfallIntyg> intyg) {
        // Rules:
        // 1. If several intyg in list are active, choose the active
        //    intyg with latest signeringsTidpunkt
        // 2. If list doesn't have an active intyg, choose the one
        //    with latest signeringsTidpunkt
        List<SjukfallIntyg> list = intyg.stream().filter(SjukfallIntyg::isAktivtIntyg).collect(Collectors.toList());
        if (list.isEmpty()) {
            list.addAll(intyg);
        }

        return list.stream().max(Comparator.comparing(SjukfallIntyg::getSigneringsTidpunkt)).get().getDiagnosKod();
    }

    // private scope

    private Patient getPatient(SjukfallIntyg sjukfallIntyg) {
        String id = StringUtils.trim(sjukfallIntyg.getPatientId());
        String namn = sjukfallIntyg.getPatientNamn();

        return new Patient(id, namn);
    }

    private List<SjukfallEnhet> assembleSjukfallEnhetList(Map<String, List<SjukfallIntyg>> intygsData, LocalDate aktivtDatum) {
        LOG.debug("  - Assembling 'sjukfall for healt care unit'");

        return intygsData.entrySet().stream()
                .map(e -> toSjukfallEnhet(e.getValue(), aktivtDatum))
                .collect(Collectors.toList());
    }

    private List<SjukfallPatient> assembleSjukfallPatientList(Map<Integer, List<SjukfallIntyg>> intygsData, int maxIntygsGlapp,
                                                              LocalDate aktivtDatum) {
        LOG.debug("  - Assembling 'sjukfall for patient'");

        Comparator<SjukfallPatient> dateComparator
            = Comparator.comparing(SjukfallPatient::getStart, Comparator.reverseOrder());

        // 1. Build sjukfall for patient object
        // 2. Filter out any future sjukfall
        // 3. Sort by start date with descending order
        return intygsData.entrySet().stream()
            .map(e -> buildSjukfallPatient(e.getValue()))
            .filter(value -> aktivtDatum.plusDays(maxIntygsGlapp + 1).isAfter(value.getStart()))
            .sorted(dateComparator)
            .collect(Collectors.toList());
    }

    private SjukfallEnhet toSjukfallEnhet(List<SjukfallIntyg> list, LocalDate aktivtDatum) {
        // 1. Find the active object
        SjukfallIntyg aktivtIntyg = list.stream()
                .filter(o -> o.isAktivtIntyg())
                .findFirst()
                .orElseThrow(() -> new SjukfallEngineServiceException("Unable to find a 'aktivt intyg'"));

        // 2. Build sjukfall for enhet object
        return buildSjukfallEnhet(list, aktivtIntyg, aktivtDatum);
    }

    private Vardgivare getVardgivare(SjukfallIntyg sjukfallIntyg) {
        return new Vardgivare(sjukfallIntyg.getVardgivareId(), sjukfallIntyg.getVardgivareNamn());
    }

    private Vardenhet getVardenhet(SjukfallIntyg sjukfallIntyg) {
        return new Vardenhet(sjukfallIntyg.getVardenhetId(), sjukfallIntyg.getVardenhetNamn());
    }

    private Lakare getLakare(SjukfallIntyg sjukfallIntyg) {
        return new Lakare(sjukfallIntyg.getLakareId(), sjukfallIntyg.getLakareNamn());
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
            .sorted(Comparator.comparing(Formaga::getStartdatum))
            .map(Formaga::getNedsattning)
            .collect(Collectors.toList());
    }

    private LocalDate getMinimumDate(List<SjukfallIntyg> list) {
        return list.stream().min(Comparator.comparing(SjukfallIntyg::getStartDatum)).get().getStartDatum();
    }

    private LocalDate getMaximumDate(List<SjukfallIntyg> list) {
        return list.stream().max(Comparator.comparing(SjukfallIntyg::getSlutDatum)).get().getSlutDatum();
    }

}
