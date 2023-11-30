/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
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

package se.inera.intyg.infra.integration.intygproxyservice.services.organization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static se.inera.intyg.infra.integration.intygproxyservice.services.organization.OrganizationUtil.DEFAULT_ARBETSPLATSKOD;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.inera.intyg.infra.integration.hsatk.model.Commission;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMember;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.model.legacy.AgandeForm;
import se.inera.intyg.infra.integration.hsatk.model.legacy.Mottagning;

@ExtendWith(MockitoExtension.class)
class CareUnitConverterTest {

    private static final String PRIVATE = "12xyz";
    private static final String PUBLIC = "2xyz";
    private static final String ZIP_CODE = "ZIP_CODE";
    private static final String CITY = "CITY";
    private static final String ADDRESS = "ADDRESS";
    private static final Mottagning CONVERTED_MEMBER = new Mottagning();

    @Mock
    private UnitAddressConverter unitAddressConverter;

    @Mock
    private CareUnitMemberConverter careUnitMemberConverter;

    @InjectMocks
    private CareUnitConverter converter;

    @Nested
    class WithMembers {

        @BeforeEach
        void setup() {
            CONVERTED_MEMBER.setNamn("NAMN");
            CONVERTED_MEMBER.setId("ID");
            when(careUnitMemberConverter.convert(any(HealthCareUnitMember.class), anyString(), any(AgandeForm.class)))
                .thenReturn(CONVERTED_MEMBER);
        }

        @Test
        void shouldConvertId() {
            final var commission = getCommission();
            final var unit = getUnit();
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertEquals(commission.getHealthCareUnitHsaId(), response.getId());
        }

        @Test
        void shouldConvertName() {
            final var commission = getCommission();
            final var unit = getUnit();
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertEquals(commission.getHealthCareUnitName(), response.getNamn());
        }

        @Test
        void shouldConvertStart() {
            final var commission = getCommission();
            final var unit = getUnit();
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertEquals(commission.getHealthCareUnitStartDate(), response.getStart());
        }

        @Test
        void shouldConvertEnd() {
            final var commission = getCommission();
            final var unit = getUnit();
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertEquals(commission.getHealthCareUnitEndDate(), response.getEnd());
        }

        @Test
        void shouldConvertProviderId() {
            final var commission = getCommission();
            final var unit = getUnit();
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertEquals(commission.getHealthCareProviderHsaId(), response.getVardgivareHsaId());
        }

        @Test
        void shouldConvertOrgNo() {
            final var commission = getCommission();
            commission.setHealthCareProviderOrgNo("ORG_NO");
            final var unit = getUnit();
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertEquals(commission.getHealthCareProviderOrgNo(), response.getVardgivareOrgnr());
        }

        @Test
        void shouldConvertMail() {
            final var commission = getCommission();
            final var unit = getUnit();
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertEquals(unit.getMail(), response.getEpost());
        }

        @Test
        void shouldConvertToFirstPhoneNumber() {
            final var commission = getCommission();
            final var unit = getUnit();
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertEquals(unit.getTelephoneNumber().get(0), response.getTelefonnummer());
        }

        @Test
        void shouldConvertTelephoneNumberToNullIfEmpty() {
            final var commission = getCommission();
            final var unit = getUnit();
            unit.setTelephoneNumber(Collections.emptyList());
            final var members = getMembers();

            final var response = converter.convert(commission, unit, members);

            assertNull(response.getTelefonnummer());
        }

        @Nested
        class AgandeFormTest {

            @Test
            void shouldConvertToUnknownIfOrgNoIsNull() {
                final var commission = getCommission();
                commission.setHealthCareProviderOrgNo(null);
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                assertEquals(AgandeForm.OKAND, response.getAgandeForm());
            }

            @Test
            void shouldConvertToUnknownIfOrgNoIsEmpty() {
                final var commission = getCommission();
                commission.setHealthCareProviderOrgNo("");
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                assertEquals(AgandeForm.OKAND, response.getAgandeForm());
            }

            @Test
            void shouldConvertToPublicIfOrgNoStartsWith2() {
                final var commission = getCommission();
                commission.setHealthCareProviderOrgNo(PUBLIC);
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                assertEquals(AgandeForm.OFFENTLIG, response.getAgandeForm());
            }

            @Test
            void shouldConvertToPrivateIfOrgNoDoesntStartWith2() {
                final var commission = getCommission();
                commission.setHealthCareProviderOrgNo(PRIVATE);
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                assertEquals(AgandeForm.PRIVAT, response.getAgandeForm());
            }
        }

        @Nested
        class WorkPlaceCodeTest {

            @Test
            void shouldConvertToDefaultIfNoPrescriptionCode() {
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();
                members.setHealthCareUnitPrescriptionCode(Collections.emptyList());

                final var response = converter.convert(commission, unit, members);

                assertEquals(DEFAULT_ARBETSPLATSKOD, response.getArbetsplatskod());
            }

            @Test
            void shouldConvertToDefaultIfNullPrescriptionCode() {
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();
                members.setHealthCareUnitPrescriptionCode(null);

                final var response = converter.convert(commission, unit, members);

                assertEquals(DEFAULT_ARBETSPLATSKOD, response.getArbetsplatskod());
            }

            @Test
            void shouldConvertToFirstPrescriptionCode() {
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();
                members.setHealthCareUnitPrescriptionCode(List.of("CODE", "CODE2"));

                final var response = converter.convert(commission, unit, members);

                assertEquals("CODE", response.getArbetsplatskod());
            }
        }

        @Nested
        class AddressTest {

            @BeforeEach
            void setup() {
                when(unitAddressConverter.convertAddress(any(List.class)))
                    .thenReturn(ADDRESS);

                when(unitAddressConverter.convertCity(any(List.class)))
                    .thenReturn(CITY);

                when(unitAddressConverter.convertZipCode(any(List.class), anyString()))
                    .thenReturn(ZIP_CODE);
            }

            @Test
            void shouldConvertAddress() {
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                verify(unitAddressConverter).convertAddress(unit.getPostalAddress());
                assertEquals(ADDRESS, response.getPostadress());
            }

            @Test
            void shouldConvertCity() {
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                verify(unitAddressConverter).convertCity(unit.getPostalAddress());
                assertEquals(CITY, response.getPostort());
            }

            @Test
            void shouldConvertZipCode() {
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                verify(unitAddressConverter).convertZipCode(unit.getPostalAddress(), unit.getPostalCode());
                assertEquals(ZIP_CODE, response.getPostnummer());
            }
        }

        @Nested
        class Members {

            @Test
            void shouldConvertOnlyActiveMembers() {
                final var captor = ArgumentCaptor.forClass(HealthCareUnitMember.class);
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();

                converter.convert(commission, unit, members);

                verify(careUnitMemberConverter, times(1)).convert(captor.capture(), anyString(), any(AgandeForm.class));
                assertEquals(members.getHealthCareUnitMember().get(0), captor.getValue());
            }

            @Test
            void shouldSendUnitId() {
                final var captor = ArgumentCaptor.forClass(String.class);
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                verify(careUnitMemberConverter, times(1)).convert(any(HealthCareUnitMember.class), captor.capture(), any(AgandeForm.class));
                assertEquals(response.getId(), captor.getValue());
            }

            @Test
            void shouldSendUnitAgandeForm() {
                final var captor = ArgumentCaptor.forClass(AgandeForm.class);
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                verify(careUnitMemberConverter, times(1)).convert(any(HealthCareUnitMember.class), anyString(), captor.capture());
                assertEquals(response.getAgandeForm(), captor.getValue());
            }

            @Test
            void shouldSetConvertedMembers() {
                final var commission = getCommission();
                final var unit = getUnit();
                final var members = getMembers();

                final var response = converter.convert(commission, unit, members);

                assertEquals(1, response.getMottagningar().size());
                assertEquals(CONVERTED_MEMBER, response.getMottagningar().get(0));
            }
        }
    }

    @Test
    void shouldSetEmptyListIfEmptyListAsMembers() {
        final var commission = getCommission();
        final var unit = getUnit();

        final var response = converter.convert(commission, unit, new HealthCareUnitMembers());

        assertEquals(Collections.emptyList(), response.getMottagningar());
    }

    private Unit getUnit() {
        final var unit = new Unit();
        unit.setMail("MAIL");
        unit.setTelephoneNumber(List.of("PHONE_NUMBER1", "PHONE_NUMBER2"));
        unit.setPostalCode("ZIP");
        unit.setPostalAddress(List.of("A1", "A2"));

        return unit;
    }

    private Commission getCommission() {
        final var commission = new Commission();

        commission.setHealthCareUnitHsaId("HSA_ID");
        commission.setHealthCareUnitName("HSA_NAME");
        commission.setHealthCareUnitStartDate(LocalDateTime.now().minusDays(1));
        commission.setHealthCareUnitEndDate(LocalDateTime.now().plusDays(2));
        commission.setHealthCareProviderHsaId("P_HSA_ID");
        commission.setHealthCareProviderName("P_HSA_NAME");

        return commission;
    }

    private HealthCareUnitMembers getMembers() {
        final var members = new HealthCareUnitMembers();
        final var activeMember = new HealthCareUnitMember();
        final var inactiveStartMember = new HealthCareUnitMember();
        final var inactiveEndMember = new HealthCareUnitMember();
        final var inactiveMember = new HealthCareUnitMember();
        inactiveStartMember.setHealthCareUnitMemberStartDate(LocalDateTime.now().plusDays(5));
        inactiveEndMember.setHealthCareUnitMemberEndDate(LocalDateTime.now().minusDays(1));
        inactiveMember.setHealthCareUnitMemberEndDate(LocalDateTime.now().minusDays(5));
        inactiveMember.setHealthCareUnitMemberStartDate(LocalDateTime.now().minusDays(6));

        members.setHealthCareUnitMember(
            List.of(activeMember, inactiveMember, inactiveStartMember, inactiveEndMember)
        );

        return members;
    }

}