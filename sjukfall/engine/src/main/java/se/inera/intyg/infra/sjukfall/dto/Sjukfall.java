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
package se.inera.intyg.infra.sjukfall.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Magnus Ekstrand on 2017-02-10.
 */
public class Sjukfall {

    private Lakare lakare;

    private Patient patient;

    private DiagnosKod diagnosKod;

    private LocalDate start;
    private LocalDate slut;

    private int dagar;
    private int intyg;
    private int aktivGrad;

    private List<Integer> grader;


    // getters and setters

    public Lakare getLakare() {
        return lakare;
    }

    public void setLakare(Lakare lakare) {
        this.lakare = lakare;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public DiagnosKod getDiagnosKod() {
        return diagnosKod;
    }

    public void setDiagnosKod(DiagnosKod diagnosKod) {
        this.diagnosKod = diagnosKod;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getSlut() {
        return slut;
    }

    public void setSlut(LocalDate slut) {
        this.slut = slut;
    }

    public int getDagar() {
        return dagar;
    }

    public void setDagar(int dagar) {
        this.dagar = dagar;
    }

    public int getIntyg() {
        return intyg;
    }

    public void setIntyg(int intyg) {
        this.intyg = intyg;
    }

    public int getAktivGrad() {
        return aktivGrad;
    }

    public void setAktivGrad(int aktivGrad) {
        this.aktivGrad = aktivGrad;
    }

    public List<Integer> getGrader() {
        return grader;
    }

    public void setGrader(List<Integer> grader) {
        this.grader = grader;
    }

}