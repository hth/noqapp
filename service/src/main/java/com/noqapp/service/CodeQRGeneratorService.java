package com.noqapp.service;

import com.noqapp.common.type.FileExtensionTypeEnum;
import com.noqapp.common.utils.FileUtil;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.apache.commons.io.FilenameUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * Creates Code QR image.
 * User: hitender
 * Date: 11/21/16 8:57 AM
 */
@Service
public class CodeQRGeneratorService {
    private static final Logger LOG = LoggerFactory.getLogger(CodeQRGeneratorService.class);

    private int imageSize;

    private Map<EncodeHintType, Object> hintMap;
    private BufferedImage overlay;

    public CodeQRGeneratorService(
        @Value ("${imageSize:300}")
        int imageSize,

        /* Relative path from base folder. */
        @Value ("${overlayFileLocation:conf/300x300_overlay_code_qr.png}")
        String overlayFileLocation
    ) {
        this.imageSize = imageSize;

        /* Create the ByteMatrix for the QR-Code that encodes the given String. */
        hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(overlayFileLocation);
            this.overlay = ImageIO.read(Objects.requireNonNull(inputStream));
        } catch (IOException e) {
            LOG.error("Failed to load image={} reason={}", overlayFileLocation, e.getLocalizedMessage(), e);
        }
    }

    public String createQRImage(String qrCodeText) throws WriterException, IOException {
        LOG.info("QR Code={}", qrCodeText);
        File toFile = FileUtil.createTempFile(FileUtil.createRandomFilenameOf16Chars(), FileExtensionTypeEnum.PNG.name().toLowerCase());

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, imageSize, imageSize, hintMap);

        /* Make the BufferedImage that are to hold the QRCode. */
        int matrixWidth = byteMatrix.getWidth();
        int matrixHeight = byteMatrix.getHeight();
        BufferedImage imageOfCodeQR = new BufferedImage(matrixWidth, matrixHeight, BufferedImage.TYPE_INT_RGB);
        imageOfCodeQR.createGraphics();

        Graphics2D graphics = (Graphics2D) imageOfCodeQR.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixHeight);
        /* Paint and save the image using the ByteMatrix. */
        //graphics.setColor(new Color(233, 34, 112));
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        //ImageIO.write(imageOfCodeQR, FileExtensionTypeEnum.PNG.name(), toFile);
        ImageIO.write(getQRCodeWithOverlay(imageOfCodeQR), FileExtensionTypeEnum.PNG.name().toLowerCase(), toFile);
        return FilenameUtils.getBaseName(toFile.getName());
    }

    /**
     * Overlays logo on top of QRCode.
     *
     * @param imageOfCodeQR
     * @return
     */
    private BufferedImage getQRCodeWithOverlay(BufferedImage imageOfCodeQR) {
        float overlayTransparency = 1f;

        int deltaHeight = imageOfCodeQR.getHeight() - overlay.getHeight();
        int deltaWidth = imageOfCodeQR.getWidth() - overlay.getWidth();

        BufferedImage combined = new BufferedImage(imageOfCodeQR.getWidth(), imageOfCodeQR.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) combined.getGraphics();
        g2.drawImage(imageOfCodeQR, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, overlayTransparency));
        g2.drawImage(overlay, Math.round(deltaWidth / 2), Math.round(deltaHeight / 2), null);
        return combined;
    }
}
