package com.example.goodsprice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Tesseract OCR configuration properties.
 * Maps to ocr.tesseract.* properties in config files.
 */
@Data
@ConfigurationProperties(prefix = "ocr.tesseract")
public class TesseractProperties {

    /**
     * Enable/disable Tesseract OCR
     */
    private boolean enabled = false;

    /**
     * Path to Tesseract executable.
     * Default: "tesseract" (assumes it's in PATH)
     */
    private String executablePath = "tesseract";

    /**
     * Path to tessdata directory containing language models.
     * Default: null (uses system default)
     */
    private String dataPath;

    /**
     * Language(s) for OCR recognition.
     * Examples: "eng" (English), "ind" (Indonesian), "eng+ind" (both)
     * Default: "eng+ind" for receipt processing
     */
    private String language = "eng+ind";

    /**
     * Page segmentation mode.
     * 0 = Orientation and script detection (OSD) only.
     * 1 = Automatic page segmentation with OSD.
     * 2 = Automatic page segmentation, but no OSD, or OCR.
     * 3 = Fully automatic page segmentation, but no OSD. (Default)
     * 4 = Assume a single column of text of variable sizes.
     * 5 = Assume a single uniform block of vertically aligned text.
     * 6 = Assume a single uniform block of text.
     * 7 = Treat the image as a single text line.
     * 8 = Treat the image as a single word.
     * 9 = Treat the image as a single word in a circle.
     * 10 = Treat the image as a single character.
     * 11 = Sparse text. Find as much text as possible in no particular order.
     * 12 = Sparse text with OSD.
     * 13 = Raw line. Treat the image as a single text line,
     *      bypassing hacks that are Tesseract-specific.
     */
    private int pageSegmentationMode = 3;

    /**
     * OCR Engine mode.
     * 0 = Legacy engine only.
     * 1 = Neural nets LSTM engine only.
     * 2 = Legacy + LSTM engines.
     * 3 = Default, based on what is available.
     */
    private int ocrEngineMode = 3;

    /**
     * DPI for image preprocessing.
     * Higher DPI = better accuracy but slower processing.
     * Default: 300
     */
    private int dpi = 300;

    /**
     * Timeout in seconds for OCR processing.
     * Default: 30 seconds
     */
    private int timeout = 30;

    /**
     * Check if Tesseract is properly configured and available.
     */
    public boolean isAvailable() {
        return enabled && executablePath != null && !executablePath.isEmpty();
    }
}
