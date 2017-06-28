package se.inera.intyg.infra.links.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.repository.DynamicLinkRepository;
import se.inera.intyg.infra.dynamiclink.service.DynamicLinkServiceImpl;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

/**
 * Created by eriklupander on 2017-05-08.
 */
@RunWith(MockitoJUnitRunner.class)
public class DynamicLinkServiceImplTest {

    private static final String KEY1 = "key1";
    private static final String KEY2 = "key2";
    private static final String URL1 = "http://my.url/now";
    private static final String TEXT1 = "My URL now";
    private static final String URL2 = "http://my.url/then";
    private static final String TEXT2 = "My URL then";
    private static final String MESSAGE1 = "This message is sponsored by <LINK:key1>";
    private static final String MESSAGE2 = "This <LINK:key1> is sponsored by <LINK:key2>";

    @Mock
    private DynamicLinkRepository dynamicLinkRepository;

    @InjectMocks
    private DynamicLinkServiceImpl testee;

    @Before
    public void setup() {
        when(dynamicLinkRepository.getAll()).thenReturn(buildLinksMap());
    }

    @Test
    public void applyOne() {
        String finalMessage = testee.apply("<LINK:", MESSAGE1);
        assertEquals("This message is sponsored by <a href=\"" + URL1 + "\">" + TEXT1 + "</a>", finalMessage);
    }

    @Test
    public void applyTwo() {
        String finalMessage = testee.apply("<LINK:", MESSAGE2);
        assertEquals("This <a href=\"" + URL1 + "\">" + TEXT1 + "</a> is sponsored by <a href=\"" + URL2 + "\">" + TEXT2 + "</a>", finalMessage);
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyUnknownKey() {
        testee.apply("<LINK:", "This is <LINK:invalidKey> link");
        fail("Should never get here!");
    }

    private Map<String, DynamicLink> buildLinksMap() {
        Map<String, DynamicLink> links = new HashMap<>();
        links.put(KEY1, new DynamicLink(KEY1, URL1, TEXT1, null, null));
        links.put(KEY2, new DynamicLink(KEY2, URL2, TEXT2, null, null));
        return links;
    }
}