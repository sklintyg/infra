package se.inera.intyg.common.security.siths;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.inera.intyg.common.integration.hsa.model.AuthenticationMethod;
import se.inera.intyg.common.integration.hsa.model.Vardenhet;
import se.inera.intyg.common.integration.hsa.model.Vardgivare;
import se.inera.intyg.common.security.common.model.IntygUser;
import se.riv.infrastructure.directory.v1.PersonInformationType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by eriklupander on 2016-05-17.
 */
public class DefaultUserDetailsHelper {

    private static final String SPACE = " ";
    private static final Logger LOG = LoggerFactory.getLogger(DefaultUserDetailsHelper.class);

    public void decorateIntygUserWithAdditionalInfo(IntygUser intygUser, List<PersonInformationType> hsaPersonInfo) {

        List<String> specialiseringar = extractSpecialiseringar(hsaPersonInfo);
        List<String> legitimeradeYrkesgrupper = extractLegitimeradeYrkesgrupper(hsaPersonInfo);
        List<String> befattningar = extractBefattningar(hsaPersonInfo);
        String titel = extractTitel(hsaPersonInfo);

        intygUser.setSpecialiseringar(specialiseringar);
        intygUser.setLegitimeradeYrkesgrupper(legitimeradeYrkesgrupper);
        intygUser.setBefattningar(befattningar);
        intygUser.setTitel(titel);
    }

    public List<String> extractBefattningar(List<PersonInformationType> hsaPersonInfo) {
        Set<String> befattningar = new TreeSet<>();

        for (PersonInformationType userType : hsaPersonInfo) {
            if (userType.getPaTitle() != null) {
                List<String> hsaTitles = userType.getPaTitle().stream().map(paTitle -> paTitle.getPaTitleName()).collect(Collectors.toList());
                befattningar.addAll(hsaTitles);
            }
        }
        return new ArrayList<>(befattningar);
    }

    /**
     * Tries to use title attribute, otherwise resorts to healthcareProfessionalLicenses.
     */
    public String extractTitel(List<PersonInformationType> hsaPersonInfo) {
        Set<String> titleSet = new HashSet<>();
        for (PersonInformationType pit : hsaPersonInfo) {
            if (pit.getTitle() != null && pit.getTitle().trim().length() > 0) {
                titleSet.add(pit.getTitle());
            } else if (pit.getHealthCareProfessionalLicence() != null && pit.getHealthCareProfessionalLicence().size() > 0) {
                titleSet.addAll(pit.getHealthCareProfessionalLicence());
            }
        }
        return titleSet.stream().sorted().collect(Collectors.joining(", "));
    }

    public void decorateIntygUserWithAuthenticationMethod(IntygUser intygUser, String authenticationScheme) {

        if (authenticationScheme.endsWith(":fake")) {
            intygUser.setAuthenticationMethod(AuthenticationMethod.FAKE);
        } else {
            intygUser.setAuthenticationMethod(AuthenticationMethod.SITHS);
        }
    }

    public void decorateIntygUserWithDefaultVardenhet(IntygUser intygUser) {
        setFirstVardenhetOnFirstVardgivareAsDefault(intygUser);

        // TODO Get HSA id for the first MIU
//        String medarbetaruppdragHsaId = ""; //getAssertion(credential).getEnhetHsaId();
//
//        boolean changeSuccess;
//
//        if (StringUtils.isNotBlank(medarbetaruppdragHsaId)) {
//            changeSuccess = user.changeValdVardenhet(medarbetaruppdragHsaId);
//        } else {
//            LOG.error("Assertion did not contain any 'medarbetaruppdrag', defaulting to use one of the Vardenheter present in the user");
//            changeSuccess =
//        }
//
//        if (!changeSuccess) {
//            LOG.error("When logging in user '{}', unit with HSA-id {} could not be found in users MIUs", user.getHsaId(), medarbetaruppdragHsaId);
//            throw new MissingMedarbetaruppdragException(user.getHsaId());
//        }

        LOG.debug("Setting care unit '{}' as default unit on user '{}'", intygUser.getValdVardenhet().getId(), intygUser.getHsaId());
    }

    public List<String> extractLegitimeradeYrkesgrupper(List<PersonInformationType> hsaUserTypes) {
        Set<String> lygSet = new TreeSet<>();

        for (PersonInformationType userType : hsaUserTypes) {
            if (userType.getPaTitle() != null) {
                List<String> hsaTitles = userType.getPaTitle().stream().map(paTitle -> paTitle.getPaTitleName()).collect(Collectors.toList());
                lygSet.addAll(hsaTitles);
            }
        }

        return new ArrayList<>(lygSet);
    }

    public List<String> extractSpecialiseringar(List<PersonInformationType> hsaUserTypes) {
        Set<String> specSet = new TreeSet<>();

        for (PersonInformationType userType : hsaUserTypes) {
            if (userType.getSpecialityName() != null) {
                List<String> specialityNames = userType.getSpecialityName();
                specSet.addAll(specialityNames);
            }
        }

        return new ArrayList<>(specSet);
    }

//    private String extractTitel(List<PersonInformationType> hsaUserTypes) {
//        List<String> titlar = new ArrayList<>();
//
//        for (PersonInformationType userType : hsaUserTypes) {
//            if (StringUtils.isNotBlank(userType.getTitle())) {
//                titlar.add(userType.getTitle());
//            }
//        }
//
//        return StringUtils.join(titlar, COMMA);
//    }

    public boolean setFirstVardenhetOnFirstVardgivareAsDefault(IntygUser intygUser) {
        Vardgivare firstVardgivare = intygUser.getVardgivare().get(0);
        intygUser.setValdVardgivare(firstVardgivare);

        Vardenhet firstVardenhet = firstVardgivare.getVardenheter().get(0);
        intygUser.setValdVardenhet(firstVardenhet);

        return true;
    }

    public String compileName(String fornamn, String mellanOchEfterNamn) {

        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(fornamn)) {
            sb.append(fornamn);
        }

        if (StringUtils.isNotBlank(mellanOchEfterNamn)) {
            if (sb.length() > 0) {
                sb.append(SPACE);
            }
            sb.append(mellanOchEfterNamn);
        }

        return sb.toString();
    }
    
}
