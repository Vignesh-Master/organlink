package com.organlink.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Service for IPFS (InterPlanetary File System) operations via Pinata
 * Handles decentralized storage of encrypted signature images
 *
 * REAL IPFS WORKFLOW:
 * 1. Encrypt signature file with AES-256
 * 2. Upload encrypted file to Pinata IPFS
 * 3. Pin file for permanent availability
 * 4. Return IPFS hash for blockchain storage
 * 5. Files viewable at: https://gateway.pinata.cloud/ipfs/{hash}
 */
@Service
@RequiredArgsConstructor
public class IPFSService {

    private static final Logger logger = LoggerFactory.getLogger(IPFSService.class);

    @Value("${organlink.ipfs.api.url}")
    private String pinataApiUrl;

    @Value("${organlink.ipfs.api.key}")
    private String pinataApiKey;

    @Value("${organlink.ipfs.secret.key}")
    private String pinataSecretKey;

    @Value("${organlink.ipfs.jwt}")
    private String pinataJWT;

    @Value("${organlink.ipfs.gateway.url}")
    private String ipfsGatewayUrl;

    private final FileEncryptionService fileEncryptionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Upload encrypted signature to Pinata IPFS
     * Returns real IPFS hash for blockchain storage
     */
    public String uploadSignatureToIPFS(MultipartFile signatureFile, String signerName, String entityType) {
        try {
            logger.info("🌐 Uploading encrypted signature to Pinata IPFS for {} ({})", signerName, entityType);

            // Step 1: Encrypt the file
            byte[] originalFileData = signatureFile.getBytes();
            String entityId = signerName.replaceAll("[^a-zA-Z0-9]", ""); // Simple ID generation

            FileEncryptionService.EncryptedFileData encryptedFile = fileEncryptionService.encryptFile(
                originalFileData,
                signatureFile.getOriginalFilename(),
                entityType,
                entityId
            );

            // Step 2: Upload encrypted file to Pinata
            String ipfsHash = uploadToPinata(
                encryptedFile.getEncryptedData(),
                encryptedFile.getEncryptedFileName(),
                signerName,
                entityType
            );

            if (ipfsHash != null) {
                // Step 3: Pin the file for permanent availability
                boolean pinned = pinFileOnPinata(ipfsHash);
                if (pinned) {
                    logger.info("✅ File uploaded and pinned to IPFS: {}", ipfsHash);
                    logger.info("🔗 View file at: {}{}", ipfsGatewayUrl, ipfsHash);
                } else {
                    logger.warn("⚠️ File uploaded but pinning failed: {}", ipfsHash);
                }
                return ipfsHash;
            } else {
                throw new RuntimeException("Pinata upload failed");
            }

        } catch (Exception e) {
            logger.error("❌ Failed to upload signature to IPFS: {}", e.getMessage());
            throw new RuntimeException("IPFS upload failed: " + e.getMessage());
        }
    }

    /**
     * Upload encrypted data to Pinata IPFS
     */
    private String uploadToPinata(byte[] encryptedData, String fileName, String signerName, String entityType) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            // Create multipart request
            HttpPost uploadRequest = new HttpPost(pinataApiUrl + "pinning/pinFileToIPFS");

            // Add authentication headers
            uploadRequest.addHeader("Authorization", "Bearer " + pinataJWT);

            // Create multipart entity
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", encryptedData, ContentType.APPLICATION_OCTET_STREAM, fileName);

            // Add metadata
            String metadata = String.format(
                "{\"name\":\"%s\",\"keyvalues\":{\"signer\":\"%s\",\"type\":\"%s\",\"encrypted\":\"true\"}}",
                fileName, signerName, entityType
            );
            builder.addTextBody("pinataMetadata", metadata, ContentType.APPLICATION_JSON);

            uploadRequest.setEntity(builder.build());

            // Execute request
            try (CloseableHttpResponse response = httpClient.execute(uploadRequest)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                if (response.getCode() == 200) {
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);
                    String ipfsHash = jsonResponse.get("IpfsHash").asText();
                    logger.info("✅ Successfully uploaded to Pinata: {}", ipfsHash);
                    return ipfsHash;
                } else {
                    logger.error("❌ Pinata upload failed: {} - {}", response.getCode(), responseBody);
                    return null;
                }
            }

        } catch (Exception e) {
            logger.error("❌ Pinata upload error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Pin file on Pinata for permanent availability
     */
    private boolean pinFileOnPinata(String ipfsHash) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpPost pinRequest = new HttpPost(pinataApiUrl + "pinning/pinByHash");
            pinRequest.addHeader("Authorization", "Bearer " + pinataJWT);
            pinRequest.addHeader("Content-Type", "application/json");

            String requestBody = String.format("{\"hashToPin\":\"%s\"}", ipfsHash);
            pinRequest.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(requestBody));

            try (CloseableHttpResponse response = httpClient.execute(pinRequest)) {
                if (response.getCode() == 200) {
                    logger.info("✅ Successfully pinned file: {}", ipfsHash);
                    return true;
                } else {
                    logger.warn("⚠️ Pinning failed for: {} (Code: {})", ipfsHash, response.getCode());
                    return false;
                }
            }

        } catch (Exception e) {
            logger.error("❌ Pinning error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Retrieve and decrypt signature from IPFS
     */
    public byte[] retrieveSignatureFromIPFS(String ipfsHash) {
        try {
            logger.info("🔍 Retrieving encrypted signature from IPFS: {}", ipfsHash);

            // Download encrypted file from IPFS gateway
            byte[] encryptedData = downloadFromIPFS(ipfsHash);

            if (encryptedData != null) {
                // For demo, we'll return the encrypted data
                // In production, you'd decrypt it here with proper authorization
                logger.info("✅ Retrieved encrypted file: {} bytes", encryptedData.length);
                return encryptedData;
            } else {
                throw new RuntimeException("Failed to download from IPFS");
            }

        } catch (Exception e) {
            logger.error("❌ Failed to retrieve signature from IPFS: {}", e.getMessage());
            throw new RuntimeException("IPFS retrieval failed: " + e.getMessage());
        }
    }

    /**
     * Download file from IPFS gateway
     */
    private byte[] downloadFromIPFS(String ipfsHash) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

            HttpGet downloadRequest = new HttpGet(ipfsGatewayUrl + ipfsHash);

            try (CloseableHttpResponse response = httpClient.execute(downloadRequest)) {
                if (response.getCode() == 200) {
                    return EntityUtils.toByteArray(response.getEntity());
                } else {
                    logger.error("❌ IPFS download failed: {}", response.getCode());
                    return null;
                }
            }

        } catch (Exception e) {
            logger.error("❌ IPFS download error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Pin signature to ensure it stays available on IPFS network
     * (Already handled in uploadSignatureToIPFS method)
     */
    public boolean pinSignatureToIPFS(String ipfsHash) {
        logger.info("📌 Pinning signature to IPFS: {}", ipfsHash);
        // Pinning is already handled during upload, but we can re-pin if needed
        return pinFileOnPinata(ipfsHash);
    }

    /**
     * Generate simulated IPFS hash for demo purposes
     * In production, this would be the actual IPFS hash
     */
    private String generateSimulatedIPFSHash(String signerName, String entityType) {
        // Create a realistic-looking IPFS hash
        String baseHash = "Qm" + Integer.toHexString((signerName + entityType + System.currentTimeMillis()).hashCode());
        return baseHash + "abcdef123456789"; // Pad to realistic length
    }

    /**
     * Validate IPFS hash format
     */
    public boolean isValidIPFSHash(String hash) {
        return hash != null && 
               hash.length() >= 46 && 
               (hash.startsWith("Qm") || hash.startsWith("bafy"));
    }

    /**
     * Get IPFS gateway URL for viewing files
     */
    public String getIPFSGatewayUrl(String ipfsHash) {
        return ipfsGatewayUrl + ipfsHash;
    }

    /**
     * List all pinned signatures from Pinata (for admin purposes)
     */
    public String[] listPinnedSignatures() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            logger.info("📋 Listing pinned signatures from Pinata");

            HttpGet listRequest = new HttpGet(pinataApiUrl + "data/pinList?status=pinned&pageLimit=100");
            listRequest.addHeader("Authorization", "Bearer " + pinataJWT);

            try (CloseableHttpResponse response = httpClient.execute(listRequest)) {
                if (response.getCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    JsonNode jsonResponse = objectMapper.readTree(responseBody);

                    JsonNode rows = jsonResponse.get("rows");
                    String[] hashes = new String[rows.size()];

                    for (int i = 0; i < rows.size(); i++) {
                        hashes[i] = rows.get(i).get("ipfs_pin_hash").asText();
                    }

                    logger.info("✅ Found {} pinned files", hashes.length);
                    return hashes;
                } else {
                    logger.error("❌ Failed to list pinned files: {}", response.getCode());
                    return new String[0];
                }
            }

        } catch (Exception e) {
            logger.error("❌ Error listing pinned signatures: {}", e.getMessage());
            return new String[0];
        }
    }
}
