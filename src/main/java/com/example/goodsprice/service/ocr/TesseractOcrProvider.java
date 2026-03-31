package com.example.goodsprice.service.ocr;

import com.example.goodsprice.config.properties.TesseractProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Tesseract OCR provider implementation.
 * Uses open-source Tesseract OCR library for text extraction.
 * Only created when ocr.tesseract.enabled=true.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "ocr.tesseract", name = "enabled", havingValue = "true")
public class TesseractOcrProvider implements OcrProvider {

    private final TesseractProperties tesseractProperties;

    @Override
    public String extractText(String imageBase64) {
        log.info("Extracting text using Tesseract OCR");

        if (!isAvailable()) {
            throw new IllegalStateException("Tesseract OCR is not enabled or not configured");
        }

        try {
            // Decode base64 image
            byte[] imageBytes = Base64.getDecoder().decode(imageBase64);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

            if (image == null) {
                throw new IOException("Failed to decode image from base64");
            }

            // Configure Tesseract
            Tesseract tesseract = new Tesseract();
            if (tesseractProperties.getDataPath() != null && !tesseractProperties.getDataPath().isEmpty()) {
                tesseract.setDatapath(tesseractProperties.getDataPath());
            }
            tesseract.setLanguage(tesseractProperties.getLanguage());
            tesseract.setPageSegMode(tesseractProperties.getPageSegmentationMode());
            tesseract.setOcrEngineMode(tesseractProperties.getOcrEngineMode());

            // Perform OCR
            String extractedText = tesseract.doOCR(image);

            log.info("Tesseract OCR completed successfully");
            return extractedText;

        } catch (UnsatisfiedLinkError e) {
            log.error("Tesseract native library not found. Please install Tesseract:", e);
            throw new RuntimeException(
                "Tesseract OCR requires native library installation. " +
                "Mac: brew install tesseract tesseract-lang | " +
                "Ubuntu: sudo apt-get install tesseract-ocr tesseract-ocr-ind | " +
                "Windows: download from https://github.com/UB-Mannheim/tesseract/wiki", e);
        } catch (TesseractException e) {
            log.error("Tesseract OCR failed", e);
            throw new RuntimeException("OCR processing failed: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("Failed to process image", e);
            throw new RuntimeException("Image processing failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAvailable() {
        return tesseractProperties.isAvailable();
    }

    @Override
    public String getProviderName() {
        return "tesseract";
    }
}
