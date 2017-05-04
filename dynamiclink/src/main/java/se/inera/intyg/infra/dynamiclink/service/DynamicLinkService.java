package se.inera.intyg.infra.dynamiclink.service;


import se.inera.intyg.infra.dynamiclink.model.DynamicLink;

import java.util.List;
import java.util.Map;

/**
 * Created by eriklupander on 2017-05-03.
 */
public interface DynamicLinkService {

    Map<String, DynamicLink> getAllAsMap();
    List<DynamicLink> getAllAsList();
    DynamicLink get(String key);

}
