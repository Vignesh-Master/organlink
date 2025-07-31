package com.organlink.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing digital signatures for donors and patients
 * Integrates with IPFS and Ethereum blockchain for secure storage
 */
@Entity
@Table(name = "digital_signatures")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DigitalSignature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 20)
    private String entityType; // DONOR, PATIENT

    @Column(name = "entity_id", nullable = false)
    private Long entityId; // Donor ID or Patient ID

    @Column(name = "signer_name", nullable = false, length = 100)
    private String signerName; // Name of person who signed

    @Column(name = "signer_type", nullable = false, length = 20)
    private String signerType; // SELF, GUARDIAN

    @Column(name = "guardian_name", length = 100)
    private String guardianName; // If signed by guardian

    @Column(name = "guardian_relation", length = 50)
    private String guardianRelation; // Relationship to patient/donor

    @Column(name = "signature_image_path", length = 500)
    private String signatureImagePath; // Local file path (temporary)

    @Column(name = "ipfs_hash", length = 100)
    private String ipfsHash; // IPFS hash of signature image

    @Column(name = "ethereum_tx_hash", length = 100)
    private String ethereumTxHash; // Ethereum transaction hash

    @Column(name = "blockchain_address", length = 100)
    private String blockchainAddress; // Ethereum contract address

    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus = "PENDING"; // PENDING, VERIFIED, REJECTED

    @Column(name = "verification_score")
    private Double verificationScore; // AI confidence score (0.0 to 1.0)

    @Column(name = "verification_details", columnDefinition = "TEXT")
    private String verificationDetails; // AI verification details

    @Column(name = "hospital_id", nullable = false)
    private Long hospitalId;

    @Column(name = "tenant_id", nullable = false, length = 50)
    private String tenantId;

    @Column(name = "doctor_id", length = 50)
    private String doctorId; // Doctor who registered

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    /**
     * Check if signature is verified and stored on blockchain
     */
    public boolean isBlockchainVerified() {
        return "VERIFIED".equals(verificationStatus) && 
               ipfsHash != null && 
               ethereumTxHash != null;
    }

    /**
     * Check if signature verification passed AI threshold
     */
    public boolean isAIVerified() {
        return verificationScore != null && verificationScore >= 0.75; // 75% confidence threshold
    }

    /**
     * Get verification status display
     */
    public String getVerificationStatusDisplay() {
        return switch (verificationStatus) {
            case "VERIFIED" -> "✅ Verified & Blockchain Stored";
            case "REJECTED" -> "❌ Verification Failed";
            case "PENDING" -> "⏳ Verification in Progress";
            default -> "❓ Unknown Status";
        };
    }
}
