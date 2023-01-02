/*
 * Copyright (C) 2023 Inera AB (http://www.inera.se)
 *
 * This file is part of sklintyg (https://github.com/sklintyg).
 *
 * sklintyg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * sklintyg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.inera.intyg.infra.integration.ia.stub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import se.inera.intyg.infra.driftbannerdto.Banner;
import se.inera.intyg.infra.integration.ia.cache.IaCacheConfiguration;

public class IAStubRestApi {

    @Autowired
    private Cache iaCache;

    @GET
    @Path("/")
    public Response getBanners() {
        List<Banner> banners = queryCache();

        return Response.ok(banners).build();
    }

    @PUT
    @Path("/banner")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addBanner(Banner banner) {
        banner.setId(UUID.randomUUID());

        List<Banner> banners = queryCache();
        banners.add(banner);

        store(banners.toArray(new Banner[0]));

        return Response.ok().build();
    }

    private void store(Banner[] banners) {
        iaCache.put(IaCacheConfiguration.CACHE_KEY, banners);
    }

    private List<Banner> queryCache() {
        Banner[] banners = iaCache.get(IaCacheConfiguration.CACHE_KEY, Banner[].class);

        if (banners == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(Arrays.asList(banners));
    }

    /**
     * Use to evict all clear all banners.
     */
    @DELETE
    @Path("/cache")
    public Response clearCache() {
        try {
            iaCache.clear();
            return Response.ok().build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }
}
