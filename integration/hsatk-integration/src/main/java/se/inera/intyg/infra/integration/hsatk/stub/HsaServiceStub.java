package se.inera.intyg.infra.integration.hsatk.stub;

import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.stub.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HsaServiceStub {


    private List<String> readOnlyCareProvider = new ArrayList<>();

    private Map<String, HsaPerson> hsaPersonMap = new HashMap<>();
    private Map<String, CredentialInformation> credentialInformationMap = new HashMap<>();
    private Map<String, CareProviderStub> careProviderMap = new HashMap<>();
    private Map<String, CareUnitStub> careUnitMap = new HashMap<>();
    private Map<String, SubUnit> subUnitMap = new HashMap<>();

    private LocalDateTime lastHospUpdate = LocalDateTime.now();

    public void addCredentialInformation(CredentialInformation credentialInformation) {
        credentialInformationMap.put(credentialInformation.getHsaId(), credentialInformation);
    }

    public void deleteCredentialInformation(String hsaId) {
        credentialInformationMap.remove(hsaId);
    }

    public CredentialInformation getCredentialInformation(String hsaId) {
        if (credentialInformationMap.containsKey(hsaId)) {
            return credentialInformationMap.get(hsaId);
        } else if (credentialInformationMap.containsKey(hsaId.toUpperCase())) {
            return credentialInformationMap.get(hsaId.toUpperCase());
        } else if (credentialInformationMap.containsKey(hsaId.toLowerCase())) {
            return credentialInformationMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }
    }

    public void addHsaPerson(HsaPerson hsaPerson) {
        if (hsaPerson.getHsaId() != null) {
            hsaPersonMap.put(hsaPerson.getHsaId(), hsaPerson);
        }
        if (hsaPerson.getPersonalIdentityNumber() != null) {
            hsaPersonMap.put(hsaPerson.getPersonalIdentityNumber(), hsaPerson);
        }
    }

    public void deleteHsaPerson(String id) {
        hsaPersonMap.remove(id);
    }

    public HsaPerson getHsaPerson(String id) {
        if (hsaPersonMap.containsKey(id)) {
            return hsaPersonMap.get(id);
        } else if (hsaPersonMap.containsKey(id.toUpperCase())) {
            return hsaPersonMap.get(id.toUpperCase());
        } else if (hsaPersonMap.containsKey(id.toLowerCase())) {
            return hsaPersonMap.get(id.toLowerCase());
        } else {
            return null;
        }
    }

    public void addCareProvider(CareProviderStub careProviderStub) {
        if (careProviderStub != null && careProviderStub.getId() != null) {
            careProviderMap.put(careProviderStub.getId(), careProviderStub);
            if (careProviderStub.getCareUnits() != null && careProviderStub.getCareUnits().size() > 0) {
                for (CareUnitStub careUnitStub : careProviderStub.getCareUnits()) {
                    careUnitStub.setCareProviderHsaId(careProviderStub.getId());
                    careUnitMap.put(careUnitStub.getId(), careUnitStub);
                    if (careUnitStub.getSubUnits() != null && careUnitStub.getSubUnits().size() > 0) {
                        for (SubUnit subUnit : careUnitStub.getSubUnits()) {
                            subUnit.setParentHsaId(careUnitStub.getId());
                            subUnitMap.put(subUnit.getId(), subUnit);
                        }
                    }
                }
            }
        }
    }

    public void deleteCareProvider(String hsaId) {
        for (CareUnitStub careUnitStub : careProviderMap.get(hsaId).getCareUnits()) {
            for (SubUnit subUnit : careUnitStub.getSubUnits()) {
                subUnitMap.remove(subUnit.getId());
            }
            careUnitMap.remove(careUnitStub.getId());
        }
        careProviderMap.remove(hsaId);
    }

    public CareProviderStub getCareProvider(String hsaId) {
        if (careProviderMap.containsKey(hsaId)) {
            return careProviderMap.get(hsaId);
        } else if (careProviderMap.containsKey(hsaId.toUpperCase())) {
            return careProviderMap.get(hsaId.toUpperCase());
        } else if (careProviderMap.containsKey(hsaId.toLowerCase())) {
            return careProviderMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }
    }

    public CareUnitStub getCareUnit(String hsaId) {
        if (careUnitMap.containsKey(hsaId)) {
            return careUnitMap.get(hsaId);
        } else if (careUnitMap.containsKey(hsaId.toUpperCase())) {
            return careUnitMap.get(hsaId.toUpperCase());
        } else if (careUnitMap.containsKey(hsaId.toLowerCase())) {
            return careUnitMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }
    }

    public SubUnit getSubUnit(String hsaId) {
        if (subUnitMap.containsKey(hsaId)) {
            return subUnitMap.get(hsaId);
        } else if (subUnitMap.containsKey(hsaId.toUpperCase())) {
            return subUnitMap.get(hsaId.toUpperCase());
        } else if (subUnitMap.containsKey(hsaId.toLowerCase())) {
            return subUnitMap.get(hsaId.toLowerCase());
        } else {
            return null;
        }
    }

    public void markAsReadOnly(String hsaId) {
        readOnlyCareProvider.add(hsaId);
    }

    public boolean isCareProviderReadOnly(String hsaId) {
        return readOnlyCareProvider.contains(hsaId);
    }

    public LocalDateTime getHospLastUpdate() {
        return lastHospUpdate;
    }

    public void resetHospLastUpdate() {
        lastHospUpdate = LocalDateTime.now();
    }

}
