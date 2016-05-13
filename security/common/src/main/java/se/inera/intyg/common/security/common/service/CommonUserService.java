package se.inera.intyg.common.security.common.service;

import se.inera.intyg.common.security.common.model.IntygUser;
import se.inera.intyg.common.support.modules.support.feature.ModuleFeature;

import java.util.List;
import java.util.Set;

/**
 * Created by eriklupander on 2016-05-13.
 */
public interface CommonUserService {

    /**
     * Implementation should return the {@link IntygUser} instance representing the currently logged in user.
     *
     * @return WebCertUser
     */
    IntygUser getUser();

    void enableFeaturesOnUser(Feature... featuresToEnable);

    void enableModuleFeatureOnUser(String moduleName, ModuleFeature... modulefeaturesToEnable);

    Set<String> getIntygstyper(String privilegeName);

    boolean isAuthorizedForUnit(String vardgivarHsaId, String enhetsHsaId, boolean isReadOnlyOperation);

    boolean isAuthorizedForUnit(String enhetsHsaId, boolean isReadOnlyOperation);

    boolean isAuthorizedForUnits(List<String> enhetsHsaIds);

    void updateOrigin(String origin);

    void updateUserRole(String roleName);

}
