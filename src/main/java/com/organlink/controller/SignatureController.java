package com.organlink.controller;

import com.organlink.model.entity.DigitalSignature;
import com.organlink.service.SignatureVerificationService;
import com.organlink.service.SignatureVerificationService.SignatureVerificationResult;
import com.organlink.utils.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST Controller for Signature Verification + Blockchain Integration
 * Implements your brilliant workflow:
 * 
 * Doctor Registers Patient/Donor ➝ Upload Signature ➝ Verify Name–Signature Match ➝
 * If Valid ➝ Upload to IPFS ➝ Get IPFS Hash ➝ Store Hash + Metadata in Ethereum ➝ DB Final Save
 */
@RestController
@RequestMapping("/signatures")
@RequiredArgsConstructor
@Tag(name = "Signature Verification", description = "AI-powered signature verification with blockchain storage")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SignatureController {

    private static final Logger logger = LoggerFactory.getLogger(SignatureController.class);

    private final SignatureVerificationService signatureVerificationService;

    /**
     * Upload and verify signature with blockchain storage
     * This is the main endpoint that implements your entire concept!
     */
    @PostMapping("/verify-and-store")
    @Operation(summary = "Verify signature and store on blockchain", 
               description = "Complete workflow: AI verification → IPFS upload → Ethereum storage")
    public ResponseEntity<ApiResponse<SignatureVerificationResponse>> verifyAndStoreSignature(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            
            @Parameter(description = "Signature image file") 
            @RequestParam("signatureFile") MultipartFile signatureFile,
            
            @Parameter(description = "Name of person signing") 
            @RequestParam("signerName") String signerName,
            
            @Parameter(description = "Type of signer: SELF or GUARDIAN") 
            @RequestParam("signerType") String signerType,
            
            @Parameter(description = "Guardian name (if signer is guardian)") 
            @RequestParam(value = "guardianName", required = false) String guardianName,
            
            @Parameter(description = "Guardian relation (if signer is guardian)") 
            @RequestParam(value = "guardianRelation", required = false) String guardianRelation,
            
            @Parameter(description = "Entity type: DONOR or PATIENT") 
            @RequestParam("entityType") String entityType,
            
            @Parameter(description = "Entity ID (donor or patient ID)") 
            @RequestParam("entityId") Long entityId,
            
            @Parameter(description = "Doctor ID who is registering") 
            @RequestParam("doctorId") String doctorId) {

        logger.info("🔄 Signature verification request for {} ({}) by doctor {}", 
                   signerName, entityType, doctorId);

        try {
            // Determine hospital ID from tenant ID
            Long hospitalId = getHospitalIdFromTenant(tenantId);

            // Execute the complete signature verification workflow
            SignatureVerificationResult result = signatureVerificationService.processSignatureVerification(
                    signatureFile,
                    signerName,
                    signerType,
                    guardianName,
                    guardianRelation,
                    entityType,
                    entityId,
                    hospitalId,
                    tenantId,
                    doctorId
            );

            if (result.isSuccess()) {
                // Create response with blockchain details
                SignatureVerificationResponse response = new SignatureVerificationResponse(
                        true,
                        result.getIpfsHash(),
                        result.getEthereumTxHash(),
                        result.getConfidenceScore(),
                        result.getDetails(),
                        result.getMessage(),
                        "https://ipfs.io/ipfs/" + result.getIpfsHash(),
                        "https://sepolia.etherscan.io/tx/" + result.getEthereumTxHash()
                );

                ApiResponse<SignatureVerificationResponse> apiResponse = ApiResponse.success(
                        "Signature verified and stored on blockchain successfully!", 
                        response
                );

                logger.info("✅ Signature verification completed successfully for {}", signerName);
                return ResponseEntity.ok(apiResponse);

            } else {
                // Verification failed
                SignatureVerificationResponse response = new SignatureVerificationResponse(
                        false,
                        null,
                        null,
                        0.0,
                        null,
                        result.getMessage(),
                        null,
                        null
                );

                ApiResponse<SignatureVerificationResponse> apiResponse = ApiResponse.error(
                        "Signature verification failed"
                );
                apiResponse.setData(response);

                logger.warn("❌ Signature verification failed for {}: {}", signerName, result.getMessage());
                return ResponseEntity.status(400).body(apiResponse);
            }

        } catch (Exception e) {
            logger.error("💥 Signature verification error: {}", e.getMessage());
            
            ApiResponse<SignatureVerificationResponse> response = ApiResponse.error(
                    "Signature verification system error: " + e.getMessage(), 
                    null
            );
            
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get signature verification status
     */
    @GetMapping("/status/{entityType}/{entityId}")
    @Operation(summary = "Get signature verification status", 
               description = "Check if signature is verified and stored on blockchain")
    public ResponseEntity<ApiResponse<SignatureStatusResponse>> getSignatureStatus(
            @Parameter(description = "Hospital tenant ID") 
            @RequestHeader("X-Tenant-ID") String tenantId,
            
            @Parameter(description = "Entity type: DONOR or PATIENT") 
            @PathVariable String entityType,
            
            @Parameter(description = "Entity ID") 
            @PathVariable Long entityId) {

        logger.info("Checking signature status for {} ID: {}", entityType, entityId);

        // In a real implementation, this would query the database
        // For demo, we'll return a simulated status
        SignatureStatusResponse status = new SignatureStatusResponse(
                true,
                "VERIFIED",
                0.85,
                "QmDemo123456789",
                "0xdemo123456789",
                "Signature verified and stored on blockchain"
        );

        ApiResponse<SignatureStatusResponse> response = ApiResponse.success(
                "Signature status retrieved successfully", 
                status
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Map tenant ID to hospital ID
     */
    private Long getHospitalIdFromTenant(String tenantId) {
        return switch (tenantId) {
            case "apollo-chennai" -> 2L;
            case "apollo-mumbai" -> 1L;
            default -> 1L; // Default to Mumbai
        };
    }

    // Response DTOs
    public static class SignatureVerificationResponse {
        private final boolean success;
        private final String ipfsHash, ethereumTxHash, message, details, ipfsUrl, blockchainUrl;
        private final double confidenceScore;

        public SignatureVerificationResponse(boolean success, String ipfsHash, String ethereumTxHash, 
                                           double confidenceScore, String details, String message,
                                           String ipfsUrl, String blockchainUrl) {
            this.success = success;
            this.ipfsHash = ipfsHash;
            this.ethereumTxHash = ethereumTxHash;
            this.confidenceScore = confidenceScore;
            this.details = details;
            this.message = message;
            this.ipfsUrl = ipfsUrl;
            this.blockchainUrl = blockchainUrl;
        }

        // Getters
        public boolean isSuccess() { return success; }
        public String getIpfsHash() { return ipfsHash; }
        public String getEthereumTxHash() { return ethereumTxHash; }
        public double getConfidenceScore() { return confidenceScore; }
        public String getDetails() { return details; }
        public String getMessage() { return message; }
        public String getIpfsUrl() { return ipfsUrl; }
        public String getBlockchainUrl() { return blockchainUrl; }
    }

    public static class SignatureStatusResponse {
        private final boolean hasSignature;
        private final String status;
        private final double confidenceScore;
        private final String ipfsHash, ethereumTxHash, message;

        public SignatureStatusResponse(boolean hasSignature, String status, double confidenceScore,
                                     String ipfsHash, String ethereumTxHash, String message) {
            this.hasSignature = hasSignature;
            this.status = status;
            this.confidenceScore = confidenceScore;
            this.ipfsHash = ipfsHash;
            this.ethereumTxHash = ethereumTxHash;
            this.message = message;
        }

        // Getters
        public boolean isHasSignature() { return hasSignature; }
        public String getStatus() { return status; }
        public double getConfidenceScore() { return confidenceScore; }
        public String getIpfsHash() { return ipfsHash; }
        public String getEthereumTxHash() { return ethereumTxHash; }
        public String getMessage() { return message; }
    }
}
