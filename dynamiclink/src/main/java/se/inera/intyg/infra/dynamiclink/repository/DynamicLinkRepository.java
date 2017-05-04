package se.inera.intyg.infra.dynamiclink.repository;

import se.inera.intyg.infra.dynamiclink.model.DynamicLink;

import java.util.Map;

/**
 * Created by eriklupander on 2017-05-03.
 */
public interface DynamicLinkRepository {

    Map<String, DynamicLink> getAll();

}
