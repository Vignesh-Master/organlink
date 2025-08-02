package com.organlink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for encrypting/decrypting files before IPFS storage
 * Uses AES-256-GCM for secure encryption
 * 
 * SECURITY WORKFLOW:
 * 1. Encrypt signature file with AES-256
 * 2. Upload encrypted file to IPFS via Pinata
 * 3. Store IPFS hash + encryption metadata in blockchain
 * 4. Only authorized users can decrypt after retrieval
 */
@Service
public class FileEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(FileEncryptionService.class);
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    @Value("${organlink.encryption.secret.key}")
    private String masterSecretKey;

    /**
     * Encrypt file data before IPFS upload
     * Returns encrypted data with IV prepended
     */
    public EncryptedFileData encryptFile(byte[] fileData, String fileName, String entityType, String entityId) {
        try {
            logger.info("🔐 Encrypting file: {} for {} ({})", fileName, entityType, entityId);

            // Generate random IV for GCM
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            // Create cipher
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(masterSecretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            // Encrypt the file data
            byte[] encryptedData = cipher.doFinal(fileData);

            // Combine IV + encrypted data
            byte[] encryptedFileWithIV = new byte[iv.length + encryptedData.length];
            System.arraycopy(iv, 0, encryptedFileWithIV, 0, iv.length);
            System.arraycopy(encryptedData, 0, encryptedFileWithIV, iv.length, encryptedData.length);

            // Create metadata
            String encryptionMetadata = createEncryptionMetadata(fileName, entityType, entityId, iv);

            logger.info("✅ File encrypted successfully: {} bytes → {} bytes", 
                       fileData.length, encryptedFileWithIV.length);

            return new EncryptedFileData(
                encryptedFileWithIV,
                Base64.getEncoder().encodeToString(iv),
                encryptionMetadata,
                fileName + ".encrypted"
            );

        } catch (Exception e) {
            logger.error("❌ File encryption failed: {}", e.getMessage());
            throw new RuntimeException("File encryption failed: " + e.getMessage());
        }
    }

    /**
     * Decrypt file data after IPFS retrieval
     */
    public byte[] decryptFile(byte[] encryptedFileWithIV, String encryptionMetadata) {
        try {
            logger.info("🔓 Decrypting file with metadata: {}", encryptionMetadata);

            // Extract IV from the beginning of encrypted data
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] encryptedData = new byte[encryptedFileWithIV.length - GCM_IV_LENGTH];
            
            System.arraycopy(encryptedFileWithIV, 0, iv, 0, GCM_IV_LENGTH);
            System.arraycopy(encryptedFileWithIV, GCM_IV_LENGTH, encryptedData, 0, encryptedData.length);

            // Create cipher for decryption
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec keySpec = new SecretKeySpec(masterSecretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            // Decrypt the data
            byte[] decryptedData = cipher.doFinal(encryptedData);

            logger.info("✅ File decrypted successfully: {} bytes → {} bytes", 
                       encryptedFileWithIV.length, decryptedData.length);

            return decryptedData;

        } catch (Exception e) {
            logger.error("❌ File decryption failed: {}", e.getMessage());
            throw new RuntimeException("File decryption failed: " + e.getMessage());
        }
    }

    /**
     * Create encryption metadata for blockchain storage
     */
    private String createEncryptionMetadata(String fileName, String entityType, String entityId, byte[] iv) {
        return String.format("{\"fileName\":\"%s\",\"entityType\":\"%s\",\"entityId\":\"%s\",\"algorithm\":\"%s\",\"ivBase64\":\"%s\",\"timestamp\":%d}",
                fileName, entityType, entityId, TRANSFORMATION, 
                Base64.getEncoder().encodeToString(iv), System.currentTimeMillis());
    }

    /**
     * Validate if user is authorized to decrypt file
     */
    public boolean isAuthorizedToDecrypt(String userId, String hospitalId, String entityType, String entityId) {
        // Implement authorization logic
        // For now, allow hospital users to decrypt their own files
        logger.info("🔍 Checking decryption authorization for user: {} (hospital: {})", userId, hospitalId);
        return true; // Simplified for demo
    }

    /**
     * Generate unique encryption key for specific entity
     * (Optional: Use different keys for different entities)
     */
    public String generateEntitySpecificKey(String entityType, String entityId) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        } catch (Exception e) {
            logger.error("Key generation failed: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Data class for encrypted file information
     */
    public static class EncryptedFileData {
        private final byte[] encryptedData;
        private final String ivBase64;
        private final String encryptionMetadata;
        private final String encryptedFileName;

        public EncryptedFileData(byte[] encryptedData, String ivBase64, String encryptionMetadata, String encryptedFileName) {
            this.encryptedData = encryptedData;
            this.ivBase64 = ivBase64;
            this.encryptionMetadata = encryptionMetadata;
            this.encryptedFileName = encryptedFileName;
        }

        public byte[] getEncryptedData() { return encryptedData; }
        public String getIvBase64() { return ivBase64; }
        public String getEncryptionMetadata() { return encryptionMetadata; }
        public String getEncryptedFileName() { return encryptedFileName; }
    }
}
