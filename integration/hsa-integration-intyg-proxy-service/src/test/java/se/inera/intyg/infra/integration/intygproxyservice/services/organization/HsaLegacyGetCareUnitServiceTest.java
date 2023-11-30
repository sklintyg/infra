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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.xml.ws.WebServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMember;
import se.inera.intyg.infra.integration.hsatk.model.HealthCareUnitMembers;
import se.inera.intyg.infra.integration.hsatk.model.Unit;
import se.inera.intyg.infra.integration.hsatk.model.legacy.AgandeForm;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetHealthCareUnitMembersRequestDTO;
import se.inera.intyg.infra.integration.intygproxyservice.dto.organization.GetUnitRequestDTO;

@ExtendWith(MockitoExtension.class)
class HsaLegacyGetCareUnitServiceTest {

    @Mock
    private GetHealthCareUnitMembersService getHealthCareUnitMembersService;
    @Mock
    private GetUnitService getUnitService;

    @InjectMocks
    private HsaLegacyGetCareUnitService hsaLegacyGetCareUnitService;

    private static final String DEFAULT_ARBETSPLATSKOD = "0000000";
    private static final String UNIT_HSA_ID = "HSA_ID";
    private static final String UNIT_NAME = "UNIT_NAME";
    private static final String UNIT_EMAIL = "UNIT_EMAIL";
    private static final String UNIT_PHONE_NUMBER_1 = "UNIT_PHONE_NUMBER_1";
    private static final String UNIT_PHONE_NUMBER_2 = "UNIT_PHONE_NUMBER_2";
    private static final String UNIT_POSTAL_CODE = "UNIT_POSTAL_CODE_1";
    private static final String UNIT_ADDRESS_LINE_1 = "UNIT_ADDRESS_LINE_1";
    private static final String UNIT_ADDRESS_LINE_2 = "UNIT_ADDRESS_LINE_2";
    private static final String UNIT_ADDRESS_LINE_3 = "54321 POST_TOWN";
    private static final String UNIT_PRESCRIPTION_CODE = "UNIT_PRESCRIPTION_CODE_1";

    private static final LocalDateTime ACTIVE_START_DATE = LocalDateTime.now(ZoneId.systemDefault()).minusYears(1L);
    private static final LocalDateTime ACTIVE_END_DATE = LocalDateTime.now(ZoneId.systemDefault()).plusYears(1L);

    private static final GetUnitRequestDTO UNIT_REQUEST = GetUnitRequestDTO.builder().hsaId(UNIT_HSA_ID).build();
    private static final GetHealthCareUnitMembersRequestDTO UNIT_MEMBERS_REQUEST =
        GetHealthCareUnitMembersRequestDTO.builder().hsaId(UNIT_HSA_ID).build();

    @Nested
    class NonTypicalResponsesFromIntygProxyService {

        @Test
        void shouldThrowIfNoUnitIsReturnedFromGetUnitService() {
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(null);
            assertThrows(WebServiceException.class, () ->
                hsaLegacyGetCareUnitService.get(UNIT_HSA_ID)
            );
        }

        @Test
        void shouldThrowIfConnectionToIntygProxyServiceFailsFromGetUnitService() {
            when(getUnitService.get(UNIT_REQUEST)).thenThrow(RestClientException.class);
            assertThrows(WebServiceException.class, () ->
                hsaLegacyGetCareUnitService.get(UNIT_HSA_ID)
            );
        }

        @Test
        void shouldNotThrowIfNoUnitIsReturndFromGetHealthCareUnitMembersService() {
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(new Unit());
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(null);
            assertDoesNotThrow(() ->
                hsaLegacyGetCareUnitService.get(UNIT_HSA_ID)
            );
        }

        @Test
        void shouldNotThrowIfConnectionToIntygProxyServiceFailsFromGetHealthCareUnitMembersService() {
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(new Unit());
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenThrow(RestClientException.class);
            assertDoesNotThrow(() ->
                hsaLegacyGetCareUnitService.get(UNIT_HSA_ID)
            );
        }
    }

    @Nested
    class UnitBasicProperties {

        @BeforeEach
        void init() {
            Unit unit = new Unit();
            setBasicProperties(unit, UNIT_HSA_ID, UNIT_NAME, ACTIVE_START_DATE, ACTIVE_END_DATE);
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(unit);
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(null);
        }

        @Test
        void shouldSetHsaIdOfCareUnit() {
            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(UNIT_HSA_ID, careUnit.getId());
        }

        @Test
        void shouldSetNameOfCareUnit() {
            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(UNIT_NAME, careUnit.getNamn());
        }

        @Test
        void shouldSetStartDateOfCareUnit() {
            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(ACTIVE_START_DATE, careUnit.getStart());
        }

        @Test
        void shouldSetEndDateOfCareUnit() {
            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(ACTIVE_END_DATE, careUnit.getEnd());
        }
    }

    @Nested
    class UnitEmailAndPhoneNumber {

        private Unit unit;

        @BeforeEach
        void init() {
            unit = new Unit();
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(unit);
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(null);
        }

        @Test
        void shouldSetEmailOfCareUnit() {
            setEmailAndPhoneNumber(unit, List.of(UNIT_PHONE_NUMBER_1));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(UNIT_EMAIL, careUnit.getEpost());
        }

        @Test
        void shouldSetFirstPhoneNumberFromList() {
            setEmailAndPhoneNumber(unit, List.of(UNIT_PHONE_NUMBER_1, "UNIT_PHONE_NUMBER_2"));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(UNIT_PHONE_NUMBER_1, careUnit.getTelefonnummer());
        }

        @Test
        void shouldNotSetPhoneNumberIfEmptyPhoneNumberList() {
            setEmailAndPhoneNumber(unit, List.of());

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertNull(careUnit.getTelefonnummer());
        }
    }

    @Nested
    class UnitAddress {

        private Unit unit;

        @BeforeEach
        void init() {
            unit = new Unit();
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(unit);
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(null);
        }

        @Test
        void shouldSetPostalAddressToEmptyStringIfAddressListIsEmpty() {
            setAddress(unit, List.of());

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals("", careUnit.getPostadress());
        }

        @Test
        void shouldSetPostalAddressToEmptyStringIfSingleAddressLine() {
            setAddress(unit, List.of(UNIT_ADDRESS_LINE_1));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals("", careUnit.getPostadress());
        }

        @Test
        void shouldConcatenateAddressLinesExceptLastLineForPostalAddress() {
            setAddress(unit, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, UNIT_ADDRESS_LINE_3));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(UNIT_ADDRESS_LINE_1 + " " + UNIT_ADDRESS_LINE_2, careUnit.getPostadress());
        }

        @Test
        void shouldUsePostalCodeFromPostalCodeFieldIfExists() {
            setPostalCode(unit);
            setAddress(unit, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, UNIT_ADDRESS_LINE_3));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(UNIT_POSTAL_CODE, careUnit.getPostnummer());
        }

        @Test
        void shouldUsePostalCodeFromLastAddressLineIfExistsAndUnitObjectPostalCodeFieldNotSet() {
            setAddress(unit, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, UNIT_ADDRESS_LINE_3));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals("54321", careUnit.getPostnummer());
        }

        @Test
        void shouldSetPostalCodeToEmptyStringIfLastAddressLineNotStartingWithDigit() {
            setAddress(unit, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals("", careUnit.getPostnummer());
        }

        @Test
        void shouldSetPostalCodeToEmptyStringIfLastAddressLineTooShort() {
            setAddress(unit, List.of(UNIT_ADDRESS_LINE_1, "5432 Po"));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals("", careUnit.getPostnummer());
        }

        @Test
        void shouldUsePostTownFromLastAddressLineIfStartsWithDigitAndLengthAbove7() {
            setAddress(unit, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, UNIT_ADDRESS_LINE_3));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals("POST_TOWN", careUnit.getPostort());
        }

        @Test
        void shouldUseLastAddressLineForPostTownIfNotNullAndNotStartingWithDigit() {
            setAddress(unit, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, "UNIT_ADDRESS_LINE_4 "));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals("UNIT_ADDRESS_LINE_4", careUnit.getPostort());
        }

        @Test
        void shouldSetPostTownToEmptyStringIfAddressListIsEmpty() {
            setAddress(unit, List.of());

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals("", careUnit.getPostort());
        }
    }

    @Nested
    class UnitPrescriptionCode {

        private HealthCareUnitMembers unitMembers;

        @BeforeEach
        void init() {
            unitMembers = new HealthCareUnitMembers();
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(new Unit());
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(unitMembers);
        }

        @Test
        void shouldSetDefaultPrescriptionCodeIfEmptyListReceived() {
            setPrescriptionCodeUnitMembers(unitMembers, List.of());

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(DEFAULT_ARBETSPLATSKOD, careUnit.getArbetsplatskod());
        }

        @Test
        void shouldSetDefaultPrescriptionCodeIfNullReceived() {
            final var list = new ArrayList<String>();
            list.add(null);
            setPrescriptionCodeUnitMembers(unitMembers, list);

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(DEFAULT_ARBETSPLATSKOD, careUnit.getArbetsplatskod());
        }

        @Test
        void shouldUseFirstPrescriptionCodeInListToSetPrescriptionCode() {
            setPrescriptionCodeUnitMembers(unitMembers, List.of(UNIT_PRESCRIPTION_CODE, "UNIT_PRESCRIPTION_CODE_2"));

            final var careUnit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID);
            assertEquals(UNIT_PRESCRIPTION_CODE, careUnit.getArbetsplatskod());
        }
    }

    @Nested
    class InactiveSubunitsExclusion {

        private HealthCareUnitMembers unitMembers;

        @BeforeEach
        void init() {
            unitMembers = new HealthCareUnitMembers();
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(new Unit());
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(unitMembers);
        }

        @Test
        void shouldNotAttachInactiveSubunits() {
            final var activeSubunit = new HealthCareUnitMember();
            final var inactiveSubunit = new HealthCareUnitMember();
            unitMembers.setHealthCareUnitMember(List.of(inactiveSubunit, activeSubunit));

            setBasicPropertiesUnitMember(activeSubunit, UNIT_HSA_ID, UNIT_NAME, ACTIVE_END_DATE);
            setBasicPropertiesUnitMember(inactiveSubunit, "INACTIVE_HSA_ID", "INACTIVE_NAME",
                LocalDateTime.now(ZoneId.systemDefault()).minusMonths(1L));

            final var subunits = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar();

            assertEquals(1, subunits.size());
            assertEquals(UNIT_HSA_ID, subunits.get(0).getId());
        }
    }

    @Nested
    class SubunitBasicProperties {

        @BeforeEach
        void init() {
            Unit unit = new Unit();
            HealthCareUnitMembers unitMembers = new HealthCareUnitMembers();

            setBasicProperties(unit, "PARENT_HSA_ID", null, null, null);
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(unit);

            final var unitMember = new HealthCareUnitMember();
            unitMembers.setHealthCareUnitMember(List.of(unitMember));
            setBasicPropertiesUnitMember(unitMember, UNIT_HSA_ID, UNIT_NAME, ACTIVE_END_DATE);
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(unitMembers);
        }

        @Test
        void shouldSetHsaIdOfSubunit() {
            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(UNIT_HSA_ID, subunit.getId());
        }

        @Test
        void shouldSetNameOfSubunit() {
            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(UNIT_NAME, subunit.getNamn());
        }

        @Test
        void shouldSetStartDateOfSubunit() {
            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(ACTIVE_START_DATE, subunit.getStart());
        }

        @Test
        void shouldSetEndDateOfSubunit() {
            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(ACTIVE_END_DATE, subunit.getEnd());
        }

        @Test
        void shouldSetParentHsaIdOfSubunit() {
            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("PARENT_HSA_ID", subunit.getParentHsaId());
        }

        @Test
        void shouldSetAgandeFormToUnknown() {
            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(AgandeForm.OKAND, subunit.getAgandeForm());
        }
    }

    @Nested
    class SubunitAddress {

        private HealthCareUnitMember unitMember;

        @BeforeEach
        void init() {
            unitMember = new HealthCareUnitMember();
            final var unitMembers = new HealthCareUnitMembers();
            unitMembers.setHealthCareUnitMember(List.of(unitMember));
            setBasicPropertiesUnitMember(unitMember, UNIT_HSA_ID, UNIT_NAME, ACTIVE_END_DATE);
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(new Unit());
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(unitMembers);
        }

        @Test
        void shouldSetPostalAddressToEmptyStringIfAddressListIsEmpty() {
            setAddressUnitMember(unitMember, List.of());

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("", subunit.getPostadress());
        }

        @Test
        void shouldSetPostalAddressToEmptyStringIfSingleAddressLine() {
            setAddressUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("", subunit.getPostadress());
        }

        @Test
        void shouldConcatenateAddressLinesExceptLastLineForPostalAddress() {
            setAddressUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, UNIT_ADDRESS_LINE_3));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(UNIT_ADDRESS_LINE_1 + " " + UNIT_ADDRESS_LINE_2, subunit.getPostadress());
        }

        @Test
        void shouldUsePostalCodeFromPostalCodeFieldIfExists() {
            setPostalCodeUnitMember(unitMember);
            setAddressUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, UNIT_ADDRESS_LINE_3));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(UNIT_POSTAL_CODE, subunit.getPostnummer());
        }

        @Test
        void shouldUsePostalCodeFromLastAddressLineIfExistsAndUnitObjectPostalCodeFieldNotSet() {
            setAddressUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, UNIT_ADDRESS_LINE_3));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("54321", subunit.getPostnummer());
        }

        @Test
        void shouldSetPostalCodeToEmptyStringIfLastAddressLineNotStartingWithDigit() {
            setAddressUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("", subunit.getPostnummer());
        }

        @Test
        void shouldSetPostalCodeToEmptyStringIfLastAddressLineTooShort() {
            setAddressUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1, "5432 Po"));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("", subunit.getPostnummer());
        }

        @Test
        void shouldUsePostTownFromLastAddressLineIfStartsWithDigitAndLengthAbove7() {
            setAddressUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, UNIT_ADDRESS_LINE_3));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("POST_TOWN", subunit.getPostort());
        }

        @Test
        void shouldUseLastAddressLineForPostTownIfNotNullAndNotStartingWithDigit() {
            setAddressUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1, UNIT_ADDRESS_LINE_2, "UNIT_ADDRESS_LINE_4 "));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("UNIT_ADDRESS_LINE_4", subunit.getPostort());
        }

        @Test
        void shouldSetPostTownToEmptyStringIfAddressListIsEmpty() {
            setAddressUnitMember(unitMember, List.of());

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals("", subunit.getPostort());
        }
    }

    @Nested
    class SubunitPhoneNumber {

        private HealthCareUnitMember unitMember;

        @BeforeEach
        void init() {
            unitMember = new HealthCareUnitMember();
            final var unitMembers = new HealthCareUnitMembers();
            unitMembers.setHealthCareUnitMember(List.of(unitMember));
            setBasicPropertiesUnitMember(unitMember, UNIT_HSA_ID, UNIT_NAME, ACTIVE_END_DATE);
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(new Unit());
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(unitMembers);
        }

        @Test
        void shouldJoinPhoneNumberListInCommaSeparatedString() {
            setPhoneNumberUnitMember(unitMember, List.of(UNIT_ADDRESS_LINE_1, UNIT_PHONE_NUMBER_2));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(UNIT_ADDRESS_LINE_1 + ", " + UNIT_PHONE_NUMBER_2, subunit.getTelefonnummer());
        }
    }

    @Nested
    class SubunitPrescriptionCode {

        private HealthCareUnitMember unitMember;

        @BeforeEach
        void init() {
            unitMember = new HealthCareUnitMember();
            final var unitMembers = new HealthCareUnitMembers();
            unitMembers.setHealthCareUnitMember(List.of(unitMember));
            setBasicPropertiesUnitMember(unitMember, UNIT_HSA_ID, UNIT_NAME, ACTIVE_END_DATE);
            when(getUnitService.get(UNIT_REQUEST)).thenReturn(new Unit());
            when(getHealthCareUnitMembersService.get(UNIT_MEMBERS_REQUEST)).thenReturn(unitMembers);
        }

        @Test
        void shouldSetDefaultPrescriptionCodeIfEmptyListReceived() {
            setPrescriptionCodeUnitMember(unitMember, List.of());

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(DEFAULT_ARBETSPLATSKOD, subunit.getArbetsplatskod());
        }

        @Test
        void shouldSetDefaultPrescriptionCodeIfNullReceived() {
            final var list = new ArrayList<String>();
            list.add(null);
            setPrescriptionCodeUnitMember(unitMember, list);

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(DEFAULT_ARBETSPLATSKOD, subunit.getArbetsplatskod());
        }

        @Test
        void shouldUseFirstPrescriptionCodeInListToSetPrescriptionCode() {
            setPrescriptionCodeUnitMember(unitMember, List.of(UNIT_PRESCRIPTION_CODE, "UNIT_PRESCRIPTION_CODE_2"));

            final var subunit = hsaLegacyGetCareUnitService.get(UNIT_HSA_ID).getMottagningar().get(0);
            assertEquals(UNIT_PRESCRIPTION_CODE, subunit.getArbetsplatskod());
        }
    }

    private void setBasicProperties(Unit unit, String hsaId, String name, LocalDateTime startDate, LocalDateTime endDate) {
        unit.setUnitHsaId(hsaId);
        unit.setUnitName(name);
        unit.setUnitStartDate(startDate);
        unit.setUnitEndDate(endDate);
    }

    private void setEmailAndPhoneNumber(Unit unit, List<String> phoneNumber) {
        unit.setMail(UNIT_EMAIL);
        unit.setTelephoneNumber(phoneNumber);
    }

    private void setPostalCode(Unit unit) {
        unit.setPostalCode(UNIT_POSTAL_CODE);
    }

    private void setAddress(Unit unit, List<String> postalAddress) {
        unit.setPostalAddress(postalAddress);
    }

    private void setPrescriptionCodeUnitMembers(HealthCareUnitMembers unitMembers, List<String> codes) {
        unitMembers.setHealthCareUnitPrescriptionCode(codes);
    }

    private void setBasicPropertiesUnitMember(HealthCareUnitMember unitMember, String hsaId, String name, LocalDateTime endDate) {
        unitMember.setHealthCareUnitMemberHsaId(hsaId);
        unitMember.setHealthCareUnitMemberName(name);
        unitMember.setHealthCareUnitMemberStartDate(ACTIVE_START_DATE);
        unitMember.setHealthCareUnitMemberEndDate(endDate);
    }

    private void setAddressUnitMember(HealthCareUnitMember unitMember, List<String> postalAddress) {
        unitMember.setHealthCareUnitMemberpostalAddress(postalAddress);
    }

    private void setPostalCodeUnitMember(HealthCareUnitMember unitMember) {
        unitMember.setHealthCareUnitMemberpostalCode(UNIT_POSTAL_CODE);
    }

    private void setPhoneNumberUnitMember(HealthCareUnitMember unitMember, List<String> phoneNumber) {
        unitMember.setHealthCareUnitMemberTelephoneNumber(phoneNumber);
    }

    private void setPrescriptionCodeUnitMember(HealthCareUnitMember unitMember, List<String> codes) {
        unitMember.setHealthCareUnitMemberPrescriptionCode(codes);
    }
}
