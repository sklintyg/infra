package se.inera.intyg.infra.dynamiclink.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import se.inera.intyg.infra.dynamiclink.model.DynamicLink;
import se.inera.intyg.infra.dynamiclink.repository.DynamicLinkRepository;


/**
 * Created by eriklupander on 2017-05-03.
 */
@Service
public class DynamicLinkServiceImpl implements DynamicLinkService {

    @Autowired
    DynamicLinkRepository dynamicLinkRepository;

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
}
