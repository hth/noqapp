package com.noqapp.service.utils;

import static com.noqapp.common.utils.Constants.FILE_SEPARATOR;

import com.noqapp.common.utils.IntRandomNumberGenerator;
import com.noqapp.domain.types.BusinessTypeEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * hitender
 * 4/5/20 12:16 AM
 */
public class RandomBannerImage {
    private static final Logger LOG = LoggerFactory.getLogger(RandomBannerImage.class);

    public static String pickRandomImage(BusinessTypeEnum businessType) {
        try {
            URL url = RandomBannerImage.class.getClassLoader().getResource("banner_images" + FILE_SEPARATOR + businessType.getName());
            Path path;
            if (url != null) {
                path = Paths.get(url.toURI());

                List<String> files = new ArrayList<>();
                Files.find(path, 2, (p, bfa) -> bfa.isRegularFile()).forEach(x -> files.add(x.toAbsolutePath().toString()));
                if (0 < files.size()) {
                    IntRandomNumberGenerator intRandomNumberGenerator = IntRandomNumberGenerator.newInstanceExclusiveOfMaxRange(0, files.size());
                    return files.get(intRandomNumberGenerator.nextInt());
                }
            }
        } catch (IOException | URISyntaxException e) {
            LOG.error("Failed creating URI businessType={} reason={}", businessType, e.getLocalizedMessage(), e);
        }
        return null;
    }
}
