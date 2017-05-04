package se.inera.intyg.infra.links.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.service.DynamicLinkService;


/**
 * Created by eriklupander on 2017-05-03.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = se.inera.intyg.infra.links.service.TestConfig.class)
public class LinksServiceTest {

    @Autowired
    private DynamicLinkService dynamicLinkService;
    
    @Test
    public void testLoadAndGetLink() {
        List<DynamicLink> allAsList = dynamicLinkService.getAllAsList();
        assertEquals(1, allAsList.size());
    }

}
