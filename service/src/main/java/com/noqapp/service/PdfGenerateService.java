package com.noqapp.service;

import com.noqapp.common.type.FileExtensionTypeEnum;
import com.noqapp.common.utils.Constants;
import com.noqapp.common.utils.FileUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * hitender
 * 1/12/18 12:10 AM
 */
@Service
public class PdfGenerateService {
    private static final Logger LOG = LoggerFactory.getLogger(PdfGenerateService.class);

    private Resource businessDetailXsl;

    private FopFactory fopFactory;
    private FOUserAgent userAgent;

    @Autowired
    public PdfGenerateService(
            @Value("${fopConfig:classpath:/xslfo/fop-conf.xml}")
            Resource fopConfig,

            @Value("${businessDetailXsl:classpath:/xslfo/xslt/business-detail.xsl}")
            Resource businessDetailXsl
    ) throws IOException {
        this.businessDetailXsl = businessDetailXsl;

        try {
            if (fopConfig.exists()) {
                this.fopFactory = FopFactory.newInstance(fopConfig.getURI());
            } else {
                LOG.warn("Failed, check why fopConfig={} failed to load", fopConfig.getFilename());
                this.fopFactory = FopFactory.newInstance(new File(".").toURI());
            }
            userAgent = fopFactory.newFOUserAgent();
            userAgent.setProducer("NoQueue Inc");
            userAgent.setCreator("NoQApp, NoQueue Inc");
            userAgent.setAuthor("NoQueue Inc");
        } catch (IOException e) {
            LOG.error("Failed loading fopConfig={} reason={}", fopConfig.getFilename(), e.getLocalizedMessage(), e);
            throw e;
        }
    }

    public File createPDF(String xmlContent, String businessName) {
        OutputStream out = null;
        try {
            File toFile = FileUtil.createTempFile(FileUtil.createRandomFilename(), FileExtensionTypeEnum.PDF.name().toLowerCase());
            out = new BufferedOutputStream(new FileOutputStream(toFile));

            // Step 3: Construct fop with desired output format
            userAgent.setCreationDate(new Date());
            userAgent.setTitle("NoQueue_" + businessName);
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, out);

            // Step 4: Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            StreamSource xslt = new StreamSource(businessDetailXsl.getInputStream());
            Transformer transformer = factory.newTransformer(xslt); // identity transformer

            // Step 5: Setup input and output for XSLT transformation
            // Setup input stream
            File xmlFile = FileUtil.createTempFile(FileUtil.createRandomFilename(), FileExtensionTypeEnum.XML.name().toLowerCase());
            FileUtils.writeStringToFile(xmlFile, xmlContent, Constants.CHAR_SET_UTF8, false);
            Source src = new StreamSource(xmlFile);

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result res = new SAXResult(fop.getDefaultHandler());

            // Step 6: Start XSLT transformation and FOP processing
            transformer.transform(src, res);
            return toFile;
        } catch (TransformerException | FOPException | IOException e) {
            LOG.error("Error transforming to PDF reason={}", e.getLocalizedMessage(), e);
            return null;
        } finally {
            if (out != null) {
                IOUtils.closeQuietly(out);
            }
        }
    }
}
