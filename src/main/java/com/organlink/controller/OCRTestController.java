package com.organlink.controller;

import net.sourceforge.tess4j.Tesseract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to verify OCR (Tesseract) setup
 * Use this to test if Tesseract is properly installed and configured
 */
@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = "*")
public class OCRTestController {

    private static final Logger logger = LoggerFactory.getLogger(OCRTestController.class);

    @Value("${organlink.ocr.tesseract.path}")
    private String tesseractPath;

    @Value("${organlink.ocr.tessdata.path}")
    private String tessdataPath;

    @Value("${organlink.ocr.enabled:true}")
    private boolean ocrEnabled;

    /**
     * Test OCR setup and configuration
     */
    @GetMapping("/ocr-status")
    public ResponseEntity<Map<String, Object>> getOCRStatus() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            logger.info("🔍 Testing OCR configuration...");
            
            // Check configuration
            status.put("ocrEnabled", ocrEnabled);
            status.put("tesseractPath", tesseractPath);
            status.put("tessdataPath", tessdataPath);
            
            // Check if Tesseract executable exists
            File tesseractExe = new File(tesseractPath);
            boolean tesseractExists = tesseractExe.exists();
            status.put("tesseractExecutableExists", tesseractExists);
            
            // Check if tessdata folder exists
            File tessdataDir = new File(tessdataPath);
            boolean tessdataExists = tessdataDir.exists() && tessdataDir.isDirectory();
            status.put("tessdataFolderExists", tessdataExists);
            
            if (tessdataExists) {
                // List available languages
                File[] langFiles = tessdataDir.listFiles((dir, name) -> name.endsWith(".traineddata"));
                if (langFiles != null) {
                    String[] languages = new String[langFiles.length];
                    for (int i = 0; i < langFiles.length; i++) {
                        languages[i] = langFiles[i].getName().replace(".traineddata", "");
                    }
                    status.put("availableLanguages", languages);
                }
            }
            
            // Try to initialize Tesseract
            try {
                Tesseract tesseract = new Tesseract();
                
                // Set the path to your Tesseract installation
                if (tesseractPath != null && !tesseractPath.isEmpty()) {
                    System.setProperty("jna.library.path", tesseractPath.replace("/tesseract.exe", ""));
                }
                
                tesseract.setDatapath(tessdataPath);
                tesseract.setLanguage("eng");
                tesseract.setPageSegMode(8);
                tesseract.setOcrEngineMode(1);
                
                status.put("tesseractInitialization", "SUCCESS");
                status.put("message", "✅ Tesseract OCR is properly configured and ready to use!");
                
                logger.info("✅ OCR configuration test passed");
                
            } catch (Exception e) {
                status.put("tesseractInitialization", "FAILED");
                status.put("initializationError", e.getMessage());
                status.put("message", "❌ Tesseract initialization failed: " + e.getMessage());
                
                logger.error("❌ OCR configuration test failed: {}", e.getMessage());
            }
            
            return ResponseEntity.ok(status);
            
        } catch (Exception e) {
            status.put("error", e.getMessage());
            status.put("message", "❌ OCR status check failed: " + e.getMessage());
            logger.error("❌ OCR status check failed: {}", e.getMessage());
            return ResponseEntity.ok(status);
        }
    }

    /**
     * Test OCR with an uploaded image
     */
    @PostMapping("/ocr-test")
    public ResponseEntity<Map<String, Object>> testOCR(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            logger.info("🔍 Testing OCR with uploaded image: {}", file.getOriginalFilename());
            
            if (!ocrEnabled) {
                result.put("success", false);
                result.put("message", "OCR is disabled in configuration");
                return ResponseEntity.ok(result);
            }
            
            // Initialize Tesseract
            Tesseract tesseract = new Tesseract();
            
            // Set the path to your Tesseract installation
            if (tesseractPath != null && !tesseractPath.isEmpty()) {
                System.setProperty("jna.library.path", tesseractPath.replace("/tesseract.exe", ""));
            }
            
            tesseract.setDatapath(tessdataPath);
            tesseract.setLanguage("eng");
            tesseract.setPageSegMode(8); // Single word mode
            tesseract.setOcrEngineMode(1); // LSTM OCR Engine
            
            // Load and process image
            BufferedImage image = ImageIO.read(file.getInputStream());
            
            // Extract text using OCR
            String extractedText = tesseract.doOCR(image);
            
            // Clean the extracted text
            String cleanedText = extractedText.toLowerCase()
                                             .replaceAll("[^a-z\\s]", "")
                                             .replaceAll("\\s+", " ")
                                             .trim();
            
            result.put("success", true);
            result.put("originalText", extractedText);
            result.put("cleanedText", cleanedText);
            result.put("fileName", file.getOriginalFilename());
            result.put("fileSize", file.getSize());
            result.put("message", "✅ OCR processing completed successfully!");
            
            logger.info("✅ OCR test successful. Extracted: '{}'", extractedText);
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("message", "❌ OCR test failed: " + e.getMessage());
            
            logger.error("❌ OCR test failed: {}", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}
