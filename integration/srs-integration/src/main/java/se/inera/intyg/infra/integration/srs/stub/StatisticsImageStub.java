package se.inera.intyg.infra.integration.srs.stub;

import com.google.common.io.ByteStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.io.InputStream;

public class StatisticsImageStub {
    private static final Logger LOG = LoggerFactory.getLogger(StatisticsImageStub.class);

    @GET
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @Produces(MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] getImage() {
        byte[] bytes = null;
        try {
            Resource res = new ClassPathResource("statistik/M18.jpg");
            LOG.info("SRS-statistics-stub is serving {}", res.getFilename());
            InputStream is = res.getInputStream();
            bytes = ByteStreams.toByteArray(is);
            is.close();
        } catch (IOException e) {
            LOG.error("Failed to read file: $e");
        }
        return bytes;
    }

}
