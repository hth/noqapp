package com.noqapp.search.elastic.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Loads specific mapping file.
 * hitender
 * 11/23/17 3:09 PM
 */
public class LoadMappingFiles {
    private static final Logger LOG = LoggerFactory.getLogger(LoadMappingFiles.class);

    private static final String MAPPING_LOCATION = "elastic_mapping" + File.separator;

    private LoadMappingFiles() {
    }

    /**
     * Loads file mapping for Elastic Type.
     *
     * @param loadFileOfType Name of Type who's mapping need to be loaded. Example, biz_store type mapping
     * @return
     */
    public static String loadMapping(String loadFileOfType, String buildNumber) {
        URL url = null;
        try {
            String supportedVersion;
            if ("6".equalsIgnoreCase(buildNumber.split("\\.")[0])) {
                supportedVersion = "e6";
            } else {
                supportedVersion = "e7";
            }
            url = LoadMappingFiles.class.getClassLoader().getResource(
                MAPPING_LOCATION + File.separator
                    + supportedVersion + File.separator
                    + loadFileOfType + ".mapping.json");

            Path path = Paths.get(url.toURI());
            StringBuilder data = new StringBuilder();
            Stream<String> lines = Files.lines(path);
            lines.forEach(line -> data.append(line).append("\n"));
            lines.close();

            return data.toString();
        } catch (URISyntaxException e) {
            LOG.error("Failed creating URI url={} fileName={} reason={}", url.toString(), loadFileOfType, e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error("Failed reading url={} fileName={} reason={}", url.toString(), loadFileOfType, e.getLocalizedMessage(), e);
        }

        return null;
    }
}
