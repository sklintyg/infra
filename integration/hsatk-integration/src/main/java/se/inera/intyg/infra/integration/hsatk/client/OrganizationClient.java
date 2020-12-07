package se.inera.intyg.infra.integration.hsatk.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.integration.hsatk.exception.HsaServiceCallException;
import se.riv.infrastructure.directory.organization.gethealthcareprovider.v1.rivtabp21.GetHealthCareProviderResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.GetHealthCareProviderResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.GetHealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareproviderresponder.v1.HealthCareProviderType;
import se.riv.infrastructure.directory.organization.gethealthcareunit.v2.rivtabp21.GetHealthCareUnitResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembers.v2.rivtabp21.GetHealthCareUnitMembersResponderInterface;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.GetHealthCareUnitMembersResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.GetHealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitmembersresponder.v2.HealthCareUnitMembersType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.GetHealthCareUnitResponseType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.GetHealthCareUnitType;
import se.riv.infrastructure.directory.organization.gethealthcareunitresponder.v2.HealthCareUnitType;
import se.riv.infrastructure.directory.organization.getunit.v2.rivtabp21.GetUnitResponderInterface;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GetUnitResponseType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.GetUnitType;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.ProfileEnum;
import se.riv.infrastructure.directory.organization.getunitresponder.v2.UnitType;

import java.util.List;

@Service
public class OrganizationClient {

    @Autowired
    private GetHealthCareProviderResponderInterface getHealthCareProviderResponderInterface;

    @Autowired
    private GetHealthCareUnitResponderInterface getHealthCareUnitResponderInterface;

    @Autowired
    private GetHealthCareUnitMembersResponderInterface getHealthCareUnitMembersResponderInterface;

    @Autowired
    private GetUnitResponderInterface getUnitResponderInterface;

    @Value("${infrastructure.directory.logicalAddress}")
    private String logicalAddress;

    private static boolean includeFeignedObject = false;

    public List<HealthCareProviderType> getHealthCareProvider(
            String healthCareProviderHsaId, String healthCareProviderOrgNo)
            throws HsaServiceCallException {

        GetHealthCareProviderType parameters = new GetHealthCareProviderType();

        parameters.setHealthCareProviderHsaId(healthCareProviderHsaId);
        parameters.setHealthCareProviderOrgNo(healthCareProviderOrgNo);
        parameters.setIncludeFeignedObject(includeFeignedObject);

        GetHealthCareProviderResponseType response =
                getHealthCareProviderResponderInterface.getHealthCareProvider(logicalAddress, parameters);

        if (response == null || response.getHealthCareProvider() == null || response.getHealthCareProvider().isEmpty()) {
            System.out.println("Response is null or empty");
            throw new HsaServiceCallException("Could not GetHealthCareProvider for hsaId " + healthCareProviderHsaId);
        }

        return response.getHealthCareProvider();
    }

    public HealthCareUnitType getHealthCareUnit(String healthCareUnitMemberHsaId)
            throws HsaServiceCallException {

        GetHealthCareUnitType parameters = new GetHealthCareUnitType();

        parameters.setHealthCareUnitMemberHsaId(healthCareUnitMemberHsaId);
        parameters.setIncludeFeignedObject(includeFeignedObject);

        GetHealthCareUnitResponseType response = getHealthCareUnitResponderInterface.getHealthCareUnit(logicalAddress, parameters);

        if (response.getHealthCareUnit() == null) {
            System.out.println("Response is null");
            throw new HsaServiceCallException("Could not GetHealthCareUnit for healthCareUnitMemberHsaId " + healthCareUnitMemberHsaId);
        }

        return response.getHealthCareUnit();
    }

    public HealthCareUnitMembersType getHealthCareUnitMembers(String healtCareUnitHsaId)
            throws HsaServiceCallException {
        GetHealthCareUnitMembersType parameters = new GetHealthCareUnitMembersType();

        parameters.setHealthCareUnitHsaId(healtCareUnitHsaId);
        parameters.setIncludeFeignedObject(includeFeignedObject);

        GetHealthCareUnitMembersResponseType response = getHealthCareUnitMembersResponderInterface
                .getHealthCareUnitMembers(logicalAddress, parameters);

        if (response == null || response.getHealthCareUnitMembers() == null) {
            System.out.println("Response is null");
            throw new HsaServiceCallException("Could not GetHealthCareUnitMembers for healtCareUnitHsaId " + healtCareUnitHsaId);
        }

        return response.getHealthCareUnitMembers();
    }

    public UnitType getUnit(String unitHsaId, ProfileEnum profile)
            throws HsaServiceCallException {

        GetUnitType parameters = new GetUnitType();

        parameters.setUnitHsaId(unitHsaId);
        parameters.setProfile(profile);
        parameters.setIncludeFeignedObject(includeFeignedObject);

        GetUnitResponseType response = getUnitResponderInterface.getUnit(logicalAddress, parameters);

        if (response == null || response.getUnit() == null) {
            System.out.println("Response is null");
            throw new HsaServiceCallException("Could not GetUnit for unitHsaId " + unitHsaId);
        }

        return response.getUnit();
    }
}
