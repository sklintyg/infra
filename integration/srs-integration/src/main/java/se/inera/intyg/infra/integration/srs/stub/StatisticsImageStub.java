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
package se.inera.intyg.infra.integration.srs.stub;

import com.google.common.io.ByteStreams;
import java.io.IOException;
import java.io.InputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseBody;

public class StatisticsImageStub {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsImageStub.class);

    @GET
    @Path("/{filename}")
    @Produces(MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage(@PathParam("filename") String filename) {
        byte[] bytes = null;
        try {
            Resource res = new ClassPathResource("statistik/" + filename + ".jpg");
            LOG.info("SRS-statistics-stub is serving {}", res.getFilename());
            InputStream is = res.getInputStream();
            bytes = ByteStreams.toByteArray(is);
            is.close();
        } catch (IOException e) {
            LOG.error("Failed to read file for " + filename, e);
        }
        return bytes;
    }

}
