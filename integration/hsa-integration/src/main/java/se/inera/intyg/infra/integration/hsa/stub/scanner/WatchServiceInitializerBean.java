package se.inera.intyg.infra.integration.hsa.stub.scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by eriklupander on 2017-04-12.
 */
@Service
@EnableAsync
@Profile({"dev", "wc-hsa-stub", "wc-all-stubs"})
public class WatchServiceInitializerBean {

    @Autowired
    private WatchServiceBean watchServiceBean;

    @PostConstruct
    public void init() {
        watchServiceBean.start();
    }

}
