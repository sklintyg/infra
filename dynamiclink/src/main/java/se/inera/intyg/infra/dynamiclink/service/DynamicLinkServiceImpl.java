package se.inera.intyg.infra.dynamiclink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.repository.DynamicLinkRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by eriklupander on 2017-05-03.
 */
@Service
public class DynamicLinkServiceImpl implements DynamicLinkService {

    @Autowired
    private DynamicLinkRepository dynamicLinkRepository;

    @Override
    public Map<String, DynamicLink> getAllAsMap() {
        return dynamicLinkRepository.getAll();
    }

    @Override
    public List<DynamicLink> getAllAsList() {
        return new ArrayList<>(dynamicLinkRepository.getAll().values());
    }

    @Override
    public DynamicLink get(String key) {
        return dynamicLinkRepository.getAll().get(key);
    }

    @Override
    public String apply(String placeholderToken, String message) {
        String finalMessage = message;
        Map<String, DynamicLink> linkMap = dynamicLinkRepository.getAll();

        Pattern p = Pattern.compile(placeholderToken + "(.*?)>");
        Matcher m = p.matcher(message);
        while (m.find()) {
            String match = m.group(0);
            String key = m.group(1);
            finalMessage = finalMessage.replaceAll(match, buildDynamicUrl(key, linkMap));
        }

        return finalMessage;
    }

    private String buildDynamicUrl(String key, Map<String, DynamicLink> linkMap) {
        DynamicLink dynamicLink = linkMap.get(key);
        if (dynamicLink == null) {
            throw new IllegalArgumentException("No link found in DynamicLinkRepository for key '" + key + "'");
        }

        StringBuilder buf = new StringBuilder();
        buf.append("<a href=\"").append(dynamicLink.getUrl()).append("\"");
        if (dynamicLink.getTooltip() != null) {
            buf.append(" title=\"").append(dynamicLink.getTooltip()).append("\"");
        }
        if (dynamicLink.getTarget() != null) {
            buf.append(" target=\"").append(dynamicLink.getTarget()).append("\"");
        }
        buf.append(">").append(dynamicLink.getText()).append("</a>");
        return buf.toString();
    }
}
