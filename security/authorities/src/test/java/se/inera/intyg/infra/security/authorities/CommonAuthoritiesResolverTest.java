package se.inera.intyg.infra.security.authorities;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import java.util.Collections;
import org.junit.BeforeClass;
import org.junit.Test;
import se.inera.intyg.infra.security.authorities.bootstrap.SecurityConfigurationLoader;
import se.inera.intyg.infra.security.common.model.AuthoritiesConstants;

public class CommonAuthoritiesResolverTest {

    private static final String authoritiesConfigurationLocation = "classpath:AuthoritiesConfigurationLoaderTest/authorities-test.yaml";
    private static final String featuresConfigurationLocation = "classpath:AuthoritiesConfigurationLoaderTest/features-test.yaml";

    private static final SecurityConfigurationLoader configurationLoader = new SecurityConfigurationLoader(authoritiesConfigurationLocation,
        featuresConfigurationLocation);

    private static final CommonAuthoritiesResolver commonAuthoritiesResolver = new CommonAuthoritiesResolver();

    @BeforeClass
    public static void setupAuthoritiesConfiguration() throws Exception {
        // Load configuration
        configurationLoader.afterPropertiesSet();

        // Setup resolver class
        commonAuthoritiesResolver.setConfigurationLoader(configurationLoader);
    }

    @Test
    public void testFeaturesDisabled() {
        final var availableFeatures = commonAuthoritiesResolver.getFeatures(Collections.emptyList());
        assertFalse(availableFeatures.get(AuthoritiesConstants.FEATURE_ENABLE_WARNING_ORIGIN_NORMAL).getGlobal());
        assertFalse(availableFeatures.get(AuthoritiesConstants.FEATURE_ENABLE_BLOCK_ORIGIN_NORMAL).getGlobal());
    }

    @Test
    public void testFeaturesEnabled() {
        final var availableFeatures = commonAuthoritiesResolver.getFeatures(Collections.singletonList("TSTNMT2321000156-1077"));
        assertTrue(availableFeatures.get(AuthoritiesConstants.FEATURE_ENABLE_WARNING_ORIGIN_NORMAL).getGlobal());
        assertTrue(availableFeatures.get(AuthoritiesConstants.FEATURE_ENABLE_BLOCK_ORIGIN_NORMAL).getGlobal());
    }
}