/*
 * Copyright (C) 2019 Inera AB (http://www.inera.se)
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
package se.inera.intyg.infra.integration.hsa.stub;

import static java.util.Arrays.asList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * @author andreaskaltenbach
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Medarbetaruppdrag {

    public static final String VARD_OCH_BEHANDLING = "Vård och behandling";
    public static final String STATISTIK = "Statistik";

    private String forNamn;
    private String efterNamn;
    private String hsaId;
    private List<Uppdrag> uppdrag;
    private String titel;

    public Medarbetaruppdrag() {
        // Needed for deserialization
    }

    public Medarbetaruppdrag(String hsaId, List<Uppdrag> uppdrag) {
        this.hsaId = hsaId;
        this.uppdrag = uppdrag;
    }

    public Medarbetaruppdrag(String forNamn, String efterNamn, String hsaId, List<Uppdrag> uppdrag) {
        this.forNamn = forNamn;
        this.efterNamn = efterNamn;
        this.hsaId = hsaId;
        this.uppdrag = uppdrag;
    }

    public Medarbetaruppdrag(String forNamn, String efterNamn, String hsaId, List<Uppdrag> uppdrag, String titel) {
        this.forNamn = forNamn;
        this.efterNamn = efterNamn;
        this.hsaId = hsaId;
        this.uppdrag = uppdrag;
        this.titel = titel;
    }

    public String getHsaId() {
        return hsaId;
    }

    public void setHsaId(String hsaId) {
        this.hsaId = hsaId;
    }

    public String getForNamn() {
        return forNamn;
    }

    public void setForNamn(String forNamn) {
        this.forNamn = forNamn;
    }

    public String getEfterNamn() {
        return efterNamn;
    }

    public void setEfterNamn(String efterNamn) {
        this.efterNamn = efterNamn;
    }

    public List<Uppdrag> getUppdrag() {
        return uppdrag;
    }

    public void setUppdrag(List<Uppdrag> uppdrag) {
        this.uppdrag = uppdrag;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public static class Uppdrag {

        private String vardgivare;
        private String enhet;
        private List<String> andamal;
        private List<String> systemRoles;
        /**
         * Medarbetaruppdragets namn, motsv. CommissionType#commissionName
         */
        private String namn;

        public Uppdrag() {
            enhet = "";
            andamal = asList(VARD_OCH_BEHANDLING);
        }

        public Uppdrag(String vardgivare, String enhet) {
            this(vardgivare, enhet, VARD_OCH_BEHANDLING);
        }

        public Uppdrag(String vardgivare, String enhet, String andamal) {
            this.vardgivare = vardgivare;
            this.enhet = enhet;
            this.andamal = asList(andamal);
        }

        public Uppdrag(String vardgivare, String enhet, List<String> andamal) {
            this.vardgivare = vardgivare;
            this.enhet = enhet;
            this.andamal = andamal;
        }

        public Uppdrag(String vardgivare, String enhet, List<String> andamal, List<String> systemRoles) {
            this.vardgivare = vardgivare;
            this.enhet = enhet;
            this.andamal = andamal;
            this.systemRoles = systemRoles;
        }

        public Uppdrag(String vardgivare, String enhet, List<String> andamal, List<String> systemRoles, String namn) {
            this.vardgivare = vardgivare;
            this.enhet = enhet;
            this.andamal = andamal;
            this.systemRoles = systemRoles;
            this.namn = namn;
        }

        public String getVardgivare() {
            return vardgivare;
        }

        public void setVardgivare(String vardgivare) {
            this.vardgivare = vardgivare;
        }

        public String getEnhet() {
            return enhet;
        }

        public void setEnhet(String enhet) {
            this.enhet = enhet;
        }

        public List<String> getAndamal() {
            return andamal;
        }

        public void setAndamal(List<String> andamal) {
            this.andamal = andamal;
        }

        public List<String> getSystemRoles() {
            return systemRoles;
        }

        public void setSystemRoles(List<String> systemRoles) {
            this.systemRoles = systemRoles;
        }

        public String getNamn() {
            return namn;
        }

        public void setNamn(String namn) {
            this.namn = namn;
        }
    }
}
