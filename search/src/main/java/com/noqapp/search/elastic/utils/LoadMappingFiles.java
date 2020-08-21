package com.noqapp.search.elastic.utils;

import static com.noqapp.common.utils.Constants.FILE_SEPARATOR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Loads specific mapping file.
 * hitender
 * 11/23/17 3:09 PM
 */
public class LoadMappingFiles {
    private static final Logger LOG = LoggerFactory.getLogger(LoadMappingFiles.class);

    private static final String MAPPING_LOCATION = "elastic_mapping" + FILE_SEPARATOR;

    private LoadMappingFiles() {
    }

    /**
     * Loads file mapping for Elastic Type.
     *
     * @param loadFileOfType Name of Type who's mapping need to be loaded. Example, biz_store type mapping
     * @return
     */
    public static String loadMapping(String loadFileOfType) {
        try {
            URL url = LoadMappingFiles.class.getClassLoader().getResource(MAPPING_LOCATION + loadFileOfType + ".mapping.json");
            Path path = Paths.get(Objects.requireNonNull(url).toURI());
            StringBuilder data = new StringBuilder();
            Stream<String> lines = Files.lines(path);
            lines.forEach(line -> data.append(line).append("\n"));
            lines.close();

            return data.toString();
        } catch (URISyntaxException e) {
            LOG.error("Failed creating URI fileName={} reason={}", loadFileOfType, e.getLocalizedMessage(), e);
        } catch (IOException e) {
            LOG.error("Failed reading fileName={} reason={}", loadFileOfType, e.getLocalizedMessage(), e);
        }

        return null;
    }
}
